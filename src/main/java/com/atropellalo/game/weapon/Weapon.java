package com.atropellalo.game.weapon;

import com.atropellalo.game.enemy.Enemy;

import java.util.List;

/**
 * Clase abstracta base para todas las armas.
 * Define la interfaz común para el sistema de armas.
 */
public abstract class Weapon {
    
    protected float damage;
    protected float range;
    protected float fireDelay;
    protected float currentCooldown;
    
    /**
     * Constructor base para armas.
     * @param damage Daño del arma
     * @param range Rango de alcance
     * @param fireDelay Delay entre disparos (segundos)
     */
    protected Weapon(float damage, float range, float fireDelay) {
        this.damage = damage;
        this.range = range;
        this.fireDelay = fireDelay;
        this.currentCooldown = 0;
    }
    
    /**
     * Actualiza el estado del arma (cooldown, etc.).
     * @param deltaTime Tiempo desde el último frame
     */
    public void update(float deltaTime) {
        if (currentCooldown > 0) {
            currentCooldown -= deltaTime;
        }
    }
    
    /**
     * Verifica si el arma puede disparar.
     * @return true si puede disparar
     */
    public boolean canFire() {
        return currentCooldown <= 0;
    }
    
    /**
     * Intenta disparar hacia el enemigo más cercano.
     * @param playerX Posición X del jugador
     * @param playerY Posición Y del jugador
     * @param enemies Lista de enemigos
     * @return Proyectil creado o null si no puede disparar
     */
    public abstract Projectile tryFire(float playerX, float playerY, List<Enemy> enemies);
    
    /**
     * Encuentra el enemigo más cercano dentro del rango.
     * @param playerX Posición X del jugador
     * @param playerY Posición Y del jugador
     * @param enemies Lista de enemigos
     * @return Enemigo más cercano o null si no hay ninguno en rango
     */
    protected Enemy findClosestEnemy(float playerX, float playerY, List<Enemy> enemies) {
        Enemy closest = null;
        float closestDistance = Float.MAX_VALUE;
        
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            
            float dx = enemy.getCenterX() - playerX;
            float dy = enemy.getCenterY() - playerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (distance <= range && distance < closestDistance) {
                closestDistance = distance;
                closest = enemy;
            }
        }
        
        return closest;
    }
    
    /**
     * Reinicia el cooldown del arma.
     */
    protected void resetCooldown() {
        currentCooldown = fireDelay;
    }
    
    // Getters
    
    public float getDamage() {
        return damage;
    }
    
    public float getRange() {
        return range;
    }
    
    public float getFireDelay() {
        return fireDelay;
    }
    
    public float getCurrentCooldown() {
        return currentCooldown;
    }
}
