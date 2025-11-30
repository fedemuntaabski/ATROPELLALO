package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.sprite.ZombieSpriteGenerator;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Zombie lento - Baja velocidad, alta salud.
 * Representado en color púrpura oscuro, más grande y resistente.
 */
public class SlowZombie extends Enemy {
    
    private static final Color BODY_COLOR = new Color(75, 0, 130); // Índigo
    private static final Color BORDER_COLOR = new Color(48, 0, 48); // Púrpura muy oscuro
    private static final Color EYE_COLOR = new Color(255, 255, 0); // Amarillo
    
    /**
     * Crea un nuevo zombie lento sin escalado.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     */
    public SlowZombie(float x, float y) {
        this(x, y, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Crea un nuevo zombie lento con escalado por oleada.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param healthScale Factor de escalado de salud
     * @param speedScale Factor de escalado de velocidad
     * @param damageScale Factor de escalado de daño
     */
    public SlowZombie(float x, float y, float healthScale, float speedScale, float damageScale) {
        super(x, y, 
              GameConfig.SLOW_ZOMBIE_HEALTH, 
              GameConfig.SLOW_ZOMBIE_SPEED, 
              GameConfig.SLOW_ZOMBIE_SIZE, 
              GameConfig.SLOW_ZOMBIE_DAMAGE,
              healthScale, speedScale, damageScale);
        
        // Cargar animaciones del zombie lento
        this.animations = ZombieSpriteGenerator.generateSlowZombieAnimations();
    }
    
    @Override
    public void render(Graphics2D g2d) {
        // Calcular escala para ajustar el sprite al tamaño del enemigo
        float scale = (float) size / ZombieSpriteGenerator.SLOW_WIDTH;
        
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
        
        // Cuerpo principal (más cuadrado para verse más pesado)
        g2d.setColor(BODY_COLOR);
        g2d.fillRoundRect(px, py, size, size, 8, 8);
        
        // Borde grueso
        g2d.setColor(BORDER_COLOR);
        g2d.drawRoundRect(px, py, size, size, 8, 8);
        g2d.drawRoundRect(px + 1, py + 1, size - 2, size - 2, 6, 6);
        
        // Ojos (amarillos)
        int eyeSize = size / 5;
        int eyeY = py + size / 3;
        g2d.setColor(EYE_COLOR);
        g2d.fillOval(px + size / 4 - eyeSize / 2, eyeY, eyeSize, eyeSize);
        g2d.fillOval(px + 3 * size / 4 - eyeSize / 2, eyeY, eyeSize, eyeSize);
        
        // Pupilas
        g2d.setColor(Color.BLACK);
        int pupilSize = eyeSize / 2;
        g2d.fillOval(px + size / 4 - pupilSize / 2, eyeY + eyeSize / 4, pupilSize, pupilSize);
        g2d.fillOval(px + 3 * size / 4 - pupilSize / 2, eyeY + eyeSize / 4, pupilSize, pupilSize);
        
        // Barra de vida (solo si tiene daño)
        if (health < maxHealth) {
            renderHealthBar(g2d);
        }
    }
    
    @Override
    public EnemyType getType() {
        return EnemyType.SLOW;
    }
}
