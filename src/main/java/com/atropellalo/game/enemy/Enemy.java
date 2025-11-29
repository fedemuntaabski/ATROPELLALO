package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;

import java.awt.Graphics2D;

/**
 * Clase abstracta base para todos los enemigos.
 * Implementa comportamiento de persecución hacia el jugador.
 */
public abstract class Enemy {
    
    protected float x;
    protected float y;
    protected float health;
    protected float maxHealth;
    protected float speed;
    protected int size;
    protected float damage;
    protected boolean alive;
    protected float damageCooldown;
    protected float xpMultiplier;
    
    /**
     * Constructor base para enemigos.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param health Salud del enemigo
     * @param speed Velocidad de movimiento
     * @param size Tamaño del enemigo
     * @param damage Daño al jugador por contacto
     */
    protected Enemy(float x, float y, float health, float speed, int size, float damage) {
        this(x, y, health, speed, size, damage, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Constructor con factores de escalado.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param health Salud base del enemigo
     * @param speed Velocidad base de movimiento
     * @param size Tamaño del enemigo
     * @param damage Daño base al jugador por contacto
     * @param healthScale Factor de escalado de salud
     * @param speedScale Factor de escalado de velocidad
     * @param damageScale Factor de escalado de daño
     */
    protected Enemy(float x, float y, float health, float speed, int size, float damage,
                   float healthScale, float speedScale, float damageScale) {
        this.x = x;
        this.y = y;
        this.health = health * healthScale;
        this.maxHealth = health * healthScale;
        this.speed = speed * speedScale;
        this.size = size;
        this.damage = damage * damageScale;
        this.alive = true;
        this.damageCooldown = 0;
        this.xpMultiplier = (healthScale + speedScale + damageScale) / 3.0f;
    }
    
    /**
     * Actualiza el estado del enemigo.
     * @param deltaTime Tiempo desde el último frame
     * @param playerX Posición X del jugador
     * @param playerY Posición Y del jugador
     */
    public void update(float deltaTime, float playerX, float playerY) {
        if (!alive) {
            return;
        }
        
        // Actualizar cooldown de daño
        if (damageCooldown > 0) {
            damageCooldown -= deltaTime;
        }
        
        // Perseguir al jugador
        moveTowards(playerX, playerY, deltaTime);
        
        // Limitar al mundo
        x = Math.max(0, Math.min(x, GameConfig.WORLD_WIDTH - size));
        y = Math.max(0, Math.min(y, GameConfig.WORLD_HEIGHT - size));
    }
    
    /**
     * Mueve al enemigo hacia una posición objetivo.
     */
    protected void moveTowards(float targetX, float targetY, float deltaTime) {
        float dx = targetX - getCenterX();
        float dy = targetY - getCenterY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            // Normalizar dirección y aplicar velocidad
            float moveX = (dx / distance) * speed * deltaTime;
            float moveY = (dy / distance) * speed * deltaTime;
            
            x += moveX;
            y += moveY;
        }
    }
    
    /**
     * Renderiza el enemigo.
     * @param g2d Contexto gráfico
     */
    public abstract void render(Graphics2D g2d);
    
    /**
     * Obtiene el tipo de enemigo.
     * @return Tipo del enemigo
     */
    public abstract EnemyType getType();
    
    /**
     * Llamado cuando el enemigo muere.
     * Permite comportamiento especial (como explosiones).
     */
    public void onDeath() {
        alive = false;
    }
    
    /**
     * Inflige daño al enemigo.
     * @param amount Cantidad de daño
     */
    public void takeDamage(float amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            onDeath();
        }
    }
    
    /**
     * Verifica si puede hacer daño (cooldown).
     * @return true si puede dañar
     */
    public boolean canDamage() {
        return damageCooldown <= 0;
    }
    
    /**
     * Reinicia el cooldown de daño.
     */
    public void resetDamageCooldown() {
        damageCooldown = GameConfig.ENEMY_DAMAGE_COOLDOWN;
    }
    
    /**
     * Calcula la distancia al jugador.
     * @param playerX Centro X del jugador
     * @param playerY Centro Y del jugador
     * @return Distancia en píxeles
     */
    public float distanceToPlayer(float playerX, float playerY) {
        float dx = getCenterX() - playerX;
        float dy = getCenterY() - playerY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Calcula la distancia a otro enemigo.
     * @param other Otro enemigo
     * @return Distancia en píxeles
     */
    public float distanceToEnemy(Enemy other) {
        float dx = getCenterX() - other.getCenterX();
        float dy = getCenterY() - other.getCenterY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    // Getters
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getCenterX() {
        return x + size / 2f;
    }
    
    public float getCenterY() {
        return y + size / 2f;
    }
    
    public float getHealth() {
        return health;
    }
    
    public float getMaxHealth() {
        return maxHealth;
    }
    
    public int getSize() {
        return size;
    }
    
    public float getDamage() {
        return damage;
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public float getSpeed() {
        return speed;
    }
    
    /**
     * Obtiene el multiplicador de XP basado en el escalado del enemigo.
     * @return Multiplicador de XP
     */
    public float getXPMultiplier() {
        return xpMultiplier;
    }
}
