package com.atropellalo.game.loot;

import com.atropellalo.game.config.GameConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Item de loot de combustible.
 * Restaura la barra de combustible del jugador al ser recolectado.
 * Usa sprite personalizado Fuel.png.
 */
public class Fuel extends Loot {
    
    private static final String SPRITE_PATH = "/img/fuel.png";
    private static final float SPRITE_SCALE = 0.08f;
    
    // Sprite compartido
    private static BufferedImage sprite = null;
    private static int spriteWidth = 0;
    private static int spriteHeight = 0;
    
    // Colores de fallback
    private static final Color FUEL_COLOR = new Color(255, 165, 0);
    private static final Color FUEL_BORDER = new Color(200, 100, 0);
    private static final Color FUEL_HIGHLIGHT = new Color(255, 200, 100);
    
    /**
     * Crea un nuevo item de combustible.
     * @param x Posición X en el mundo
     * @param y Posición Y en el mundo
     */
    public Fuel(float x, float y) {
        super(x, y, GameConfig.FUEL_SIZE);
        loadSprite();
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
    
    @Override
    public void render(Graphics2D g2d) {
        if (collected) {
            return;
        }
        
        if (sprite != null) {
            int scaledWidth = (int)(spriteWidth * SPRITE_SCALE);
            int scaledHeight = (int)(spriteHeight * SPRITE_SCALE);
            int drawX = (int)(x - scaledWidth / 2 + size / 2);
            int drawY = (int)(y - scaledHeight / 2 + size / 2);
            
            g2d.drawImage(sprite, drawX, drawY, scaledWidth, scaledHeight, null);
        } else {
            renderFallback(g2d);
        }
    }
    
    /**
     * Renderizado de respaldo cuando no hay sprite disponible.
     */
    private void renderFallback(Graphics2D g2d) {
        int px = (int) x;
        int py = (int) y;
        
        g2d.setColor(FUEL_COLOR);
        g2d.fillRect(px + 2, py + 4, size - 4, size - 6);
        
        g2d.setColor(FUEL_BORDER);
        g2d.drawRect(px + 2, py + 4, size - 4, size - 6);
        
        g2d.setColor(FUEL_BORDER);
        g2d.fillRect(px + size / 3, py, size / 3, 5);
        
        g2d.setColor(FUEL_HIGHLIGHT);
        g2d.fillRect(px + 4, py + 6, 3, size - 10);
        
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
