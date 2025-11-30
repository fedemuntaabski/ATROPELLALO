package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Ametralladora ligera con alta cadencia de fuego y bajo daño.
 * Dispara automáticamente a los enemigos más cercanos.
 * Con mejoras de multi-target, dispara a múltiples enemigos simultáneamente.
 */
public class LightMachineGun extends Weapon {
    
    private static final Color PROJECTILE_COLOR = new Color(255, 200, 100);
    
    /**
     * Crea una nueva ametralladora ligera con valores de configuración.
     */
    public LightMachineGun() {
        super(
            GameConfig.LMG_DAMAGE,
            GameConfig.LMG_RANGE,
            GameConfig.LMG_FIRE_DELAY,
            GameConfig.LMG_PROJECTILE_COUNT,
            GameConfig.LMG_IMPACT_AREA,
            WeaponType.LIGHT_MACHINE_GUN
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
        
        // Crear un proyectil por cada objetivo
        for (Enemy target : targets) {
            float targetX = target.getCenterX();
            float targetY = target.getCenterY();
            
            Projectile projectile = new Projectile(
                playerX, playerY,
                targetX, targetY,
                damage, range, impactArea,
                GameConfig.PROJECTILE_SPEED * 1.2f,
                PROJECTILE_COLOR,
                6
            );
            projectiles.add(projectile);
        }
        
        resetCooldown();
        return projectiles;
    }
}
