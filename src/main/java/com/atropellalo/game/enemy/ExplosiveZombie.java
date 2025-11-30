package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.effect.VisualEffectManager;
import com.atropellalo.game.sprite.Animation;
import com.atropellalo.game.sprite.AnimationState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Zombie explosivo - Explota al morir, dañando al jugador y otros enemigos.
 * Usa sprite personalizado ExplosiveZombie.png.
 */
public class ExplosiveZombie extends Enemy {
    
    private static final String SPRITE_PATH = "/images/ExplosiveZombie.png";
    private static final float SPRITE_SCALE = 3.0f;
    
    // Colores de fallback
    private static final Color BODY_COLOR = new Color(255, 69, 0);
    private static final Color BORDER_COLOR = new Color(139, 0, 0);
    private static final Color WARNING_COLOR = new Color(255, 255, 0);
    private static final Color EYE_COLOR = Color.WHITE;
    
    // Sprite compartido para todos los ExplosiveZombies
    private static BufferedImage sprite = null;
    private static int spriteWidth = 0;
    private static int spriteHeight = 0;
    
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
        
        loadSprite();
        initializeAnimations();
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
            this.animations.put(AnimationState.DEATH, new Animation(sprite));
        }
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
        
        // Crear efecto visual de explosión
        VisualEffectManager.getInstance().createExplosion(
            getCenterX(), getCenterY(), explosionRadius
        );
        
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
        
        // Si explotó, el efecto visual lo maneja VisualEffectManager
        if (exploded) {
            return;
        }
        
        if (animations != null && !animations.isEmpty()) {
            float scale = calculateScale();
            renderWithAnimation(g2d, scale);
            
            if (health < maxHealth) {
                renderHealthBar(g2d);
            }
        } else {
            renderFallback(g2d, px, py);
        }
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
     * Renderizado de respaldo cuando no hay sprite disponible.
     */
    private void renderFallback(Graphics2D g2d, int px, int py) {
        if (!alive) return;
        
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
    
    @Override
    public EnemyType getType() {
        return EnemyType.EXPLOSIVE;
    }
    
    public boolean hasExploded() {
        return exploded;
    }
}
