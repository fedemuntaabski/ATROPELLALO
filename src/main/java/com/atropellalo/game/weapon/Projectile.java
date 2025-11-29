package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Representa un proyectil disparado por un arma.
 * Se mueve en línea recta hacia una dirección y daña enemigos al contacto.
 */
public class Projectile {
    
    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private float damage;
    private boolean active;
    private float distanceTraveled;
    private float maxRange;
    
    /**
     * Crea un nuevo proyectil.
     * @param startX Posición X inicial
     * @param startY Posición Y inicial
     * @param targetX Posición X del objetivo
     * @param targetY Posición Y del objetivo
     * @param damage Daño que causa el proyectil
     * @param maxRange Rango máximo del proyectil
     */
    public Projectile(float startX, float startY, float targetX, float targetY, float damage, float maxRange) {
        this.x = startX;
        this.y = startY;
        this.damage = damage;
        this.active = true;
        this.distanceTraveled = 0;
        this.maxRange = maxRange;
        
        // Calcular dirección normalizada
        float dx = targetX - startX;
        float dy = targetY - startY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            this.velocityX = (dx / distance) * GameConfig.PROJECTILE_SPEED;
            this.velocityY = (dy / distance) * GameConfig.PROJECTILE_SPEED;
        } else {
            this.velocityX = 0;
            this.velocityY = 0;
            this.active = false;
        }
    }
    
    /**
     * Actualiza la posición del proyectil.
     * @param deltaTime Tiempo desde el último frame
     */
    public void update(float deltaTime) {
        if (!active) {
            return;
        }
        
        // Calcular movimiento
        float moveX = velocityX * deltaTime;
        float moveY = velocityY * deltaTime;
        
        x += moveX;
        y += moveY;
        
        // Actualizar distancia recorrida
        distanceTraveled += (float) Math.sqrt(moveX * moveX + moveY * moveY);
        
        // Desactivar si excede el rango
        if (distanceTraveled >= maxRange) {
            active = false;
        }
        
        // Desactivar si sale del mundo
        if (x < 0 || x > GameConfig.WORLD_WIDTH || y < 0 || y > GameConfig.WORLD_HEIGHT) {
            active = false;
        }
    }
    
    /**
     * Verifica colisiones con enemigos y aplica daño.
     * @param enemies Lista de enemigos
     * @return true si impactó a un enemigo
     */
    public boolean checkCollisions(List<Enemy> enemies) {
        if (!active) {
            return false;
        }
        
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            
            // Verificar colisión con el enemigo
            float dx = x - enemy.getCenterX();
            float dy = y - enemy.getCenterY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            // Colisión si la distancia es menor que la suma de los radios
            float collisionDistance = (GameConfig.PROJECTILE_SIZE / 2f) + (enemy.getSize() / 2f);
            
            if (distance <= collisionDistance) {
                // Aplicar daño
                enemy.takeDamage(damage);
                active = false;
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Renderiza el proyectil.
     * @param g2d Contexto gráfico
     */
    public void render(Graphics2D g2d) {
        if (!active) {
            return;
        }
        
        int size = GameConfig.PROJECTILE_SIZE;
        int drawX = (int)(x - size / 2);
        int drawY = (int)(y - size / 2);
        
        // Proyectil amarillo con borde naranja
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(drawX, drawY, size, size);
        g2d.setColor(Color.ORANGE);
        g2d.drawOval(drawX, drawY, size, size);
    }
    
    /**
     * Verifica si el proyectil está activo.
     * @return true si está activo
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Desactiva el proyectil.
     */
    public void deactivate() {
        active = false;
    }
    
    // Getters
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getDamage() {
        return damage;
    }
}
