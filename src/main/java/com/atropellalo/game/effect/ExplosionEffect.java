package com.atropellalo.game.effect;

import com.atropellalo.game.config.GameConfig;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Efecto visual de explosión para zombies explosivos.
 * Simula una explosión con ondas de choque, llamas y partículas.
 */
public class ExplosionEffect implements VisualEffect {
    
    private final float x;
    private final float y;
    private final float maxRadius;
    private final float duration;
    private float timer;
    private final Random random;
    private final List<Particle> particles;
    private final List<Spark> sparks;
    
    // Colores de la explosión
    private static final Color CORE_COLOR = new Color(255, 255, 200);
    private static final Color INNER_COLOR = new Color(255, 200, 50);
    private static final Color MIDDLE_COLOR = new Color(255, 100, 0);
    private static final Color OUTER_COLOR = new Color(200, 50, 0);
    private static final Color SMOKE_COLOR = new Color(80, 80, 80);
    
    /**
     * Crea un nuevo efecto de explosión.
     * @param x Posición X central
     * @param y Posición Y central
     * @param radius Radio máximo de la explosión
     */
    public ExplosionEffect(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.maxRadius = radius;
        this.duration = GameConfig.VFX_EXPLOSION_DURATION;
        this.timer = 0;
        this.random = new Random();
        this.particles = new ArrayList<>();
        this.sparks = new ArrayList<>();
        
        initializeParticles();
        initializeSparks();
    }
    
    /**
     * Inicializa las partículas de escombros.
     */
    private void initializeParticles() {
        int count = GameConfig.VFX_EXPLOSION_PARTICLE_COUNT;
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * (float)(Math.PI * 2);
            float speed = 100 + random.nextFloat() * 200;
            float size = 4 + random.nextFloat() * 8;
            float life = 0.3f + random.nextFloat() * 0.4f;
            
            Color color;
            float colorChoice = random.nextFloat();
            if (colorChoice < 0.3f) {
                color = INNER_COLOR;
            } else if (colorChoice < 0.6f) {
                color = MIDDLE_COLOR;
            } else {
                color = OUTER_COLOR;
            }
            
            particles.add(new Particle(x, y, angle, speed, size, life, color));
        }
    }
    
    /**
     * Inicializa las chispas de la explosión.
     */
    private void initializeSparks() {
        int count = GameConfig.VFX_EXPLOSION_SPARK_COUNT;
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * (float)(Math.PI * 2);
            float speed = 200 + random.nextFloat() * 300;
            float length = 10 + random.nextFloat() * 20;
            float life = 0.2f + random.nextFloat() * 0.3f;
            
            sparks.add(new Spark(x, y, angle, speed, length, life));
        }
    }
    
    @Override
    public void update(float deltaTime) {
        timer += deltaTime;
        
        // Actualizar partículas
        for (Particle p : particles) {
            p.update(deltaTime);
        }
        
        // Actualizar chispas
        for (Spark s : sparks) {
            s.update(deltaTime);
        }
    }
    
    @Override
    public void render(Graphics2D g2d) {
        float progress = timer / duration;
        if (progress > 1) return;
        
        // Guardar estado
        java.awt.Composite oldComposite = g2d.getComposite();
        
        // Calcular radio actual (crece rápido al principio)
        float easedProgress = 1f - (1f - progress) * (1f - progress);
        float currentRadius = maxRadius * easedProgress;
        
        // Alpha general que disminuye con el tiempo
        float alpha = 1f - progress;
        
        // Renderizar ondas de choque
        renderShockwaves(g2d, currentRadius, alpha, progress);
        
        // Renderizar resplandor central con gradiente
        renderCoreGlow(g2d, currentRadius, alpha);
        
        // Renderizar chispas
        for (Spark s : sparks) {
            s.render(g2d);
        }
        
        // Renderizar partículas
        for (Particle p : particles) {
            p.render(g2d);
        }
        
        // Renderizar humo (aparece después del flash inicial)
        if (progress > 0.3f) {
            renderSmoke(g2d, currentRadius, progress);
        }
        
        // Restaurar estado
        g2d.setComposite(oldComposite);
    }
    
    /**
     * Renderiza las ondas de choque expansivas.
     */
    private void renderShockwaves(Graphics2D g2d, float radius, float alpha, float progress) {
        for (int ring = 0; ring < 3; ring++) {
            float ringDelay = ring * 0.1f;
            float ringProgress = Math.max(0, progress - ringDelay);
            if (ringProgress <= 0) continue;
            
            float ringRadius = maxRadius * (0.5f + ringProgress * 0.8f);
            float ringAlpha = Math.max(0, alpha - ring * 0.2f) * (1f - ringProgress);
            
            if (ringAlpha > 0) {
                g2d.setColor(new Color(255, 200, 100, (int)(ringAlpha * 150)));
                g2d.setStroke(new BasicStroke(3 - ring));
                int r = (int) ringRadius;
                g2d.drawOval((int)x - r, (int)y - r, r * 2, r * 2);
            }
        }
    }
    
    /**
     * Renderiza el resplandor central con gradiente radial.
     */
    private void renderCoreGlow(Graphics2D g2d, float radius, float alpha) {
        if (radius < 1) return;
        
        Point2D center = new Point2D.Float(x, y);
        float[] dist = {0.0f, 0.3f, 0.6f, 1.0f};
        
        int coreAlpha = (int)(alpha * 255);
        Color[] colors = {
            new Color(255, 255, 255, Math.min(255, (int)(coreAlpha * 1.2f))),
            new Color(255, 200, 50, coreAlpha),
            new Color(255, 100, 0, (int)(coreAlpha * 0.7f)),
            new Color(200, 50, 0, 0)
        };
        
        try {
            RadialGradientPaint gradient = new RadialGradientPaint(
                center, radius, dist, colors
            );
            g2d.setPaint(gradient);
            int r = (int) radius;
            g2d.fillOval((int)x - r, (int)y - r, r * 2, r * 2);
        } catch (Exception e) {
            // Fallback si el gradiente falla
            g2d.setColor(new Color(255, 150, 50, (int)(alpha * 200)));
            int r = (int) radius;
            g2d.fillOval((int)x - r, (int)y - r, r * 2, r * 2);
        }
    }
    
    /**
     * Renderiza el efecto de humo.
     */
    private void renderSmoke(Graphics2D g2d, float radius, float progress) {
        float smokeProgress = (progress - 0.3f) / 0.7f;
        float smokeAlpha = Math.max(0, 0.5f - smokeProgress * 0.5f);
        float smokeRadius = radius * (0.5f + smokeProgress * 0.5f);
        
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, smokeAlpha));
        
        // Múltiples círculos de humo
        for (int i = 0; i < 3; i++) {
            float offsetX = (random.nextFloat() - 0.5f) * smokeRadius * 0.3f;
            float offsetY = -smokeProgress * 30 * (i + 1);
            float cloudRadius = smokeRadius * (0.4f + i * 0.2f);
            
            g2d.setColor(new Color(
                SMOKE_COLOR.getRed(),
                SMOKE_COLOR.getGreen(),
                SMOKE_COLOR.getBlue(),
                (int)(smokeAlpha * 100)
            ));
            
            int r = (int) cloudRadius;
            g2d.fillOval((int)(x + offsetX) - r, (int)(y + offsetY) - r, r * 2, r * 2);
        }
        
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
    
    @Override
    public boolean isAlive() {
        return timer < duration;
    }
    
    @Override
    public float getX() {
        return x;
    }
    
    @Override
    public float getY() {
        return y;
    }
    
    /**
     * Partícula de escombros.
     */
    private static class Particle {
        float x, y;
        float vx, vy;
        float size;
        float life;
        float maxLife;
        Color color;
        
        Particle(float x, float y, float angle, float speed, float size, float life, Color color) {
            this.x = x;
            this.y = y;
            this.vx = (float) Math.cos(angle) * speed;
            this.vy = (float) Math.sin(angle) * speed;
            this.size = size;
            this.life = life;
            this.maxLife = life;
            this.color = color;
        }
        
        void update(float deltaTime) {
            x += vx * deltaTime;
            y += vy * deltaTime;
            vy += 200 * deltaTime; // Gravedad
            vx *= 0.98f;
            vy *= 0.98f;
            life -= deltaTime;
        }
        
        void render(Graphics2D g2d) {
            if (life <= 0) return;
            
            float alpha = life / maxLife;
            g2d.setColor(new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                (int)(alpha * 255)
            ));
            
            int s = (int)(size * alpha);
            g2d.fillOval((int)x - s/2, (int)y - s/2, s, s);
        }
    }
    
    /**
     * Chispa de la explosión.
     */
    private static class Spark {
        float x, y;
        float vx, vy;
        float length;
        float life;
        float maxLife;
        
        Spark(float x, float y, float angle, float speed, float length, float life) {
            this.x = x;
            this.y = y;
            this.vx = (float) Math.cos(angle) * speed;
            this.vy = (float) Math.sin(angle) * speed;
            this.length = length;
            this.life = life;
            this.maxLife = life;
        }
        
        void update(float deltaTime) {
            x += vx * deltaTime;
            y += vy * deltaTime;
            vx *= 0.95f;
            vy *= 0.95f;
            life -= deltaTime;
        }
        
        void render(Graphics2D g2d) {
            if (life <= 0) return;
            
            float alpha = life / maxLife;
            float currentLength = length * alpha;
            
            // Calcular punto final de la línea
            float speed = (float) Math.sqrt(vx * vx + vy * vy);
            if (speed < 1) return;
            
            float endX = x - (vx / speed) * currentLength;
            float endY = y - (vy / speed) * currentLength;
            
            // Gradiente de color (amarillo a naranja)
            g2d.setColor(new Color(255, 255, 100, (int)(alpha * 255)));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine((int)x, (int)y, (int)endX, (int)endY);
            
            // Punto brillante en la punta
            g2d.setColor(new Color(255, 255, 255, (int)(alpha * 255)));
            g2d.fillOval((int)x - 2, (int)y - 2, 4, 4);
        }
    }
}
