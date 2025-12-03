package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.sprite.Animation;
import com.atropellalo.game.sprite.AnimationState;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * El Aplastador (Bruiser Boss) - Jefe de la oleada 10.
 * 
 * Un tanque masivo con ataques devastadores:
 * - Golpe de Terremoto: Crea una onda de choque circular que daña al jugador
 * - Carga Frontal: Embiste al jugador en línea recta con alto daño
 * 
 * Usa sprite personalizado BruiserBoss.png.
 */
public class BruiserBoss extends Enemy {
    
    private static final String SPRITE_PATH = "/img/bruiser_boss.png";
    private static final float SPRITE_SCALE = 3.0f;
    
    // Sprite compartido para todos los BruiserBoss
    private static BufferedImage sprite = null;
    private static int spriteWidth = 0;
    private static int spriteHeight = 0;
    
    // Estados del jefe
    private enum BossState {
        WALKING,
        CHARGING,
        EARTHQUAKE,
        COOLDOWN
    }
    
    private BossState currentState = BossState.WALKING;
    
    // Timers para habilidades
    private float earthquakeCooldownTimer = 0;
    private float chargeCooldownTimer = 3.0f; // Empieza con algo de cooldown
    private float chargeTimer = 0;
    private float cooldownTimer = 0;
    private float earthquakeAnimTimer = 0;
    private static final float EARTHQUAKE_ANIM_DURATION = 0.5f;
    
    // Dirección de carga
    private float chargeDirectionX = 0;
    private float chargeDirectionY = 0;
    
    // Callback para daño al jugador
    private BossDamageCallback damageCallback;
    
    // Colores del jefe
    private static final Color BODY_COLOR = new Color(80, 60, 50);
    private static final Color ARMOR_COLOR = new Color(50, 50, 55);
    private static final Color ARM_COLOR = new Color(120, 80, 70);
    private static final Color EYE_COLOR = new Color(255, 50, 50);
    private static final Color EARTHQUAKE_COLOR = new Color(139, 90, 43, 150);
    
    /**
     * Interface para callback de daño al jugador.
     */
    public interface BossDamageCallback {
        void onBossDamage(float damage);
    }
    
    /**
     * Constructor del Aplastador.
     * 
     * @param x Posición X inicial
     * @param y Posición Y inicial
     */
    public BruiserBoss(float x, float y) {
        super(x, y, 
              GameConfig.BRUISER_BOSS_HEALTH,
              GameConfig.BRUISER_BOSS_SPEED,
              GameConfig.BRUISER_BOSS_SIZE,
              GameConfig.BRUISER_BOSS_CONTACT_DAMAGE);
        this.xpMultiplier = (float) GameConfig.XP_BRUISER_BOSS / GameConfig.XP_SLOW_ZOMBIE;
        
        loadSprite();
        initializeAnimations();
        this.currentAnimState = AnimationState.IDLE;
    }
    
    /**
     * Carga el sprite desde recursos (lazy loading).
     */
    private void loadSprite() {
        if (sprite == null) {
            try {
                sprite = ImageIO.read(getClass().getResourceAsStream(SPRITE_PATH));
                if (sprite != null) {
                    spriteWidth = sprite.getWidth();
                    spriteHeight = sprite.getHeight();
                }
            } catch (IOException e) {
                System.err.println("Error cargando " + SPRITE_PATH + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Inicializa las animaciones con el sprite cargado.
     */
    private void initializeAnimations() {
        if (sprite != null) {
            this.animations = new java.util.HashMap<>();
            this.animations.put(AnimationState.IDLE, new Animation(sprite));
            this.animations.put(AnimationState.MOVING, new Animation(sprite));
            this.animations.put(AnimationState.ATTACK, new Animation(sprite));
            this.animations.put(AnimationState.DEATH, new Animation(sprite));
        }
    }
    
    /**
     * Establece el callback para daño al jugador.
     * 
     * @param callback Callback de daño
     */
    public void setDamageCallback(BossDamageCallback callback) {
        this.damageCallback = callback;
    }
    
    @Override
    public void update(float deltaTime, float playerX, float playerY) {
        // Actualizar cooldowns
        if (earthquakeCooldownTimer > 0) {
            earthquakeCooldownTimer -= deltaTime;
        }
        if (chargeCooldownTimer > 0) {
            chargeCooldownTimer -= deltaTime;
        }
        
        // Calcular distancia al jugador
        float dx = playerX - x;
        float dy = playerY - y;
        float distanceToPlayer = (float) Math.sqrt(dx * dx + dy * dy);
        
        // Actualizar rotación hacia el jugador
        if (dx != 0 || dy != 0) {
            rotation = (float) Math.atan2(dx, -dy);
        }
        
        // Actualizar animación según estado
        updateBossAnimationState(deltaTime);
        
        switch (currentState) {
            case WALKING:
                handleWalkingState(deltaTime, playerX, playerY, dx, dy, distanceToPlayer);
                break;
            case CHARGING:
                handleChargingState(deltaTime, playerX, playerY);
                break;
            case EARTHQUAKE:
                handleEarthquakeState(deltaTime, playerX, playerY, distanceToPlayer);
                break;
            case COOLDOWN:
                handleCooldownState(deltaTime);
                break;
        }
    }
    
    /**
     * Actualiza el estado de animación del jefe.
     */
    private void updateBossAnimationState(float deltaTime) {
        AnimationState newState;
        
        switch (currentState) {
            case CHARGING:
            case EARTHQUAKE:
                newState = AnimationState.ATTACK;
                break;
            case WALKING:
                newState = AnimationState.MOVING;
                break;
            default:
                newState = AnimationState.IDLE;
        }
        
        if (!alive) {
            newState = AnimationState.DEATH;
        }
        
        if (newState != currentAnimState) {
            currentAnimState = newState;
            if (animations != null && animations.containsKey(currentAnimState)) {
                animations.get(currentAnimState).reset();
            }
        }
        
        if (animations != null && animations.containsKey(currentAnimState)) {
            animations.get(currentAnimState).update(deltaTime);
        }
    }
    
    /**
     * Maneja el estado de caminar hacia el jugador.
     */
    private void handleWalkingState(float deltaTime, float playerX, float playerY, 
                                     float dx, float dy, float distanceToPlayer) {
        // Verificar si puede hacer carga (jugador lejos)
        if (chargeCooldownTimer <= 0 && distanceToPlayer > GameConfig.BRUISER_CHARGE_MIN_DISTANCE) {
            startCharge(dx, dy, distanceToPlayer);
            return;
        }
        
        // Verificar si puede hacer terremoto (jugador cerca)
        if (earthquakeCooldownTimer <= 0 && distanceToPlayer < GameConfig.BRUISER_EARTHQUAKE_RADIUS * 0.8f) {
            startEarthquake();
            return;
        }
        
        // Moverse hacia el jugador
        if (distanceToPlayer > 0) {
            float normalizedDx = dx / distanceToPlayer;
            float normalizedDy = dy / distanceToPlayer;
            
            x += normalizedDx * speed * deltaTime;
            y += normalizedDy * speed * deltaTime;
        }
    }
    
    /**
     * Inicia el ataque de carga.
     */
    private void startCharge(float dx, float dy, float distance) {
        currentState = BossState.CHARGING;
        chargeTimer = GameConfig.BRUISER_CHARGE_DURATION;
        
        // Guardar dirección de carga
        chargeDirectionX = dx / distance;
        chargeDirectionY = dy / distance;
    }
    
    /**
     * Maneja el estado de carga.
     */
    private void handleChargingState(float deltaTime, float playerX, float playerY) {
        chargeTimer -= deltaTime;
        
        if (chargeTimer <= 0) {
            // Terminar carga
            currentState = BossState.COOLDOWN;
            cooldownTimer = 0.5f; // Pequeña pausa después de cargar
            chargeCooldownTimer = GameConfig.BRUISER_CHARGE_COOLDOWN;
            return;
        }
        
        // Moverse en la dirección de carga
        x += chargeDirectionX * GameConfig.BRUISER_CHARGE_SPEED * deltaTime;
        y += chargeDirectionY * GameConfig.BRUISER_CHARGE_SPEED * deltaTime;
        
        // Verificar colisión con jugador durante carga
        float dx = playerX - x;
        float dy = playerY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance < size && damageCallback != null) {
            damageCallback.onBossDamage(GameConfig.BRUISER_CHARGE_DAMAGE);
            // Terminar carga al impactar
            currentState = BossState.COOLDOWN;
            cooldownTimer = 0.8f;
            chargeCooldownTimer = GameConfig.BRUISER_CHARGE_COOLDOWN;
        }
    }
    
    /**
     * Inicia el ataque de terremoto.
     */
    private void startEarthquake() {
        currentState = BossState.EARTHQUAKE;
        earthquakeAnimTimer = EARTHQUAKE_ANIM_DURATION;
    }
    
    /**
     * Maneja el estado de terremoto.
     */
    private void handleEarthquakeState(float deltaTime, float playerX, float playerY, float distanceToPlayer) {
        earthquakeAnimTimer -= deltaTime;
        
        // Al llegar al pico de la animación (mitad), aplicar daño
        if (earthquakeAnimTimer <= EARTHQUAKE_ANIM_DURATION / 2 && earthquakeAnimTimer + deltaTime > EARTHQUAKE_ANIM_DURATION / 2) {
            // Aplicar daño si el jugador está en rango
            if (distanceToPlayer <= GameConfig.BRUISER_EARTHQUAKE_RADIUS && damageCallback != null) {
                damageCallback.onBossDamage(GameConfig.BRUISER_EARTHQUAKE_DAMAGE);
            }
        }
        
        if (earthquakeAnimTimer <= 0) {
            currentState = BossState.COOLDOWN;
            cooldownTimer = 0.3f;
            earthquakeCooldownTimer = GameConfig.BRUISER_EARTHQUAKE_COOLDOWN;
        }
    }
    
    /**
     * Maneja el estado de cooldown entre ataques.
     */
    private void handleCooldownState(float deltaTime) {
        cooldownTimer -= deltaTime;
        
        if (cooldownTimer <= 0) {
            currentState = BossState.WALKING;
        }
    }
    
    /**
     * Verifica si el jefe está cargando (para daño aumentado en colisión).
     * 
     * @return true si está en estado de carga
     */
    public boolean isCharging() {
        return currentState == BossState.CHARGING;
    }
    
    /**
     * Obtiene el daño actual del jefe según su estado.
     * 
     * @return Daño de contacto actual
     */
    public float getCurrentDamage() {
        if (currentState == BossState.CHARGING) {
            return GameConfig.BRUISER_CHARGE_DAMAGE;
        }
        return damage;
    }
    
    @Override
    public void render(Graphics2D g2d) {
        int renderX = (int) x - size / 2;
        int renderY = (int) y - size / 2;
        
        if (currentState == BossState.EARTHQUAKE) {
            renderEarthquakeEffect(g2d);
        }
        
        if (animations != null && !animations.isEmpty()) {
            float scale = calculateScale();
            renderWithAnimation(g2d, scale);
        } else {
            renderFallback(g2d, renderX, renderY);
        }
        
        if (currentState == BossState.CHARGING) {
            g2d.setColor(new Color(255, 100, 50, 100));
            g2d.fillOval(renderX - 10, renderY - 5, size + 20, size + 10);
        }
        
        renderBossHealthBar(g2d, renderX, renderY);
        renderBossIndicator(g2d, renderX, renderY);
    }
    
    /**
     * Calcula el factor de escala del sprite.
     * @return Factor de escala
     */
    private float calculateScale() {
        return (spriteWidth > 0) ? ((float) size / spriteWidth) * SPRITE_SCALE : SPRITE_SCALE;
    }
    
    /**
     * Renderizado con animación sin rotación.
     * El sprite se mantiene siempre en la misma orientación.
     */
    @Override
    protected void renderWithAnimation(Graphics2D g2d, float scale) {
        if (animations != null && animations.containsKey(currentAnimState)) {
            Animation anim = animations.get(currentAnimState);
            if (anim.getCurrentFrame() != null) {
                anim.renderScaled(g2d, getCenterX(), getCenterY(), scale);
            }
        }
    }
    
    /**
     * Renderizado de respaldo cuando no hay animaciones.
     */
    private void renderFallback(Graphics2D g2d, int renderX, int renderY) {
        // Sombra del jefe
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillOval(renderX + 4, renderY + size - 8, size, 12);
        
        // Cuerpo principal (más grande en un lado por el brazo)
        g2d.setColor(BODY_COLOR);
        g2d.fillOval(renderX, renderY, size, size);
        
        // Armadura del torso
        g2d.setColor(ARMOR_COLOR);
        int armorWidth = size * 3 / 5;
        int armorHeight = size / 2;
        g2d.fillRoundRect(renderX + size / 5, renderY + size / 4, armorWidth, armorHeight, 8, 8);
        
        // Brazo hipertrofiado (lado derecho)
        g2d.setColor(ARM_COLOR);
        int armSize = size * 2 / 3;
        g2d.fillOval(renderX + size - armSize / 2, renderY + size / 4, armSize, armSize);
        
        // Puño del brazo
        g2d.setColor(new Color(100, 60, 50));
        int fistSize = size / 3;
        g2d.fillOval(renderX + size, renderY + size / 3, fistSize, fistSize);
        
        // Ojos rojos brillantes
        g2d.setColor(EYE_COLOR);
        int eyeSize = size / 8;
        g2d.fillOval(renderX + size / 3 - eyeSize / 2, renderY + size / 4, eyeSize, eyeSize);
        g2d.fillOval(renderX + size / 2, renderY + size / 4, eyeSize, eyeSize);
    }
    
    /**
     * Renderiza el efecto visual del terremoto.
     */
    private void renderEarthquakeEffect(Graphics2D g2d) {
        float progress = 1 - (earthquakeAnimTimer / EARTHQUAKE_ANIM_DURATION);
        int currentRadius = (int) (GameConfig.BRUISER_EARTHQUAKE_RADIUS * progress);
        int alpha = (int) (150 * (1 - progress));
        
        g2d.setColor(new Color(139, 90, 43, Math.max(0, alpha)));
        g2d.setStroke(new BasicStroke(8));
        g2d.drawOval((int) x - currentRadius, (int) y - currentRadius, 
                     currentRadius * 2, currentRadius * 2);
        
        // Círculo interno más sólido
        g2d.setColor(new Color(139, 90, 43, Math.max(0, alpha / 2)));
        g2d.fillOval((int) x - currentRadius / 2, (int) y - currentRadius / 2,
                     currentRadius, currentRadius);
    }
    
    /**
     * Renderiza la barra de vida del jefe.
     */
    private void renderBossHealthBar(Graphics2D g2d, int renderX, int renderY) {
        int barWidth = size + 20;
        int barHeight = 8;
        int barX = renderX - 10;
        int barY = renderY - 20;
        
        // Fondo de la barra
        g2d.setColor(new Color(40, 40, 40));
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        // Barra de vida (rojo oscuro -> rojo brillante según salud)
        float healthPercent = health / maxHealth;
        g2d.setColor(new Color(180, 20, 20));
        g2d.fillRect(barX + 1, barY + 1, (int) ((barWidth - 2) * healthPercent), barHeight - 2);
        
        // Borde dorado (indica jefe)
        g2d.setColor(new Color(218, 165, 32));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(barX, barY, barWidth, barHeight);
    }
    
    /**
     * Renderiza el indicador de jefe sobre el enemigo.
     */
    private void renderBossIndicator(Graphics2D g2d, int renderX, int renderY) {
        g2d.setColor(new Color(218, 165, 32));
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String bossLabel = "★ APLASTADOR ★";
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = fm.stringWidth(bossLabel);
        g2d.drawString(bossLabel, renderX + size / 2 - labelWidth / 2, renderY - 25);
    }
    
    @Override
    public EnemyType getType() {
        return EnemyType.BOSS_BRUISER;
    }
}
