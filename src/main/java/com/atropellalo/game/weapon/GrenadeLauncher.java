package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Lanzagranadas con alto daño explosivo en área y baja cadencia.
 * Dispara granadas a los enemigos más cercanos.
 * Con mejoras de multi-target, dispara a múltiples enemigos simultáneamente.
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
        
        // Buscar los N enemigos más cercanos según targetCount
        List<Enemy> targets = findClosestEnemies(playerX, playerY, enemies, targetCount);
        
        if (targets.isEmpty()) {
            return projectiles;
        }
        
        // Crear una granada por cada objetivo
        for (Enemy target : targets) {
            float targetX = target.getCenterX();
            float targetY = target.getCenterY();
            
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
        
        // Reproducir sonido de disparo
        playFireSound();
        
        resetCooldown();
        return projectiles;
    }
}
