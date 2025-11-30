package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.sprite.Animation;
import com.atropellalo.game.sprite.AnimationState;
import com.atropellalo.game.sprite.ZombieSpriteGenerator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Zombie Portador (Brood Carrier) - Enemigo que libera zombies al morir.
 * 
 * Comportamiento:
 * - Más grande y lento que otros zombies
 * - Alta salud
 * - Al morir, genera 6 zombies rápidos pequeños
 * - Los zombies generados tienen stats reducidos
 * - Visual: abdomen hinchado con movimiento de "huevos" internos
 */
public class BroodCarrier extends Enemy {
    
    // Estado de muerte y spawn
    private boolean hasBurst;
    private float deathTimer;
    private float burstAnimationProgress;
    
    // Escalado de los spawns
    private final float spawnHealthScale;
    private final float spawnDamageScale;
    
    // Callback para spawn de enemigos
    private BroodCallback callback;
    
    // Animación visual del abdomen
    private float pulseTimer;
    
    // Colores del portador
    private static final Color BODY_COLOR = new Color(100, 70, 60);       // Marrón oscuro
    private static final Color BODY_DARK = new Color(70, 45, 35);         // Marrón más oscuro
    private static final Color BELLY_COLOR = new Color(140, 90, 70);      // Abdomen hinchado
    private static final Color EGG_COLOR = new Color(180, 160, 120);      // Huevos internos
    private static final Color EYE_COLOR = new Color(200, 100, 80);       // Ojos rojizos
    private static final Color VEIN_COLOR = new Color(120, 60, 70);       // Venas
    
    /**
     * Interface para notificar spawn de enemigos.
     */
    public interface BroodCallback {
        /**
         * Llamado cuando el portador muere y genera zombies.
         * @param spawnX Posición X del spawn
         * @param spawnY Posición Y del spawn
         * @param count Cantidad de zombies a generar
         * @param healthScale Factor de salud
         * @param damageScale Factor de daño
         * @return Lista de enemigos generados
         */
        List<Enemy> onBroodBurst(float spawnX, float spawnY, int count, 
                                  float healthScale, float damageScale);
    }
    
    /**
     * Constructor del zombie portador.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     */
    public BroodCarrier(float x, float y) {
        this(x, y, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Constructor con escalado.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param healthScale Factor de escalado de salud
     * @param speedScale Factor de escalado de velocidad
     * @param damageScale Factor de escalado de daño
     */
    public BroodCarrier(float x, float y, float healthScale, float speedScale, float damageScale) {
        super(x, y, 
              GameConfig.BROOD_CARRIER_HEALTH,
              GameConfig.BROOD_CARRIER_SPEED,
              GameConfig.BROOD_CARRIER_SIZE,
              GameConfig.BROOD_CARRIER_DAMAGE,
              healthScale, speedScale, damageScale);
        
        this.hasBurst = false;
        this.deathTimer = 0;
        this.burstAnimationProgress = 0;
        this.pulseTimer = 0;
        
        // Los spawns heredan parte del escalado del portador
        this.spawnHealthScale = healthScale * GameConfig.BROOD_CARRIER_SPAWN_HEALTH_SCALE;
        this.spawnDamageScale = damageScale * GameConfig.BROOD_CARRIER_SPAWN_DAMAGE_SCALE;
        
        // Cargar animaciones
        loadAnimations();
    }
    
    /**
     * Carga las animaciones del portador.
     */
    private void loadAnimations() {
        animations = new HashMap<>();
        
        // Generar sprites usando ZombieSpriteGenerator con colores personalizados
        animations.put(AnimationState.IDLE, ZombieSpriteGenerator.generateZombieAnimation(
            AnimationState.IDLE, size, BODY_COLOR, BODY_DARK, EYE_COLOR));
        animations.put(AnimationState.MOVING, ZombieSpriteGenerator.generateZombieAnimation(
            AnimationState.MOVING, size, BODY_COLOR, BODY_DARK, EYE_COLOR));
        animations.put(AnimationState.DEATH, ZombieSpriteGenerator.generateZombieAnimation(
            AnimationState.DEATH, size, BODY_COLOR, BODY_DARK, EYE_COLOR));
        
        currentAnimState = AnimationState.IDLE;
    }
    
    /**
     * Establece el callback para spawn de enemigos.
     */
    public void setCallback(BroodCallback callback) {
        this.callback = callback;
    }
    
    @Override
    public void update(float deltaTime, float playerX, float playerY) {
        // Actualizar animación de pulso del abdomen
        pulseTimer += deltaTime;
        
        if (!alive) {
            // Proceso de muerte y burst
            if (!hasBurst) {
                deathTimer += deltaTime;
                burstAnimationProgress = Math.min(1.0f, deathTimer / 0.5f);
                
                // Después de 0.5 segundos, hacer burst
                if (deathTimer >= 0.5f) {
                    burst();
                }
            } else {
                // Después del burst, continuar animación de muerte normal
                updateDeathAnimation(deltaTime);
            }
            return;
        }
        
        // Actualizar cooldown de daño
        if (damageCooldown > 0) {
            damageCooldown -= deltaTime;
        }
        
        // Guardar posición anterior
        float oldX = x;
        float oldY = y;
        
        // Moverse hacia el jugador usando pathfinding
        moveTowardsDirect(playerX, playerY, deltaTime);
        
        // Verificar colisiones
        if (collisionChecker != null && collisionChecker.checkCollision(x, y, size, size)) {
            x = oldX;
            y = oldY;
        }
        
        // Limitar al mundo
        x = Math.max(0, Math.min(x, GameConfig.WORLD_WIDTH - size));
        y = Math.max(0, Math.min(y, GameConfig.WORLD_HEIGHT - size));
        
        // Actualizar animación
        updateAnimationState(deltaTime, oldX, oldY, playerX, playerY);
    }
    
    /**
     * Realiza el burst al morir, generando zombies.
     */
    private void burst() {
        hasBurst = true;
        
        if (callback != null) {
            callback.onBroodBurst(
                getCenterX(), 
                getCenterY(),
                GameConfig.BROOD_CARRIER_SPAWN_COUNT,
                spawnHealthScale,
                spawnDamageScale
            );
        }
        
        // Iniciar animación de muerte después del burst
        if (animations != null && animations.containsKey(AnimationState.DEATH)) {
            currentAnimState = AnimationState.DEATH;
            animations.get(AnimationState.DEATH).reset();
        }
    }
    
    @Override
    public void onDeath() {
        alive = false;
        // No hacer burst inmediatamente - se hace en update después de la animación de "inflarse"
    }
    
    /**
     * Verifica si el burst ya ocurrió.
     */
    public boolean hasBurst() {
        return hasBurst;
    }
    
    @Override
    public void render(Graphics2D g2d) {
        if (hasBurst && isDeathAnimationFinished()) {
            return;
        }
        
        // Si está en proceso de burst, renderizar animación especial
        if (!alive && !hasBurst) {
            renderBurstAnimation(g2d);
            return;
        }
        
        // Renderizar cuerpo con abdomen hinchado
        if (animations != null && animations.containsKey(currentAnimState)) {
            Animation anim = animations.get(currentAnimState);
            if (anim.getCurrentFrame() != null) {
                // Renderizar base
                anim.renderScaled(g2d, getCenterX(), getCenterY(), rotation, 1.0f);
                // Renderizar abdomen extra
                if (alive) {
                    renderBellyOverlay(g2d);
                }
            } else {
                renderFallback(g2d);
            }
        } else {
            renderFallback(g2d);
        }
        
        // Renderizar barra de vida
        if (alive) {
            renderHealthBar(g2d);
        }
    }
    
    /**
     * Renderiza el abdomen hinchado sobre el sprite base.
     */
    private void renderBellyOverlay(Graphics2D g2d) {
        float pulse = (float) Math.sin(pulseTimer * 2) * 0.1f + 1.0f;
        int bellySize = (int)(size * 0.6f * pulse);
        int bellyX = (int)(getCenterX() - bellySize/2);
        int bellyY = (int)(getCenterY() + size * 0.1f);
        
        // Abdomen principal
        g2d.setColor(BELLY_COLOR);
        g2d.fillOval(bellyX, bellyY, bellySize, (int)(bellySize * 0.8f));
        
        // Venas
        g2d.setColor(VEIN_COLOR);
        g2d.drawLine(bellyX + bellySize/4, bellyY + bellySize/4, 
                     bellyX + bellySize/2, bellyY + bellySize/2);
        g2d.drawLine(bellyX + bellySize*3/4, bellyY + bellySize/4, 
                     bellyX + bellySize/2, bellyY + bellySize/2);
        
        // Huevos visibles moviéndose dentro
        g2d.setColor(new Color(EGG_COLOR.getRed(), EGG_COLOR.getGreen(), EGG_COLOR.getBlue(), 150));
        for (int i = 0; i < 4; i++) {
            float eggAngle = (float)(pulseTimer * 1.5 + i * Math.PI / 2);
            float eggDist = bellySize * 0.2f;
            int eggX = (int)(bellyX + bellySize/2 + Math.cos(eggAngle) * eggDist - 4);
            int eggY = (int)(bellyY + bellySize*0.4f + Math.sin(eggAngle) * eggDist * 0.5f - 3);
            g2d.fillOval(eggX, eggY, 8, 6);
        }
    }
    
    /**
     * Renderiza la animación de burst.
     */
    private void renderBurstAnimation(Graphics2D g2d) {
        float scale = 1.0f + burstAnimationProgress * 0.5f; // Se infla
        int currentSize = (int)(size * scale);
        int drawX = (int)(getCenterX() - currentSize/2);
        int drawY = (int)(getCenterY() - currentSize/2);
        
        // Cuerpo inflándose
        int alpha = (int)(255 * (1.0f - burstAnimationProgress * 0.3f));
        g2d.setColor(new Color(BODY_COLOR.getRed(), BODY_COLOR.getGreen(), BODY_COLOR.getBlue(), alpha));
        g2d.fillOval(drawX, drawY, currentSize, currentSize);
        
        // Abdomen muy hinchado
        int bellySize = (int)(currentSize * 0.7f * scale);
        g2d.setColor(new Color(BELLY_COLOR.getRed(), BELLY_COLOR.getGreen(), 
                               BELLY_COLOR.getBlue(), (int)(alpha * 0.8f)));
        g2d.fillOval((int)(getCenterX() - bellySize/2), 
                     (int)(getCenterY()), 
                     bellySize, (int)(bellySize * 0.8f));
        
        // Grietas/fisuras apareciendo
        g2d.setColor(new Color(60, 40, 30, (int)(burstAnimationProgress * 200)));
        for (int i = 0; i < 5; i++) {
            float angle = (float)(i * Math.PI * 2 / 5);
            int x1 = (int)(getCenterX() + Math.cos(angle) * currentSize * 0.2f);
            int y1 = (int)(getCenterY() + Math.sin(angle) * currentSize * 0.2f);
            int x2 = (int)(getCenterX() + Math.cos(angle) * currentSize * 0.4f * (1 + burstAnimationProgress));
            int y2 = (int)(getCenterY() + Math.sin(angle) * currentSize * 0.4f * (1 + burstAnimationProgress));
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // Huevos saliendo
        g2d.setColor(EGG_COLOR);
        for (int i = 0; i < GameConfig.BROOD_CARRIER_SPAWN_COUNT; i++) {
            float eggAngle = (float)(i * Math.PI * 2 / GameConfig.BROOD_CARRIER_SPAWN_COUNT);
            float eggDist = currentSize * 0.3f * burstAnimationProgress;
            int eggX = (int)(getCenterX() + Math.cos(eggAngle) * eggDist);
            int eggY = (int)(getCenterY() + Math.sin(eggAngle) * eggDist);
            g2d.fillOval(eggX - 5, eggY - 4, 10, 8);
        }
    }
    
    /**
     * Renderizado de respaldo si no hay animación.
     */
    private void renderFallback(Graphics2D g2d) {
        // Cuerpo principal
        g2d.setColor(BODY_COLOR);
        g2d.fillOval((int) x, (int) y, size, size);
        
        // Abdomen hinchado
        float pulse = (float) Math.sin(pulseTimer * 2) * 0.1f + 1.0f;
        int bellySize = (int)(size * 0.55f * pulse);
        g2d.setColor(BELLY_COLOR);
        g2d.fillOval((int)(x + size/2 - bellySize/2), (int)(y + size * 0.4f), 
                     bellySize, (int)(bellySize * 0.8f));
        
        // Huevos dentro
        g2d.setColor(new Color(EGG_COLOR.getRed(), EGG_COLOR.getGreen(), EGG_COLOR.getBlue(), 150));
        for (int i = 0; i < 3; i++) {
            float eggAngle = (float)(pulseTimer + i * Math.PI * 2 / 3);
            int eggX = (int)(x + size/2 + Math.cos(eggAngle) * 6 - 4);
            int eggY = (int)(y + size * 0.55f + Math.sin(eggAngle) * 3 - 3);
            g2d.fillOval(eggX, eggY, 8, 6);
        }
        
        // Ojos
        g2d.setColor(EYE_COLOR);
        g2d.fillOval((int) x + size/4, (int) y + size/5, size/5, size/5);
        g2d.fillOval((int) x + size/2, (int) y + size/5, size/5, size/5);
        
        // Venas
        g2d.setColor(VEIN_COLOR);
        g2d.drawLine((int)(x + size * 0.3f), (int)(y + size * 0.5f),
                     (int)(x + size * 0.5f), (int)(y + size * 0.7f));
        g2d.drawLine((int)(x + size * 0.7f), (int)(y + size * 0.5f),
                     (int)(x + size * 0.5f), (int)(y + size * 0.7f));
    }
    
    @Override
    public EnemyType getType() {
        return EnemyType.BROOD_CARRIER;
    }
}
