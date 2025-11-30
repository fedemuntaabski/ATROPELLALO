package com.atropellalo.game.enemy;

/**
 * Tipos de enemigos disponibles en el juego.
 */
public enum EnemyType {
    /** Zombie rápido - Alta velocidad, baja salud */
    FAST,
    /** Zombie lento - Baja velocidad, alta salud */
    SLOW,
    /** Zombie explosivo - Explota al morir, daña área */
    EXPLOSIVE,
    /** Zombie escupidor - Ataque a distancia con proyectiles corrosivos */
    SPITTER,
    /** Zombie buffer - Apoya a aliados cercanos con buffs */
    BUFFER,
    /** Zombie portador - Libera zombies rápidos al morir */
    BROOD_CARRIER,
    /** JEFE - El Aplastador (Oleada 10) - Tanque con golpe de terremoto y carga */
    BOSS_BRUISER,
    /** JEFE - El Infectador (Oleada 20) - Control de zonas con nubes tóxicas */
    BOSS_INFECTOR
}
