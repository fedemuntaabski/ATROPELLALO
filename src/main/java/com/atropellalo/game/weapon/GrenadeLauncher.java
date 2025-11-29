package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Lanzagranadas con alto daño explosivo en área y baja cadencia.
 * Dispara granadas al enemigo más cercano.
 * Los proyectiles explotan al impactar con efecto visual.
 */
public class GrenadeLauncher extends Weapon {
    
    private static final Color GRENADE_COLOR = new Color(100, 80, 60);
    private static final int GRENADE_SIZE = 14;
    
    /**
     * Crea un nuevo lanzagranadas con valores de configuración.
     */
    public GrenadeLauncher() {
        super(
            GameConfig.GRENADE_DAMAGE,
            GameConfig.GRENADE_RANGE,
            GameConfig.GRENADE_FIRE_DELAY,
            GameConfig.GRENADE_PROJECTILE_COUNT,
            GameConfig.GRENADE_EXPLOSION_RADIUS,
            WeaponType.GRENADE_LAUNCHER
        );
    }
    
    @Override
    public List<Projectile> tryFire(float playerX, float playerY, List<Enemy> enemies) {
        List<Projectile> projectiles = new ArrayList<>();
        
        if (!canFire()) {
            return projectiles;
        }
        
        // Buscar enemigo más cercano
        Enemy target = findClosestEnemy(playerX, playerY, enemies);
        
        if (target == null) {
            return projectiles;
        }
        
        // Apuntar al enemigo más cercano
        float targetX = target.getCenterX();
        float targetY = target.getCenterY();
        
        for (int i = 0; i < projectileCount; i++) {
            GrenadeProjectile grenade = new GrenadeProjectile(
                playerX, playerY,
                targetX, targetY,
                damage, range, impactArea,
                GameConfig.GRENADE_PROJECTILE_SPEED,
                GRENADE_COLOR,
                GRENADE_SIZE
            );
            projectiles.add(grenade);
        }
        
        resetCooldown();
        return projectiles;
    }
}
