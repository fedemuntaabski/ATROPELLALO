package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Lanzallamas - Arma de daño continuo en cono.
 * Daña a los enemigos dentro de un cono frente al jugador.
 */
public class Flamethrower extends Weapon {
    
    private static final Color[] FLAME_COLORS = {
        new Color(255, 200, 50, 180),
        new Color(255, 150, 30, 160),
        new Color(255, 100, 20, 140),
        new Color(255, 50, 10, 120)
    };
    
    private float tickTimer;
    private float aimAngle;
    private boolean active;
    private final Random random;
    
    // Partículas de fuego para efecto visual
    private final List<FlameParticle> particles;
    
    /**
     * Crea un nuevo lanzallamas con valores de configuración.
     */
    public Flamethrower() {
        super(
            GameConfig.FLAMETHROWER_DAMAGE,
            GameConfig.FLAMETHROWER_RANGE,
            GameConfig.FLAMETHROWER_TICK_RATE,
            1,
            0,
            WeaponType.FLAMETHROWER
        );
        this.tickTimer = 0;
        this.aimAngle = 0;
        this.active = false;
        this.random = new Random();
        this.particles = new ArrayList<>();
    }
    
    @Override
    public List<Projectile> tryFire(float playerX, float playerY, List<Enemy> enemies) {
        // El lanzallamas no dispara proyectiles tradicionales
        return new ArrayList<>();
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        // Actualizar partículas
        particles.removeIf(p -> {
            p.update(deltaTime);
            return !p.isAlive();
        });
        
        // Generar nuevas partículas si está activo
        if (active) {
            for (int i = 0; i < 3; i++) {
                particles.add(new FlameParticle(aimAngle, range));
            }
        }
    }
    
    @Override
    public void processContinuousDamage(float deltaTime, float playerX, float playerY, List<Enemy> enemies) {
        // Buscar enemigo más cercano para apuntar
        Enemy target = findClosestEnemy(playerX, playerY, enemies);
        
        if (target == null) {
            active = false;
            return;
        }
        
        active = true;
        
        // Calcular ángulo hacia el objetivo
        float dx = target.getCenterX() - playerX;
        float dy = target.getCenterY() - playerY;
        aimAngle = (float) Math.atan2(dy, dx);
        
        // Actualizar timer de tick
        tickTimer += deltaTime;
        
        if (tickTimer >= fireDelay) {
            tickTimer = 0;
            
            // Dañar a todos los enemigos en el cono
            float halfCone = (float) Math.toRadians(GameConfig.FLAMETHROWER_CONE_ANGLE / 2);
            
            for (Enemy enemy : enemies) {
                if (!enemy.isAlive()) {
                    continue;
                }
                
                float edx = enemy.getCenterX() - playerX;
                float edy = enemy.getCenterY() - playerY;
                float distance = (float) Math.sqrt(edx * edx + edy * edy);
                
                if (distance > range) {
                    continue;
                }
                
                // Verificar si está dentro del cono
                float enemyAngle = (float) Math.atan2(edy, edx);
                float angleDiff = Math.abs(normalizeAngle(enemyAngle - aimAngle));
                
                if (angleDiff <= halfCone) {
                    // Daño que disminuye con la distancia
                    float damageMultiplier = 1.0f - (distance / range) * 0.3f;
                    enemy.takeDamage(damage * damageMultiplier * fireDelay);
                }
            }
        }
    }
    
    @Override
    public void render(Graphics2D g2d, float playerX, float playerY) {
        if (!active) {
            return;
        }
        
        // Dibujar cono de fuego
        float halfCone = GameConfig.FLAMETHROWER_CONE_ANGLE / 2;
        float startAngle = (float) Math.toDegrees(-aimAngle) - halfCone;
        
        // Múltiples capas de fuego con diferentes transparencias
        for (int layer = FLAME_COLORS.length - 1; layer >= 0; layer--) {
            float layerRange = range * (0.5f + 0.5f * layer / FLAME_COLORS.length);
            
            g2d.setColor(FLAME_COLORS[layer]);
            Arc2D arc = new Arc2D.Float(
                playerX - layerRange,
                playerY - layerRange,
                layerRange * 2,
                layerRange * 2,
                startAngle,
                GameConfig.FLAMETHROWER_CONE_ANGLE,
                Arc2D.PIE
            );
            g2d.fill(arc);
        }
        
        // Dibujar partículas
        for (FlameParticle particle : particles) {
            particle.render(g2d, playerX, playerY);
        }
    }
    
    /**
     * Normaliza un ángulo al rango [-PI, PI].
     */
    private float normalizeAngle(float angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }
    
    /**
     * Partícula de fuego para efectos visuales.
     */
    private class FlameParticle {
        private float x, y;
        private float vx, vy;
        private float life;
        private float maxLife;
        private int colorIndex;
        
        FlameParticle(float baseAngle, float range) {
            float angle = baseAngle + (float) ((random.nextFloat() - 0.5f) * Math.toRadians(GameConfig.FLAMETHROWER_CONE_ANGLE));
            float speed = 100 + random.nextFloat() * 150;
            
            this.x = 0;
            this.y = 0;
            this.vx = (float) Math.cos(angle) * speed;
            this.vy = (float) Math.sin(angle) * speed;
            this.maxLife = 0.3f + random.nextFloat() * 0.2f;
            this.life = maxLife;
            this.colorIndex = random.nextInt(FLAME_COLORS.length);
        }
        
        void update(float deltaTime) {
            x += vx * deltaTime;
            y += vy * deltaTime;
            life -= deltaTime;
            
            // Desacelerar
            vx *= 0.95f;
            vy *= 0.95f;
        }
        
        boolean isAlive() {
            return life > 0;
        }
        
        void render(Graphics2D g2d, float playerX, float playerY) {
            float alpha = life / maxLife;
            Color baseColor = FLAME_COLORS[colorIndex];
            Color color = new Color(
                baseColor.getRed(),
                baseColor.getGreen(),
                baseColor.getBlue(),
                (int) (baseColor.getAlpha() * alpha)
            );
            
            g2d.setColor(color);
            int size = (int) (8 * alpha);
            g2d.fillOval((int)(playerX + x - size/2), (int)(playerY + y - size/2), size, size);
        }
    }
}
