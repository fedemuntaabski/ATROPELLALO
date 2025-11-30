package com.atropellalo.game.sprite;

import com.atropellalo.game.upgrade.UpgradeType;
import com.atropellalo.game.weapon.WeaponType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Generador de iconos estándar para el sistema de mejoras.
 * Crea iconos 48x48 píxeles con diseño consistente.
 */
public class UpgradeIconGenerator {
    
    /** Tamaño estándar de los iconos */
    public static final int ICON_SIZE = 48;
    
    /** Colores base para categorías */
    private static final Color VEHICLE_COLOR = new Color(100, 180, 255);
    private static final Color WEAPON_COLOR = new Color(255, 120, 80);
    private static final Color NEW_WEAPON_COLOR = new Color(255, 215, 0);
    private static final Color STAT_COLOR = new Color(150, 255, 150);
    
    /** Colores de fondo */
    private static final Color BG_VEHICLE = new Color(30, 60, 90);
    private static final Color BG_WEAPON = new Color(80, 40, 40);
    private static final Color BG_NEW = new Color(70, 60, 30);
    
    /** Cache de iconos generados */
    private static final Map<String, BufferedImage> iconCache = new HashMap<>();
    
    /**
     * Obtiene el icono para un tipo de mejora.
     * @param type Tipo de mejora
     * @param weaponType Tipo de arma (puede ser null)
     * @return Imagen del icono
     */
    public static BufferedImage getIcon(UpgradeType type, WeaponType weaponType) {
        String key = type.name() + (weaponType != null ? "_" + weaponType.name() : "");
        
        if (!iconCache.containsKey(key)) {
            iconCache.put(key, generateIcon(type, weaponType));
        }
        
        return iconCache.get(key);
    }
    
    /**
     * Genera un icono para el tipo de mejora.
     */
    private static BufferedImage generateIcon(UpgradeType type, WeaponType weaponType) {
        BufferedImage image = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Dibujar fondo redondeado
        drawBackground(g2d, type);
        
        // Dibujar icono según tipo
        switch (type) {
            case VEHICLE_HEALTH:
                drawHealthIcon(g2d);
                break;
            case VEHICLE_SPEED:
                drawSpeedIcon(g2d);
                break;
            case NEW_WEAPON:
                drawWeaponIcon(g2d, weaponType);
                break;
            case WEAPON_DAMAGE:
                drawDamageIcon(g2d);
                break;
            case WEAPON_FIRE_RATE:
                drawFireRateIcon(g2d);
                break;
            case WEAPON_IMPACT_AREA:
                drawAreaIcon(g2d);
                break;
            case WEAPON_PROJECTILE_COUNT:
                drawMultiShotIcon(g2d);
                break;
        }
        
        g2d.dispose();
        return image;
    }
    
    /**
     * Dibuja el fondo del icono.
     */
    private static void drawBackground(Graphics2D g2d, UpgradeType type) {
        Color bgColor;
        Color borderColor;
        
        switch (type) {
            case VEHICLE_HEALTH:
            case VEHICLE_SPEED:
                bgColor = BG_VEHICLE;
                borderColor = VEHICLE_COLOR;
                break;
            case NEW_WEAPON:
                bgColor = BG_NEW;
                borderColor = NEW_WEAPON_COLOR;
                break;
            default:
                bgColor = BG_WEAPON;
                borderColor = WEAPON_COLOR;
                break;
        }
        
        // Fondo
        g2d.setColor(bgColor);
        g2d.fill(new RoundRectangle2D.Float(2, 2, ICON_SIZE - 4, ICON_SIZE - 4, 8, 8));
        
        // Borde
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2f));
        g2d.draw(new RoundRectangle2D.Float(2, 2, ICON_SIZE - 4, ICON_SIZE - 4, 8, 8));
    }
    
    /**
     * Icono de salud: corazón/cruz médica
     */
    private static void drawHealthIcon(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2;
        
        // Cruz médica
        g2d.setColor(new Color(255, 100, 100));
        g2d.fillRoundRect(cx - 4, cy - 12, 8, 24, 3, 3);
        g2d.fillRoundRect(cx - 12, cy - 4, 24, 8, 3, 3);
        
        // Brillo
        g2d.setColor(new Color(255, 150, 150));
        g2d.fillRoundRect(cx - 2, cy - 10, 4, 8, 2, 2);
        
        // Símbolo +
        g2d.setColor(Color.WHITE);
        g2d.setFont(g2d.getFont().deriveFont(10f));
        g2d.drawString("+", cx + 10, cy - 8);
    }
    
    /**
     * Icono de velocidad: flecha/rayo
     */
    private static void drawSpeedIcon(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2;
        
        // Rayo
        GeneralPath lightning = new GeneralPath();
        lightning.moveTo(cx + 8, cy - 14);
        lightning.lineTo(cx - 4, cy + 2);
        lightning.lineTo(cx + 2, cy + 2);
        lightning.lineTo(cx - 8, cy + 14);
        lightning.lineTo(cx + 4, cy - 2);
        lightning.lineTo(cx - 2, cy - 2);
        lightning.closePath();
        
        g2d.setColor(new Color(255, 230, 100));
        g2d.fill(lightning);
        
        g2d.setColor(new Color(255, 200, 50));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(lightning);
    }
    
    /**
     * Icono para nueva arma.
     */
    private static void drawWeaponIcon(Graphics2D g2d, WeaponType weaponType) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2;
        
        // Estrella de "nuevo"
        g2d.setColor(NEW_WEAPON_COLOR);
        drawStar(g2d, cx, cy - 2, 14, 7, 5);
        
        // Brillo central
        g2d.setColor(Color.WHITE);
        g2d.fillOval(cx - 3, cy - 5, 6, 6);
        
        // Texto "NEW" pequeño abajo
        g2d.setColor(NEW_WEAPON_COLOR);
        g2d.setFont(g2d.getFont().deriveFont(java.awt.Font.BOLD, 8f));
        g2d.drawString("NEW", cx - 10, cy + 16);
    }
    
    /**
     * Icono de daño: espada/impacto
     */
    private static void drawDamageIcon(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2;
        
        // Espada diagonal
        AffineTransform old = g2d.getTransform();
        g2d.rotate(Math.toRadians(-45), cx, cy);
        
        // Hoja
        g2d.setColor(new Color(200, 200, 220));
        g2d.fillRect(cx - 3, cy - 16, 6, 20);
        
        // Punta
        GeneralPath tip = new GeneralPath();
        tip.moveTo(cx - 3, cy - 16);
        tip.lineTo(cx, cy - 22);
        tip.lineTo(cx + 3, cy - 16);
        tip.closePath();
        g2d.fill(tip);
        
        // Guarda
        g2d.setColor(new Color(180, 140, 60));
        g2d.fillRect(cx - 8, cy + 4, 16, 4);
        
        // Mango
        g2d.setColor(new Color(100, 60, 30));
        g2d.fillRect(cx - 2, cy + 8, 4, 10);
        
        g2d.setTransform(old);
        
        // Efecto de impacto
        g2d.setColor(new Color(255, 100, 100, 150));
        g2d.setStroke(new BasicStroke(2f));
        g2d.draw(new Line2D.Float(cx + 8, cy - 8, cx + 14, cy - 14));
        g2d.draw(new Line2D.Float(cx + 10, cy - 4, cx + 16, cy - 6));
    }
    
    /**
     * Icono de cadencia: balas múltiples
     */
    private static void drawFireRateIcon(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2;
        
        // Tres balas con estelas
        Color bulletColor = new Color(255, 200, 100);
        Color trailColor = new Color(255, 200, 100, 100);
        
        for (int i = 0; i < 3; i++) {
            int yOffset = (i - 1) * 10;
            int xOffset = i * 3 - 3;
            
            // Estela
            g2d.setColor(trailColor);
            g2d.fillRect(cx - 16 + xOffset, cy + yOffset - 2, 14, 4);
            
            // Bala
            g2d.setColor(bulletColor);
            g2d.fillOval(cx + xOffset, cy + yOffset - 3, 8, 6);
        }
        
        // Flechas de velocidad
        g2d.setColor(new Color(100, 255, 100));
        g2d.setStroke(new BasicStroke(2f));
        g2d.draw(new Line2D.Float(cx + 10, cy - 6, cx + 16, cy - 6));
        g2d.draw(new Line2D.Float(cx + 14, cy - 9, cx + 16, cy - 6));
        g2d.draw(new Line2D.Float(cx + 14, cy - 3, cx + 16, cy - 6));
    }
    
    /**
     * Icono de área: círculos concéntricos/explosión
     */
    private static void drawAreaIcon(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2;
        
        // Círculos concéntricos
        g2d.setStroke(new BasicStroke(2f));
        
        g2d.setColor(new Color(255, 150, 50, 80));
        g2d.draw(new Ellipse2D.Float(cx - 18, cy - 18, 36, 36));
        
        g2d.setColor(new Color(255, 150, 50, 120));
        g2d.draw(new Ellipse2D.Float(cx - 12, cy - 12, 24, 24));
        
        g2d.setColor(new Color(255, 150, 50, 180));
        g2d.draw(new Ellipse2D.Float(cx - 6, cy - 6, 12, 12));
        
        // Centro
        g2d.setColor(new Color(255, 100, 50));
        g2d.fillOval(cx - 4, cy - 4, 8, 8);
        
        // Flechas hacia afuera
        g2d.setColor(new Color(255, 200, 100));
        g2d.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < 4; i++) {
            double angle = i * Math.PI / 2;
            int x1 = cx + (int)(Math.cos(angle) * 8);
            int y1 = cy + (int)(Math.sin(angle) * 8);
            int x2 = cx + (int)(Math.cos(angle) * 14);
            int y2 = cy + (int)(Math.sin(angle) * 14);
            g2d.draw(new Line2D.Float(x1, y1, x2, y2));
        }
    }
    
    /**
     * Icono de disparos múltiples: balas en abanico
     */
    private static void drawMultiShotIcon(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2 + 4;
        
        Color bulletColor = new Color(255, 220, 100);
        
        // Disparos en abanico (5 direcciones)
        for (int i = -2; i <= 2; i++) {
            double angle = Math.toRadians(-90 + i * 20);
            int x = cx + (int)(Math.cos(angle) * 16);
            int y = cy + (int)(Math.sin(angle) * 16);
            
            // Línea de trayectoria
            g2d.setColor(new Color(255, 220, 100, 80));
            g2d.setStroke(new BasicStroke(1f));
            g2d.draw(new Line2D.Float(cx, cy, x, y));
            
            // Bala
            g2d.setColor(bulletColor);
            g2d.fillOval(x - 3, y - 3, 6, 6);
        }
        
        // Origen (cañón)
        g2d.setColor(new Color(100, 100, 100));
        g2d.fillRoundRect(cx - 4, cy, 8, 10, 3, 3);
        
        // Símbolo x3
        g2d.setColor(STAT_COLOR);
        g2d.setFont(g2d.getFont().deriveFont(java.awt.Font.BOLD, 9f));
        g2d.drawString("+1", cx + 8, cy + 16);
    }
    
    /**
     * Dibuja una estrella de n puntas.
     */
    private static void drawStar(Graphics2D g2d, int cx, int cy, int outerRadius, int innerRadius, int points) {
        GeneralPath star = new GeneralPath();
        double angle = -Math.PI / 2;
        double step = Math.PI / points;
        
        star.moveTo(cx + outerRadius * Math.cos(angle), cy + outerRadius * Math.sin(angle));
        
        for (int i = 0; i < points * 2; i++) {
            angle += step;
            int radius = (i % 2 == 0) ? innerRadius : outerRadius;
            star.lineTo(cx + radius * Math.cos(angle), cy + radius * Math.sin(angle));
        }
        
        star.closePath();
        g2d.fill(star);
    }
    
    /**
     * Limpia el cache de iconos.
     */
    public static void clearCache() {
        iconCache.clear();
    }
}
