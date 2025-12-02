package com.atropellalo.game.effect;

import com.atropellalo.game.config.GameConfig;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Efecto visual mejorado del lanzallamas.
 * Crea un cono de fuego animado con partículas, ondas de calor y chispas.
 */
public class FlamethrowerEffect implements VisualEffect {
    
    private float x;
    private float y;
    private float angle;
    private float coneAngle;
    private float range;
    private boolean active;
    private final Random random;
    private final List<FlameParticle> particles;
    private final List<Ember> embers;
    private float animationTimer;
    
    // Colores del fuego (de interior a exterior)
    private static final Color[] FLAME_COLORS = {
        new Color(255, 255, 220, 220),  // Núcleo blanco-amarillo
        new Color(255, 220, 100, 200),  // Amarillo brillante
        new Color(255, 150, 50, 180),   // Naranja
        new Color(255, 80, 20, 150),    // Rojo-naranja
        new Color(200, 50, 10, 100)     // Rojo oscuro
    };
    
    private static final Color HEAT_DISTORTION_COLOR = new Color(255, 200, 100, 30);
    
    /**
     * Crea un nuevo efecto de lanzallamas.
     */
    public FlamethrowerEffect() {
        this.x = 0;
        this.y = 0;
        this.angle = 0;
        this.coneAngle = GameConfig.FLAMETHROWER_CONE_ANGLE;
        this.range = GameConfig.FLAMETHROWER_RANGE;
        this.active = false;
        this.random = new Random();
        this.particles = new ArrayList<>();
        this.embers = new ArrayList<>();
        this.animationTimer = 0;
    }
    
    /**
     * Actualiza la posición y dirección del efecto.
     * @param x Posición X del origen
     * @param y Posición Y del origen
     * @param angle Ángulo en radianes
     * @param coneAngle Ángulo del cono en grados
     * @param range Alcance del lanzallamas
     * @param active Si está activo
     */
    public void setParameters(float x, float y, float angle, float coneAngle, float range, boolean active) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.coneAngle = coneAngle;
        this.range = range;
        this.active = active;
    }
    
    @Override
    public void update(float deltaTime) {
        animationTimer += deltaTime;
        
        // Actualizar partículas existentes
        particles.removeIf(p -> {
            p.update(deltaTime);
            return !p.isAlive();
        });
        
        // Actualizar brasas
        embers.removeIf(e -> {
            e.update(deltaTime);
            return !e.isAlive();
        });
        
        // Generar nuevas partículas si está activo
        if (active) {
            generateParticles(deltaTime);
            generateEmbers(deltaTime);
        }
    }
    
    /**
     * Genera partículas de fuego.
     */
    private void generateParticles(float deltaTime) {
        int particlesToSpawn = (int)(GameConfig.VFX_FLAME_PARTICLES_PER_SECOND * deltaTime);
        particlesToSpawn = Math.max(1, particlesToSpawn);
        
        float halfCone = (float) Math.toRadians(coneAngle / 2);
        
        for (int i = 0; i < particlesToSpawn; i++) {
            // Ángulo aleatorio dentro del cono
            float particleAngle = angle + (random.nextFloat() - 0.5f) * 2 * halfCone;
            
            // Velocidad variable
            float speed = 150 + random.nextFloat() * 100;
            
            // Tamaño basado en posición (más grandes en el centro)
            float distFromCenter = Math.abs(particleAngle - angle) / halfCone;
            float size = 15 * (1 - distFromCenter * 0.5f) + random.nextFloat() * 10;
            
            // Vida más corta para partículas en los bordes
            float life = (0.3f + random.nextFloat() * 0.2f) * (1 - distFromCenter * 0.3f);
            
            // Color aleatorio del espectro de fuego
            int colorIndex = random.nextInt(FLAME_COLORS.length);
            
            particles.add(new FlameParticle(x, y, particleAngle, speed, size, life, colorIndex));
        }
    }
    
    /**
     * Genera brasas/chispas flotantes.
     */
    private void generateEmbers(float deltaTime) {
        if (random.nextFloat() < GameConfig.VFX_FLAME_EMBER_CHANCE * deltaTime * 60) {
            float halfCone = (float) Math.toRadians(coneAngle / 2);
            float emberAngle = angle + (random.nextFloat() - 0.5f) * 2 * halfCone;
            float speed = 50 + random.nextFloat() * 100;
            float size = 2 + random.nextFloat() * 3;
            float life = 0.5f + random.nextFloat() * 0.5f;
            
            embers.add(new Ember(x, y, emberAngle, speed, size, life));
        }
    }
    
    @Override
    public void render(Graphics2D g2d) {
        if (!active && particles.isEmpty() && embers.isEmpty()) {
            return;
        }
        
        // Guardar estado
        AffineTransform oldTransform = g2d.getTransform();
        java.awt.Composite oldComposite = g2d.getComposite();
        
        // Orden de renderizado: primero partículas VFX (debajo), luego cono base (encima)
        
        // Renderizar partículas VFX primero (quedan debajo)
        for (FlameParticle p : particles) {
            p.render(g2d);
        }
        
        // Renderizar brasas
        for (Ember e : embers) {
            e.render(g2d);
        }
        
        // Renderizar cono base encima de las partículas
        if (active) {
            renderBaseCone(g2d);
            renderHeatDistortion(g2d);
        }
        
        // Restaurar estado
        g2d.setTransform(oldTransform);
        g2d.setComposite(oldComposite);
    }
    
    /**
     * Renderiza el cono base del fuego con gradiente.
     */
    private void renderBaseCone(Graphics2D g2d) {
        float halfCone = coneAngle / 2;
        float startAngle = (float) Math.toDegrees(-angle) - halfCone;
        
        // Múltiples capas con diferentes tamaños y colores
        for (int layer = FLAME_COLORS.length - 1; layer >= 0; layer--) {
            float layerScale = 0.4f + (layer / (float)FLAME_COLORS.length) * 0.6f;
            float layerRange = range * layerScale;
            float layerCone = coneAngle * (0.7f + layerScale * 0.3f);
            float layerStart = (float) Math.toDegrees(-angle) - layerCone / 2;
            
            // Pulso de intensidad
            float pulse = 1f + (float)Math.sin(animationTimer * 20 + layer) * 0.1f;
            
            Color baseColor = FLAME_COLORS[layer];
            int alpha = (int)(baseColor.getAlpha() * pulse);
            alpha = Math.min(255, alpha);
            
            g2d.setColor(new Color(
                baseColor.getRed(),
                baseColor.getGreen(),
                baseColor.getBlue(),
                alpha
            ));
            
            Arc2D arc = new Arc2D.Float(
                x - layerRange,
                y - layerRange,
                layerRange * 2,
                layerRange * 2,
                layerStart,
                layerCone,
                Arc2D.PIE
            );
            g2d.fill(arc);
        }
    }
    
    /**
     * Renderiza el efecto de distorsión de calor.
     */
    private void renderHeatDistortion(Graphics2D g2d) {
        float halfCone = coneAngle / 2;
        float distortionRange = range * 1.1f;
        float startAngle = (float) Math.toDegrees(-angle) - halfCone - 5;
        
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2d.setColor(HEAT_DISTORTION_COLOR);
        
        // Ondas de calor animadas
        for (int wave = 0; wave < 3; wave++) {
            float waveOffset = (animationTimer * 2 + wave * 0.3f) % 1f;
            float waveRange = distortionRange * (0.5f + waveOffset * 0.5f);
            float waveAlpha = (1f - waveOffset) * 0.3f;
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, waveAlpha));
            
            Arc2D arc = new Arc2D.Float(
                x - waveRange,
                y - waveRange,
                waveRange * 2,
                waveRange * 2,
                startAngle,
                coneAngle + 10,
                Arc2D.PIE
            );
            g2d.draw(arc);
        }
        
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
    
    @Override
    public boolean isAlive() {
        return active || !particles.isEmpty() || !embers.isEmpty();
    }
    
    @Override
    public float getX() {
        return x;
    }
    
    @Override
    public float getY() {
        return y;
    }
    
    public boolean isActive() {
        return active;
    }
    
    /**
     * Partícula de fuego.
     */
    private class FlameParticle {
        float x, y;
        float vx, vy;
        float size;
        float life;
        float maxLife;
        int colorIndex;
        
        FlameParticle(float x, float y, float angle, float speed, float size, float life, int colorIndex) {
            this.x = x;
            this.y = y;
            this.vx = (float) Math.cos(angle) * speed;
            this.vy = (float) Math.sin(angle) * speed;
            this.size = size;
            this.life = life;
            this.maxLife = life;
            this.colorIndex = colorIndex;
        }
        
        void update(float deltaTime) {
            x += vx * deltaTime;
            y += vy * deltaTime;
            
            // Desaceleración y deriva hacia arriba (efecto de calor)
            vx *= 0.96f;
            vy *= 0.96f;
            vy -= 20 * deltaTime; // Subida por calor
            
            life -= deltaTime;
        }
        
        boolean isAlive() {
            return life > 0;
        }
        
        void render(Graphics2D g2d) {
            if (!isAlive()) return;
            
            float lifeRatio = life / maxLife;
            
            // Transición de color (del colorIndex al siguiente)
            int nextIndex = Math.min(colorIndex + 1, FLAME_COLORS.length - 1);
            float transition = 1f - lifeRatio;
            
            Color c1 = FLAME_COLORS[colorIndex];
            Color c2 = FLAME_COLORS[nextIndex];
            
            int r = (int)(c1.getRed() * (1 - transition) + c2.getRed() * transition);
            int g = (int)(c1.getGreen() * (1 - transition) + c2.getGreen() * transition);
            int b = (int)(c1.getBlue() * (1 - transition) + c2.getBlue() * transition);
            int a = (int)(c1.getAlpha() * lifeRatio);
            
            g2d.setColor(new Color(r, g, b, a));
            
            // Tamaño que varía con el tiempo
            float currentSize = size * (0.5f + lifeRatio * 0.5f);
            
            int s = (int) currentSize;
            g2d.fillOval((int)x - s/2, (int)y - s/2, s, s);
        }
    }
    
    /**
     * Brasa/chispa flotante.
     */
    private static class Ember {
        float x, y;
        float vx, vy;
        float size;
        float life;
        float maxLife;
        float twinkle;
        
        Ember(float x, float y, float angle, float speed, float size, float life) {
            this.x = x;
            this.y = y;
            this.vx = (float) Math.cos(angle) * speed;
            this.vy = (float) Math.sin(angle) * speed;
            this.size = size;
            this.life = life;
            this.maxLife = life;
            this.twinkle = (float)(ThreadLocalRandom.current().nextDouble() * Math.PI * 2);
        }
        
        void update(float deltaTime) {
            x += vx * deltaTime;
            y += vy * deltaTime;
            
            // Movimiento errático
            vx += (ThreadLocalRandom.current().nextDouble() - 0.5f) * 50 * deltaTime;
            vy -= 30 * deltaTime; // Sube por el calor
            
            vx *= 0.98f;
            vy *= 0.98f;
            
            life -= deltaTime;
            twinkle += deltaTime * 15;
        }
        
        boolean isAlive() {
            return life > 0;
        }
        
        void render(Graphics2D g2d) {
            if (!isAlive()) return;
            
            float lifeRatio = life / maxLife;
            float brightness = 0.5f + (float)Math.sin(twinkle) * 0.5f;
            
            int alpha = (int)(lifeRatio * brightness * 255);
            g2d.setColor(new Color(255, 200, 100, alpha));
            
            int s = (int)(size * lifeRatio);
            g2d.fillOval((int)x - s/2, (int)y - s/2, Math.max(1, s), Math.max(1, s));
        }
    }
}
