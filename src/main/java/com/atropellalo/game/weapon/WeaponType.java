package com.atropellalo.game.weapon;

/**
 * Tipos de armas disponibles en el juego.
 */
public enum WeaponType {
    
    /** Pistola - arma inicial del jugador */
    PISTOL("Pistola", "Arma básica con disparo preciso"),
    
    /** Ametralladora ligera - alta cadencia, bajo daño */
    LIGHT_MACHINE_GUN("Ametralladora Ligera", "Alta cadencia de fuego, daño bajo"),
    
    /** Lanzagranadas - alto daño en área, baja cadencia, disparo aleatorio */
    GRENADE_LAUNCHER("Lanzagranadas", "Daño explosivo en área, disparo aleatorio"),
    
    /** Lanzallamas - daño continuo en cono */
    FLAMETHROWER("Lanzallamas", "Daño continuo en área cercana"),
    
    /** Escopeta - alto daño, rango corto, múltiples proyectiles */
    SHOTGUN("Escopeta", "Alto daño, rango corto, 5 proyectiles"),
    
    /** Rifle de francotirador - daño puntual altísimo, penetración */
    SNIPER_RAILGUN("Rifle de Francotirador", "Daño extremo, atraviesa 3 enemigos");
    
    private final String displayName;
    private final String description;
    
    WeaponType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Obtiene el nombre para mostrar del arma.
     * @return Nombre del arma
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Obtiene la descripción del arma.
     * @return Descripción del arma
     */
    public String getDescription() {
        return description;
    }
}
