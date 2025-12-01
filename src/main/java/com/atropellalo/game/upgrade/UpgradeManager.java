package com.atropellalo.game.upgrade;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.entity.Player;
import com.atropellalo.game.weapon.Flamethrower;
import com.atropellalo.game.weapon.Weapon;
import com.atropellalo.game.weapon.WeaponManager;
import com.atropellalo.game.weapon.WeaponType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Gestiona la generación y aplicación de mejoras.
 * Genera opciones aleatorias basadas en el estado actual del jugador.
 */
public class UpgradeManager {
    
    private final Random random;
    
    public UpgradeManager() {
        this.random = new Random();
    }
    
    /**
     * Genera 3 opciones de mejora aleatorias.
     * Las opciones se basan en el estado actual del jugador y sus armas.
     * @param player Jugador
     * @param weaponManager Gestor de armas
     * @return Lista de 3 opciones de mejora
     */
    public List<UpgradeOption> generateOptions(Player player, WeaponManager weaponManager) {
        List<UpgradeOption> allOptions = new ArrayList<>();
        
        // Añadir mejoras de vehículo
        allOptions.addAll(generateVehicleUpgrades());
        
        // Añadir nuevas armas (si puede obtener más)
        if (weaponManager.canAddWeapon()) {
            allOptions.addAll(generateNewWeaponOptions(weaponManager));
        }
        
        // Añadir mejoras de armas existentes
        allOptions.addAll(generateWeaponUpgrades(weaponManager));
        
        // Mezclar y seleccionar 3
        Collections.shuffle(allOptions, random);
        
        // Si hay menos de 3 opciones, duplicar algunas
        while (allOptions.size() < 3) {
            allOptions.addAll(generateVehicleUpgrades());
        }
        
        return allOptions.subList(0, Math.min(3, allOptions.size()));
    }
    
    /**
     * Genera opciones de mejora del vehículo.
     */
    private List<UpgradeOption> generateVehicleUpgrades() {
        List<UpgradeOption> options = new ArrayList<>();
        
        options.add(new UpgradeOption(
            UpgradeType.VEHICLE_HEALTH,
            "Blindaje +",
            "+" + (int) GameConfig.UPGRADE_HEALTH_AMOUNT + " Salud Máxima"
        ));
        
        options.add(new UpgradeOption(
            UpgradeType.VEHICLE_SPEED,
            "Motor Mejorado",
            "+" + (int) GameConfig.UPGRADE_SPEED_AMOUNT + " Velocidad"
        ));
        
        return options;
    }
    
    /**
     * Genera opciones de nuevas armas.
     */
    private List<UpgradeOption> generateNewWeaponOptions(WeaponManager weaponManager) {
        List<UpgradeOption> options = new ArrayList<>();
        List<WeaponType> available = weaponManager.getAvailableWeaponTypes();
        
        for (WeaponType type : available) {
            options.add(new UpgradeOption(
                UpgradeType.NEW_WEAPON,
                type.getDisplayName(),
                type.getDescription(),
                type
            ));
        }
        
        return options;
    }
    
    /**
     * Genera opciones de mejora para armas existentes.
     */
    private List<UpgradeOption> generateWeaponUpgrades(WeaponManager weaponManager) {
        List<UpgradeOption> options = new ArrayList<>();
        
        for (Weapon weapon : weaponManager.getWeapons()) {
            WeaponType type = weapon.getWeaponType();
            
            // Mejora de daño
            options.add(new UpgradeOption(
                UpgradeType.WEAPON_DAMAGE,
                type.getDisplayName() + ": Daño +",
                "+" + (int)((GameConfig.UPGRADE_WEAPON_DAMAGE_FACTOR - 1) * 100) + "% Daño",
                type
            ));
            
            // Mejora de cadencia (no aplica a lanzallamas)
            if (type != WeaponType.FLAMETHROWER) {
                options.add(new UpgradeOption(
                    UpgradeType.WEAPON_FIRE_RATE,
                    type.getDisplayName() + ": Cadencia +",
                    "+" + (int)((1 - GameConfig.UPGRADE_WEAPON_FIRE_RATE_FACTOR) * 100) + "% Cadencia",
                    type
                ));
            }
            
            // Mejora de área (solo para armas con área)
            if (type == WeaponType.GRENADE_LAUNCHER || type == WeaponType.FLAMETHROWER) {
                options.add(new UpgradeOption(
                    UpgradeType.WEAPON_IMPACT_AREA,
                    type.getDisplayName() + ": Área +",
                    "+" + (int)((GameConfig.UPGRADE_WEAPON_AREA_FACTOR - 1) * 100) + "% Área",
                    type
                ));
            }
            
            // Mejora de multi-target (NO para escopeta ni lanzallamas, máx 2 veces)
            if (type != WeaponType.SHOTGUN && type != WeaponType.FLAMETHROWER) {
                if (weapon.canUpgradeMultiTarget()) {
                    options.add(new UpgradeOption(
                        UpgradeType.WEAPON_MULTI_TARGET,
                        type.getDisplayName() + ": Multi-Objetivo",
                        "+" + GameConfig.UPGRADE_WEAPON_TARGET_COUNT + " Objetivo Simultáneo",
                        type
                    ));
                }
            }
            
            // Mejora de ángulo de cono (solo para lanzallamas)
            if (type == WeaponType.FLAMETHROWER) {
                Flamethrower flamethrower = (Flamethrower) weapon;
                if (flamethrower.canUpgradeConeAngle()) {
                    options.add(new UpgradeOption(
                        UpgradeType.WEAPON_CONE_ANGLE,
                        type.getDisplayName() + ": Ángulo +",
                        "+" + (int)GameConfig.UPGRADE_FLAMETHROWER_CONE_ANGLE + "° de cono",
                        type
                    ));
                }
            }
        }
        
        return options;
    }
    
    /**
     * Aplica una mejora al jugador.
     * @param option Opción de mejora seleccionada
     * @param player Jugador
     * @param weaponManager Gestor de armas
     * @return true si se aplicó exitosamente
     */
    public boolean applyUpgrade(UpgradeOption option, Player player, WeaponManager weaponManager) {
        switch (option.getType()) {
            case VEHICLE_HEALTH:
                player.upgradeMaxHealth(GameConfig.UPGRADE_HEALTH_AMOUNT);
                return true;
                
            case VEHICLE_SPEED:
                player.upgradeSpeed(GameConfig.UPGRADE_SPEED_AMOUNT);
                return true;
                
            case NEW_WEAPON:
                return weaponManager.addWeapon(option.getWeaponType());
                
            case WEAPON_DAMAGE:
                return upgradeWeaponStat(weaponManager, option.getWeaponType(), 
                    w -> w.upgradeDamage(GameConfig.UPGRADE_WEAPON_DAMAGE_FACTOR));
                
            case WEAPON_FIRE_RATE:
                return upgradeWeaponStat(weaponManager, option.getWeaponType(),
                    w -> w.upgradeFireRate(GameConfig.UPGRADE_WEAPON_FIRE_RATE_FACTOR));
                
            case WEAPON_IMPACT_AREA:
                return upgradeWeaponStat(weaponManager, option.getWeaponType(),
                    w -> w.upgradeImpactArea(GameConfig.UPGRADE_WEAPON_AREA_FACTOR));
                
            case WEAPON_MULTI_TARGET:
                Weapon multiTargetWeapon = weaponManager.getWeapon(option.getWeaponType());
                if (multiTargetWeapon != null) {
                    return multiTargetWeapon.upgradeTargetCount(GameConfig.UPGRADE_WEAPON_TARGET_COUNT);
                }
                return false;
                
            case WEAPON_CONE_ANGLE:
                Weapon coneWeapon = weaponManager.getWeapon(option.getWeaponType());
                if (coneWeapon != null) {
                    return coneWeapon.upgradeConeAngle(GameConfig.UPGRADE_FLAMETHROWER_CONE_ANGLE);
                }
                return false;
                
            default:
                return false;
        }
    }
    
    /**
     * Aplica una mejora a un arma específica.
     */
    private boolean upgradeWeaponStat(WeaponManager weaponManager, WeaponType type, 
                                      java.util.function.Consumer<Weapon> upgrader) {
        Weapon weapon = weaponManager.getWeapon(type);
        if (weapon != null) {
            upgrader.accept(weapon);
            return true;
        }
        return false;
    }
}
