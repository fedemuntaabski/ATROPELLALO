package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Gestiona las armas del jugador y los proyectiles activos.
 * Maneja el disparo automático, actualización y colisiones de proyectiles.
 * Soporta múltiples armas simultáneas (máximo configurable).
 */
public class WeaponManager {
    
    private final List<Weapon> weapons;
    private final List<Projectile> projectiles;
    
    // Estadísticas
    private int shotsFired;
    private int shotsHit;
    
    /**
     * Crea un nuevo gestor de armas con la pistola por defecto.
     */
    public WeaponManager() {
        this.weapons = new ArrayList<>();
        this.projectiles = new ArrayList<>();
        this.shotsFired = 0;
        this.shotsHit = 0;
        
        // Añadir pistola por defecto
        weapons.add(new Pistol());
    }
    
    /**
     * Actualiza el sistema de armas.
     * @param deltaTime Tiempo desde el último frame
     * @param playerX Centro X del jugador
     * @param playerY Centro Y del jugador
     * @param enemies Lista de enemigos
     */
    public void update(float deltaTime, float playerX, float playerY, List<Enemy> enemies) {
        // Actualizar todas las armas y sus efectos
        for (Weapon weapon : weapons) {
            weapon.update(deltaTime);
            
            // Procesar daño continuo (para púas y lanzallamas)
            weapon.processContinuousDamage(deltaTime, playerX, playerY, enemies);
            
            // Intentar disparar automáticamente
            List<Projectile> newProjectiles = weapon.tryFire(playerX, playerY, enemies);
            if (!newProjectiles.isEmpty()) {
                projectiles.addAll(newProjectiles);
                shotsFired += newProjectiles.size();
            }
        }
        
        // Actualizar proyectiles existentes
        updateProjectiles(deltaTime, enemies);
    }
    
    /**
     * Actualiza todos los proyectiles y verifica colisiones.
     */
    private void updateProjectiles(float deltaTime, List<Enemy> enemies) {
        Iterator<Projectile> iterator = projectiles.iterator();
        
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            
            // Actualizar posición
            projectile.update(deltaTime);
            
            // Verificar colisiones
            if (projectile.isActive()) {
                boolean hit = projectile.checkCollisions(enemies);
                if (hit) {
                    shotsHit++;
                }
            }
            
            // Eliminar proyectiles inactivos
            if (!projectile.isActive()) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Renderiza todos los proyectiles activos y efectos de armas.
     * @param g2d Contexto gráfico
     * @param playerX Centro X del jugador
     * @param playerY Centro Y del jugador
     */
    public void render(Graphics2D g2d, float playerX, float playerY) {
        // Renderizar efectos de armas (púas, lanzallamas)
        for (Weapon weapon : weapons) {
            weapon.render(g2d, playerX, playerY);
        }
        
        // Renderizar proyectiles
        for (Projectile projectile : projectiles) {
            projectile.render(g2d);
        }
    }
    
    /**
     * Renderiza solo los proyectiles (para compatibilidad).
     * @param g2d Contexto gráfico
     */
    public void render(Graphics2D g2d) {
        for (Projectile projectile : projectiles) {
            projectile.render(g2d);
        }
    }
    
    /**
     * Añade una nueva arma al jugador.
     * @param weaponType Tipo de arma a añadir
     * @return true si se añadió exitosamente
     */
    public boolean addWeapon(WeaponType weaponType) {
        // Verificar si ya tiene el máximo de armas
        if (weapons.size() >= GameConfig.MAX_WEAPONS) {
            return false;
        }
        
        // Verificar si ya tiene este tipo de arma
        if (hasWeapon(weaponType)) {
            return false;
        }
        
        // Crear y añadir el arma
        Weapon weapon = createWeapon(weaponType);
        if (weapon != null) {
            weapons.add(weapon);
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica si el jugador tiene un tipo de arma.
     * @param weaponType Tipo a verificar
     * @return true si tiene el arma
     */
    public boolean hasWeapon(WeaponType weaponType) {
        for (Weapon weapon : weapons) {
            if (weapon.getWeaponType() == weaponType) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene un arma por su tipo.
     * @param weaponType Tipo de arma
     * @return Arma o null si no la tiene
     */
    public Weapon getWeapon(WeaponType weaponType) {
        for (Weapon weapon : weapons) {
            if (weapon.getWeaponType() == weaponType) {
                return weapon;
            }
        }
        return null;
    }
    
    /**
     * Crea una instancia de arma según el tipo.
     * @param weaponType Tipo de arma
     * @return Nueva instancia del arma
     */
    private Weapon createWeapon(WeaponType weaponType) {
        switch (weaponType) {
            case PISTOL:
                return new Pistol();
            case LIGHT_MACHINE_GUN:
                return new LightMachineGun();
            case GRENADE_LAUNCHER:
                return new GrenadeLauncher();
            case CIRCULAR_SAW:
                return new CircularSaw();
            case FLAMETHROWER:
                return new Flamethrower();
            case SHOTGUN:
                return new Shotgun();
            default:
                return null;
        }
    }
    
    /**
     * Obtiene la cantidad de armas que tiene el jugador.
     * @return Número de armas
     */
    public int getWeaponCount() {
        return weapons.size();
    }
    
    /**
     * Verifica si el jugador puede obtener más armas.
     * @return true si puede obtener más armas
     */
    public boolean canAddWeapon() {
        return weapons.size() < GameConfig.MAX_WEAPONS;
    }
    
    /**
     * Obtiene la lista de armas del jugador.
     * @return Lista de armas
     */
    public List<Weapon> getWeapons() {
        return new ArrayList<>(weapons);
    }
    
    /**
     * Obtiene los tipos de armas que el jugador no tiene.
     * @return Lista de tipos de armas disponibles para obtener
     */
    public List<WeaponType> getAvailableWeaponTypes() {
        List<WeaponType> available = new ArrayList<>();
        for (WeaponType type : WeaponType.values()) {
            if (type != WeaponType.PISTOL && !hasWeapon(type)) {
                available.add(type);
            }
        }
        return available;
    }
    
    // Getters de estadísticas
    
    public int getShotsFired() {
        return shotsFired;
    }
    
    public int getShotsHit() {
        return shotsHit;
    }
    
    public int getActiveProjectiles() {
        return projectiles.size();
    }
    
    /**
     * Obtiene la precisión del jugador.
     * @return Porcentaje de precisión (0-100)
     */
    public float getAccuracy() {
        if (shotsFired == 0) {
            return 0;
        }
        return (shotsHit / (float) shotsFired) * 100;
    }
}
