package com.atropellalo.game.upgrade;

import com.atropellalo.game.weapon.WeaponType;

/**
 * Representa una opción de mejora que puede aparecer en el menú.
 * Contiene toda la información necesaria para mostrar y aplicar la mejora.
 */
public class UpgradeOption {
    
    private final UpgradeType type;
    private final String title;
    private final String description;
    private final WeaponType weaponType; // Solo para mejoras de arma o nueva arma
    
    /**
     * Constructor para mejoras del vehículo.
     */
    public UpgradeOption(UpgradeType type, String title, String description) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.weaponType = null;
    }
    
    /**
     * Constructor para mejoras de arma o nueva arma.
     */
    public UpgradeOption(UpgradeType type, String title, String description, WeaponType weaponType) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.weaponType = weaponType;
    }
    
    public UpgradeType getType() {
        return type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public WeaponType getWeaponType() {
        return weaponType;
    }
    
    /**
     * Verifica si esta mejora es para un arma específica.
     */
    public boolean isWeaponUpgrade() {
        return weaponType != null && type != UpgradeType.NEW_WEAPON;
    }
    
    /**
     * Verifica si esta mejora es para obtener una nueva arma.
     */
    public boolean isNewWeapon() {
        return type == UpgradeType.NEW_WEAPON;
    }
    
    /**
     * Verifica si esta mejora es para el vehículo.
     */
    public boolean isVehicleUpgrade() {
        return type == UpgradeType.VEHICLE_HEALTH || type == UpgradeType.VEHICLE_SPEED;
    }
}
