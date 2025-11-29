package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Zombie explosivo - Explota al morir, dañando al jugador y otros enemigos.
 * Representado en color naranja/rojo con advertencia visual.
 */
public class ExplosiveZombie extends Enemy {
    
    private static final Color BODY_COLOR = new Color(255, 69, 0); // Naranja rojizo
    private static final Color BORDER_COLOR = new Color(139, 0, 0); // Rojo oscuro
    private static final Color WARNING_COLOR = new Color(255, 255, 0); // Amarillo
    private static final Color EYE_COLOR = Color.WHITE;
    
    private boolean exploded;
    private float explosionTimer;
    private static final float EXPLOSION_DURATION = 0.3f;
    
    // Referencias para la explosión
    private List<Enemy> allEnemies;
    private float playerX;
    private float playerY;
    private ExplosionCallback explosionCallback;
    
    /**
     * Interface para notificar daño por explosión al jugador.
     */
    public interface ExplosionCallback {
        void onExplosionDamagePlayer(float damage);
    }
    
    /**
     * Crea un nuevo zombie explosivo sin escalado.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     */
    public ExplosiveZombie(float x, float y) {
        this(x, y, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Crea un nuevo zombie explosivo con escalado por oleada.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param healthScale Factor de escalado de salud
     * @param speedScale Factor de escalado de velocidad
     * @param damageScale Factor de escalado de daño
     */
    public ExplosiveZombie(float x, float y, float healthScale, float speedScale, float damageScale) {
        super(x, y, 
              GameConfig.EXPLOSIVE_ZOMBIE_HEALTH, 
              GameConfig.EXPLOSIVE_ZOMBIE_SPEED, 
              GameConfig.EXPLOSIVE_ZOMBIE_SIZE, 
              GameConfig.EXPLOSIVE_ZOMBIE_DAMAGE,
              healthScale, speedScale, damageScale);
        this.exploded = false;
        this.explosionTimer = 0;
    }
    
    /**
     * Configura las referencias necesarias para la explosión.
     * @param enemies Lista de todos los enemigos
     * @param callback Callback para dañar al jugador
     */
    public void setExplosionContext(List<Enemy> enemies, ExplosionCallback callback) {
        this.allEnemies = enemies;
        this.explosionCallback = callback;
    }
    
    /**
     * Actualiza la posición del jugador para la explosión.
     */
    public void updatePlayerPosition(float px, float py) {
        this.playerX = px;
        this.playerY = py;
    }
    
    @Override
    public void update(float deltaTime, float playerX, float playerY) {
        // Guardar posición del jugador
        this.playerX = playerX;
        this.playerY = playerY;
        
        // Si explotó, manejar animación de explosión
        if (exploded) {
            explosionTimer += deltaTime;
            if (explosionTimer >= EXPLOSION_DURATION) {
                alive = false;
            }
            return;
        }
        
        super.update(deltaTime, playerX, playerY);
    }
    
    @Override
    public void onDeath() {
        if (!exploded) {
            exploded = true;
            explosionTimer = 0;
            triggerExplosion();
        }
    }
    
    /**
     * Ejecuta la explosión, dañando enemigos cercanos y al jugador.
     */
    private void triggerExplosion() {
        float explosionRadius = GameConfig.EXPLOSIVE_ZOMBIE_RADIUS;
        float explosionDamage = GameConfig.EXPLOSIVE_ZOMBIE_EXPLOSION_DAMAGE;
        
        // Dañar al jugador si está en rango
        float distToPlayer = distanceToPlayer(playerX, playerY);
        if (distToPlayer <= explosionRadius && explosionCallback != null) {
            // Daño basado en distancia (más cerca = más daño)
            float damageMultiplier = 1.0f - (distToPlayer / explosionRadius);
            explosionCallback.onExplosionDamagePlayer(explosionDamage * damageMultiplier);
        }
        
        // Dañar enemigos cercanos
        if (allEnemies != null) {
            for (Enemy enemy : allEnemies) {
                if (enemy != this && enemy.isAlive()) {
                    float distToEnemy = distanceToEnemy(enemy);
                    if (distToEnemy <= explosionRadius) {
                        float damageMultiplier = 1.0f - (distToEnemy / explosionRadius);
                        enemy.takeDamage(explosionDamage * damageMultiplier);
                    }
                }
            }
        }
    }
    
    @Override
    public void render(Graphics2D g2d) {
        int px = (int) x;
        int py = (int) y;
        
        // Si explotó, dibujar explosión
        if (exploded) {
            renderExplosion(g2d, px, py);
            return;
        }
        
        if (!alive) {
            return;
        }
        
        // Cuerpo principal (triangular para verse peligroso)
        g2d.setColor(BODY_COLOR);
        int[] xPoints = {px + size / 2, px, px + size};
        int[] yPoints = {py, py + size, py + size};
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // Borde
        g2d.setColor(BORDER_COLOR);
        g2d.drawPolygon(xPoints, yPoints, 3);
        
        // Símbolo de advertencia (!)
        g2d.setColor(WARNING_COLOR);
        g2d.setFont(g2d.getFont().deriveFont(12.0f));
        g2d.drawString("!", px + size / 2 - 3, py + size / 2 + 4);
        
        // Ojos
        int eyeSize = size / 6;
        int eyeY = py + size / 2;
        g2d.setColor(EYE_COLOR);
        g2d.fillOval(px + size / 3 - eyeSize / 2, eyeY, eyeSize, eyeSize);
        g2d.fillOval(px + 2 * size / 3 - eyeSize / 2, eyeY, eyeSize, eyeSize);
        
        // Barra de vida
        if (health < maxHealth) {
            renderHealthBar(g2d, px, py);
        }
    }
    
    /**
     * Dibuja la animación de explosión.
     */
    private void renderExplosion(Graphics2D g2d, int px, int py) {
        float progress = explosionTimer / EXPLOSION_DURATION;
        int maxRadius = (int) GameConfig.EXPLOSIVE_ZOMBIE_RADIUS;
        int currentRadius = (int) (maxRadius * progress);
        
        // Círculos concéntricos de explosión
        int centerX = px + size / 2;
        int centerY = py + size / 2;
        
        // Círculo exterior (naranja)
        int alpha = (int) ((1 - progress) * 200);
        g2d.setColor(new Color(255, 165, 0, Math.max(0, alpha)));
        g2d.fillOval(centerX - currentRadius, centerY - currentRadius, 
                     currentRadius * 2, currentRadius * 2);
        
        // Círculo medio (rojo)
        int innerRadius = currentRadius * 2 / 3;
        g2d.setColor(new Color(255, 0, 0, Math.max(0, alpha)));
        g2d.fillOval(centerX - innerRadius, centerY - innerRadius, 
                     innerRadius * 2, innerRadius * 2);
        
        // Círculo central (amarillo)
        int coreRadius = currentRadius / 3;
        g2d.setColor(new Color(255, 255, 0, Math.max(0, alpha)));
        g2d.fillOval(centerX - coreRadius, centerY - coreRadius, 
                     coreRadius * 2, coreRadius * 2);
    }
    
    /**
     * Dibuja la barra de vida sobre el enemigo.
     */
    private void renderHealthBar(Graphics2D g2d, int px, int py) {
        int barWidth = size;
        int barHeight = 4;
        int barY = py - 8;
        
        // Fondo
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(px, barY, barWidth, barHeight);
        
        // Vida actual
        float healthPercent = health / maxHealth;
        g2d.setColor(healthPercent > 0.5f ? Color.ORANGE : Color.RED);
        g2d.fillRect(px, barY, (int)(barWidth * healthPercent), barHeight);
    }
    
    @Override
    public EnemyType getType() {
        return EnemyType.EXPLOSIVE;
    }
    
    public boolean hasExploded() {
        return exploded;
    }
}
