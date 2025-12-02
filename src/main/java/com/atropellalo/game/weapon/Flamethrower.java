package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.effect.VisualEffectManager;
import com.atropellalo.game.enemy.Enemy;
import com.atropellalo.game.sound.SoundManager;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Lanzallamas - Arma de daño continuo en cono.
 * Daña a los enemigos dentro de un cono frente al jugador.
 * Los efectos visuales son manejados por VisualEffectManager.
 */
public class Flamethrower extends Weapon {
    
    private float tickTimer;
    private float aimAngle;
    private boolean active;
    private float coneAngle;
    private float lastPlayerX;
    private float lastPlayerY;
    
    /**
     * Crea un nuevo lanzallamas con valores de configuración.
     */
    public Flamethrower() {
        super(
            GameConfig.FLAMETHROWER_DAMAGE,
            GameConfig.FLAMETHROWER_RANGE,
            GameConfig.FLAMETHROWER_TICK_RATE,
            1,
            0,
            WeaponType.FLAMETHROWER
        );
        this.tickTimer = 0;
        this.aimAngle = 0;
        this.active = false;
        this.coneAngle = GameConfig.FLAMETHROWER_CONE_ANGLE;
    }
    
    @Override
    public List<Projectile> tryFire(float playerX, float playerY, List<Enemy> enemies) {
        // El lanzallamas no dispara proyectiles tradicionales
        return new ArrayList<>();
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        // Actualizar efecto visual del lanzallamas
        VisualEffectManager.getInstance().updateFlamethrower(
            lastPlayerX, lastPlayerY, aimAngle, coneAngle, range, active
        );
        
        // Nota: La gestión del sonido se hace en processContinuousDamage
        // porque necesita el estado actualizado de 'active'
    }
    
    @Override
    public void processContinuousDamage(float deltaTime, float playerX, float playerY, List<Enemy> enemies) {
        // Guardar posición del jugador para el efecto visual
        this.lastPlayerX = playerX;
        this.lastPlayerY = playerY;
        
        // Guardar estado previo para detección de cambios
        boolean previousActive = active;
        
        // Buscar enemigo más cercano para apuntar
        Enemy target = findClosestEnemy(playerX, playerY, enemies);
        
        if (target == null) {
            active = false;
            // Detener sonido inmediatamente cuando no hay enemigos
            if (GameConfig.SOUND_WEAPON_LOOP_ENABLED && SoundManager.getInstance().isLooping(weaponType)) {
                SoundManager.getInstance().stopWeaponLoop(weaponType);
            }
            return;
        }
        
        active = true;
        
        // Iniciar sonido inmediatamente cuando comienza a disparar
        if (GameConfig.SOUND_WEAPON_LOOP_ENABLED && !SoundManager.getInstance().isLooping(weaponType)) {
            SoundManager.getInstance().startWeaponLoop(weaponType);
        }
        
        // Calcular ángulo hacia el objetivo
        float dx = target.getCenterX() - playerX;
        float dy = target.getCenterY() - playerY;
        aimAngle = (float) Math.atan2(dy, dx);
        
        // Actualizar timer de tick
        tickTimer += deltaTime;
        
        // Aplicar daño en cada tick (fireDelay = tick rate)
        if (tickTimer >= fireDelay) {
            tickTimer = 0;
            
            // Dañar a TODOS los enemigos dentro del cono
            float halfCone = (float) Math.toRadians(coneAngle / 2);
            
            for (Enemy enemy : enemies) {
                if (!enemy.isAlive()) {
                    continue;
                }
                
                float edx = enemy.getCenterX() - playerX;
                float edy = enemy.getCenterY() - playerY;
                float distance = (float) Math.sqrt(edx * edx + edy * edy);
                
                // Verificar si está dentro del rango
                if (distance > range) {
                    continue;
                }
                
                // Verificar si está dentro del cono
                float enemyAngle = (float) Math.atan2(edy, edx);
                float angleDiff = Math.abs(normalizeAngle(enemyAngle - aimAngle));
                
                if (angleDiff <= halfCone) {
                    // Daño base que disminuye ligeramente con la distancia
                    float damageMultiplier = 1.0f - (distance / range) * 0.2f;
                    float finalDamage = damage * damageMultiplier;
                    enemy.takeDamage(finalDamage);
                }
            }
        }
    }
    
    @Override
    public void render(Graphics2D g2d, float playerX, float playerY) {
        // Los efectos visuales son renderizados por VisualEffectManager
        // Este método se mantiene vacío para evitar duplicación
    }
    
    /**
     * Normaliza un ángulo al rango [-PI, PI].
     */
    private float normalizeAngle(float angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }
    
    /**
     * Mejora el ángulo del cono del lanzallamas.
     * @param amount Cantidad de grados a incrementar
     * @return true si se aplicó la mejora
     */
    @Override
    public boolean upgradeConeAngle(float amount) {
        if (coneAngle >= GameConfig.FLAMETHROWER_MAX_CONE_ANGLE) {
            return false;
        }
        coneAngle = Math.min(coneAngle + amount, GameConfig.FLAMETHROWER_MAX_CONE_ANGLE);
        return true;
    }
    
    /**
     * Obtiene el ángulo actual del cono.
     * @return Ángulo en grados
     */
    public float getConeAngle() {
        return coneAngle;
    }
    
    /**
     * Verifica si puede mejorar el ángulo del cono.
     * @return true si no ha llegado al máximo
     */
    public boolean canUpgradeConeAngle() {
        return coneAngle < GameConfig.FLAMETHROWER_MAX_CONE_ANGLE;
    }
}
