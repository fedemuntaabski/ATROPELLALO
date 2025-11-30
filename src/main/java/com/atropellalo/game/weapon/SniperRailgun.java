package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Rifle de Francotirador (Sniper Railgun) - Arma de precisión.
 * 
 * Rol: Daño puntual altísimo, ideal para eliminar amenazas específicas
 *      (Spitters, Buffers, Explosivos).
 * 
 * Características:
 * - Daño muy alto (one-shot a mayoría de enemigos normales)
 * - Penetración: atraviesa 2-3 objetivos en línea recta
 * - Cadencia muy baja (requiere precisión en el momento)
 * - Rango muy largo
 * 
 * Estrategia: Usar para eliminar objetivos prioritarios antes de que
 *             lleguen a rango peligroso.
 */
public class SniperRailgun extends Weapon {
    
    /** Color del proyectil del francotirador */
    private static final Color SNIPER_COLOR = new Color(100, 200, 255);
    
    /** Penetración del proyectil (enemigos que atraviesa) */
    private int penetration;
    
    /**
     * Crea un nuevo Rifle de Francotirador con valores de configuración.
     */
    public SniperRailgun() {
        super(
            GameConfig.SNIPER_DAMAGE,
            GameConfig.SNIPER_RANGE,
            GameConfig.SNIPER_FIRE_DELAY,
            1, // Un proyectil por disparo
            0, // Sin área de impacto
            WeaponType.SNIPER_RAILGUN
        );
        this.penetration = GameConfig.SNIPER_PENETRATION;
    }
    
    /**
     * Intenta disparar hacia el enemigo más cercano.
     * Crea un proyectil penetrante que atraviesa múltiples enemigos.
     * @param playerX Posición X del jugador
     * @param playerY Posición Y del jugador
     * @param enemies Lista de enemigos
     * @return Lista con el proyectil creado o lista vacía
     */
    @Override
    public List<Projectile> tryFire(float playerX, float playerY, List<Enemy> enemies) {
        List<Projectile> projectiles = new ArrayList<>();
        
        if (!canFire()) {
            return projectiles;
        }
        
        // Buscar el enemigo más peligroso/prioritario en rango
        Enemy target = findPriorityTarget(playerX, playerY, enemies);
        
        if (target == null) {
            return projectiles;
        }
        
        // Calcular dirección hacia el objetivo
        float targetX = target.getCenterX();
        float targetY = target.getCenterY();
        
        // Crear proyectil penetrante
        PenetratingProjectile railProjectile = new PenetratingProjectile(
            playerX, playerY,
            targetX, targetY,
            damage,
            range,
            penetration,
            GameConfig.SNIPER_PROJECTILE_SPEED,
            GameConfig.SNIPER_PROJECTILE_SIZE
        );
        
        projectiles.add(railProjectile);
        resetCooldown();
        
        return projectiles;
    }
    
    /**
     * Busca el objetivo prioritario para el francotirador.
     * Prioriza enemigos peligrosos (Spitters, Buffers, Explosivos) sobre otros.
     * @param playerX Posición X del jugador
     * @param playerY Posición Y del jugador
     * @param enemies Lista de enemigos
     * @return Enemigo prioritario o null si no hay ninguno en rango
     */
    private Enemy findPriorityTarget(float playerX, float playerY, List<Enemy> enemies) {
        Enemy closestPriority = null;
        Enemy closestNormal = null;
        float closestPriorityDistance = Float.MAX_VALUE;
        float closestNormalDistance = Float.MAX_VALUE;
        
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            
            float dx = enemy.getCenterX() - playerX;
            float dy = enemy.getCenterY() - playerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (distance > range) {
                continue;
            }
            
            // Determinar si es un objetivo prioritario
            boolean isPriority = isPriorityTarget(enemy);
            
            if (isPriority && distance < closestPriorityDistance) {
                closestPriorityDistance = distance;
                closestPriority = enemy;
            } else if (!isPriority && distance < closestNormalDistance) {
                closestNormalDistance = distance;
                closestNormal = enemy;
            }
        }
        
        // Priorizar objetivos peligrosos, luego el más cercano
        return closestPriority != null ? closestPriority : closestNormal;
    }
    
    /**
     * Determina si un enemigo es un objetivo prioritario.
     * @param enemy Enemigo a evaluar
     * @return true si debe ser eliminado con prioridad
     */
    private boolean isPriorityTarget(Enemy enemy) {
        // Usar el nombre de la clase para determinar prioridad
        String className = enemy.getClass().getSimpleName();
        return className.contains("Spitter") || 
               className.contains("Buffer") || 
               className.contains("Explosive") ||
               className.contains("Boss") ||
               className.contains("Brood");
    }
    
    /**
     * Mejora la penetración del arma.
     * @param amount Cantidad de penetración adicional
     */
    public void upgradePenetration(int amount) {
        this.penetration += amount;
    }
    
    /**
     * Obtiene la penetración actual.
     * @return Número de enemigos que atraviesa
     */
    public int getPenetration() {
        return penetration;
    }
}
