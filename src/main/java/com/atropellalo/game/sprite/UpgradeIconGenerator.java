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
            case WEAPON_MULTI_TARGET:
                drawMultiTargetIcon(g2d);
                break;
            case WEAPON_CONE_ANGLE:
                drawConeAngleIcon(g2d);
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
     * Icono para nueva arma - dibuja el sprite específico del arma.
     */
    private static void drawWeaponIcon(Graphics2D g2d, WeaponType weaponType) {
        if (weaponType == null) {
            drawGenericWeaponIcon(g2d);
            return;
        }
        
        switch (weaponType) {
            case PISTOL:
                drawPistolSprite(g2d);
                break;
            case LIGHT_MACHINE_GUN:
                drawLMGSprite(g2d);
                break;
            case GRENADE_LAUNCHER:
                drawGrenadeLauncherSprite(g2d);
                break;
            case FLAMETHROWER:
                drawFlamethrowerSprite(g2d);
                break;
            case SHOTGUN:
                drawShotgunSprite(g2d);
                break;
            case SNIPER_RAILGUN:
                drawSniperSprite(g2d);
                break;
            default:
                drawGenericWeaponIcon(g2d);
                break;
        }
    }
    
    /**
     * Icono genérico para armas no reconocidas.
     */
    private static void drawGenericWeaponIcon(Graphics2D g2d) {
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
    
    // ==================== SPRITES DE ARMAS ====================
    
    /**
     * Sprite de Pistola - arma clásica de mano.
     */
    private static void drawPistolSprite(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2;
        
        // Cañón
        g2d.setColor(new Color(80, 80, 90));
        g2d.fillRoundRect(cx - 2, cy - 16, 6, 18, 2, 2);
        
        // Cuerpo principal
        g2d.setColor(new Color(60, 60, 70));
        g2d.fillRoundRect(cx - 6, cy - 4, 14, 10, 4, 4);
        
        // Empuñadura
        g2d.setColor(new Color(100, 70, 40));
        g2d.fillRoundRect(cx - 4, cy + 4, 8, 14, 3, 3);
        
        // Textura madera
        g2d.setColor(new Color(80, 55, 30));
        g2d.drawLine(cx - 2, cy + 6, cx - 2, cy + 15);
        g2d.drawLine(cx + 2, cy + 8, cx + 2, cy + 16);
        
        // Gatillo
        g2d.setColor(new Color(50, 50, 55));
        g2d.fillOval(cx, cy + 2, 4, 5);
        
        // Brillo metálico
        g2d.setColor(new Color(120, 120, 130));
        g2d.drawLine(cx, cy - 14, cx, cy - 6);
    }
    
    /**
     * Sprite de Ametralladora Ligera (LMG).
     */
    private static void drawLMGSprite(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2;
        
        // Cañón largo
        g2d.setColor(new Color(70, 70, 80));
        g2d.fillRoundRect(cx - 3, cy - 20, 8, 24, 2, 2);
        
        // Orificios de ventilación
        g2d.setColor(new Color(40, 40, 45));
        for (int i = 0; i < 4; i++) {
            g2d.fillOval(cx - 1, cy - 18 + i * 5, 4, 2);
        }
        
        // Cuerpo
        g2d.setColor(new Color(60, 60, 65));
        g2d.fillRoundRect(cx - 8, cy - 2, 18, 12, 4, 4);
        
        // Cargador
        g2d.setColor(new Color(50, 50, 55));
        g2d.fillRoundRect(cx - 12, cy + 2, 8, 16, 2, 2);
        
        // Empuñadura
        g2d.setColor(new Color(90, 60, 35));
        g2d.fillRoundRect(cx + 2, cy + 8, 8, 12, 2, 2);
        
        // Balas visibles
        g2d.setColor(new Color(200, 180, 80));
        g2d.fillOval(cx - 10, cy + 4, 4, 3);
        g2d.fillOval(cx - 10, cy + 8, 4, 3);
    }
    
    /**
     * Sprite de Lanzagranadas.
     */
    private static void drawGrenadeLauncherSprite(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2;
        
        // Tubo lanzador (grande)
        g2d.setColor(new Color(70, 80, 60));
        g2d.fillRoundRect(cx - 6, cy - 18, 14, 22, 6, 6);
        
        // Boca del tubo
        g2d.setColor(new Color(40, 45, 35));
        g2d.fillOval(cx - 4, cy - 20, 10, 6);
        
        // Granada visible dentro
        g2d.setColor(new Color(80, 120, 60));
        g2d.fillOval(cx - 2, cy - 16, 6, 8);
        
        // Empuñadura
        g2d.setColor(new Color(100, 70, 45));
        g2d.fillRoundRect(cx - 4, cy + 4, 10, 14, 3, 3);
        
        // Gatillo
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillOval(cx + 4, cy + 2, 4, 5);
        
        // Correa
        g2d.setColor(new Color(60, 50, 40));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawArc(cx + 6, cy - 10, 10, 20, -90, 180);
    }
    
    /**
     * Sprite de Lanzallamas.
     */
    private static void drawFlamethrowerSprite(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2;
        
        // Tanque de combustible
        g2d.setColor(new Color(180, 60, 40));
        g2d.fillRoundRect(cx - 10, cy - 4, 12, 20, 4, 4);
        
        // Tubo principal
        g2d.setColor(new Color(70, 70, 75));
        g2d.fillRoundRect(cx - 2, cy - 18, 8, 22, 3, 3);
        
        // Boquilla
        g2d.setColor(new Color(50, 50, 55));
        g2d.fillRoundRect(cx - 1, cy - 22, 6, 6, 2, 2);
        
        // Llamas saliendo
        GeneralPath flame = new GeneralPath();
        flame.moveTo(cx + 2, cy - 22);
        flame.curveTo(cx - 4, cy - 30, cx, cy - 36, cx + 2, cy - 32);
        flame.curveTo(cx + 4, cy - 36, cx + 8, cy - 30, cx + 2, cy - 22);
        
        g2d.setColor(new Color(255, 200, 50));
        g2d.fill(flame);
        
        // Núcleo de la llama
        g2d.setColor(new Color(255, 100, 30));
        g2d.fillOval(cx, cy - 28, 4, 6);
        
        // Empuñadura
        g2d.setColor(new Color(90, 65, 40));
        g2d.fillRoundRect(cx + 4, cy + 6, 8, 12, 2, 2);
        
        // Manguera
        g2d.setColor(new Color(50, 50, 50));
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawArc(cx - 14, cy + 8, 12, 12, 0, 180);
    }
    
    /**
     * Sprite de Escopeta.
     */
    private static void drawShotgunSprite(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2;
        
        // Cañones dobles
        g2d.setColor(new Color(70, 70, 75));
        g2d.fillRoundRect(cx - 5, cy - 20, 5, 24, 2, 2);
        g2d.fillRoundRect(cx + 1, cy - 20, 5, 24, 2, 2);
        
        // Bocas de los cañones
        g2d.setColor(new Color(40, 40, 45));
        g2d.fillOval(cx - 4, cy - 22, 4, 3);
        g2d.fillOval(cx + 2, cy - 22, 4, 3);
        
        // Cuerpo/receptor
        g2d.setColor(new Color(60, 60, 65));
        g2d.fillRoundRect(cx - 6, cy, 14, 8, 3, 3);
        
        // Culata de madera
        g2d.setColor(new Color(110, 75, 45));
        GeneralPath stock = new GeneralPath();
        stock.moveTo(cx - 4, cy + 8);
        stock.lineTo(cx - 6, cy + 20);
        stock.lineTo(cx + 8, cy + 20);
        stock.lineTo(cx + 6, cy + 8);
        stock.closePath();
        g2d.fill(stock);
        
        // Vetas de madera
        g2d.setColor(new Color(90, 60, 35));
        g2d.drawLine(cx - 2, cy + 10, cx - 4, cy + 18);
        g2d.drawLine(cx + 2, cy + 10, cx + 4, cy + 18);
        
        // Gatillo
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillOval(cx + 2, cy + 4, 4, 5);
    }
    
    /**
     * Sprite de Rifle de Francotirador (Sniper Railgun).
     */
    private static void drawSniperSprite(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2;
        
        // Cañón largo y fino
        g2d.setColor(new Color(60, 65, 70));
        g2d.fillRoundRect(cx - 2, cy - 22, 6, 28, 2, 2);
        
        // Supresor/silenciador
        g2d.setColor(new Color(50, 50, 55));
        g2d.fillRoundRect(cx - 3, cy - 26, 8, 6, 3, 3);
        
        // Mira telescópica
        g2d.setColor(new Color(40, 45, 50));
        g2d.fillRoundRect(cx + 4, cy - 14, 10, 6, 2, 2);
        
        // Lente de la mira
        g2d.setColor(new Color(100, 150, 200));
        g2d.fillOval(cx + 5, cy - 13, 4, 4);
        g2d.setColor(new Color(150, 200, 255, 150));
        g2d.fillOval(cx + 6, cy - 12, 2, 2);
        
        // Cuerpo
        g2d.setColor(new Color(55, 60, 65));
        g2d.fillRoundRect(cx - 6, cy - 2, 14, 10, 3, 3);
        
        // Cargador
        g2d.setColor(new Color(45, 45, 50));
        g2d.fillRoundRect(cx - 8, cy + 2, 6, 10, 2, 2);
        
        // Culata táctica
        g2d.setColor(new Color(50, 55, 60));
        GeneralPath stock = new GeneralPath();
        stock.moveTo(cx - 4, cy + 8);
        stock.lineTo(cx - 8, cy + 18);
        stock.lineTo(cx + 6, cy + 18);
        stock.lineTo(cx + 4, cy + 8);
        stock.closePath();
        g2d.fill(stock);
        
        // Detalles tácticos (raíles)
        g2d.setColor(new Color(70, 75, 80));
        g2d.fillRect(cx - 1, cy - 18, 4, 2);
        g2d.fillRect(cx - 1, cy - 14, 4, 2);
        
        // Efecto de energía (railgun)
        g2d.setColor(new Color(100, 200, 255, 100));
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawLine(cx + 1, cy - 24, cx + 1, cy - 20);
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
     * Icono de multi-objetivo: balas a múltiples enemigos
     */
    private static void drawMultiTargetIcon(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2;
        
        Color bulletColor = new Color(255, 220, 100);
        Color targetColor = new Color(255, 100, 100);
        
        // Tres enemigos/objetivos en diferentes direcciones
        int[][] targets = {{cx - 12, cy - 10}, {cx + 12, cy - 8}, {cx, cy + 12}};
        
        for (int[] target : targets) {
            // Línea de trayectoria
            g2d.setColor(new Color(255, 220, 100, 80));
            g2d.setStroke(new BasicStroke(1f));
            g2d.draw(new Line2D.Float(cx, cy, target[0], target[1]));
            
            // Bala
            g2d.setColor(bulletColor);
            int midX = (cx + target[0]) / 2;
            int midY = (cy + target[1]) / 2;
            g2d.fillOval(midX - 2, midY - 2, 4, 4);
            
            // Objetivo (enemigo)
            g2d.setColor(targetColor);
            g2d.fillOval(target[0] - 4, target[1] - 4, 8, 8);
        }
        
        // Origen (jugador)
        g2d.setColor(new Color(100, 150, 255));
        g2d.fillOval(cx - 5, cy - 5, 10, 10);
        
        // Símbolo x3
        g2d.setColor(STAT_COLOR);
        g2d.setFont(g2d.getFont().deriveFont(java.awt.Font.BOLD, 8f));
        g2d.drawString("x3", cx + 10, cy + 18);
    }
    
    /**
     * Icono de ángulo de cono: lanzallamas con ángulo amplio
     */
    private static void drawConeAngleIcon(Graphics2D g2d) {
        int cx = ICON_SIZE / 2;
        int cy = ICON_SIZE / 2 + 6;
        
        // Cono de fuego amplio
        g2d.setColor(new Color(255, 150, 50, 100));
        Arc2D outerArc = new Arc2D.Float(cx - 20, cy - 20, 40, 40, 60, 60, Arc2D.PIE);
        g2d.fill(outerArc);
        
        g2d.setColor(new Color(255, 200, 50, 150));
        Arc2D innerArc = new Arc2D.Float(cx - 14, cy - 14, 28, 28, 65, 50, Arc2D.PIE);
        g2d.fill(innerArc);
        
        g2d.setColor(new Color(255, 100, 30, 200));
        Arc2D coreArc = new Arc2D.Float(cx - 8, cy - 8, 16, 16, 70, 40, Arc2D.PIE);
        g2d.fill(coreArc);
        
        // Flechas indicando expansión del ángulo
        g2d.setColor(new Color(255, 255, 100));
        g2d.setStroke(new BasicStroke(2f));
        // Flecha izquierda
        g2d.draw(new Line2D.Float(cx - 8, cy - 12, cx - 14, cy - 16));
        g2d.draw(new Line2D.Float(cx - 14, cy - 16, cx - 12, cy - 12));
        // Flecha derecha
        g2d.draw(new Line2D.Float(cx + 8, cy - 12, cx + 14, cy - 16));
        g2d.draw(new Line2D.Float(cx + 14, cy - 16, cx + 12, cy - 12));
        
        // Texto de grados
        g2d.setColor(STAT_COLOR);
        g2d.setFont(g2d.getFont().deriveFont(java.awt.Font.BOLD, 8f));
        g2d.drawString("+°", cx + 12, cy + 14);
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
