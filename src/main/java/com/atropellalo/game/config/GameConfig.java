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
    
    // ==================== ENEMIGOS - ZOMBIE ESCUPIDOR (SPITTER) ====================
    
    /** Velocidad del zombie escupidor (píxeles/segundo) */
    public static float SPITTER_ZOMBIE_SPEED = 60.0f;
    
    /** Salud del zombie escupidor */
    public static float SPITTER_ZOMBIE_HEALTH = 50.0f;
    
    /** Tamaño del zombie escupidor */
    public static int SPITTER_ZOMBIE_SIZE = 28;
    
    /** Daño del zombie escupidor por contacto */
    public static float SPITTER_ZOMBIE_DAMAGE = 6.0f;
    
    /** Daño del proyectil de ácido */
    public static float SPITTER_PROJECTILE_DAMAGE = 12.0f;
    
    /** Velocidad del proyectil de ácido */
    public static float SPITTER_PROJECTILE_SPEED = 180.0f;
    
    /** Rango de disparo del escupidor */
    public static float SPITTER_ATTACK_RANGE = 280.0f;
    
    /** Intervalo entre disparos (segundos) */
    public static float SPITTER_FIRE_RATE = 2.5f;
    
    /** Radio del charco corrosivo */
    public static float SPITTER_PUDDLE_RADIUS = 35.0f;
    
    /** Duración del charco corrosivo (segundos) */
    public static float SPITTER_PUDDLE_DURATION = 4.0f;
    
    /** Daño por segundo del charco corrosivo */
    public static float SPITTER_PUDDLE_DAMAGE = 8.0f;
    
    // ==================== ENEMIGOS - ZOMBIE BUFFER (WAR CRIER) ====================
    
    /** Velocidad del zombie buffer (píxeles/segundo) */
    public static float BUFFER_ZOMBIE_SPEED = 45.0f;
    
    /** Salud del zombie buffer */
    public static float BUFFER_ZOMBIE_HEALTH = 70.0f;
    
    /** Tamaño del zombie buffer */
    public static int BUFFER_ZOMBIE_SIZE = 32;
    
    /** Daño del zombie buffer por contacto (muy bajo) */
    public static float BUFFER_ZOMBIE_DAMAGE = 4.0f;
    
    /** Radio del aura de buff */
    public static float BUFFER_AURA_RADIUS = 150.0f;
    
    /** Bonus de velocidad para aliados (porcentaje) */
    public static float BUFFER_SPEED_BONUS = 0.20f;
    
    /** Bonus de daño para aliados (porcentaje) */
    public static float BUFFER_DAMAGE_BONUS = 0.15f;
    
    /** Duración del buff después de salir del aura (segundos) */
    public static float BUFFER_BUFF_DURATION = 3.0f;
    
    /** Intervalo de pulso del aura (segundos) */
    public static float BUFFER_AURA_PULSE_RATE = 0.5f;
    
    // ==================== ENEMIGOS - ZOMBIE PORTADOR (BROOD CARRIER) ====================
    
    /** Velocidad del zombie portador (píxeles/segundo) */
    public static float BROOD_CARRIER_SPEED = 35.0f;
    
    /** Salud del zombie portador (alta) */
    public static float BROOD_CARRIER_HEALTH = 120.0f;
    
    /** Tamaño del zombie portador */
    public static int BROOD_CARRIER_SIZE = 40;
    
    /** Daño del zombie portador por contacto (bajo) */
    public static float BROOD_CARRIER_DAMAGE = 8.0f;
    
    /** Cantidad de zombies rápidos generados al morir */
    public static int BROOD_CARRIER_SPAWN_COUNT = 6;
    
    /** Radio de spawn de los zombies pequeños */
    public static float BROOD_CARRIER_SPAWN_RADIUS = 50.0f;
    
    /** Factor de salud de los zombies generados (respecto al zombie rápido base) */
    public static float BROOD_CARRIER_SPAWN_HEALTH_SCALE = 0.5f;
    
    /** Factor de daño de los zombies generados */
    public static float BROOD_CARRIER_SPAWN_DAMAGE_SCALE = 0.6f;
    
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
    public static int EXPLOSIVE_ZOMBIE_SPAWN_CHANCE = 15;
    
    /** Probabilidad de zombie escupidor (0-100) */
    public static int SPITTER_ZOMBIE_SPAWN_CHANCE = 12;
    
    /** Probabilidad de zombie buffer (0-100) */
    public static int BUFFER_ZOMBIE_SPAWN_CHANCE = 8;
    
    /** Probabilidad de zombie portador (0-100) */
    public static int BROOD_CARRIER_SPAWN_CHANCE = 5;
    
    /** Oleada mínima para spawn de escupidores */
    public static int SPITTER_MIN_WAVE = 3;
    
    /** Oleada mínima para spawn de buffers */
    public static int BUFFER_MIN_WAVE = 4;
    
    /** Oleada mínima para spawn de portadores */
    public static int BROOD_CARRIER_MIN_WAVE = 6;
    
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
    
    // ==================== ARMAS - RIFLE DE FRANCOTIRADOR (SNIPER RAILGUN) ====================
    
    /** Daño del rifle de francotirador (muy alto - one-shot mayoría de enemigos) */
    public static float SNIPER_DAMAGE = 120.0f;
    
    /** Rango del rifle de francotirador (muy largo) */
    public static float SNIPER_RANGE = 500.0f;
    
    /** Delay entre disparos (muy alto - cadencia baja) */
    public static float SNIPER_FIRE_DELAY = 2.5f;
    
    /** Cantidad de enemigos que atraviesa el proyectil */
    public static int SNIPER_PENETRATION = 3;
    
    /** Velocidad del proyectil del francotirador */
    public static float SNIPER_PROJECTILE_SPEED = 800.0f;
    
    /** Tamaño del proyectil del francotirador */
    public static int SNIPER_PROJECTILE_SIZE = 12;
    
    /** Ancho del rayo del francotirador para colisiones */
    public static float SNIPER_RAY_WIDTH = 3.0f;
    
    /** Duración visual del rastro del francotirador (segundos) */
    public static float SNIPER_TRAIL_DURATION = 0.08f;
    
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
    
    /** XP base otorgado por zombie escupidor */
    public static int XP_SPITTER_ZOMBIE = 25;
    
    /** XP base otorgado por zombie buffer */
    public static int XP_BUFFER_ZOMBIE = 30;
    
    /** XP base otorgado por zombie portador */
    public static int XP_BROOD_CARRIER = 35;
    
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
    public static int MAX_WEAPONS = 4;
    
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
    public static float BRUISER_BOSS_HEALTH = 800.0f;
    
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
    public static int XP_BRUISER_BOSS = 550;
    
    /** Oleada en la que aparece el Aplastador */
    public static int BRUISER_BOSS_WAVE = 5;
    
    // ==================== JEFE - EL INFECTADOR (INFECTOR) - OLEADA 20 ====================
    
    /** Salud del Infectador (media-alta) */
    public static float INFECTOR_BOSS_HEALTH = 650.0f;
    
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
