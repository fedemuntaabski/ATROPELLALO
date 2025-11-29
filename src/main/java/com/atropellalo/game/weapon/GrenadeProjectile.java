package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Proyectil de granada con efecto de explosión visual.
 * Al impactar o alcanzar su destino, muestra una animación de explosión
 * similar a la del zombie explosivo.
 */
public class GrenadeProjectile extends Projectile {
    
    private boolean exploding;
    private float explosionTimer;
    private float explosionDuration;
    private float explosionRadius;
    private boolean damageApplied;
    
    private static final float EXPLOSION_DURATION = 0.5f;
    private static final Color[] EXPLOSION_COLORS = {
        new Color(255, 200, 50),   // Amarillo brillante
        new Color(255, 150, 0),    // Naranja
        new Color(255, 100, 0),    // Naranja rojizo
        new Color(200, 50, 0),     // Rojo oscuro
        new Color(100, 100, 100)   // Humo gris
    };
    
    /**
     * Crea un nuevo proyectil de granada.
     */
    public GrenadeProjectile(float startX, float startY, float targetX, float targetY,
                            float damage, float maxRange, float impactArea,
                            float speed, Color color, int size) {
        super(startX, startY, targetX, targetY, damage, maxRange, impactArea, speed, color, size);
        this.exploding = false;
        this.explosionTimer = 0;
        this.explosionDuration = EXPLOSION_DURATION;
        this.explosionRadius = impactArea;
        this.damageApplied = false;
    }
    
    @Override
    public void update(float deltaTime) {
        if (exploding) {
            explosionTimer += deltaTime;
            if (explosionTimer >= explosionDuration) {
                deactivate();
            }
            return;
        }
        
        super.update(deltaTime);
        
        // Iniciar explosión cuando el proyectil se desactiva (alcanzó destino)
        if (!isActive() && !exploding) {
            startExplosion();
        }
    }
    
    @Override
    public boolean checkCollisions(List<Enemy> enemies) {
        if (exploding) {
            return false;
        }
        
        // Verificar colisión con enemigos
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            
            float dx = getX() - enemy.getCenterX();
            float dy = getY() - enemy.getCenterY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            // Reducir distancia de colisión para que sea más difícil acertar directamente
            float collisionDist = 10 + enemy.getSize() / 2f;
            
            if (distance <= collisionDist) {
                startExplosion();
                applyExplosionDamage(enemies);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Inicia la animación de explosión.
     */
    private void startExplosion() {
        exploding = true;
        explosionTimer = 0;
    }
    
    /**
     * Aplica daño en área a todos los enemigos cercanos.
     */
    private void applyExplosionDamage(List<Enemy> enemies) {
        if (damageApplied) {
            return;
        }
        damageApplied = true;
        
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            
            float dx = getX() - enemy.getCenterX();
            float dy = getY() - enemy.getCenterY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (distance <= explosionRadius) {
                // Daño disminuye con la distancia
                float damageMultiplier = 1.0f - (distance / explosionRadius) * 0.5f;
                enemy.takeDamage(getDamage() * damageMultiplier);
            }
        }
    }
    
    @Override
    public void render(Graphics2D g2d) {
        if (exploding) {
            renderExplosion(g2d);
            return;
        }
        
        super.render(g2d);
    }
    
    /**
     * Renderiza el efecto de explosión.
     */
    private void renderExplosion(Graphics2D g2d) {
        float progress = explosionTimer / explosionDuration;
        float x = getX();
        float y = getY();
        
        Composite oldComposite = g2d.getComposite();
        
        // Fase de expansión (primera mitad)
        if (progress < 0.5f) {
            float expansionProgress = progress * 2;
            float currentRadius = explosionRadius * expansionProgress;
            
            // Círculos concéntricos de diferentes colores
            for (int i = EXPLOSION_COLORS.length - 1; i >= 0; i--) {
                float layerProgress = (float) i / EXPLOSION_COLORS.length;
                float layerRadius = currentRadius * (1 - layerProgress * 0.3f);
                float alpha = 1.0f - layerProgress * 0.3f;
                
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.setColor(EXPLOSION_COLORS[i]);
                g2d.fillOval(
                    (int)(x - layerRadius), 
                    (int)(y - layerRadius), 
                    (int)(layerRadius * 2), 
                    (int)(layerRadius * 2)
                );
            }
            
            // Destello central
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - expansionProgress));
            g2d.setColor(Color.WHITE);
            float flashRadius = currentRadius * 0.3f;
            g2d.fillOval(
                (int)(x - flashRadius), 
                (int)(y - flashRadius), 
                (int)(flashRadius * 2), 
                (int)(flashRadius * 2)
            );
        } 
        // Fase de disipación (segunda mitad)
        else {
            float fadeProgress = (progress - 0.5f) * 2;
            float alpha = 1.0f - fadeProgress;
            
            // Humo que se disipa
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.6f));
            g2d.setColor(EXPLOSION_COLORS[EXPLOSION_COLORS.length - 1]);
            
            float smokeRadius = explosionRadius * (1 + fadeProgress * 0.5f);
            g2d.fillOval(
                (int)(x - smokeRadius), 
                (int)(y - smokeRadius), 
                (int)(smokeRadius * 2), 
                (int)(smokeRadius * 2)
            );
            
            // Restos de fuego
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.setColor(EXPLOSION_COLORS[2]);
            float fireRadius = explosionRadius * 0.5f * (1 - fadeProgress);
            g2d.fillOval(
                (int)(x - fireRadius), 
                (int)(y - fireRadius), 
                (int)(fireRadius * 2), 
                (int)(fireRadius * 2)
            );
        }
        
        g2d.setComposite(oldComposite);
    }
    
    @Override
    public boolean isActive() {
        // Mantener activo durante la explosión
        if (exploding) {
            return explosionTimer < explosionDuration;
        }
        return super.isActive();
    }
    
    /**
     * Indica si la granada está en proceso de explosión.
     * @return true si está explotando
     */
    public boolean isExploding() {
        return exploding;
    }
    
    /**
     * Fuerza la aplicación del daño en área (llamado cuando alcanza destino).
     * @param enemies Lista de enemigos
     */
    public void forceExplosion(List<Enemy> enemies) {
        if (!exploding) {
            startExplosion();
            applyExplosionDamage(enemies);
        }
    }
}
