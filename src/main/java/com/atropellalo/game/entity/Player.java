package com.atropellalo.game.entity;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Representa al jugador en el juego.
 * Por ahora es un cuadrado simple para prototipo.
 */
public class Player {
    
    private static final int PLAYER_SIZE = 32;
    private static final float PLAYER_SPEED = 200.0f; // píxeles por segundo
    
    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    
    public Player(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.velocityX = 0;
        this.velocityY = 0;
    }
    
    /**
     * Actualiza la posición del jugador según su velocidad.
     * @param deltaTime Tiempo transcurrido desde el último update (en segundos)
     */
    public void update(float deltaTime) {
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;
    }
    
    /**
     * Renderiza el jugador como un cuadrado.
     */
    public void render(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.fillRect((int)x, (int)y, PLAYER_SIZE, PLAYER_SIZE);
        
        // Borde para mejor visibilidad
        g2d.setColor(Color.WHITE);
        g2d.drawRect((int)x, (int)y, PLAYER_SIZE, PLAYER_SIZE);
    }
    
    /**
     * Establece la velocidad del jugador basado en input.
     * @param moveX Dirección horizontal (-1, 0, 1)
     * @param moveY Dirección vertical (-1, 0, 1)
     */
    public void setMovement(int moveX, int moveY) {
        velocityX = moveX * PLAYER_SPEED;
        velocityY = moveY * PLAYER_SPEED;
        
        // Normalizar velocidad diagonal
        if (moveX != 0 && moveY != 0) {
            float diagonal = (float) (1.0 / Math.sqrt(2.0));
            velocityX *= diagonal;
            velocityY *= diagonal;
        }
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public int getSize() {
        return PLAYER_SIZE;
    }
    
    /**
     * Obtiene el centro X del jugador (para la cámara).
     */
    public float getCenterX() {
        return x + PLAYER_SIZE / 2f;
    }
    
    /**
     * Obtiene el centro Y del jugador (para la cámara).
     */
    public float getCenterY() {
        return y + PLAYER_SIZE / 2f;
    }
}
