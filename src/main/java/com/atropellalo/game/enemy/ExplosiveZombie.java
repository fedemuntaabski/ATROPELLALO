package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.sprite.ZombieSpriteGenerator;

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
        
        // Cargar animaciones del zombie explosivo
        this.animations = ZombieSpriteGenerator.generateExplosiveZombieAnimations();
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
        
        // Si explotó, mostrar efecto de explosión
        if (exploded) {
            renderExplosionEffect(g2d, px, py);
            return;
        }
        
        // Calcular escala para ajustar el sprite al tamaño del enemigo
        float scale = (float) size / ZombieSpriteGenerator.EXPLOSIVE_WIDTH;
        
        // Intentar renderizar con animación
        if (animations != null && !animations.isEmpty()) {
            renderWithAnimation(g2d, scale);
            
            // Barra de vida
            if (health < maxHealth) {
                renderHealthBar(g2d);
            }
        } else {
            // Fallback al render original
            renderFallback(g2d, px, py);
        }
    }
    
    /**
     * Renderizado de respaldo cuando no hay animaciones.
     */
    private void renderFallback(Graphics2D g2d, int px, int py) {
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
            renderHealthBar(g2d);
        }
    }
    
    /**
     * Dibuja el efecto visual de explosión mejorado.
     */
    private void renderExplosionEffect(Graphics2D g2d, int px, int py) {
        float progress = explosionTimer / EXPLOSION_DURATION;
        int maxRadius = (int) GameConfig.EXPLOSIVE_ZOMBIE_RADIUS;
        
        // El radio crece rápidamente al principio y luego se estabiliza
        float easedProgress = 1f - (1f - progress) * (1f - progress);
        int currentRadius = (int) (maxRadius * easedProgress);
        
        int centerX = px + size / 2;
        int centerY = py + size / 2;
        
        // Alpha disminuye con el tiempo
        int baseAlpha = (int) ((1 - progress) * 255);
        
        // Ondas de choque (anillos que se expanden)
        for (int ring = 0; ring < 3; ring++) {
            float ringOffset = ring * 0.15f;
            float ringProgress = Math.min(1f, easedProgress + ringOffset);
            int ringRadius = (int) (maxRadius * ringProgress * 0.8f);
            int ringAlpha = Math.max(0, (int)(baseAlpha * (1f - ringOffset * 2)));
            
            g2d.setColor(new Color(255, 200, 100, ringAlpha / 3));
            g2d.setStroke(new java.awt.BasicStroke(3 - ring));
            g2d.drawOval(centerX - ringRadius, centerY - ringRadius, 
                        ringRadius * 2, ringRadius * 2);
        }
        
        // Círculo exterior de fuego (naranja)
        int outerAlpha = Math.max(0, baseAlpha - 50);
        g2d.setColor(new Color(255, 120, 0, outerAlpha));
        g2d.fillOval(centerX - currentRadius, centerY - currentRadius, 
                     currentRadius * 2, currentRadius * 2);
        
        // Círculo medio (rojo-naranja)
        int middleRadius = (int)(currentRadius * 0.7f);
        int middleAlpha = Math.max(0, baseAlpha);
        g2d.setColor(new Color(255, 60, 0, middleAlpha));
        g2d.fillOval(centerX - middleRadius, centerY - middleRadius, 
                     middleRadius * 2, middleRadius * 2);
        
        // Núcleo brillante (amarillo-blanco)
        int coreRadius = (int)(currentRadius * 0.35f);
        int coreAlpha = Math.max(0, (int)(baseAlpha * 1.2f));
        coreAlpha = Math.min(255, coreAlpha);
        g2d.setColor(new Color(255, 255, 150, coreAlpha));
        g2d.fillOval(centerX - coreRadius, centerY - coreRadius, 
                     coreRadius * 2, coreRadius * 2);
        
        // Centro blanco muy brillante
        int hotCoreRadius = (int)(currentRadius * 0.15f);
        g2d.setColor(new Color(255, 255, 255, coreAlpha));
        g2d.fillOval(centerX - hotCoreRadius, centerY - hotCoreRadius, 
                     hotCoreRadius * 2, hotCoreRadius * 2);
        
        // Partículas de escombros (pequeños círculos que salen)
        java.util.Random rand = new java.util.Random((long)(x * 1000 + y));
        int particleCount = 8;
        for (int i = 0; i < particleCount; i++) {
            float angle = (float)(i * Math.PI * 2 / particleCount) + rand.nextFloat() * 0.5f;
            float particleDist = currentRadius * (0.8f + rand.nextFloat() * 0.4f);
            int particleX = centerX + (int)(Math.cos(angle) * particleDist);
            int particleY = centerY + (int)(Math.sin(angle) * particleDist);
            int particleSize = 3 + rand.nextInt(4);
            
            int particleAlpha = Math.max(0, baseAlpha - 30);
            g2d.setColor(new Color(255, 150, 50, particleAlpha));
            g2d.fillOval(particleX - particleSize/2, particleY - particleSize/2, 
                        particleSize, particleSize);
        }
    }
    
    @Override
    public EnemyType getType() {
        return EnemyType.EXPLOSIVE;
    }
    
    public boolean hasExploded() {
        return exploded;
    }
}
