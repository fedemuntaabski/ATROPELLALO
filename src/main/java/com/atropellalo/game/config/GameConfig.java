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
    public static final int FUEL_MAX_ON_MAP = 6;
    
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
    public static final int LOOT_SPAWN_MARGIN = 75;
    
    // ==================== ENEMIGOS - GENERAL ====================
    
    
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
    public static int WAVE_BASE_ENEMIES = 10;
    
    /** Incremento de enemigos por oleada */
    public static int WAVE_ENEMY_INCREMENT = 5;
    
    /** Máximo de enemigos simultáneos en el mapa */
    public static int MAX_ENEMIES_ON_MAP = 100;
    
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
    
    /** Disparos simultáneos de la pistola */
    public static int PISTOL_PROJECTILE_COUNT = 1;
    
    /** Área de impacto de la pistola */
    public static float PISTOL_IMPACT_AREA = 0.0f;
    
    /** Velocidad del proyectil (píxeles/segundo) */
    public static float PROJECTILE_SPEED = 400.0f;
    
    /** Tamaño del proyectil (píxeles) */
    public static int PROJECTILE_SIZE = 8;
    
    // ==================== ARMAS - AMETRALLADORA LIGERA ====================
    
    /** Daño de la ametralladora por disparo (bajo) */
    public static float LMG_DAMAGE = 3.0f;
    
    /** Rango de la ametralladora (medio) */
    public static float LMG_RANGE = 200.0f;
    
    /** Delay entre disparos (muy bajo - alta cadencia) */
    public static float LMG_FIRE_DELAY = 0.15f;
    
    /** Disparos simultáneos de la ametralladora */
    public static int LMG_PROJECTILE_COUNT = 1;
    
    /** Área de impacto de la ametralladora */
    public static float LMG_IMPACT_AREA = 0.0f;
    
    // ==================== ARMAS - LANZAGRANADAS ====================
    
    /** Daño del lanzagranadas (muy alto) */
    public static float GRENADE_DAMAGE = 50.0f;
    
    /** Rango del lanzagranadas (medio) */
    public static float GRENADE_RANGE = 200.0f;
    
    /** Delay entre disparos (alto - baja cadencia) */
    public static float GRENADE_FIRE_DELAY = 2.0f;
    
    /** Disparos simultáneos del lanzagranadas */
    public static int GRENADE_PROJECTILE_COUNT = 1;
    
    /** Radio de explosión de la granada */
    public static float GRENADE_EXPLOSION_RADIUS = 60.0f;
    
    /** Velocidad del proyectil de granada */
    public static float GRENADE_PROJECTILE_SPEED = 250.0f;
    
    // ==================== ARMAS - PÚAS/SPIKES ====================
    
    /** Daño de las púas por contacto (bajo) */
    public static float SPIKES_DAMAGE = 8.0f;
    
    /** Radio de daño de las púas (muy corto) */
    public static float SPIKES_RANGE = 50.0f;
    
    /** Cooldown de daño de las púas */
    public static float SPIKES_DAMAGE_COOLDOWN = 0.25f;
    
    // ==================== ARMAS - SIERRAS CIRCULARES ====================
    
    /** Daño de las sierras por contacto */
    public static float SAW_DAMAGE = 12.0f;
    
    /** Radio de cada sierra */
    public static float SAW_RADIUS = 25.0f;
    
    /** Distancia de las sierras desde el centro del jugador */
    public static float SAW_DISTANCE = 45.0f;
    
    /** Cooldown de daño de las sierras */
    public static float SAW_DAMAGE_COOLDOWN = 0.25f;
    
    /** Velocidad de rotación de las sierras (rad/s) */
    public static float SAW_ROTATION_SPEED = 10.0f;
    
    // ==================== ARMAS - ESCOPETA ====================
    
    /** Daño de la escopeta por proyectil (alto) */
    public static float SHOTGUN_DAMAGE = 25.0f;
    
    /** Rango de la escopeta (corto) */
    public static float SHOTGUN_RANGE = 150.0f;
    
    /** Delay entre disparos (medio) */
    public static float SHOTGUN_FIRE_DELAY = 1.0f;
    
    /** Proyectiles por disparo */
    public static int SHOTGUN_PROJECTILE_COUNT = 5;
    
    /** Ángulo de dispersión total (15% de 360 = ~54 grados) */
    public static float SHOTGUN_SPREAD_ANGLE = 54.0f;
    
    /** Velocidad de los proyectiles */
    public static float SHOTGUN_PROJECTILE_SPEED = 350.0f;
    
    // ==================== ARMAS - LANZALLAMAS ====================
    
    /** Daño del lanzallamas por segundo (medio) */
    public static float FLAMETHROWER_DAMAGE = 15.0f;
    
    /** Rango del lanzallamas (corto) */
    public static float FLAMETHROWER_RANGE = 100.0f;
    
    /** Ángulo del cono del lanzallamas (grados) */
    public static float FLAMETHROWER_CONE_ANGLE = 45.0f;
    
    /** Tick rate del daño del lanzallamas (segundos) */
    public static float FLAMETHROWER_TICK_RATE = 0.1f;
    
    // ==================== SISTEMA DE XP Y NIVELES ====================
    
    /** Tamaño del orbe de XP */
    public static int XP_ORB_SIZE = 12;
    
    /** Distancia de atracción magnética del orbe */
    public static float XP_ORB_MAGNET_DISTANCE = 100.0f;
    
    /** Velocidad de atracción del orbe */
    public static float XP_ORB_MAGNET_SPEED = 220.0f;
    
    /** Distancia de recolección del orbe */
    public static float XP_ORB_PICKUP_DISTANCE = 20.0f;
    
    /** XP base otorgado por zombie rápido */
    public static int XP_FAST_ZOMBIE = 10;
    
    /** XP base otorgado por zombie lento */
    public static int XP_SLOW_ZOMBIE = 25;
    
    /** XP base otorgado por zombie explosivo */
    public static int XP_EXPLOSIVE_ZOMBIE = 20;
    
    /** XP requerido para nivel 1 -> 2 */
    public static int XP_BASE_TO_LEVEL_UP = 100;
    
    /** Factor de incremento de XP por nivel */
    public static float XP_LEVEL_SCALING = 1.2f;
    
    // ==================== MEJORAS/UPGRADES ====================
    
    /** Cantidad de mejora de salud máxima */
    public static float UPGRADE_HEALTH_AMOUNT = 20.0f;
    
    /** Cantidad de mejora de velocidad */
    public static float UPGRADE_SPEED_AMOUNT = 20.0f;
    
    /** Factor de mejora de daño de arma */
    public static float UPGRADE_WEAPON_DAMAGE_FACTOR = 1.2f;
    
    /** Factor de mejora de cadencia de arma */
    public static float UPGRADE_WEAPON_FIRE_RATE_FACTOR = 0.85f;
    
    /** Factor de mejora de área de impacto */
    public static float UPGRADE_WEAPON_AREA_FACTOR = 1.3f;
    
    /** Incremento de disparos simultáneos */
    public static int UPGRADE_WEAPON_PROJECTILE_COUNT = 1;
    
    /** Máximo de armas que puede tener el jugador */
    public static int MAX_WEAPONS = 3;
    
    // ==================== ESCALADO DE OLEADAS ====================
    
    /** Factor de escalado de vida de enemigos por oleada */
    public static float WAVE_HEALTH_SCALING = 1.05f;
    
    /** Factor de escalado de velocidad de enemigos por oleada */
    public static float WAVE_SPEED_SCALING = 1.02f;
    
    /** Factor de escalado de daño de enemigos por oleada */
    public static float WAVE_DAMAGE_SCALING = 1.03f;
    
    /** Factor de escalado de XP de enemigos por oleada */
    public static float WAVE_XP_SCALING = 1.05f;
    
    // ==================== BONIFICACIONES DE NIVEL ====================
    
    /** Combustible otorgado al subir de nivel */
    public static float LEVEL_UP_FUEL_BONUS = 10.0f;
    
    // ==================== JEFE - EL APLASTADOR (BRUISER) - OLEADA 10 ====================
    
    /** Salud del Aplastador (muy alta) */
    public static float BRUISER_BOSS_HEALTH = 700.0f;
    
    /** Velocidad del Aplastador (baja-media) */
    public static float BRUISER_BOSS_SPEED = 60.0f;
    
    /** Tamaño del Aplastador */
    public static int BRUISER_BOSS_SIZE = 74;
    
    /** Daño por contacto del Aplastador (alto) */
    public static float BRUISER_BOSS_CONTACT_DAMAGE = 35.0f;
    
    /** Radio del golpe de terremoto */
    public static float BRUISER_EARTHQUAKE_RADIUS = 150.0f;
    
    /** Daño del golpe de terremoto */
    public static float BRUISER_EARTHQUAKE_DAMAGE = 30.0f;
    
    /** Cooldown del golpe de terremoto (segundos) */
    public static float BRUISER_EARTHQUAKE_COOLDOWN = 4.0f;
    
    /** Velocidad de la carga frontal */
    public static float BRUISER_CHARGE_SPEED = 400.0f;
    
    /** Daño de la carga frontal */
    public static float BRUISER_CHARGE_DAMAGE = 40.0f;
    
    /** Duración de la carga (segundos) */
    public static float BRUISER_CHARGE_DURATION = 2.0f;
    
    /** Cooldown de la carga (segundos) */
    public static float BRUISER_CHARGE_COOLDOWN = 6.0f;
    
    /** Distancia mínima para activar carga */
    public static float BRUISER_CHARGE_MIN_DISTANCE = 150.0f;
    
    /** XP otorgado por el Aplastador */
    public static int XP_BRUISER_BOSS = 500;
    
    /** Oleada en la que aparece el Aplastador */
    public static int BRUISER_BOSS_WAVE = 5;
    
    // ==================== JEFE - EL INFECTADOR (INFECTOR) - OLEADA 20 ====================
    
    /** Salud del Infectador (media-alta) */
    public static float INFECTOR_BOSS_HEALTH = 550.0f;
    
    /** Velocidad del Infectador (media) */
    public static float INFECTOR_BOSS_SPEED = 80.0f;
    
    /** Tamaño del Infectador */
    public static int INFECTOR_BOSS_SIZE = 66;
    
    /** Daño por contacto del Infectador (medio) */
    public static float INFECTOR_BOSS_CONTACT_DAMAGE = 25.0f;
    
    /** Radio de la nube tóxica periódica */
    public static float INFECTOR_TOXIC_CLOUD_RADIUS = 100.0f;
    
    /** Daño por segundo de la nube tóxica */
    public static float INFECTOR_TOXIC_CLOUD_DAMAGE = 10.0f;
    
    /** Intervalo de tick de la nube tóxica (segundos) */
    public static float INFECTOR_TOXIC_TICK_RATE = 0.5f;
    
    /** Daño de la bomba química */
    public static float INFECTOR_CHEMICAL_BOMB_DAMAGE = 30.0f;
    
    /** Radio de la bomba química */
    public static float INFECTOR_CHEMICAL_BOMB_RADIUS = 70.0f;
    
    /** Duración de la poza tóxica (segundos) */
    public static float INFECTOR_TOXIC_POOL_DURATION = 6.0f;
    
    /** Daño por segundo de la poza tóxica */
    public static float INFECTOR_TOXIC_POOL_DAMAGE = 8.0f;
    
    /** Cooldown de la bomba química (segundos) */
    public static float INFECTOR_BOMB_COOLDOWN = 3.0f;
    
    /** Radio de la explosión final al morir */
    public static float INFECTOR_DEATH_EXPLOSION_RADIUS = 100.0f;
    
    /** Daño de la explosión final */
    public static float INFECTOR_DEATH_EXPLOSION_DAMAGE = 50.0f;
    
    /** XP otorgado por el Infectador */
    public static int XP_INFECTOR_BOSS = 750;
    
    /** Oleada en la que aparece el Infectador */
    public static int INFECTOR_BOSS_WAVE = 10;
}
