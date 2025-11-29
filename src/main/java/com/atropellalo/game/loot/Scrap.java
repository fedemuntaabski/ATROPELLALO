package com.atropellalo.game.loot;

import com.atropellalo.game.config.GameConfig;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Item de loot de chatarra.
 * Restaura la salud del jugador al ser recolectado.
 */
public class Scrap extends Loot {
    
    private static final Color SCRAP_COLOR = new Color(128, 128, 128); // Gris
    private static final Color SCRAP_DARK = new Color(80, 80, 80); // Gris oscuro
    private static final Color SCRAP_LIGHT = new Color(180, 180, 180); // Gris claro
    private static final Color SCRAP_RUST = new Color(139, 90, 43); // Óxido
    
    /**
     * Crea un nuevo item de chatarra.
     * @param x Posición X en el mundo
     * @param y Posición Y en el mundo
     */
    public Scrap(float x, float y) {
        super(x, y, GameConfig.SCRAP_SIZE);
    }
    
    @Override
    public void render(Graphics2D g2d) {
        if (collected) {
            return;
        }
        
        int px = (int) x;
        int py = (int) y;
        
        // Dibujar chatarra como piezas metálicas irregulares
        // Pieza principal
        g2d.setColor(SCRAP_COLOR);
        int[] xPoints1 = {px, px + size / 2, px + size, px + size - 3, px + 3};
        int[] yPoints1 = {py + 3, py, py + 4, py + size, py + size - 2};
        g2d.fillPolygon(xPoints1, yPoints1, 5);
        
        // Borde oscuro
        g2d.setColor(SCRAP_DARK);
        g2d.drawPolygon(xPoints1, yPoints1, 5);
        
        // Highlight metálico
        g2d.setColor(SCRAP_LIGHT);
        g2d.drawLine(px + 2, py + 5, px + size / 2, py + 3);
        
        // Manchas de óxido
        g2d.setColor(SCRAP_RUST);
        g2d.fillOval(px + size / 3, py + size / 2, 4, 3);
        g2d.fillOval(px + size / 2 + 2, py + size / 3, 3, 4);
        
        // Cruz de salud pequeña para indicar que cura
        g2d.setColor(new Color(50, 200, 50));
        int crossX = px + size / 2;
        int crossY = py + size / 2;
        g2d.fillRect(crossX - 1, crossY - 4, 3, 8);
        g2d.fillRect(crossX - 4, crossY - 1, 8, 3);
    }
    
    @Override
    public float getValue() {
        return GameConfig.SCRAP_HEAL_AMOUNT;
    }
    
    @Override
    public LootType getType() {
        return LootType.SCRAP;
    }
}
