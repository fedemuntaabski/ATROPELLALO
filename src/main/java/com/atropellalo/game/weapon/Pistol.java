package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.util.List;

/**
 * Pistola básica del jugador.
 * Dispara automáticamente al enemigo más cercano dentro de su rango.
 */
public class Pistol extends Weapon {
    
    /**
     * Crea una nueva pistola con los valores de configuración.
     */
    public Pistol() {
        super(
            GameConfig.PISTOL_DAMAGE,
            GameConfig.PISTOL_RANGE,
            GameConfig.PISTOL_FIRE_DELAY
        );
    }
    
    /**
     * Intenta disparar al enemigo más cercano.
     * @param playerX Posición X del jugador (centro)
     * @param playerY Posición Y del jugador (centro)
     * @param enemies Lista de enemigos
     * @return Proyectil creado o null si no puede disparar
     */
    @Override
    public Projectile tryFire(float playerX, float playerY, List<Enemy> enemies) {
        if (!canFire()) {
            return null;
        }
        
        // Buscar enemigo más cercano
        Enemy target = findClosestEnemy(playerX, playerY, enemies);
        
        if (target == null) {
            return null; // No hay enemigos en rango
        }
        
        // Crear proyectil hacia el objetivo
        Projectile projectile = new Projectile(
            playerX,
            playerY,
            target.getCenterX(),
            target.getCenterY(),
            damage,
            range
        );
        
        resetCooldown();
        
        return projectile;
    }
}
