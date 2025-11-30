package com.atropellalo.game.weapon;

import com.atropellalo.game.enemy.Enemy;

import java.awt.Graphics2D;
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
    protected int projectileCount;
    protected float impactArea;
    protected WeaponType weaponType;
    protected int level;
    
    /**
     * Constructor base para armas.
     * @param damage Daño del arma
     * @param range Rango de alcance
     * @param fireDelay Delay entre disparos (segundos)
     * @param weaponType Tipo de arma
     */
    protected Weapon(float damage, float range, float fireDelay, WeaponType weaponType) {
        this.damage = damage;
        this.range = range;
        this.fireDelay = fireDelay;
        this.currentCooldown = 0;
        this.projectileCount = 1;
        this.impactArea = 0;
        this.weaponType = weaponType;
        this.level = 1;
    }
    
    /**
     * Constructor extendido para armas con más parámetros.
     */
    protected Weapon(float damage, float range, float fireDelay, 
                    int projectileCount, float impactArea, WeaponType weaponType) {
        this.damage = damage;
        this.range = range;
        this.fireDelay = fireDelay;
        this.currentCooldown = 0;
        this.projectileCount = projectileCount;
        this.impactArea = impactArea;
        this.weaponType = weaponType;
        this.level = 1;
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
     * @return Lista de proyectiles creados o lista vacía si no puede disparar
     */
    public abstract List<Projectile> tryFire(float playerX, float playerY, List<Enemy> enemies);
    
    /**
     * Renderiza efectos visuales del arma (si los tiene).
     * @param g2d Contexto gráfico
     * @param playerX Centro X del jugador
     * @param playerY Centro Y del jugador
     */
    public void render(Graphics2D g2d, float playerX, float playerY) {
        // Implementación por defecto vacía - las armas con efectos visuales la sobrescriben
    }
    
    /**
     * Procesa el daño continuo del arma (para armas como púas o lanzallamas).
     * @param deltaTime Tiempo desde el último frame
     * @param playerX Centro X del jugador
     * @param playerY Centro Y del jugador
     * @param enemies Lista de enemigos
     */
    public void processContinuousDamage(float deltaTime, float playerX, float playerY, List<Enemy> enemies) {
        // Implementación por defecto vacía - solo armas de daño continuo la sobrescriben
    }
    
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
    
    // ==================== MÉTODOS DE MEJORA ====================
    
    /**
     * Mejora el daño del arma.
     * @param factor Factor multiplicador
     */
    public void upgradeDamage(float factor) {
        this.damage *= factor;
    }
    
    /**
     * Mejora la cadencia del arma (reduce el delay).
     * @param factor Factor multiplicador (< 1 para mejorar)
     */
    public void upgradeFireRate(float factor) {
        this.fireDelay *= factor;
        if (this.fireDelay < 0.05f) {
            this.fireDelay = 0.05f; // Mínimo delay
        }
    }
    
    /**
     * Mejora el área de impacto.
     * @param factor Factor multiplicador
     */
    public void upgradeImpactArea(float factor) {
        this.impactArea *= factor;
    }
    /**
     * Incrementa la cantidad de disparos simultáneos.
     * @param amount Cantidad a incrementar
     */
    public void upgradeProjectileCount(int amount) {
        this.projectileCount += amount;
    }
    
    /**
     * Incrementa el nivel del arma.
     */
    public void upgradeLevel() {
        this.level++;
    }
    
    // ==================== MÉTODOS PARA UI ====================
    
    /**
     * Obtiene el nombre del arma.
     * @return Nombre del arma
     */
    public String getName() {
        return weaponType.getDisplayName();
    }
    
    /**
     * Obtiene la velocidad de proyectil.
     * @return Velocidad del proyectil o 0 si no aplica
     */
    public float getProjectileSpeed() {
        return 0; // Override en armas con proyectiles
    }
    
    /**
     * Obtiene la cadencia de fuego (disparos por segundo).
     * @return Delay entre disparos
     */
    public float getFireRate() {
        return fireDelay;
    }
    
    // Getters
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
    
    public int getProjectileCount() {
        return projectileCount;
    }
    
    public float getImpactArea() {
        return impactArea;
    }
    
    public WeaponType getWeaponType() {
        return weaponType;
    }
    
    public int getLevel() {
        return level;
    }
}
