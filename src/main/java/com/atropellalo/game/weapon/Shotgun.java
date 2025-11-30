package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Escopeta - Alto daño, rango corto, 5 proyectiles en área reducida (15%).
 * Dispara múltiples proyectiles con dispersión hacia el enemigo más cercano.
 */
public class Shotgun extends Weapon {
    
    private static final Color SHOTGUN_PELLET_COLOR = new Color(255, 200, 100);
    private static final int PELLET_SIZE = 6;
    
    /**
     * Crea una nueva escopeta con valores de configuración.
     */
    public Shotgun() {
        super(
            GameConfig.SHOTGUN_DAMAGE,
            GameConfig.SHOTGUN_RANGE,
            GameConfig.SHOTGUN_FIRE_DELAY,
            GameConfig.SHOTGUN_PROJECTILE_COUNT,
            0, // Sin área de impacto individual
            WeaponType.SHOTGUN
        );
    }
    
    @Override
    public List<Projectile> tryFire(float playerX, float playerY, List<Enemy> enemies) {
        List<Projectile> projectiles = new ArrayList<>();
        
        if (!canFire()) {
            return projectiles;
        }
        
        Enemy target = findClosestEnemy(playerX, playerY, enemies);
        
        if (target == null) {
            return projectiles;
        }
        
        // Calcular ángulo base hacia el objetivo
        float targetX = target.getCenterX();
        float targetY = target.getCenterY();
        float dx = targetX - playerX;
        float dy = targetY - playerY;
        float baseAngle = (float) Math.atan2(dy, dx);
        
        // Dispersión total en radianes
        float spreadRad = (float) Math.toRadians(GameConfig.SHOTGUN_SPREAD_ANGLE);
        float halfSpread = spreadRad / 2;
        
        // Crear proyectiles distribuidos en el área de dispersión
        for (int i = 0; i < projectileCount; i++) {
            float spreadAngle;
            if (projectileCount == 1) {
                spreadAngle = 0;
            } else {
                // Distribuir uniformemente en el arco de dispersión
                spreadAngle = -halfSpread + (spreadRad / (projectileCount - 1)) * i;
            }
            
            float angle = baseAngle + spreadAngle;
            float projTargetX = playerX + (float) Math.cos(angle) * range;
            float projTargetY = playerY + (float) Math.sin(angle) * range;
            
            Projectile pellet = new Projectile(
                playerX, playerY,
                projTargetX, projTargetY,
                damage, range, 0,
                GameConfig.SHOTGUN_PROJECTILE_SPEED,
                SHOTGUN_PELLET_COLOR,
                PELLET_SIZE
            );
            projectiles.add(pellet);
        }
        
        // Reproducir sonido de disparo
        playFireSound();
        
        resetCooldown();
        return projectiles;
    }
}
