package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;

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
    }
    
    @Override
    public void render(Graphics2D g2d) {
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
            renderHealthBar(g2d, px, py);
        }
    }
    
    /**
     * Dibuja la barra de vida sobre el enemigo.
     */
    private void renderHealthBar(Graphics2D g2d, int px, int py) {
        int barWidth = size;
        int barHeight = 5;
        int barY = py - 10;
        
        // Fondo
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(px, barY, barWidth, barHeight);
        
        // Vida actual
        float healthPercent = health / maxHealth;
        g2d.setColor(healthPercent > 0.5f ? Color.GREEN : (healthPercent > 0.25f ? Color.YELLOW : Color.RED));
        g2d.fillRect(px, barY, (int)(barWidth * healthPercent), barHeight);
    }
    
    @Override
    public EnemyType getType() {
        return EnemyType.SLOW;
    }
}
