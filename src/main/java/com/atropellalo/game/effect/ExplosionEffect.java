package com.atropellalo.game.effect;

import com.atropellalo.game.config.GameConfig;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Efecto visual de explosión para zombies explosivos.
 * Simula una explosión con partículas y chispas (VFX only).
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
    
    // Colores de las partículas de explosión
    private static final Color INNER_COLOR = new Color(255, 200, 50);
    private static final Color MIDDLE_COLOR = new Color(255, 100, 0);
    private static final Color OUTER_COLOR = new Color(200, 50, 0);
    
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
        // Escalar cantidad de partículas según el radio (más área = más partículas)
        float radiusScale = maxRadius / GameConfig.GRENADE_EXPLOSION_RADIUS;
        int baseCount = GameConfig.VFX_EXPLOSION_PARTICLE_COUNT;
        int count = (int)(baseCount * Math.min(radiusScale, 2.0f)); // Máximo 2x partículas
        
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * (float)(Math.PI * 2);
            // Escalar velocidad según el radio para que cubra el área
            float baseSpeed = 100 + random.nextFloat() * 200;
            float speed = baseSpeed * radiusScale;
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
        // Escalar cantidad de chispas según el radio
        float radiusScale = maxRadius / GameConfig.GRENADE_EXPLOSION_RADIUS;
        int baseCount = GameConfig.VFX_EXPLOSION_SPARK_COUNT;
        int count = (int)(baseCount * Math.min(radiusScale, 2.0f)); // Máximo 2x chispas
        
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * (float)(Math.PI * 2);
            // Escalar velocidad según el radio
            float baseSpeed = 200 + random.nextFloat() * 300;
            float speed = baseSpeed * radiusScale;
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
        
        // Renderizar chispas
        for (Spark s : sparks) {
            s.render(g2d);
        }
        
        // Renderizar partículas
        for (Particle p : particles) {
            p.render(g2d);
        }
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
