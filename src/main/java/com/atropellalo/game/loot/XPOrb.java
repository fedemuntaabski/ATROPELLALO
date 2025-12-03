package com.atropellalo.game.loot;

import com.atropellalo.game.config.GameConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Orbe de experiencia que aparece al matar enemigos.
 * Se atrae magnéticamente hacia el jugador cuando está cerca.
 */
public class XPOrb extends Loot {
    
    private final int xpValue;
    private final XPOrbRarity rarity;
    private float magnetDistance;
    private float magnetSpeed;
    private boolean beingAttracted;
    
    // Animación de brillo
    private float glowPhase;
    
    /**
     * Crea un nuevo orbe de XP.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param baseXpValue Cantidad base de XP (será multiplicado por la rareza)
     * @param rarity Rareza del orbe
     */
    public XPOrb(float x, float y, int baseXpValue, XPOrbRarity rarity) {
        super(x, y, GameConfig.XP_ORB_SIZE);
        this.rarity = rarity;
        this.xpValue = Math.round(baseXpValue * rarity.getXpMultiplier());
        this.magnetDistance = GameConfig.XP_ORB_MAGNET_DISTANCE;
        this.magnetSpeed = GameConfig.XP_ORB_MAGNET_SPEED;
        this.beingAttracted = false;
        this.glowPhase = (float) (ThreadLocalRandom.current().nextDouble() * Math.PI * 2);
    }
    
    /**
     * Actualiza el orbe, moviéndolo hacia el jugador si está en rango.
     * @param deltaTime Tiempo desde el último frame
     * @param playerX Centro X del jugador
     * @param playerY Centro Y del jugador
     */
    public void update(float deltaTime, float playerX, float playerY) {
        if (collected) {
            return;
        }
        
        // Actualizar animación
        glowPhase += deltaTime * 3;
        
        // Calcular distancia al jugador
        float dx = playerX - getCenterX();
        float dy = playerY - getCenterY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        // Atracción magnética
        if (distance <= magnetDistance) {
            beingAttracted = true;
            
            // Normalizar dirección
            if (distance > 0) {
                float dirX = dx / distance;
                float dirY = dy / distance;
                
                // Mover hacia el jugador (más rápido cuanto más cerca)
                float speedMultiplier = 1.0f + (1.0f - distance / magnetDistance) * 2;
                x += dirX * magnetSpeed * speedMultiplier * deltaTime;
                y += dirY * magnetSpeed * speedMultiplier * deltaTime;
            }
        } else {
            beingAttracted = false;
        }
    }
    
    @Override
    public void render(Graphics2D g2d) {
        if (collected) {
            return;
        }
        
        int centerX = (int) getCenterX();
        int centerY = (int) getCenterY();
        int radius = size / 2;
        
        // Efecto de brillo pulsante
        float glow = (float) (0.7f + 0.3f * Math.sin(glowPhase));
        
        // Colores según rareza
        Color orbColor = rarity.getBaseColor();
        Color glowColor = new Color(
            rarity.getGlowColor().getRed(),
            rarity.getGlowColor().getGreen(),
            rarity.getGlowColor().getBlue(),
            (int)(100 * glow)
        );
        
        // Dibujar halo de brillo
        if (beingAttracted) {
            int glowRadius = radius + 4;
            g2d.setColor(new Color(
                rarity.getGlowColor().getRed(),
                rarity.getGlowColor().getGreen(),
                rarity.getGlowColor().getBlue(),
                (int)(80 * glow)
            ));
            g2d.fillOval(centerX - glowRadius, centerY - glowRadius, 
                        glowRadius * 2, glowRadius * 2);
        }
        
        // Dibujar orbe con gradiente
        Point2D center = new Point2D.Float(centerX, centerY);
        float[] dist = {0.0f, 0.7f, 1.0f};
        Color[] colors = {
            Color.WHITE,
            orbColor,
            rarity.getDarkColor()
        };
        
        RadialGradientPaint gradient = new RadialGradientPaint(
            center, radius, dist, colors
        );
        
        g2d.setPaint(gradient);
        g2d.fillOval((int)x, (int)y, size, size);
        
        // Borde brillante
        g2d.setColor(glowColor);
        g2d.drawOval((int)x, (int)y, size, size);
    }
    
    @Override
    public float getValue() {
        return xpValue;
    }
    
    @Override
    public LootType getType() {
        return LootType.XP_ORB;
    }
    
    /**
     * Obtiene la cantidad de XP que otorga este orbe.
     * @return Valor de XP
     */
    public int getXPValue() {
        return xpValue;
    }
}
