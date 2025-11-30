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
     * Intenta disparar hacia los enemigos prioritarios.
     * Crea proyectiles penetrantes que atraviesan múltiples enemigos.
     * Con multi-target, dispara a múltiples objetivos simultáneamente.
     * @param playerX Posición X del jugador
     * @param playerY Posición Y del jugador
     * @param enemies Lista de enemigos
     * @return Lista con los proyectiles creados o lista vacía
     */
    @Override
    public List<Projectile> tryFire(float playerX, float playerY, List<Enemy> enemies) {
        List<Projectile> projectiles = new ArrayList<>();
        
        if (!canFire()) {
            return projectiles;
        }
        
        // Buscar los N objetivos prioritarios según targetCount
        List<Enemy> targets = findPriorityTargets(playerX, playerY, enemies, targetCount);
        
        if (targets.isEmpty()) {
            return projectiles;
        }
        
        // Crear un proyectil penetrante por cada objetivo
        for (Enemy target : targets) {
            float targetX = target.getCenterX();
            float targetY = target.getCenterY();
            
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
        }
        
        resetCooldown();
        
        return projectiles;
    }
    
    /**
     * Busca los N objetivos prioritarios para el francotirador.
     * Prioriza enemigos peligrosos (Spitters, Buffers, Explosivos) sobre otros.
     * @param playerX Posición X del jugador
     * @param playerY Posición Y del jugador
     * @param enemies Lista de enemigos
     * @param count Cantidad de objetivos a encontrar
     * @return Lista de enemigos prioritarios
     */
    private List<Enemy> findPriorityTargets(float playerX, float playerY, List<Enemy> enemies, int count) {
        List<Enemy> priorityTargets = new ArrayList<>();
        List<Enemy> normalTargets = new ArrayList<>();
        
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
            
            if (isPriorityTarget(enemy)) {
                priorityTargets.add(enemy);
            } else {
                normalTargets.add(enemy);
            }
        }
        
        // Ordenar por distancia
        final float px = playerX;
        final float py = playerY;
        java.util.Comparator<Enemy> byDistance = (e1, e2) -> {
            float d1 = (float) Math.sqrt(Math.pow(e1.getCenterX() - px, 2) + Math.pow(e1.getCenterY() - py, 2));
            float d2 = (float) Math.sqrt(Math.pow(e2.getCenterX() - px, 2) + Math.pow(e2.getCenterY() - py, 2));
            return Float.compare(d1, d2);
        };
        
        priorityTargets.sort(byDistance);
        normalTargets.sort(byDistance);
        
        // Combinar listas, priorizando objetivos peligrosos
        List<Enemy> result = new ArrayList<>();
        result.addAll(priorityTargets);
        result.addAll(normalTargets);
        
        // Devolver solo los N primeros
        return result.subList(0, Math.min(count, result.size()));
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
