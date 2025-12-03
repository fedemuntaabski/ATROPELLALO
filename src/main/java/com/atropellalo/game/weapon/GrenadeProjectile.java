package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.effect.VisualEffectManager;
import com.atropellalo.game.enemy.Enemy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Proyectil de granada con efecto de explosión visual.
 * Al impactar o alcanzar su destino, crea un efecto de explosión VFX
 * usando el VisualEffectManager.
 */
public class GrenadeProjectile extends Projectile {
    
    private boolean exploded;
    private float explosionRadius;
    private boolean damageApplied;
    
    /**
     * Crea un nuevo proyectil de granada.
     */
    public GrenadeProjectile(float startX, float startY, float targetX, float targetY,
                            float damage, float maxRange, float impactArea,
                            float speed, Color color, int size) {
        super(startX, startY, targetX, targetY, damage, maxRange, impactArea, speed, color, size);
        this.exploded = false;
        this.explosionRadius = impactArea;
        this.damageApplied = false;
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
    
    @Override
    public boolean checkCollisions(List<Enemy> enemies) {
        if (exploded) {
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
     * Inicia el efecto de explosión VFX.
     */
    private void startExplosion() {
        if (exploded) {
            return;
        }
        exploded = true;
        
        // Crear efecto visual de explosión
        VisualEffectManager.getInstance().createExplosion(
            getX(), getY(), explosionRadius
        );
        
        // Reproducir sonido de explosión
        com.atropellalo.game.sound.SoundManager.getInstance().playExplosionSound();
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
        // Solo renderizar el proyectil si no ha explotado
        if (!exploded) {
            renderGrenade(g2d);
        }
        // La explosión VFX se renderiza automáticamente por VisualEffectManager
    }
    
    /**
     * Renderiza una granada visual.
     */
    private void renderGrenade(Graphics2D g2d) {
        int grenadeSize = 12;
        int x = (int) getX();
        int y = (int) getY();
        
        // Cuerpo principal de la granada (óvalo verde oscuro)
        g2d.setColor(new Color(60, 80, 40));
        g2d.fillOval(x - grenadeSize/2, y - grenadeSize/2, grenadeSize, grenadeSize);
        
        // Segmentos de la granada (líneas)
        g2d.setColor(new Color(40, 60, 30));
        g2d.drawLine(x - grenadeSize/2, y, x + grenadeSize/2, y);
        g2d.drawLine(x, y - grenadeSize/2, x, y + grenadeSize/2);
        
        // Pin superior (rectángulo pequeño)
        g2d.setColor(new Color(100, 100, 100));
        g2d.fillRect(x - 2, y - grenadeSize/2 - 3, 4, 3);
        
        // Anillo del pin
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawOval(x - 3, y - grenadeSize/2 - 5, 6, 4);
        
        // Borde de la granada
        g2d.setColor(new Color(40, 60, 30));
        g2d.drawOval(x - grenadeSize/2, y - grenadeSize/2, grenadeSize, grenadeSize);
        
        // Resaltado para dar volumen
        g2d.setColor(new Color(80, 100, 60, 100));
        g2d.fillOval(x - grenadeSize/4, y - grenadeSize/3, grenadeSize/3, grenadeSize/3);
    }
    
    /**
     * Indica si la granada ya explotó.
     * @return true si explotó
     */
    public boolean isExploding() {
        return exploded;
    }
    
    /**
     * Fuerza la aplicación del daño en área (llamado cuando alcanza destino).
     * @param enemies Lista de enemigos
     */
    public void forceExplosion(List<Enemy> enemies) {
        if (!exploded) {
            startExplosion();
            applyExplosionDamage(enemies);
        }
    }
}
