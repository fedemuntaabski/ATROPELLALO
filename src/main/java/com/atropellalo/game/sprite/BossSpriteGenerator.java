package com.atropellalo.game.sprite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Genera sprites procedurales para los jefes del juego.
 * Vista desde arriba con animaciones de idle, movimiento, ataque y muerte.
 */
public class BossSpriteGenerator {
    
    // ==================== BRUISER BOSS ====================
    public static final int BRUISER_WIDTH = 64;
    public static final int BRUISER_HEIGHT = 64;
    
    private static final Color BRUISER_BODY = new Color(80, 60, 50);        // Marrón grisáceo
    private static final Color BRUISER_BODY_DARK = new Color(50, 40, 35);   // Marrón oscuro
    private static final Color BRUISER_MUSCLE = new Color(100, 70, 60);     // Músculo
    private static final Color BRUISER_EYES = new Color(255, 50, 30);       // Rojo intenso
    private static final Color BRUISER_SCAR = new Color(150, 80, 80);       // Cicatriz
    
    // ==================== INFECTOR BOSS ====================
    public static final int INFECTOR_WIDTH = 56;
    public static final int INFECTOR_HEIGHT = 56;
    
    private static final Color INFECTOR_BODY = new Color(50, 120, 70);      // Verde tóxico
    private static final Color INFECTOR_BODY_DARK = new Color(30, 80, 50);  // Verde oscuro
    private static final Color INFECTOR_SLIME = new Color(150, 220, 100);   // Baba verde brillante
    private static final Color INFECTOR_EYES = new Color(255, 255, 100);    // Amarillo tóxico
    private static final Color INFECTOR_PUSTULE = new Color(200, 180, 50);  // Pústulas
    
    // Colores comunes
    private static final Color BLOOD_COLOR = new Color(100, 20, 20);
    
    // ==================== BRUISER BOSS ANIMATIONS ====================
    
    /**
     * Genera todas las animaciones del Bruiser Boss.
     */
    public static Map<AnimationState, Animation> generateBruiserAnimations() {
        Map<AnimationState, Animation> animations = new HashMap<>();
        
        animations.put(AnimationState.IDLE, generateBruiserIdleAnimation());
        animations.put(AnimationState.MOVING, generateBruiserMovingAnimation());
        animations.put(AnimationState.ATTACK, generateBruiserAttackAnimation());
        animations.put(AnimationState.DEATH, generateBruiserDeathAnimation());
        
        return animations;
    }
    
    private static Animation generateBruiserIdleAnimation() {
        Animation anim = new Animation(0.4f, true);
        
        for (int i = 0; i < 2; i++) {
            anim.addFrame(generateBruiserFrame(i, BruiserState.IDLE));
        }
        
        return anim;
    }
    
    private static Animation generateBruiserMovingAnimation() {
        Animation anim = new Animation(0.15f, true);
        
        for (int i = 0; i < 6; i++) {
            anim.addFrame(generateBruiserFrame(i, BruiserState.MOVING));
        }
        
        return anim;
    }
    
    private static Animation generateBruiserAttackAnimation() {
        Animation anim = new Animation(0.1f, false);
        
        for (int i = 0; i < 6; i++) {
            anim.addFrame(generateBruiserFrame(i, BruiserState.ATTACK));
        }
        
        return anim;
    }
    
    private static Animation generateBruiserDeathAnimation() {
        Animation anim = new Animation(0.2f, false);
        
        for (int i = 0; i < 8; i++) {
            anim.addFrame(generateBruiserDeathFrame(i));
        }
        
        return anim;
    }
    
    private enum BruiserState { IDLE, MOVING, ATTACK }
    
    private static BufferedImage generateBruiserFrame(int frameIndex, BruiserState state) {
        BufferedImage image = new BufferedImage(BRUISER_WIDTH, BRUISER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = BRUISER_WIDTH / 2;
        int cy = BRUISER_HEIGHT / 2;
        
        int breathe = (frameIndex % 2 == 0) ? 0 : 1;
        int legOffset = 0, armOffset = 0, bodyShift = 0;
        
        switch (state) {
            case MOVING:
                legOffset = (frameIndex % 3 - 1) * 3;
                armOffset = ((frameIndex + 1) % 3 - 1) * 4;
                break;
            case ATTACK:
                // Movimiento de golpe
                if (frameIndex < 3) {
                    armOffset = -frameIndex * 4; // Preparación
                } else {
                    armOffset = 12 - (frameIndex - 3) * 4; // Golpe
                    bodyShift = (frameIndex == 3 || frameIndex == 4) ? 3 : 0;
                }
                break;
            default:
                break;
        }
        
        // Sombra grande
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.fillOval(cx - 26, cy - 22, 52, 48);
        
        // Piernas masivas
        g2d.setColor(BRUISER_BODY_DARK);
        g2d.fillOval(cx - 18 + legOffset, cy + 10, 14, 20);
        g2d.fillOval(cx + 4 - legOffset, cy + 10, 14, 20);
        
        // Brazos enormes (musculosos)
        g2d.setColor(BRUISER_MUSCLE);
        // Brazo izquierdo
        g2d.fillOval(cx - 30 + armOffset, cy - 12 + bodyShift, 16, 28);
        g2d.setColor(BRUISER_BODY);
        g2d.fillOval(cx - 32 + armOffset, cy + 8 + bodyShift, 12, 14); // Puño
        
        // Brazo derecho
        g2d.setColor(BRUISER_MUSCLE);
        g2d.fillOval(cx + 14 - armOffset, cy - 12 + bodyShift, 16, 28);
        g2d.setColor(BRUISER_BODY);
        g2d.fillOval(cx + 20 - armOffset, cy + 8 + bodyShift, 12, 14); // Puño
        
        // Cuerpo principal (torso masivo)
        g2d.setColor(BRUISER_BODY);
        g2d.fillOval(cx - 20, cy - 16 + breathe + bodyShift, 40, 36);
        
        // Detalles del torso
        g2d.setColor(BRUISER_MUSCLE);
        g2d.fillOval(cx - 14, cy - 10 + breathe + bodyShift, 12, 14);
        g2d.fillOval(cx + 2, cy - 10 + breathe + bodyShift, 12, 14);
        
        // Cicatrices del cuerpo
        g2d.setColor(BRUISER_SCAR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(cx - 8, cy - 4 + bodyShift, cx + 4, cy + 8 + bodyShift);
        g2d.drawLine(cx + 10, cy - 2 + bodyShift, cx + 6, cy + 6 + bodyShift);
        
        // Cabeza pequeña en comparación
        g2d.setColor(BRUISER_BODY);
        g2d.fillOval(cx - 10, cy - 28 + breathe + bodyShift, 20, 18);
        
        // Mandíbula prominente
        g2d.setColor(BRUISER_BODY_DARK);
        g2d.fillOval(cx - 8, cy - 18 + breathe + bodyShift, 16, 10);
        
        // Ojos rojos furiosos
        g2d.setColor(BRUISER_EYES);
        g2d.fillOval(cx - 7, cy - 25 + breathe + bodyShift, 6, 5);
        g2d.fillOval(cx + 1, cy - 25 + breathe + bodyShift, 6, 5);
        
        // Pupilas
        g2d.setColor(new Color(100, 20, 10));
        g2d.fillOval(cx - 5, cy - 24 + breathe + bodyShift, 3, 3);
        g2d.fillOval(cx + 3, cy - 24 + breathe + bodyShift, 3, 3);
        
        // Cicatriz facial
        g2d.setColor(BRUISER_SCAR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(cx - 8, cy - 26 + bodyShift, cx - 2, cy - 16 + bodyShift);
        
        // Pinchos/huesos salientes de la espalda
        g2d.setColor(new Color(200, 190, 170));
        int[] spikeX = {cx - 6, cx, cx + 6};
        for (int sx : spikeX) {
            g2d.fillOval(sx - 2, cy - 6 + bodyShift, 4, 8);
        }
        
        g2d.dispose();
        return image;
    }
    
    private static BufferedImage generateBruiserDeathFrame(int frameIndex) {
        BufferedImage image = new BufferedImage(BRUISER_WIDTH + 20, BRUISER_HEIGHT + 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = (BRUISER_WIDTH + 20) / 2;
        int cy = (BRUISER_HEIGHT + 20) / 2;
        float progress = frameIndex / 7f;
        
        // Charco de sangre masivo
        g2d.setColor(BLOOD_COLOR);
        int bloodSize = (int)(20 + progress * 45);
        g2d.fillOval(cx - bloodSize/2, cy - bloodSize/2 + 8, bloodSize, bloodSize);
        
        int collapse = (int)(progress * 12);
        int spread = (int)(progress * 8);
        
        // Piernas separándose
        g2d.setColor(BRUISER_BODY_DARK);
        g2d.fillOval(cx - 20 - spread, cy + 8, 12, 16 - collapse/2);
        g2d.fillOval(cx + 8 + spread, cy + 8, 12, 16 - collapse/2);
        
        // Brazos cayendo
        g2d.setColor(BRUISER_MUSCLE);
        g2d.fillOval(cx - 32 - spread, cy - 8 + collapse, 14, 24 - collapse);
        g2d.fillOval(cx + 18 + spread, cy - 8 + collapse, 14, 24 - collapse);
        
        // Cuerpo colapsando
        g2d.setColor(BRUISER_BODY);
        g2d.fillOval(cx - 20 - spread/2, cy - 12 + collapse, 40 + spread, 32 - collapse);
        
        // Cabeza cayendo
        int headDrop = (int)(progress * 15);
        g2d.fillOval(cx - 8, cy - 24 + headDrop, 16, 14 - collapse/2);
        
        // Ojos apagándose
        if (frameIndex < 5) {
            int alpha = 255 - frameIndex * 50;
            g2d.setColor(new Color(255, 50, 30, Math.max(0, alpha)));
            g2d.fillOval(cx - 5, cy - 21 + headDrop, 5, 4);
            g2d.fillOval(cx + 1, cy - 21 + headDrop, 5, 4);
        }
        
        // Partículas/escombros
        if (frameIndex > 2) {
            g2d.setColor(new Color(80, 60, 50, 150));
            for (int i = 0; i < 6; i++) {
                double angle = i * Math.PI / 3;
                int dist = (int)(15 + progress * 20);
                int px = cx + (int)(Math.cos(angle) * dist);
                int py = cy + (int)(Math.sin(angle) * dist);
                g2d.fillOval(px, py, 4, 4);
            }
        }
        
        g2d.dispose();
        return image;
    }
    
    // ==================== INFECTOR BOSS ANIMATIONS ====================
    
    /**
     * Genera todas las animaciones del Infector Boss.
     */
    public static Map<AnimationState, Animation> generateInfectorAnimations() {
        Map<AnimationState, Animation> animations = new HashMap<>();
        
        animations.put(AnimationState.IDLE, generateInfectorIdleAnimation());
        animations.put(AnimationState.MOVING, generateInfectorMovingAnimation());
        animations.put(AnimationState.ATTACK, generateInfectorAttackAnimation());
        animations.put(AnimationState.DEATH, generateInfectorDeathAnimation());
        
        return animations;
    }
    
    private static Animation generateInfectorIdleAnimation() {
        Animation anim = new Animation(0.25f, true);
        
        // Burbujas/pulsaciones tóxicas
        for (int i = 0; i < 4; i++) {
            anim.addFrame(generateInfectorFrame(i, InfectorState.IDLE));
        }
        
        return anim;
    }
    
    private static Animation generateInfectorMovingAnimation() {
        Animation anim = new Animation(0.12f, true);
        
        for (int i = 0; i < 6; i++) {
            anim.addFrame(generateInfectorFrame(i, InfectorState.MOVING));
        }
        
        return anim;
    }
    
    private static Animation generateInfectorAttackAnimation() {
        Animation anim = new Animation(0.08f, false);
        
        // Escupir ácido/infectar
        for (int i = 0; i < 8; i++) {
            anim.addFrame(generateInfectorFrame(i, InfectorState.ATTACK));
        }
        
        return anim;
    }
    
    private static Animation generateInfectorDeathAnimation() {
        Animation anim = new Animation(0.15f, false);
        
        for (int i = 0; i < 10; i++) {
            anim.addFrame(generateInfectorDeathFrame(i));
        }
        
        return anim;
    }
    
    private enum InfectorState { IDLE, MOVING, ATTACK }
    
    private static BufferedImage generateInfectorFrame(int frameIndex, InfectorState state) {
        BufferedImage image = new BufferedImage(INFECTOR_WIDTH, INFECTOR_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = INFECTOR_WIDTH / 2;
        int cy = INFECTOR_HEIGHT / 2;
        
        int pulse = frameIndex % 4;
        int bubblePhase = frameIndex % 3;
        int tentacleWave = 0, bodyExpand = 0;
        boolean spitting = false;
        
        switch (state) {
            case MOVING:
                tentacleWave = (frameIndex % 6) - 3;
                break;
            case ATTACK:
                if (frameIndex < 4) {
                    bodyExpand = frameIndex * 2; // Inflar
                } else {
                    bodyExpand = 8 - (frameIndex - 4) * 2; // Desinflar
                    spitting = frameIndex == 4 || frameIndex == 5;
                }
                break;
            default:
                break;
        }
        
        // Aura tóxica
        int auraAlpha = 20 + pulse * 10;
        g2d.setColor(new Color(150, 220, 100, auraAlpha));
        g2d.fillOval(cx - 26 - pulse, cy - 24 - pulse, 52 + pulse*2, 52 + pulse*2);
        
        // Sombra con baba
        g2d.setColor(new Color(30, 80, 30, 80));
        g2d.fillOval(cx - 22, cy - 18, 44, 42);
        
        // Tentáculos/apéndices (en lugar de piernas)
        g2d.setColor(INFECTOR_BODY_DARK);
        for (int i = 0; i < 6; i++) {
            double angle = i * Math.PI / 3 + Math.PI / 2;
            int dist = 18 + (i % 2) * 4;
            int wave = (int)(Math.sin(frameIndex + i) * 3) + tentacleWave;
            int tx = cx + (int)(Math.cos(angle) * dist) + wave;
            int ty = cy + (int)(Math.sin(angle) * dist);
            g2d.fillOval(tx - 4, ty - 3, 8, 12);
        }
        
        // Cuerpo principal (masa informe)
        g2d.setColor(INFECTOR_BODY);
        g2d.fillOval(cx - 18 - bodyExpand/2, cy - 16 - bodyExpand/2, 
                     36 + bodyExpand, 34 + bodyExpand);
        
        // Textura babosa
        g2d.setColor(INFECTOR_SLIME);
        for (int i = 0; i < 4; i++) {
            int bx = cx - 10 + (i % 2) * 16 + (int)(Math.sin(frameIndex + i) * 3);
            int by = cy - 6 + (i / 2) * 12;
            g2d.fillOval(bx, by, 6 + bubblePhase, 5 + bubblePhase);
        }
        
        // Pústulas
        g2d.setColor(INFECTOR_PUSTULE);
        g2d.fillOval(cx - 12, cy - 10, 8, 7);
        g2d.fillOval(cx + 6, cy - 4, 7, 6);
        g2d.fillOval(cx - 4, cy + 4, 6, 5);
        
        // Cabeza/boca
        g2d.setColor(INFECTOR_BODY);
        g2d.fillOval(cx - 12, cy - 26, 24, 18);
        
        // Boca abierta (para escupir)
        if (spitting) {
            g2d.setColor(new Color(80, 40, 50));
            g2d.fillOval(cx - 6, cy - 18, 12, 10);
            
            // Proyectil de baba
            g2d.setColor(INFECTOR_SLIME);
            g2d.fillOval(cx - 3, cy - 30, 6, 8);
            g2d.fillOval(cx - 2, cy - 38, 4, 6);
        } else {
            // Boca cerrada
            g2d.setColor(INFECTOR_BODY_DARK);
            g2d.fillOval(cx - 5, cy - 16, 10, 5);
        }
        
        // Ojos múltiples (mutación)
        g2d.setColor(INFECTOR_EYES);
        // Ojos principales
        g2d.fillOval(cx - 9, cy - 24, 7, 6);
        g2d.fillOval(cx + 2, cy - 24, 7, 6);
        // Ojo extra
        g2d.fillOval(cx - 3, cy - 28, 5, 4);
        
        // Pupilas verticales
        g2d.setColor(new Color(50, 80, 30));
        g2d.fillOval(cx - 7, cy - 23, 2, 4);
        g2d.fillOval(cx + 5, cy - 23, 2, 4);
        g2d.fillOval(cx - 1, cy - 27, 2, 3);
        
        // Burbujas flotantes de gas
        g2d.setColor(new Color(150, 220, 100, 100 + pulse * 30));
        g2d.fillOval(cx - 20 + frameIndex * 2, cy - 30 - frameIndex, 4, 4);
        g2d.fillOval(cx + 16 - frameIndex, cy - 28 - frameIndex * 2, 3, 3);
        
        g2d.dispose();
        return image;
    }
    
    private static BufferedImage generateInfectorDeathFrame(int frameIndex) {
        int size = INFECTOR_WIDTH + 40;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = size / 2;
        int cy = size / 2;
        float progress = frameIndex / 9f;
        
        if (frameIndex < 4) {
            // Fase de hinchazón/colapso tóxico
            int swell = frameIndex * 4;
            
            // Aura tóxica expandiéndose
            g2d.setColor(new Color(150, 220, 100, 80 - frameIndex * 10));
            g2d.fillOval(cx - 25 - swell, cy - 22 - swell, 50 + swell*2, 48 + swell*2);
            
            // Cuerpo hinchándose
            g2d.setColor(INFECTOR_BODY);
            g2d.fillOval(cx - 18 - swell/2, cy - 14 - swell/2, 36 + swell, 32 + swell);
            
            // Pústulas reventando
            g2d.setColor(INFECTOR_SLIME);
            for (int i = 0; i < 3 + frameIndex; i++) {
                double angle = i * Math.PI * 2 / (3 + frameIndex);
                int dist = 12 + frameIndex * 3;
                int px = cx + (int)(Math.cos(angle) * dist);
                int py = cy + (int)(Math.sin(angle) * dist);
                g2d.fillOval(px - 3, py - 3, 6 + frameIndex, 6 + frameIndex);
            }
            
            // Ojos aún visibles pero deformándose
            g2d.setColor(new Color(255, 255, 100, 200 - frameIndex * 40));
            g2d.fillOval(cx - 8, cy - 20, 6 + frameIndex, 5 + frameIndex);
            g2d.fillOval(cx + 3, cy - 20, 6 + frameIndex, 5 + frameIndex);
        } else {
            // Fase de explosión tóxica
            int explodeFrame = frameIndex - 4;
            
            // Charco de líquido tóxico expandiéndose
            int poolSize = (int)(30 + explodeFrame * 10);
            g2d.setColor(new Color(100, 180, 80, 200 - explodeFrame * 25));
            g2d.fillOval(cx - poolSize/2, cy - poolSize/2 + 5, poolSize, poolSize);
            
            // Manchas más oscuras
            g2d.setColor(new Color(50, 120, 60, 150 - explodeFrame * 20));
            g2d.fillOval(cx - poolSize/3, cy - poolSize/4 + 5, poolSize/2, poolSize/3);
            
            // Burbujas explotando
            g2d.setColor(new Color(150, 220, 100, 180 - explodeFrame * 30));
            for (int i = 0; i < 10; i++) {
                double angle = i * Math.PI / 5 + explodeFrame * 0.5;
                int dist = 15 + explodeFrame * 8;
                int bx = cx + (int)(Math.cos(angle) * dist);
                int by = cy + (int)(Math.sin(angle) * dist);
                int bSize = 8 - explodeFrame;
                if (bSize > 0) {
                    g2d.fillOval(bx - bSize/2, by - bSize/2, bSize, bSize);
                }
            }
            
            // Restos del cuerpo derritiéndose
            if (explodeFrame < 4) {
                g2d.setColor(new Color(50, 120, 70, 150 - explodeFrame * 35));
                g2d.fillOval(cx - 15 + explodeFrame * 2, cy - 10 + explodeFrame * 3, 
                            30 - explodeFrame * 4, 24 - explodeFrame * 4);
            }
            
            // Nube de gas tóxico
            g2d.setColor(new Color(120, 200, 80, 60 - explodeFrame * 10));
            int cloudSize = 20 + explodeFrame * 8;
            g2d.fillOval(cx - cloudSize/2, cy - cloudSize/2 - 10, cloudSize, cloudSize);
        }
        
        g2d.dispose();
        return image;
    }
}
