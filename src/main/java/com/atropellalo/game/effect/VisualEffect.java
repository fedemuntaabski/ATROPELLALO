package com.atropellalo.game.effect;

import java.awt.Graphics2D;

/**
 * Interfaz base para todos los efectos visuales del juego.
 */
public interface VisualEffect {
    
    /**
     * Actualiza el estado del efecto.
     * @param deltaTime Tiempo desde el último frame
     */
    void update(float deltaTime);
    
    /**
     * Renderiza el efecto.
     * @param g2d Contexto gráfico
     */
    void render(Graphics2D g2d);
    
    /**
     * Verifica si el efecto sigue activo.
     * @return true si el efecto debe seguir renderizándose
     */
    boolean isAlive();
    
    /**
     * Obtiene la posición X del efecto.
     * @return Posición X
     */
    float getX();
    
    /**
     * Obtiene la posición Y del efecto.
     * @return Posición Y
     */
    float getY();
}
