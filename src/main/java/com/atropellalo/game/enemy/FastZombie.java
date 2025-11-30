package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.sprite.Animation;
import com.atropellalo.game.sprite.AnimationState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Zombie rápido - Alta velocidad, baja salud.
 * Usa sprite personalizado FastZombie.png.
 */
public class FastZombie extends Enemy {
    
    private static final String SPRITE_PATH = "/images/FastZombie.png";
    private static final float SPRITE_SCALE = 3.0f;
    
    // Colores de fallback
    private static final Color BODY_COLOR = new Color(144, 238, 144);
    private static final Color BORDER_COLOR = new Color(34, 139, 34);
    private static final Color EYE_COLOR = Color.RED;
    
    // Sprite compartido para todos los FastZombies
    private static BufferedImage sprite = null;
    private static int spriteWidth = 0;
    private static int spriteHeight = 0;
    
    /**
     * Crea un nuevo zombie rápido sin escalado.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     */
    public FastZombie(float x, float y) {
        this(x, y, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Crea un nuevo zombie rápido con escalado por oleada.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param healthScale Factor de escalado de salud
     * @param speedScale Factor de escalado de velocidad
     * @param damageScale Factor de escalado de daño
     */
    public FastZombie(float x, float y, float healthScale, float speedScale, float damageScale) {
        super(x, y, 
              GameConfig.FAST_ZOMBIE_HEALTH, 
              GameConfig.FAST_ZOMBIE_SPEED, 
              GameConfig.FAST_ZOMBIE_SIZE, 
              GameConfig.FAST_ZOMBIE_DAMAGE,
              healthScale, speedScale, damageScale);
        
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
    
    @Override
    public void render(Graphics2D g2d) {
        if (animations != null && !animations.isEmpty()) {
            float scale = calculateScale();
            renderWithAnimation(g2d, scale);
            
            if (health < maxHealth) {
                renderHealthBar(g2d);
            }
        } else {
            renderFallback(g2d);
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
    private void renderFallback(Graphics2D g2d) {
        if (!alive) return;
        
        int px = (int) x;
        int py = (int) y;
        
        g2d.setColor(BODY_COLOR);
        g2d.fillOval(px, py, size, size);
        
        g2d.setColor(BORDER_COLOR);
        g2d.drawOval(px, py, size, size);
        
        int eyeSize = size / 6;
        int eyeY = py + size / 3;
        g2d.setColor(EYE_COLOR);
        g2d.fillOval(px + size / 4 - eyeSize / 2, eyeY, eyeSize, eyeSize);
        g2d.fillOval(px + 3 * size / 4 - eyeSize / 2, eyeY, eyeSize, eyeSize);
        
        if (health < maxHealth) {
            renderHealthBar(g2d);
        }
    }
    
    @Override
    public EnemyType getType() {
        return EnemyType.FAST;
    }
}
