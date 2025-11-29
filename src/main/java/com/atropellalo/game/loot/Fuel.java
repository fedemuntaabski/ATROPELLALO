package com.atropellalo.game.loot;

import com.atropellalo.game.config.GameConfig;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Item de loot de combustible.
 * Restaura la barra de combustible del jugador al ser recolectado.
 */
public class Fuel extends Loot {
    
    private static final Color FUEL_COLOR = new Color(255, 165, 0); // Naranja
    private static final Color FUEL_BORDER = new Color(200, 100, 0); // Naranja oscuro
    private static final Color FUEL_HIGHLIGHT = new Color(255, 200, 100); // Amarillo claro
    
    /**
     * Crea un nuevo item de combustible.
     * @param x Posición X en el mundo
     * @param y Posición Y en el mundo
     */
    public Fuel(float x, float y) {
        super(x, y, GameConfig.FUEL_SIZE);
    }
    
    @Override
    public void render(Graphics2D g2d) {
        if (collected) {
            return;
        }
        
        int px = (int) x;
        int py = (int) y;
        
        // Dibujar bidón de combustible estilizado
        // Cuerpo principal (rectángulo)
        g2d.setColor(FUEL_COLOR);
        g2d.fillRect(px + 2, py + 4, size - 4, size - 6);
        
        // Borde
        g2d.setColor(FUEL_BORDER);
        g2d.drawRect(px + 2, py + 4, size - 4, size - 6);
        
        // Tapa superior
        g2d.setColor(FUEL_BORDER);
        g2d.fillRect(px + size / 3, py, size / 3, 5);
        
        // Highlight para dar volumen
        g2d.setColor(FUEL_HIGHLIGHT);
        g2d.fillRect(px + 4, py + 6, 3, size - 10);
        
        // Símbolo de combustible (gota)
        g2d.setColor(Color.WHITE);
        int dropX = px + size / 2;
        int dropY = py + size / 2;
        g2d.fillOval(dropX - 3, dropY - 2, 6, 8);
    }
    
    @Override
    public float getValue() {
        return GameConfig.FUEL_RESTORE_AMOUNT;
    }
    
    @Override
    public LootType getType() {
        return LootType.FUEL;
    }
}
