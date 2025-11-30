package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.sprite.ZombieSpriteGenerator;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Zombie rápido - Alta velocidad, baja salud.
 * Representado en color verde claro, más pequeño y ágil.
 */
public class FastZombie extends Enemy {
    
    private static final Color BODY_COLOR = new Color(144, 238, 144); // Verde claro
    private static final Color BORDER_COLOR = new Color(34, 139, 34); // Verde oscuro
    private static final Color EYE_COLOR = Color.RED;
    
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
        
        // Cargar animaciones del zombie rápido
        this.animations = ZombieSpriteGenerator.generateFastZombieAnimations();
    }
    
    @Override
    public void render(Graphics2D g2d) {
        // Calcular escala para ajustar el sprite al tamaño del enemigo
        float scale = (float) size / ZombieSpriteGenerator.FAST_WIDTH;
        
        // Intentar renderizar con animación
        if (animations != null && !animations.isEmpty()) {
            renderWithAnimation(g2d, scale);
            
            // Barra de vida (solo si tiene daño)
            if (health < maxHealth) {
                renderHealthBar(g2d);
            }
        } else {
            // Fallback al render original
            renderFallback(g2d);
        }
    }
    
    /**
     * Renderizado de respaldo cuando no hay animaciones.
     */
    private void renderFallback(Graphics2D g2d) {
        if (!alive) {
            return;
        }
        
        int px = (int) x;
        int py = (int) y;
        
        // Cuerpo principal
        g2d.setColor(BODY_COLOR);
        g2d.fillOval(px, py, size, size);
        
        // Borde
        g2d.setColor(BORDER_COLOR);
        g2d.drawOval(px, py, size, size);
        
        // Ojos (rojos para indicar hostilidad)
        int eyeSize = size / 6;
        int eyeY = py + size / 3;
        g2d.setColor(EYE_COLOR);
        g2d.fillOval(px + size / 4 - eyeSize / 2, eyeY, eyeSize, eyeSize);
        g2d.fillOval(px + 3 * size / 4 - eyeSize / 2, eyeY, eyeSize, eyeSize);
        
        // Barra de vida (solo si tiene daño)
        if (health < maxHealth) {
            renderHealthBar(g2d);
        }
    }
    
    @Override
    public EnemyType getType() {
        return EnemyType.FAST;
    }
}
