package com.atropellalo.game.sprite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Genera sprites procedurales para los diferentes tipos de zombies.
 * Vista desde arriba con animaciones de idle, movimiento y muerte.
 */
public class ZombieSpriteGenerator {
    
    // ==================== FAST ZOMBIE ====================
    public static final int FAST_WIDTH = 24;
    public static final int FAST_HEIGHT = 24;
    
    private static final Color FAST_BODY = new Color(120, 180, 90);        // Verde claro
    private static final Color FAST_BODY_DARK = new Color(80, 140, 60);    // Verde oscuro
    private static final Color FAST_EYES = new Color(255, 50, 50);         // Rojo
    
    // ==================== SLOW ZOMBIE ====================
    public static final int SLOW_WIDTH = 36;
    public static final int SLOW_HEIGHT = 36;
    
    private static final Color SLOW_BODY = new Color(100, 70, 130);        // Púrpura
    private static final Color SLOW_BODY_DARK = new Color(70, 50, 100);    // Púrpura oscuro
    private static final Color SLOW_EYES = new Color(255, 220, 50);        // Amarillo
    
    // ==================== EXPLOSIVE ZOMBIE ====================
    public static final int EXPLOSIVE_WIDTH = 28;
    public static final int EXPLOSIVE_HEIGHT = 28;
    
    private static final Color EXPLOSIVE_BODY = new Color(220, 120, 50);   // Naranja
    private static final Color EXPLOSIVE_BODY_DARK = new Color(180, 80, 30); // Naranja oscuro
    private static final Color EXPLOSIVE_GLOW = new Color(255, 200, 100);  // Resplandor
    
    // Colores comunes
    private static final Color BLOOD_COLOR = new Color(120, 20, 20);
    private static final Color DECAY_COLOR = new Color(80, 100, 60);
    
    // ==================== ANIMACIONES GENÉRICAS CON COLORES PERSONALIZADOS ====================
    
    /**
     * Genera una animación de zombie con colores personalizados.
     * Útil para nuevos tipos de zombies que comparten la estructura base.
     * @param state Estado de animación (IDLE, MOVING, DEATH)
     * @param size Tamaño del sprite
     * @param bodyColor Color principal del cuerpo
     * @param bodyDark Color oscuro del cuerpo
     * @param eyeColor Color de los ojos
     * @return Animación generada
     */
    public static Animation generateZombieAnimation(AnimationState state, int size, 
                                                     Color bodyColor, Color bodyDark, Color eyeColor) {
        switch (state) {
            case IDLE:
                return generateGenericIdleAnimation(size, bodyColor, bodyDark, eyeColor);
            case MOVING:
                return generateGenericMovingAnimation(size, bodyColor, bodyDark, eyeColor);
            case DEATH:
                return generateGenericDeathAnimation(size, bodyColor, bodyDark, eyeColor);
            default:
                return generateGenericIdleAnimation(size, bodyColor, bodyDark, eyeColor);
        }
    }
    
    private static Animation generateGenericIdleAnimation(int size, Color bodyColor, Color bodyDark, Color eyeColor) {
        Animation anim = new Animation(0.35f, true);
        for (int i = 0; i < 2; i++) {
            anim.addFrame(generateGenericZombieFrame(i, false, size, bodyColor, bodyDark, eyeColor));
        }
        return anim;
    }
    
    private static Animation generateGenericMovingAnimation(int size, Color bodyColor, Color bodyDark, Color eyeColor) {
        Animation anim = new Animation(0.12f, true);
        for (int i = 0; i < 4; i++) {
            anim.addFrame(generateGenericZombieFrame(i, true, size, bodyColor, bodyDark, eyeColor));
        }
        return anim;
    }
    
    private static Animation generateGenericDeathAnimation(int size, Color bodyColor, Color bodyDark, Color eyeColor) {
        Animation anim = new Animation(0.14f, false);
        for (int i = 0; i < 5; i++) {
            anim.addFrame(generateGenericDeathFrame(i, size, bodyColor, bodyDark, eyeColor));
        }
        return anim;
    }
    
    private static BufferedImage generateGenericZombieFrame(int frameIndex, boolean moving, int size,
                                                             Color bodyColor, Color bodyDark, Color eyeColor) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = size / 2;
        int cy = size / 2;
        float scale = size / 28f; // Escala relativa al tamaño base de 28
        
        int limbOffset = moving ? ((frameIndex % 2 == 0) ? (int)(2 * scale) : (int)(-2 * scale)) : 0;
        int breathe = (frameIndex % 2 == 0) ? 0 : 1;
        
        // Sombra
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.fillOval((int)(cx - 9 * scale), (int)(cy - 8 * scale), (int)(18 * scale), (int)(18 * scale));
        
        // Piernas
        g2d.setColor(bodyDark);
        g2d.fillOval((int)(cx - 6 * scale + limbOffset), (int)(cy + 4 * scale), (int)(5 * scale), (int)(8 * scale));
        g2d.fillOval((int)(cx + 1 * scale - limbOffset), (int)(cy + 4 * scale), (int)(5 * scale), (int)(8 * scale));
        
        // Brazos
        g2d.fillOval((int)(cx - 10 * scale - limbOffset), (int)(cy - 4 * scale), (int)(6 * scale), (int)(10 * scale));
        g2d.fillOval((int)(cx + 4 * scale + limbOffset), (int)(cy - 4 * scale), (int)(6 * scale), (int)(10 * scale));
        
        // Cuerpo
        g2d.setColor(bodyColor);
        g2d.fillOval((int)(cx - 7 * scale), (int)(cy - 6 * scale + breathe), (int)(14 * scale), (int)(16 * scale));
        
        // Cabeza
        g2d.fillOval((int)(cx - 5 * scale), (int)(cy - 10 * scale + breathe), (int)(10 * scale), (int)(10 * scale));
        
        // Manchas
        g2d.setColor(bodyDark);
        g2d.fillOval((int)(cx - 3 * scale), (int)(cy - 2 * scale), (int)(4 * scale), (int)(3 * scale));
        
        // Ojos
        g2d.setColor(eyeColor);
        g2d.fillOval((int)(cx - 4 * scale), (int)(cy - 8 * scale + breathe), (int)(3 * scale), (int)(3 * scale));
        g2d.fillOval((int)(cx + 1 * scale), (int)(cy - 8 * scale + breathe), (int)(3 * scale), (int)(3 * scale));
        
        // Brillo en ojos
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.fillOval((int)(cx - 3 * scale), (int)(cy - 7 * scale + breathe), (int)(1 * scale), (int)(1 * scale));
        
        g2d.dispose();
        return image;
    }
    
    private static BufferedImage generateGenericDeathFrame(int frameIndex, int size,
                                                            Color bodyColor, Color bodyDark, Color eyeColor) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = size / 2;
        int cy = size / 2;
        float scale = size / 28f;
        float progress = frameIndex / 4f;
        
        // Mancha de sangre
        g2d.setColor(BLOOD_COLOR);
        int bloodSize = (int)((8 + progress * 12) * scale);
        g2d.fillOval(cx - bloodSize/2, cy - bloodSize/2 + 2, bloodSize, bloodSize);
        
        // Cuerpo colapsando
        int collapse = (int)(progress * 4 * scale);
        int spread = (int)(progress * 3 * scale);
        
        g2d.setColor(bodyDark);
        g2d.fillOval((int)(cx - 7 * scale - spread), (int)(cy - 4 * scale + collapse), 
                     (int)(14 * scale + spread * 2), (int)(14 * scale - collapse));
        
        // Cabeza cayendo
        g2d.setColor(bodyColor);
        g2d.fillOval((int)(cx - 4 * scale - spread/2), (int)(cy - 8 * scale + collapse * 2), 
                     (int)(8 * scale), (int)((8 - progress * 2) * scale));
        
        // Ojos apagándose
        if (frameIndex < 3) {
            int alpha = 255 - frameIndex * 80;
            g2d.setColor(new Color(eyeColor.getRed(), eyeColor.getGreen(), eyeColor.getBlue(), Math.max(0, alpha)));
            g2d.fillOval((int)(cx - 3 * scale), (int)(cy - 6 * scale + collapse * 2), (int)(2 * scale), (int)(2 * scale));
            g2d.fillOval((int)(cx + 1 * scale), (int)(cy - 6 * scale + collapse * 2), (int)(2 * scale), (int)(2 * scale));
        }
        
        g2d.dispose();
        return image;
    }
    
    // ==================== FAST ZOMBIE ANIMATIONS ====================
    
    /**
     * Genera todas las animaciones del zombie rápido.
     */
    public static Map<AnimationState, Animation> generateFastZombieAnimations() {
        Map<AnimationState, Animation> animations = new HashMap<>();
        
        animations.put(AnimationState.IDLE, generateFastIdleAnimation());
        animations.put(AnimationState.MOVING, generateFastMovingAnimation());
        animations.put(AnimationState.DEATH, generateFastDeathAnimation());
        
        return animations;
    }
    
    private static Animation generateFastIdleAnimation() {
        Animation anim = new Animation(0.3f, true);
        
        // 2 frames: respiración/movimiento nervioso
        for (int i = 0; i < 2; i++) {
            anim.addFrame(generateFastZombieFrame(i, false));
        }
        
        return anim;
    }
    
    private static Animation generateFastMovingAnimation() {
        Animation anim = new Animation(0.1f, true);
        
        // 4 frames de movimiento rápido
        for (int i = 0; i < 4; i++) {
            anim.addFrame(generateFastZombieFrame(i, true));
        }
        
        return anim;
    }
    
    private static Animation generateFastDeathAnimation() {
        Animation anim = new Animation(0.12f, false);
        
        for (int i = 0; i < 5; i++) {
            anim.addFrame(generateFastDeathFrame(i));
        }
        
        return anim;
    }
    
    private static BufferedImage generateFastZombieFrame(int frameIndex, boolean moving) {
        BufferedImage image = new BufferedImage(FAST_WIDTH, FAST_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = FAST_WIDTH / 2;
        int cy = FAST_HEIGHT / 2;
        
        // Movimiento de brazos/piernas
        int limbOffset = moving ? ((frameIndex % 2 == 0) ? 2 : -2) : 0;
        int breathe = (frameIndex % 2 == 0) ? 0 : 1;
        
        // Sombra
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.fillOval(cx - 9, cy - 8, 18, 18);
        
        // Piernas (atrás)
        g2d.setColor(FAST_BODY_DARK);
        g2d.fillOval(cx - 6 + limbOffset, cy + 4, 5, 8);
        g2d.fillOval(cx + 1 - limbOffset, cy + 4, 5, 8);
        
        // Brazos
        g2d.fillOval(cx - 10 - limbOffset, cy - 4, 6, 10);
        g2d.fillOval(cx + 4 + limbOffset, cy - 4, 6, 10);
        
        // Cuerpo (torso)
        g2d.setColor(FAST_BODY);
        g2d.fillOval(cx - 7, cy - 6 + breathe, 14, 16);
        
        // Cabeza
        g2d.setColor(FAST_BODY);
        g2d.fillOval(cx - 5, cy - 10 + breathe, 10, 10);
        
        // Manchas de decay
        g2d.setColor(DECAY_COLOR);
        g2d.fillOval(cx - 3, cy - 2, 4, 3);
        g2d.fillOval(cx + 2, cy + 2, 3, 2);
        
        // Ojos rojos brillantes
        g2d.setColor(FAST_EYES);
        g2d.fillOval(cx - 4, cy - 8 + breathe, 3, 3);
        g2d.fillOval(cx + 1, cy - 8 + breathe, 3, 3);
        
        // Brillo en los ojos
        g2d.setColor(new Color(255, 150, 150));
        g2d.fillOval(cx - 3, cy - 7 + breathe, 1, 1);
        g2d.fillOval(cx + 2, cy - 7 + breathe, 1, 1);
        
        g2d.dispose();
        return image;
    }
    
    private static BufferedImage generateFastDeathFrame(int frameIndex) {
        BufferedImage image = new BufferedImage(FAST_WIDTH, FAST_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = FAST_WIDTH / 2;
        int cy = FAST_HEIGHT / 2;
        float progress = frameIndex / 4f;
        
        // Mancha de sangre creciente
        g2d.setColor(BLOOD_COLOR);
        int bloodSize = (int)(8 + progress * 12);
        g2d.fillOval(cx - bloodSize/2, cy - bloodSize/2 + 2, bloodSize, bloodSize);
        
        // Cuerpo colapsando
        int collapse = (int)(progress * 4);
        int spread = (int)(progress * 3);
        
        g2d.setColor(FAST_BODY_DARK);
        g2d.fillOval(cx - 7 - spread, cy - 4 + collapse, 14 + spread * 2, 14 - collapse);
        
        // Cabeza cayendo
        g2d.setColor(FAST_BODY);
        g2d.fillOval(cx - 4 - spread/2, cy - 8 + collapse * 2, 8, 8 - collapse);
        
        // Ojos apagándose
        if (frameIndex < 3) {
            int alpha = 255 - frameIndex * 80;
            g2d.setColor(new Color(255, 50, 50, Math.max(0, alpha)));
            g2d.fillOval(cx - 3, cy - 6 + collapse * 2, 2, 2);
            g2d.fillOval(cx + 1, cy - 6 + collapse * 2, 2, 2);
        }
        
        g2d.dispose();
        return image;
    }
    
    // ==================== SLOW ZOMBIE ANIMATIONS ====================
    
    /**
     * Genera todas las animaciones del zombie lento.
     */
    public static Map<AnimationState, Animation> generateSlowZombieAnimations() {
        Map<AnimationState, Animation> animations = new HashMap<>();
        
        animations.put(AnimationState.IDLE, generateSlowIdleAnimation());
        animations.put(AnimationState.MOVING, generateSlowMovingAnimation());
        animations.put(AnimationState.DEATH, generateSlowDeathAnimation());
        
        return animations;
    }
    
    private static Animation generateSlowIdleAnimation() {
        Animation anim = new Animation(0.5f, true);
        
        for (int i = 0; i < 2; i++) {
            anim.addFrame(generateSlowZombieFrame(i, false));
        }
        
        return anim;
    }
    
    private static Animation generateSlowMovingAnimation() {
        Animation anim = new Animation(0.2f, true);
        
        // Movimiento lento y pesado
        for (int i = 0; i < 4; i++) {
            anim.addFrame(generateSlowZombieFrame(i, true));
        }
        
        return anim;
    }
    
    private static Animation generateSlowDeathAnimation() {
        Animation anim = new Animation(0.18f, false);
        
        for (int i = 0; i < 6; i++) {
            anim.addFrame(generateSlowDeathFrame(i));
        }
        
        return anim;
    }
    
    private static BufferedImage generateSlowZombieFrame(int frameIndex, boolean moving) {
        BufferedImage image = new BufferedImage(SLOW_WIDTH, SLOW_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = SLOW_WIDTH / 2;
        int cy = SLOW_HEIGHT / 2;
        
        int sway = moving ? ((frameIndex % 4 < 2) ? 1 : -1) : 0;
        int breathe = (frameIndex % 2 == 0) ? 0 : 1;
        
        // Sombra grande
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillOval(cx - 14, cy - 12, 28, 28);
        
        // Piernas gruesas
        g2d.setColor(SLOW_BODY_DARK);
        int legOffset = moving ? ((frameIndex % 2 == 0) ? 2 : -2) : 0;
        g2d.fillOval(cx - 10 + legOffset, cy + 6, 8, 12);
        g2d.fillOval(cx + 2 - legOffset, cy + 6, 8, 12);
        
        // Brazos musculosos
        g2d.fillOval(cx - 16 + sway, cy - 6, 10, 14);
        g2d.fillOval(cx + 6 - sway, cy - 6, 10, 14);
        
        // Cuerpo masivo
        g2d.setColor(SLOW_BODY);
        g2d.fillOval(cx - 12, cy - 10 + breathe, 24, 22);
        
        // Textura del cuerpo
        g2d.setColor(SLOW_BODY_DARK);
        g2d.fillOval(cx - 6, cy - 4, 6, 5);
        g2d.fillOval(cx + 2, cy + 2, 5, 4);
        
        // Cabeza grande
        g2d.setColor(SLOW_BODY);
        g2d.fillOval(cx - 8, cy - 16 + breathe, 16, 14);
        
        // Mandíbula pronunciada
        g2d.setColor(SLOW_BODY_DARK);
        g2d.fillOval(cx - 5, cy - 8 + breathe, 10, 6);
        
        // Ojos amarillos con pupilas
        g2d.setColor(SLOW_EYES);
        g2d.fillOval(cx - 6, cy - 13 + breathe, 5, 5);
        g2d.fillOval(cx + 1, cy - 13 + breathe, 5, 5);
        
        // Pupilas
        g2d.setColor(Color.BLACK);
        g2d.fillOval(cx - 5, cy - 12 + breathe, 2, 3);
        g2d.fillOval(cx + 3, cy - 12 + breathe, 2, 3);
        
        // Cicatrices/costuras
        g2d.setColor(BLOOD_COLOR);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(cx - 3, cy - 14, cx + 2, cy - 10);
        
        g2d.dispose();
        return image;
    }
    
    private static BufferedImage generateSlowDeathFrame(int frameIndex) {
        BufferedImage image = new BufferedImage(SLOW_WIDTH, SLOW_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = SLOW_WIDTH / 2;
        int cy = SLOW_HEIGHT / 2;
        float progress = frameIndex / 5f;
        
        // Gran charco de sangre
        g2d.setColor(BLOOD_COLOR);
        int bloodSize = (int)(12 + progress * 20);
        g2d.fillOval(cx - bloodSize/2, cy - bloodSize/2 + 4, bloodSize, bloodSize);
        
        // Cuerpo desplomándose
        int collapse = (int)(progress * 6);
        int spread = (int)(progress * 4);
        
        // Piernas separándose
        g2d.setColor(SLOW_BODY_DARK);
        g2d.fillOval(cx - 12 - spread, cy + 4, 8, 10 - collapse/2);
        g2d.fillOval(cx + 4 + spread, cy + 4, 8, 10 - collapse/2);
        
        // Brazos cayendo
        g2d.fillOval(cx - 18 - spread, cy - 4 + collapse, 9, 12 - collapse);
        g2d.fillOval(cx + 9 + spread, cy - 4 + collapse, 9, 12 - collapse);
        
        // Cuerpo aplastándose
        g2d.setColor(SLOW_BODY);
        g2d.fillOval(cx - 12 - spread/2, cy - 8 + collapse, 24 + spread, 20 - collapse);
        
        // Cabeza
        int headDrop = (int)(progress * 8);
        g2d.fillOval(cx - 7, cy - 14 + headDrop, 14, 12 - collapse/2);
        
        // Ojos apagándose
        if (frameIndex < 4) {
            int alpha = 255 - frameIndex * 60;
            g2d.setColor(new Color(255, 220, 50, Math.max(0, alpha)));
            g2d.fillOval(cx - 5, cy - 11 + headDrop, 4, 4);
            g2d.fillOval(cx + 2, cy - 11 + headDrop, 4, 4);
        }
        
        g2d.dispose();
        return image;
    }
    
    // ==================== EXPLOSIVE ZOMBIE ANIMATIONS ====================
    
    /**
     * Genera todas las animaciones del zombie explosivo.
     */
    public static Map<AnimationState, Animation> generateExplosiveZombieAnimations() {
        Map<AnimationState, Animation> animations = new HashMap<>();
        
        animations.put(AnimationState.IDLE, generateExplosiveIdleAnimation());
        animations.put(AnimationState.MOVING, generateExplosiveMovingAnimation());
        animations.put(AnimationState.DEATH, generateExplosiveDeathAnimation());
        
        return animations;
    }
    
    private static Animation generateExplosiveIdleAnimation() {
        Animation anim = new Animation(0.2f, true);
        
        // Pulso de brillo (es inestable)
        for (int i = 0; i < 4; i++) {
            anim.addFrame(generateExplosiveZombieFrame(i, false));
        }
        
        return anim;
    }
    
    private static Animation generateExplosiveMovingAnimation() {
        Animation anim = new Animation(0.12f, true);
        
        for (int i = 0; i < 4; i++) {
            anim.addFrame(generateExplosiveZombieFrame(i, true));
        }
        
        return anim;
    }
    
    private static Animation generateExplosiveDeathAnimation() {
        Animation anim = new Animation(0.1f, false);
        
        // Explosión dramática
        for (int i = 0; i < 8; i++) {
            anim.addFrame(generateExplosiveDeathFrame(i));
        }
        
        return anim;
    }
    
    private static BufferedImage generateExplosiveZombieFrame(int frameIndex, boolean moving) {
        BufferedImage image = new BufferedImage(EXPLOSIVE_WIDTH, EXPLOSIVE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = EXPLOSIVE_WIDTH / 2;
        int cy = EXPLOSIVE_HEIGHT / 2;
        
        int pulse = frameIndex % 4;
        int glowSize = 2 + pulse;
        int limbOffset = moving ? ((frameIndex % 2 == 0) ? 2 : -2) : 0;
        
        // Resplandor pulsante
        g2d.setColor(new Color(255, 200, 100, 30 + pulse * 15));
        g2d.fillOval(cx - 14 - glowSize, cy - 12 - glowSize, 28 + glowSize*2, 28 + glowSize*2);
        
        // Sombra
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.fillOval(cx - 11, cy - 9, 22, 22);
        
        // Piernas
        g2d.setColor(EXPLOSIVE_BODY_DARK);
        g2d.fillOval(cx - 7 + limbOffset, cy + 5, 6, 9);
        g2d.fillOval(cx + 1 - limbOffset, cy + 5, 6, 9);
        
        // Brazos
        g2d.fillOval(cx - 12 - limbOffset, cy - 4, 7, 11);
        g2d.fillOval(cx + 5 + limbOffset, cy - 4, 7, 11);
        
        // Cuerpo hinchado (lleno de gas/químicos)
        g2d.setColor(EXPLOSIVE_BODY);
        g2d.fillOval(cx - 9, cy - 8, 18, 18);
        
        // Venas/grietas brillantes
        g2d.setColor(EXPLOSIVE_GLOW);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(cx - 4, cy - 4, cx + 2, cy + 3);
        g2d.drawLine(cx + 3, cy - 2, cx - 2, cy + 4);
        
        // Cabeza
        g2d.setColor(EXPLOSIVE_BODY);
        g2d.fillOval(cx - 6, cy - 13, 12, 10);
        
        // Símbolo de advertencia "!"
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(cx, cy - 10, cx, cy - 6);
        g2d.fillOval(cx - 1, cy - 5, 2, 2);
        
        // Ojos brillantes
        g2d.setColor(new Color(255, 255, 200));
        g2d.fillOval(cx - 4, cy - 11, 3, 3);
        g2d.fillOval(cx + 1, cy - 11, 3, 3);
        
        g2d.dispose();
        return image;
    }
    
    private static BufferedImage generateExplosiveDeathFrame(int frameIndex) {
        int size = EXPLOSIVE_WIDTH + 40; // Más grande para la explosión
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = size / 2;
        int cy = size / 2;
        
        if (frameIndex < 3) {
            // Fase de hinchazón
            int swell = frameIndex * 3;
            
            g2d.setColor(new Color(255, 200, 100, 100));
            g2d.fillOval(cx - 12 - swell, cy - 10 - swell, 24 + swell*2, 24 + swell*2);
            
            g2d.setColor(EXPLOSIVE_BODY);
            g2d.fillOval(cx - 10 - swell/2, cy - 8 - swell/2, 20 + swell, 20 + swell);
            
            // Grietas más intensas
            g2d.setColor(new Color(255, 255, 200));
            g2d.setStroke(new BasicStroke(2 + frameIndex));
            for (int i = 0; i < 4 + frameIndex; i++) {
                double angle = i * Math.PI / 2 + frameIndex * 0.3;
                int len = 5 + frameIndex * 2;
                g2d.drawLine(cx, cy, 
                             cx + (int)(Math.cos(angle) * len),
                             cy + (int)(Math.sin(angle) * len));
            }
        } else {
            // Fase de explosión
            int explosionFrame = frameIndex - 3;
            float progress = explosionFrame / 4f;
            
            // Onda expansiva exterior
            int waveSize = (int)(20 + progress * 40);
            int alpha = (int)(200 - progress * 180);
            g2d.setColor(new Color(255, 150, 50, Math.max(0, alpha)));
            g2d.fillOval(cx - waveSize, cy - waveSize, waveSize * 2, waveSize * 2);
            
            // Centro de la explosión
            int coreSize = (int)(15 + progress * 15);
            g2d.setColor(new Color(255, 255, 200, Math.max(0, alpha + 30)));
            g2d.fillOval(cx - coreSize/2, cy - coreSize/2, coreSize, coreSize);
            
            // Partículas/escombros
            g2d.setColor(EXPLOSIVE_BODY_DARK);
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4 + progress;
                int dist = (int)(10 + progress * 25);
                int px = cx + (int)(Math.cos(angle) * dist);
                int py = cy + (int)(Math.sin(angle) * dist);
                int pSize = 4 - explosionFrame/2;
                if (pSize > 0) {
                    g2d.fillOval(px, py, pSize, pSize);
                }
            }
        }
        
        g2d.dispose();
        return image;
    }
}
