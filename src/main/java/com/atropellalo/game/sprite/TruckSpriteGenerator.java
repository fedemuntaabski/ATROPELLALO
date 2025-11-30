package com.atropellalo.game.sprite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Genera sprites procedurales para la camioneta del jugador.
 * Vista desde arriba con animaciones de idle, movimiento y muerte.
 */
public class TruckSpriteGenerator {
    
    // Dimensiones del sprite
    public static final int SPRITE_WIDTH = 48;
    public static final int SPRITE_HEIGHT = 64;
    
    // Colores de la camioneta
    private static final Color BODY_COLOR = new Color(45, 85, 140);       // Azul oscuro
    private static final Color BODY_HIGHLIGHT = new Color(70, 120, 180);  // Azul claro
    private static final Color BODY_SHADOW = new Color(30, 55, 95);       // Azul muy oscuro
    private static final Color WINDOW_COLOR = new Color(140, 180, 220);   // Celeste cristal
    private static final Color WHEEL_COLOR = new Color(30, 30, 30);       // Negro
    private static final Color WHEEL_RIM = new Color(80, 80, 80);         // Gris
    private static final Color HEADLIGHT_COLOR = new Color(255, 255, 200); // Amarillo claro
    private static final Color TAILLIGHT_COLOR = new Color(200, 50, 50);   // Rojo
    private static final Color BUMPER_COLOR = new Color(60, 60, 60);       // Gris oscuro
    
    // Colores de daño
    private static final Color FIRE_COLOR_1 = new Color(255, 100, 0);
    private static final Color FIRE_COLOR_2 = new Color(255, 200, 0);
    private static final Color SMOKE_COLOR = new Color(50, 50, 50, 150);
    
    /**
     * Genera todas las animaciones de la camioneta.
     * @return Mapa de estado -> animación
     */
    public static Map<AnimationState, Animation> generateAllAnimations() {
        Map<AnimationState, Animation> animations = new HashMap<>();
        
        animations.put(AnimationState.IDLE, generateIdleAnimation());
        animations.put(AnimationState.MOVING, generateMovingAnimation());
        animations.put(AnimationState.DEATH, generateDeathAnimation());
        
        return animations;
    }
    
    /**
     * Genera la animación de idle (camioneta quieta).
     */
    public static Animation generateIdleAnimation() {
        Animation anim = new Animation(0.5f, true);
        
        // Frame 1: Normal
        anim.addFrame(generateTruckFrame(0, false, 0));
        // Frame 2: Leve vibración de motor
        anim.addFrame(generateTruckFrame(1, false, 0));
        
        return anim;
    }
    
    /**
     * Genera la animación de movimiento.
     */
    public static Animation generateMovingAnimation() {
        Animation anim = new Animation(0.08f, true);
        
        // 4 frames con rotación de ruedas y efecto de velocidad
        for (int i = 0; i < 4; i++) {
            anim.addFrame(generateTruckFrame(i, true, i * 0.25f));
        }
        
        return anim;
    }
    
    /**
     * Genera la animación de muerte/destrucción.
     */
    public static Animation generateDeathAnimation() {
        Animation anim = new Animation(0.15f, false);
        
        // 6 frames de destrucción progresiva
        for (int i = 0; i < 6; i++) {
            anim.addFrame(generateDeathFrame(i));
        }
        
        return anim;
    }
    
    /**
     * Genera un frame de la camioneta.
     * @param frameIndex Índice del frame para variaciones
     * @param moving Si está en movimiento
     * @param wheelRotation Rotación de ruedas (0-1)
     */
    private static BufferedImage generateTruckFrame(int frameIndex, boolean moving, float wheelRotation) {
        BufferedImage image = new BufferedImage(SPRITE_WIDTH, SPRITE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = SPRITE_WIDTH / 2;
        int cy = SPRITE_HEIGHT / 2;
        
        // Pequeña vibración para idle
        int offsetY = (frameIndex % 2 == 0) ? 0 : 1;
        
        // Sombra bajo el vehículo
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.fillOval(cx - 18, cy - 26 + offsetY, 36, 54);
        
        // Ruedas traseras (abajo en la imagen = atrás del vehículo)
        drawWheel(g2d, cx - 16, cy + 18 + offsetY, wheelRotation);
        drawWheel(g2d, cx + 8, cy + 18 + offsetY, wheelRotation);
        
        // Ruedas delanteras (arriba en la imagen = frente del vehículo)
        drawWheel(g2d, cx - 16, cy - 22 + offsetY, wheelRotation);
        drawWheel(g2d, cx + 8, cy - 22 + offsetY, wheelRotation);
        
        // Cuerpo principal de la camioneta (pickup truck)
        // Caja trasera (bed)
        g2d.setColor(BODY_SHADOW);
        g2d.fill(new RoundRectangle2D.Float(cx - 15, cy + 2 + offsetY, 30, 24, 4, 4));
        g2d.setColor(BODY_COLOR);
        g2d.fill(new RoundRectangle2D.Float(cx - 14, cy + 3 + offsetY, 28, 22, 3, 3));
        
        // Líneas de la caja
        g2d.setColor(BODY_SHADOW);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(cx - 10, cy + 5 + offsetY, cx - 10, cy + 22 + offsetY);
        g2d.drawLine(cx + 10, cy + 5 + offsetY, cx + 10, cy + 22 + offsetY);
        
        // Cabina
        g2d.setColor(BODY_SHADOW);
        g2d.fill(new RoundRectangle2D.Float(cx - 15, cy - 24 + offsetY, 30, 28, 6, 6));
        g2d.setColor(BODY_COLOR);
        g2d.fill(new RoundRectangle2D.Float(cx - 14, cy - 23 + offsetY, 28, 26, 5, 5));
        
        // Highlight de la cabina
        g2d.setColor(BODY_HIGHLIGHT);
        g2d.fill(new RoundRectangle2D.Float(cx - 10, cy - 20 + offsetY, 8, 18, 3, 3));
        
        // Parabrisas (ventana frontal)
        g2d.setColor(WINDOW_COLOR);
        g2d.fill(new RoundRectangle2D.Float(cx - 10, cy - 18 + offsetY, 20, 10, 3, 3));
        
        // Reflejo en el parabrisas
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.fillRect(cx - 8, cy - 16 + offsetY, 6, 6);
        
        // Luces delanteras (arriba = frente)
        g2d.setColor(HEADLIGHT_COLOR);
        g2d.fillOval(cx - 12, cy - 26 + offsetY, 5, 4);
        g2d.fillOval(cx + 7, cy - 26 + offsetY, 5, 4);
        
        // Luces traseras
        g2d.setColor(TAILLIGHT_COLOR);
        g2d.fillRect(cx - 13, cy + 23 + offsetY, 4, 3);
        g2d.fillRect(cx + 9, cy + 23 + offsetY, 4, 3);
        
        // Parachoques delantero
        g2d.setColor(BUMPER_COLOR);
        g2d.fill(new RoundRectangle2D.Float(cx - 16, cy - 28 + offsetY, 32, 4, 2, 2));
        
        // Parachoques trasero
        g2d.fill(new RoundRectangle2D.Float(cx - 16, cy + 24 + offsetY, 32, 4, 2, 2));
        
        // Efecto de movimiento (líneas de velocidad)
        if (moving) {
            g2d.setColor(new Color(255, 255, 255, 40 + (frameIndex * 10)));
            g2d.setStroke(new BasicStroke(1));
            for (int i = 0; i < 3; i++) {
                int lineY = cy + 28 + (i * 4);
                g2d.drawLine(cx - 8 + (i * 3), lineY, cx - 8 + (i * 3), lineY + 6);
                g2d.drawLine(cx + 8 - (i * 3), lineY, cx + 8 - (i * 3), lineY + 6);
            }
        }
        
        g2d.dispose();
        return image;
    }
    
    /**
     * Dibuja una rueda.
     */
    private static void drawWheel(Graphics2D g2d, int x, int y, float rotation) {
        // Rueda exterior
        g2d.setColor(WHEEL_COLOR);
        g2d.fillOval(x, y, 8, 10);
        
        // Rin
        g2d.setColor(WHEEL_RIM);
        g2d.fillOval(x + 2, y + 3, 4, 4);
        
        // Líneas de rotación en el rin
        g2d.setColor(WHEEL_COLOR);
        int centerX = x + 4;
        int centerY = y + 5;
        double angle = rotation * Math.PI * 2;
        int lineLen = 2;
        g2d.drawLine(centerX, centerY, 
                     centerX + (int)(Math.cos(angle) * lineLen), 
                     centerY + (int)(Math.sin(angle) * lineLen));
    }
    
    /**
     * Genera un frame de la animación de muerte.
     */
    private static BufferedImage generateDeathFrame(int frameIndex) {
        BufferedImage image = new BufferedImage(SPRITE_WIDTH, SPRITE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = SPRITE_WIDTH / 2;
        int cy = SPRITE_HEIGHT / 2;
        
        // Progresión del daño (0-5)
        float damage = frameIndex / 5f;
        
        // Rotación progresiva (volcadura)
        double rotation = damage * 0.3;
        
        AffineTransform original = g2d.getTransform();
        g2d.translate(cx, cy);
        g2d.rotate(rotation);
        g2d.translate(-cx, -cy);
        
        // Cuerpo dañado con color alterado
        Color damagedBody = blendColors(BODY_COLOR, new Color(80, 60, 40), damage);
        
        // Sombra
        g2d.setColor(new Color(0, 0, 0, (int)(60 - damage * 30)));
        g2d.fillOval(cx - 18, cy - 26, 36, 54);
        
        // Ruedas (algunas salen volando en frames avanzados)
        if (frameIndex < 4) {
            drawWheel(g2d, cx - 16 - frameIndex * 2, cy + 18, 0);
            drawWheel(g2d, cx + 8 + frameIndex * 2, cy + 18, 0);
        }
        if (frameIndex < 3) {
            drawWheel(g2d, cx - 16, cy - 22, 0);
            drawWheel(g2d, cx + 8, cy - 22, 0);
        }
        
        // Caja trasera dañada
        g2d.setColor(new Color(50, 40, 30));
        g2d.fill(new RoundRectangle2D.Float(cx - 15, cy + 2, 30, 24, 4, 4));
        g2d.setColor(damagedBody);
        g2d.fill(new RoundRectangle2D.Float(cx - 14, cy + 3, 28, 22, 3, 3));
        
        // Abolladuras
        g2d.setColor(BODY_SHADOW);
        if (frameIndex > 1) {
            g2d.fillOval(cx - 8, cy + 8, 6, 4);
        }
        if (frameIndex > 2) {
            g2d.fillOval(cx + 2, cy + 12, 8, 5);
        }
        
        // Cabina dañada
        g2d.setColor(new Color(50, 40, 30));
        g2d.fill(new RoundRectangle2D.Float(cx - 15, cy - 24, 30, 28, 6, 6));
        g2d.setColor(damagedBody);
        g2d.fill(new RoundRectangle2D.Float(cx - 14, cy - 23, 28, 26, 5, 5));
        
        // Ventana rota
        g2d.setColor(frameIndex > 2 ? new Color(80, 80, 80, 150) : WINDOW_COLOR);
        g2d.fill(new RoundRectangle2D.Float(cx - 10, cy - 18, 20, 10, 3, 3));
        
        // Grietas en la ventana
        if (frameIndex > 1) {
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(cx - 5, cy - 16, cx + 3, cy - 10);
            if (frameIndex > 3) {
                g2d.drawLine(cx + 2, cy - 17, cx - 4, cy - 9);
            }
        }
        
        g2d.setTransform(original);
        
        // Fuego y humo
        if (frameIndex > 2) {
            // Humo
            g2d.setColor(SMOKE_COLOR);
            for (int i = 0; i < frameIndex - 1; i++) {
                int smokeX = cx - 10 + (i * 8);
                int smokeY = cy - 30 - (i * 5);
                int smokeSize = 8 + i * 3;
                g2d.fillOval(smokeX, smokeY, smokeSize, smokeSize);
            }
            
            // Fuego
            g2d.setColor(FIRE_COLOR_1);
            g2d.fillOval(cx - 6, cy - 10, 12, 15);
            g2d.setColor(FIRE_COLOR_2);
            g2d.fillOval(cx - 3, cy - 6, 6, 10);
        }
        
        // Explosión final
        if (frameIndex == 5) {
            g2d.setColor(new Color(255, 200, 100, 150));
            g2d.fillOval(cx - 25, cy - 25, 50, 50);
            g2d.setColor(new Color(255, 150, 50, 100));
            g2d.fillOval(cx - 30, cy - 30, 60, 60);
        }
        
        g2d.dispose();
        return image;
    }
    
    /**
     * Mezcla dos colores.
     */
    private static Color blendColors(Color c1, Color c2, float ratio) {
        int r = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        return new Color(r, g, b);
    }
}
