package com.atropellalo.game.upgrade;

/**
 * Tipos de mejoras disponibles en el juego.
 */
public enum UpgradeType {
    
    // Mejoras del vehículo
    VEHICLE_HEALTH("Salud Máxima", "Aumenta la salud máxima del vehículo"),
    VEHICLE_SPEED("Velocidad", "Aumenta la velocidad del vehículo"),
    
    // Obtener nueva arma
    NEW_WEAPON("Nueva Arma", "Obtén una nueva arma"),
    
    // Mejoras de armas
    WEAPON_DAMAGE("Daño", "Aumenta el daño del arma"),
    WEAPON_FIRE_RATE("Cadencia", "Aumenta la cadencia de disparo"),
    WEAPON_IMPACT_AREA("Área de Impacto", "Aumenta el área de daño"),
    WEAPON_PROJECTILE_COUNT("Disparos Simultáneos", "Añade disparos adicionales");
    
    private final String displayName;
    private final String description;
    
    UpgradeType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
}
