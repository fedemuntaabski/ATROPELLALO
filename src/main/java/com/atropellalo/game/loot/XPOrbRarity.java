package com.atropellalo.game.loot;

import java.awt.Color;

/**
 * Rareza de orbes de experiencia.
 * Determina el color y el multiplicador de XP.
 */
public enum XPOrbRarity {
    /** Orbe normal - zombies rápidos, explosivos y escupidores */
    NORMAL(1.0f, new Color(100, 180, 255), new Color(150, 200, 255), new Color(50, 100, 200)),
    
    /** Orbe especial - zombies lentos */
    SPECIAL(1.5f, new Color(255, 180, 100), new Color(255, 220, 150), new Color(200, 100, 50)),
    
    /** Orbe único - jefes */
    UNIQUE(3.0f, new Color(255, 100, 255), new Color(255, 150, 255), new Color(200, 50, 200));
    
    private final float xpMultiplier;
    private final Color baseColor;
    private final Color glowColor;
    private final Color darkColor;
    
    XPOrbRarity(float xpMultiplier, Color baseColor, Color glowColor, Color darkColor) {
        this.xpMultiplier = xpMultiplier;
        this.baseColor = baseColor;
        this.glowColor = glowColor;
        this.darkColor = darkColor;
    }
    
    public float getXpMultiplier() {
        return xpMultiplier;
    }
    
    public Color getBaseColor() {
        return baseColor;
    }
    
    public Color getGlowColor() {
        return glowColor;
    }
    
    public Color getDarkColor() {
        return darkColor;
    }
}
