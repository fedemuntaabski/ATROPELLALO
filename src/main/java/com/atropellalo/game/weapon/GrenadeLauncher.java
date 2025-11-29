package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Lanzagranadas con alto daño explosivo en área y baja cadencia.
 * Dispara granadas en direcciones ALEATORIAS (no apunta automáticamente).
 * Los proyectiles explotan al impactar con efecto visual.
 */
public class GrenadeLauncher extends Weapon {
    
    private static final Color GRENADE_COLOR = new Color(100, 80, 60);
    private static final int GRENADE_SIZE = 14;
    
    private final Random random;
    
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
        this.random = new Random();
    }
    
    @Override
    public List<Projectile> tryFire(float playerX, float playerY, List<Enemy> enemies) {
        List<Projectile> projectiles = new ArrayList<>();
        
        if (!canFire()) {
            return projectiles;
        }
        
        // Solo disparar si hay enemigos en el mapa (pero no apuntar a ellos)
        if (enemies.isEmpty()) {
            return projectiles;
        }
        
        for (int i = 0; i < projectileCount; i++) {
            // Dirección ALEATORIA (no apunta al enemigo)
            float randomAngle = random.nextFloat() * 2 * (float)Math.PI;
            
            // Distancia aleatoria dentro del rango
            float randomDistance = range * (0.5f + random.nextFloat() * 0.5f);
            
            float targetX = playerX + (float) Math.cos(randomAngle) * randomDistance;
            float targetY = playerY + (float) Math.sin(randomAngle) * randomDistance;
            
            // Limitar al mundo
            targetX = Math.max(0, Math.min(targetX, GameConfig.WORLD_WIDTH));
            targetY = Math.max(0, Math.min(targetY, GameConfig.WORLD_HEIGHT));
            
            GrenadeProjectile grenade = new GrenadeProjectile(
                playerX, playerY,
                targetX, targetY,
                damage, randomDistance, impactArea,
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
