package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.util.ArrayList;
import java.util.List;

/**
 * Pistola básica del jugador.
 * Dispara automáticamente al enemigo más cercano dentro de su rango.
 * Con mejoras de multi-target, dispara a múltiples enemigos simultáneamente.
 */
public class Pistol extends Weapon {
    
    /**
     * Crea una nueva pistola con los valores de configuración.
     */
    public Pistol() {
        super(
            GameConfig.PISTOL_DAMAGE,
            GameConfig.PISTOL_RANGE,
            GameConfig.PISTOL_FIRE_DELAY,
            GameConfig.PISTOL_PROJECTILE_COUNT,
            GameConfig.PISTOL_IMPACT_AREA,
            WeaponType.PISTOL
        );
    }
    
    /**
     * Intenta disparar a los enemigos más cercanos.
     * Con multi-target, dispara a múltiples enemigos simultáneamente.
     * @param playerX Posición X del jugador (centro)
     * @param playerY Posición Y del jugador (centro)
     * @param enemies Lista de enemigos
     * @return Lista de proyectiles creados o lista vacía si no puede disparar
     */
    @Override
    public List<Projectile> tryFire(float playerX, float playerY, List<Enemy> enemies) {
        List<Projectile> projectiles = new ArrayList<>();
        
        if (!canFire()) {
            return projectiles;
        }
        
        // Buscar los N enemigos más cercanos según targetCount
        List<Enemy> targets = findClosestEnemies(playerX, playerY, enemies, targetCount);
        
        if (targets.isEmpty()) {
            return projectiles; // No hay enemigos en rango
        }
        
        // Crear un proyectil por cada objetivo
        for (Enemy target : targets) {
            float targetX = target.getCenterX();
            float targetY = target.getCenterY();
            
            Projectile projectile = new Projectile(
                playerX,
                playerY,
                targetX,
                targetY,
                damage,
                range,
                impactArea
            );
            projectiles.add(projectile);
        }
        
        resetCooldown();
        
        return projectiles;
    }
}
