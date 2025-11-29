package com.atropellalo.game.weapon;

import com.atropellalo.game.enemy.Enemy;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Gestiona las armas del jugador y los proyectiles activos.
 * Maneja el disparo automático, actualización y colisiones de proyectiles.
 */
public class WeaponManager {
    
    private final Weapon currentWeapon;
    private final List<Projectile> projectiles;
    
    // Estadísticas
    private int shotsFired;
    private int shotsHit;
    
    /**
     * Crea un nuevo gestor de armas con la pistola por defecto.
     */
    public WeaponManager() {
        this.currentWeapon = new Pistol();
        this.projectiles = new ArrayList<>();
        this.shotsFired = 0;
        this.shotsHit = 0;
    }
    
    /**
     * Actualiza el sistema de armas.
     * @param deltaTime Tiempo desde el último frame
     * @param playerX Centro X del jugador
     * @param playerY Centro Y del jugador
     * @param enemies Lista de enemigos
     */
    public void update(float deltaTime, float playerX, float playerY, List<Enemy> enemies) {
        // Actualizar cooldown del arma
        currentWeapon.update(deltaTime);
        
        // Intentar disparar automáticamente
        Projectile newProjectile = currentWeapon.tryFire(playerX, playerY, enemies);
        if (newProjectile != null) {
            projectiles.add(newProjectile);
            shotsFired++;
        }
        
        // Actualizar proyectiles existentes
        updateProjectiles(deltaTime, enemies);
    }
    
    /**
     * Actualiza todos los proyectiles y verifica colisiones.
     */
    private void updateProjectiles(float deltaTime, List<Enemy> enemies) {
        Iterator<Projectile> iterator = projectiles.iterator();
        
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            
            // Actualizar posición
            projectile.update(deltaTime);
            
            // Verificar colisiones
            if (projectile.isActive()) {
                boolean hit = projectile.checkCollisions(enemies);
                if (hit) {
                    shotsHit++;
                }
            }
            
            // Eliminar proyectiles inactivos
            if (!projectile.isActive()) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Renderiza todos los proyectiles activos.
     * @param g2d Contexto gráfico
     */
    public void render(Graphics2D g2d) {
        for (Projectile projectile : projectiles) {
            projectile.render(g2d);
        }
    }
    
    // Getters
    
    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }
    
    public int getShotsFired() {
        return shotsFired;
    }
    
    public int getShotsHit() {
        return shotsHit;
    }
    
    public int getActiveProjectiles() {
        return projectiles.size();
    }
    
    /**
     * Obtiene la precisión del jugador.
     * @return Porcentaje de precisión (0-100)
     */
    public float getAccuracy() {
        if (shotsFired == 0) {
            return 0;
        }
        return (shotsHit / (float) shotsFired) * 100;
    }
}
