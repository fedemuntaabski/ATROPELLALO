package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * El Infectador (Infector Boss) - Jefe de la oleada 20.
 * 
 * Un controlador de zonas con ataques de área:
 * - Nube Tóxica: Emite una nube de veneno alrededor que daña al jugador
 * - Bomba Química: Lanza proyectiles que dejan pozas tóxicas en el suelo
 * - Explosión Final: Al morir, explota en una nube tóxica masiva
 * 
 * Visual: Zombie hinchado verdoso con ampollas químicas y humo emanando.
 */
public class InfectorBoss extends Enemy {
    
    // Estados del jefe
    private enum BossState {
        WALKING,
        TOXIC_CLOUD,
        THROWING_BOMB,
        DYING
    }
    
    private BossState currentState = BossState.WALKING;
    
    // Timers
    private float toxicCloudTimer = 0;
    private float bombCooldownTimer = 2.0f;
    private float deathExplosionTimer = 0;
    private static final float DEATH_EXPLOSION_DURATION = 1.0f;
    
    // Pozas tóxicas activas
    private List<ToxicPool> toxicPools = new ArrayList<>();
    
    // Callback para daño al jugador
    private BossDamageCallback damageCallback;
    
    // Colores del jefe
    private static final Color BODY_COLOR = new Color(60, 120, 60);
    private static final Color BLISTER_COLOR = new Color(150, 200, 50);
    private static final Color TOXIC_COLOR = new Color(100, 200, 50, 120);
    private static final Color EYE_COLOR = new Color(200, 255, 50);
    private static final Color POOL_COLOR = new Color(80, 180, 40, 150);
    
    /**
     * Representa una poza tóxica dejada por la bomba química.
     */
    private static class ToxicPool {
        float x, y;
        float radius;
        float duration;
        float damageTimer;
        
        ToxicPool(float x, float y, float radius, float duration) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.duration = duration;
            this.damageTimer = 0;
        }
        
        boolean isExpired() {
            return duration <= 0;
        }
        
        void update(float deltaTime) {
            duration -= deltaTime;
            damageTimer -= deltaTime;
        }
        
        boolean canDamage() {
            return damageTimer <= 0;
        }
        
        void resetDamageTimer() {
            damageTimer = GameConfig.INFECTOR_TOXIC_TICK_RATE;
        }
    }
    
    /**
     * Interface para callback de daño al jugador.
     */
    public interface BossDamageCallback {
        void onBossDamage(float damage);
    }
    
    /**
     * Constructor del Infectador.
     * 
     * @param x Posición X inicial
     * @param y Posición Y inicial
     */
    public InfectorBoss(float x, float y) {
        super(x, y,
              GameConfig.INFECTOR_BOSS_HEALTH,
              GameConfig.INFECTOR_BOSS_SPEED,
              GameConfig.INFECTOR_BOSS_SIZE,
              GameConfig.INFECTOR_BOSS_CONTACT_DAMAGE);
        this.xpMultiplier = (float) GameConfig.XP_INFECTOR_BOSS / GameConfig.XP_SLOW_ZOMBIE;
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
        // Actualizar pozas tóxicas
        updateToxicPools(deltaTime, playerX, playerY);
        
        // Actualizar timer de nube tóxica
        toxicCloudTimer -= deltaTime;
        if (toxicCloudTimer <= 0) {
            toxicCloudTimer = GameConfig.INFECTOR_TOXIC_TICK_RATE;
        }
        
        // Actualizar cooldown de bomba
        if (bombCooldownTimer > 0) {
            bombCooldownTimer -= deltaTime;
        }
        
        // Calcular distancia al jugador
        float dx = playerX - x;
        float dy = playerY - y;
        float distanceToPlayer = (float) Math.sqrt(dx * dx + dy * dy);
        
        // Aplicar daño de nube tóxica si el jugador está cerca
        if (distanceToPlayer <= GameConfig.INFECTOR_TOXIC_CLOUD_RADIUS && 
            toxicCloudTimer <= deltaTime && damageCallback != null) {
            damageCallback.onBossDamage(GameConfig.INFECTOR_TOXIC_CLOUD_DAMAGE);
        }
        
        switch (currentState) {
            case WALKING:
                handleWalkingState(deltaTime, playerX, playerY, dx, dy, distanceToPlayer);
                break;
            case THROWING_BOMB:
                handleThrowingBombState(deltaTime, playerX, playerY);
                break;
            case DYING:
                handleDyingState(deltaTime, playerX, playerY, distanceToPlayer);
                break;
            default:
                break;
        }
    }
    
    /**
     * Actualiza las pozas tóxicas y aplica daño.
     */
    private void updateToxicPools(float deltaTime, float playerX, float playerY) {
        Iterator<ToxicPool> iterator = toxicPools.iterator();
        
        while (iterator.hasNext()) {
            ToxicPool pool = iterator.next();
            pool.update(deltaTime);
            
            if (pool.isExpired()) {
                iterator.remove();
                continue;
            }
            
            // Verificar si el jugador está en la poza
            float dx = playerX - pool.x;
            float dy = playerY - pool.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (distance <= pool.radius && pool.canDamage() && damageCallback != null) {
                damageCallback.onBossDamage(GameConfig.INFECTOR_TOXIC_POOL_DAMAGE);
                pool.resetDamageTimer();
            }
        }
    }
    
    /**
     * Maneja el estado de caminar hacia el jugador.
     */
    private void handleWalkingState(float deltaTime, float playerX, float playerY,
                                     float dx, float dy, float distanceToPlayer) {
        // Intentar lanzar bomba si está en cooldown cero y el jugador está a distancia media
        if (bombCooldownTimer <= 0 && distanceToPlayer > 100 && distanceToPlayer < 300) {
            throwChemicalBomb(playerX, playerY);
            bombCooldownTimer = GameConfig.INFECTOR_BOMB_COOLDOWN;
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
     * Lanza una bomba química hacia la posición del jugador.
     */
    private void throwChemicalBomb(float playerX, float playerY) {
        // Crear poza tóxica en la posición del jugador (simplificado - sin proyectil)
        toxicPools.add(new ToxicPool(
            playerX,
            playerY,
            GameConfig.INFECTOR_CHEMICAL_BOMB_RADIUS,
            GameConfig.INFECTOR_TOXIC_POOL_DURATION
        ));
        
        // Aplicar daño inicial de impacto si el jugador está en el área
        // El daño se aplica automáticamente por la poza
    }
    
    /**
     * Maneja el estado de lanzamiento de bomba (animación).
     */
    private void handleThrowingBombState(float deltaTime, float playerX, float playerY) {
        // Estado de animación - por ahora simple
        currentState = BossState.WALKING;
    }
    
    /**
     * Inicia la secuencia de muerte con explosión.
     */
    public void startDeathSequence() {
        if (currentState != BossState.DYING) {
            currentState = BossState.DYING;
            deathExplosionTimer = DEATH_EXPLOSION_DURATION;
        }
    }
    
    /**
     * Maneja el estado de muerte con explosión final.
     */
    private void handleDyingState(float deltaTime, float playerX, float playerY, float distanceToPlayer) {
        deathExplosionTimer -= deltaTime;
        
        // Al explotar, aplicar daño en área grande
        if (deathExplosionTimer <= 0) {
            if (distanceToPlayer <= GameConfig.INFECTOR_DEATH_EXPLOSION_RADIUS && damageCallback != null) {
                damageCallback.onBossDamage(GameConfig.INFECTOR_DEATH_EXPLOSION_DAMAGE);
            }
            // Marcar como muerto (la salud ya debería ser 0)
        }
    }
    
    /**
     * Verifica si el jefe está en secuencia de muerte.
     * 
     * @return true si está muriendo
     */
    public boolean isDying() {
        return currentState == BossState.DYING;
    }
    
    /**
     * Verifica si la explosión de muerte ha terminado.
     * 
     * @return true si la animación de muerte terminó
     */
    public boolean isDeathComplete() {
        return currentState == BossState.DYING && deathExplosionTimer <= 0;
    }
    
    @Override
    public void render(Graphics2D g2d) {
        // Renderizar pozas tóxicas primero (debajo del jefe)
        renderToxicPools(g2d);
        
        int renderX = (int) x - size / 2;
        int renderY = (int) y - size / 2;
        
        // Renderizar efecto de muerte
        if (currentState == BossState.DYING) {
            renderDeathExplosion(g2d);
        }
        
        // Nube tóxica alrededor del jefe
        renderToxicCloud(g2d);
        
        // Sombra del jefe
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillOval(renderX + 4, renderY + size - 6, size, 10);
        
        // Cuerpo hinchado principal
        g2d.setColor(BODY_COLOR);
        g2d.fillOval(renderX, renderY, size, size);
        
        // Cuerpo interior más claro (efecto hinchado)
        g2d.setColor(new Color(80, 140, 80));
        g2d.fillOval(renderX + size / 6, renderY + size / 6, 
                     size * 2 / 3, size * 2 / 3);
        
        // Ampollas químicas (varias protuberancias)
        g2d.setColor(BLISTER_COLOR);
        // Ampolla grande
        g2d.fillOval(renderX + size / 4, renderY + size / 5, size / 4, size / 4);
        // Ampolla media
        g2d.fillOval(renderX + size * 3 / 5, renderY + size / 3, size / 5, size / 5);
        // Ampollas pequeñas
        g2d.fillOval(renderX + size / 8, renderY + size / 2, size / 6, size / 6);
        g2d.fillOval(renderX + size * 2 / 3, renderY + size * 3 / 5, size / 7, size / 7);
        
        // Ojos amarillo-verdosos brillantes
        g2d.setColor(EYE_COLOR);
        int eyeSize = size / 7;
        g2d.fillOval(renderX + size / 3 - eyeSize / 2, renderY + size / 4, eyeSize, eyeSize);
        g2d.fillOval(renderX + size / 2 + 2, renderY + size / 4, eyeSize, eyeSize);
        
        // Gotas de químico cayendo
        renderDrippingChemicals(g2d, renderX, renderY);
        
        // Barra de vida del jefe
        renderBossHealthBar(g2d, renderX, renderY);
        
        // Indicador de jefe
        renderBossIndicator(g2d, renderX, renderY);
    }
    
    /**
     * Renderiza las pozas tóxicas en el suelo.
     */
    private void renderToxicPools(Graphics2D g2d) {
        for (ToxicPool pool : toxicPools) {
            float fadeAlpha = Math.min(1.0f, pool.duration / 2.0f);
            int alpha = (int) (150 * fadeAlpha);
            
            // Poza principal
            g2d.setColor(new Color(80, 180, 40, alpha));
            g2d.fillOval((int) (pool.x - pool.radius), (int) (pool.y - pool.radius),
                        (int) (pool.radius * 2), (int) (pool.radius * 2));
            
            // Borde más oscuro
            g2d.setColor(new Color(50, 120, 30, alpha));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval((int) (pool.x - pool.radius), (int) (pool.y - pool.radius),
                        (int) (pool.radius * 2), (int) (pool.radius * 2));
            
            // Burbujas dentro de la poza
            g2d.setColor(new Color(120, 220, 60, alpha / 2));
            for (int i = 0; i < 3; i++) {
                int bubbleX = (int) (pool.x - pool.radius / 2 + Math.random() * pool.radius);
                int bubbleY = (int) (pool.y - pool.radius / 2 + Math.random() * pool.radius);
                int bubbleSize = (int) (5 + Math.random() * 10);
                g2d.fillOval(bubbleX, bubbleY, bubbleSize, bubbleSize);
            }
        }
    }
    
    /**
     * Renderiza la nube tóxica alrededor del jefe.
     */
    private void renderToxicCloud(Graphics2D g2d) {
        // Varias capas de nube con diferentes transparencias
        float cloudRadius = GameConfig.INFECTOR_TOXIC_CLOUD_RADIUS;
        
        // Capa exterior difusa
        g2d.setColor(new Color(100, 200, 50, 30));
        g2d.fillOval((int) (x - cloudRadius), (int) (y - cloudRadius),
                    (int) (cloudRadius * 2), (int) (cloudRadius * 2));
        
        // Capa interior más concentrada
        g2d.setColor(new Color(100, 200, 50, 50));
        g2d.fillOval((int) (x - cloudRadius * 0.7f), (int) (y - cloudRadius * 0.7f),
                    (int) (cloudRadius * 1.4f), (int) (cloudRadius * 1.4f));
        
        // Partículas de humo flotantes
        g2d.setColor(new Color(80, 180, 40, 80));
        for (int i = 0; i < 5; i++) {
            float angle = (float) (System.currentTimeMillis() / 1000.0 + i * Math.PI / 2.5);
            float particleX = x + (float) Math.cos(angle) * cloudRadius * 0.5f;
            float particleY = y + (float) Math.sin(angle) * cloudRadius * 0.5f;
            g2d.fillOval((int) particleX - 8, (int) particleY - 8, 16, 16);
        }
    }
    
    /**
     * Renderiza el efecto de explosión de muerte.
     */
    private void renderDeathExplosion(Graphics2D g2d) {
        float progress = 1 - (deathExplosionTimer / DEATH_EXPLOSION_DURATION);
        int currentRadius = (int) (GameConfig.INFECTOR_DEATH_EXPLOSION_RADIUS * progress);
        int alpha = (int) (200 * (1 - progress));
        
        // Onda de expansión tóxica
        g2d.setColor(new Color(80, 200, 40, Math.max(0, alpha)));
        g2d.fillOval((int) x - currentRadius, (int) y - currentRadius,
                    currentRadius * 2, currentRadius * 2);
        
        // Borde brillante
        g2d.setColor(new Color(150, 255, 50, Math.max(0, alpha / 2)));
        g2d.setStroke(new BasicStroke(5));
        g2d.drawOval((int) x - currentRadius, (int) y - currentRadius,
                    currentRadius * 2, currentRadius * 2);
    }
    
    /**
     * Renderiza gotas de químico cayendo del cuerpo.
     */
    private void renderDrippingChemicals(Graphics2D g2d, int renderX, int renderY) {
        g2d.setColor(new Color(120, 200, 50, 180));
        long time = System.currentTimeMillis();
        
        // Gotas animadas
        for (int i = 0; i < 3; i++) {
            int dropY = (int) ((time / 50 + i * 20) % 20);
            g2d.fillOval(renderX + size / 4 + i * 15, renderY + size - 5 + dropY, 4, 6);
        }
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
        
        // Barra de vida (verde tóxico)
        float healthPercent = health / maxHealth;
        g2d.setColor(new Color(80, 180, 40));
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
        g2d.setColor(new Color(100, 200, 50));
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String bossLabel = "☣ INFECTADOR ☣";
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = fm.stringWidth(bossLabel);
        g2d.drawString(bossLabel, renderX + size / 2 - labelWidth / 2, renderY - 25);
    }
    
    @Override
    public EnemyType getType() {
        return EnemyType.BOSS_INFECTOR;
    }
    
    /**
     * Obtiene las pozas tóxicas activas para renderizar externamente si es necesario.
     * 
     * @return Lista de pozas tóxicas
     */
    public List<ToxicPool> getToxicPools() {
        return new ArrayList<>(toxicPools);
    }
}
