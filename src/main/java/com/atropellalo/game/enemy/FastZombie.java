package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;

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
     * Crea un nuevo zombie rápido.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     */
    public FastZombie(float x, float y) {
        super(x, y, 
              GameConfig.FAST_ZOMBIE_HEALTH, 
              GameConfig.FAST_ZOMBIE_SPEED, 
              GameConfig.FAST_ZOMBIE_SIZE, 
              GameConfig.FAST_ZOMBIE_DAMAGE);
    }
    
    @Override
    public void render(Graphics2D g2d) {
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
            renderHealthBar(g2d, px, py);
        }
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
        g2d.setColor(healthPercent > 0.5f ? Color.GREEN : (healthPercent > 0.25f ? Color.YELLOW : Color.RED));
        g2d.fillRect(px, barY, (int)(barWidth * healthPercent), barHeight);
    }
    
    @Override
    public EnemyType getType() {
        return EnemyType.FAST;
    }
}
