package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.util.ArrayList;
import java.util.List;

/**
 * Pistola básica del jugador.
 * Dispara automáticamente al enemigo más cercano dentro de su rango.
 * Los proyectiles múltiples van TODOS en la misma dirección.
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
     * Intenta disparar al enemigo más cercano.
     * Todos los proyectiles van en la MISMA dirección (sin spread).
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
        
        // Buscar enemigo más cercano
        Enemy target = findClosestEnemy(playerX, playerY, enemies);
        
        if (target == null) {
            return projectiles; // No hay enemigos en rango
        }
        
        // Crear proyectiles - TODOS en la misma dirección
        float targetX = target.getCenterX();
        float targetY = target.getCenterY();
        
        for (int i = 0; i < projectileCount; i++) {
            // Sin spread - todos van al mismo objetivo
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
