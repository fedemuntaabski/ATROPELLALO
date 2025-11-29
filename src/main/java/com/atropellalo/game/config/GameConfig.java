package com.atropellalo.game.config;

/**
 * Configuración centralizada del juego.
 * Modifica estos valores para ajustar el balance del juego.
 * 
 * Esta clase utiliza constantes estáticas para facilitar el acceso
 * y la modificación de parámetros del juego.
 */
public final class GameConfig {
    
    private GameConfig() {
        // Clase de configuración, no instanciable
    }
    
    // ==================== JUGADOR ====================
    
    /** Salud máxima del jugador */
    public static final float PLAYER_MAX_HEALTH = 100.0f;
    
    /** Salud inicial del jugador */
    public static final float PLAYER_INITIAL_HEALTH = 100.0f;
    
    /** Combustible máximo del jugador */
    public static final float PLAYER_MAX_FUEL = 100.0f;
    
    /** Combustible inicial del jugador */
    public static final float PLAYER_INITIAL_FUEL = 100.0f;
    
    /** Consumo de combustible por segundo mientras se mueve */
    public static final float FUEL_CONSUMPTION_RATE = 3.0f;
    
    /** Velocidad del jugador en píxeles por segundo */
    public static final float PLAYER_SPEED = 200.0f;
    
    /** Tamaño del jugador en píxeles */
    public static final int PLAYER_SIZE = 32;
    
    // ==================== LOOT - COMBUSTIBLE ====================
    
    /** Cantidad de combustible que restaura cada item de fuel */
    public static final float FUEL_RESTORE_AMOUNT = 25.0f;
    
    /** Tamaño del item de combustible en píxeles */
    public static final int FUEL_SIZE = 24;
    
    /** Intervalo mínimo de spawn de combustible (segundos) */
    public static final float FUEL_SPAWN_INTERVAL_MIN = 4.0f;
    
    /** Intervalo máximo de spawn de combustible (segundos) */
    public static final float FUEL_SPAWN_INTERVAL_MAX = 8.0f;
    
    /** Cantidad máxima de items de combustible en el mapa */
    public static final int FUEL_MAX_ON_MAP = 10;
    
    /** Cantidad inicial de combustible en el mapa */
    public static final int FUEL_INITIAL_SPAWN = 2;
    
    // ==================== LOOT - CHATARRA ====================
    
    /** Cantidad de salud que restaura cada item de chatarra */
    public static final float SCRAP_HEAL_AMOUNT = 15.0f;
    
    /** Tamaño del item de chatarra en píxeles */
    public static final int SCRAP_SIZE = 20;
    
    /** Intervalo mínimo de spawn de chatarra (segundos) */
    public static final float SCRAP_SPAWN_INTERVAL_MIN = 5.0f;
    
    /** Intervalo máximo de spawn de chatarra (segundos) */
    public static final float SCRAP_SPAWN_INTERVAL_MAX = 10.0f;
    
    /** Cantidad máxima de items de chatarra en el mapa */
    public static final int SCRAP_MAX_ON_MAP = 8;
    
    /** Cantidad inicial de chatarra en el mapa */
    public static final int SCRAP_INITIAL_SPAWN = 1;
    
    // ==================== COLISIONES ====================
    
    /** Distancia para recoger loot (en píxeles desde el centro) */
    public static final float LOOT_PICKUP_DISTANCE = 30.0f;
    
    // ==================== HUD ====================
    
    /** Ancho de las barras del HUD */
    public static final int HUD_BAR_WIDTH = 200;
    
    /** Alto de las barras del HUD */
    public static final int HUD_BAR_HEIGHT = 20;
    
    /** Margen del HUD desde los bordes de la pantalla */
    public static final int HUD_MARGIN = 20;
    
    /** Espaciado entre elementos del HUD */
    public static final int HUD_SPACING = 10;
    
    // ==================== MUNDO ====================
    
    /** Ancho del mundo en píxeles */
    public static final int WORLD_WIDTH = 2560;
    
    /** Alto del mundo en píxeles */
    public static final int WORLD_HEIGHT = 1440;
    
    /** Margen de spawn de loot desde los bordes del mundo */
    public static final int LOOT_SPAWN_MARGIN = 50;
}
