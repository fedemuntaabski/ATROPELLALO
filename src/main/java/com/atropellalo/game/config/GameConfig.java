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
    public static final int SCRAP_SIZE = 22;
    
    /** Intervalo mínimo de spawn de chatarra (segundos) */
    public static final float SCRAP_SPAWN_INTERVAL_MIN = 5.0f;
    
    /** Intervalo máximo de spawn de chatarra (segundos) */
    public static final float SCRAP_SPAWN_INTERVAL_MAX = 10.0f;
    
    /** Cantidad máxima de items de chatarra en el mapa */
    public static final int SCRAP_MAX_ON_MAP = 4;
    
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
    
    // ==================== ENEMIGOS - GENERAL ====================
    
    /** Daño que hace el jugador al atropellar enemigos */
    public static float PLAYER_DAMAGE_TO_ENEMY = 50.0f;
    
    /** Daño que hacen los enemigos al jugador por contacto */
    public static float ENEMY_DAMAGE_TO_PLAYER = 10.0f;
    
    /** Cooldown de daño al jugador por contacto (segundos) */
    public static float ENEMY_DAMAGE_COOLDOWN = 0.5f;
    
    /** Distancia de colisión entre jugador y enemigo */
    public static float ENEMY_COLLISION_DISTANCE = 25.0f;
    
    /** Margen de spawn de enemigos desde los bordes */
    public static int ENEMY_SPAWN_MARGIN = 100;
    
    /** Distancia mínima de spawn desde el jugador */
    public static float ENEMY_MIN_SPAWN_DISTANCE = 200.0f;
    
    // ==================== ENEMIGOS - ZOMBIE RÁPIDO ====================
    
    /** Velocidad del zombie rápido (píxeles/segundo) */
    public static float FAST_ZOMBIE_SPEED = 150.0f;
    
    /** Salud del zombie rápido */
    public static float FAST_ZOMBIE_HEALTH = 30.0f;
    
    /** Tamaño del zombie rápido */
    public static int FAST_ZOMBIE_SIZE = 24;
    
    /** Daño del zombie rápido al jugador */
    public static float FAST_ZOMBIE_DAMAGE = 8.0f;
    
    // ==================== ENEMIGOS - ZOMBIE LENTO ====================
    
    /** Velocidad del zombie lento (píxeles/segundo) */
    public static float SLOW_ZOMBIE_SPEED = 50.0f;
    
    /** Salud del zombie lento */
    public static float SLOW_ZOMBIE_HEALTH = 100.0f;
    
    /** Tamaño del zombie lento */
    public static int SLOW_ZOMBIE_SIZE = 36;
    
    /** Daño del zombie lento al jugador */
    public static float SLOW_ZOMBIE_DAMAGE = 15.0f;
    
    // ==================== ENEMIGOS - ZOMBIE EXPLOSIVO ====================
    
    /** Velocidad del zombie explosivo (píxeles/segundo) */
    public static float EXPLOSIVE_ZOMBIE_SPEED = 80.0f;
    
    /** Salud del zombie explosivo */
    public static float EXPLOSIVE_ZOMBIE_HEALTH = 40.0f;
    
    /** Tamaño del zombie explosivo */
    public static int EXPLOSIVE_ZOMBIE_SIZE = 28;
    
    /** Daño del zombie explosivo al jugador (contacto) */
    public static float EXPLOSIVE_ZOMBIE_DAMAGE = 10.0f;
    
    /** Radio de explosión al morir */
    public static float EXPLOSIVE_ZOMBIE_RADIUS = 80.0f;
    
    /** Daño de la explosión */
    public static float EXPLOSIVE_ZOMBIE_EXPLOSION_DAMAGE = 25.0f;
    
    // ==================== SISTEMA DE OLEADAS ====================
    
    /** Tiempo entre oleadas (segundos) */
    public static float WAVE_INTERVAL = 5.0f;
    
    /** Número base de enemigos por oleada */
    public static int WAVE_BASE_ENEMIES = 5;
    
    /** Incremento de enemigos por oleada */
    public static int WAVE_ENEMY_INCREMENT = 5;
    
    /** Máximo de enemigos simultáneos en el mapa */
    public static int MAX_ENEMIES_ON_MAP = 50;
    
    /** Intervalo de spawn entre enemigos de una oleada (segundos) */
    public static float ENEMY_SPAWN_INTERVAL = 0.3f;
    
    /** Probabilidad de zombie rápido (0-100) */
    public static int FAST_ZOMBIE_SPAWN_CHANCE = 50;
    
    /** Probabilidad de zombie lento (0-100) */
    public static int SLOW_ZOMBIE_SPAWN_CHANCE = 30;
    
    /** Probabilidad de zombie explosivo (0-100) */
    public static int EXPLOSIVE_ZOMBIE_SPAWN_CHANCE = 20;
    
    // ==================== ARMAS - PISTOLA ====================
    
    /** Daño de la pistola por disparo */
    public static float PISTOL_DAMAGE = 15.0f;
    
    /** Rango de la pistola (píxeles) */
    public static float PISTOL_RANGE = 250.0f;
    
    /** Delay entre disparos (segundos) */
    public static float PISTOL_FIRE_DELAY = 0.5f;
    
    /** Velocidad del proyectil (píxeles/segundo) */
    public static float PROJECTILE_SPEED = 400.0f;
    
    /** Tamaño del proyectil (píxeles) */
    public static int PROJECTILE_SIZE = 8;
}
