package com.atropellalo.game.camera;

/**
 * Cámara que sigue al jugador y define qué parte del mundo se renderiza.
 * Maneja el offset de renderizado para crear el efecto de cámara.
 */
public class Camera {
    
    private float x;
    private float y;
    private final int viewportWidth;
    private final int viewportHeight;
    private final int worldWidth;
    private final int worldHeight;
    
    public Camera(int viewportWidth, int viewportHeight, int worldWidth, int worldHeight) {
        this.x = 0;
        this.y = 0;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }
    
    /**
     * Centra la cámara en una posición específica (generalmente el jugador).
     * @param targetX Coordenada X del objetivo
     * @param targetY Coordenada Y del objetivo
     */
    public void centerOn(float targetX, float targetY) {
        // Centrar la cámara en el objetivo
        x = targetX - viewportWidth / 2f;
        y = targetY - viewportHeight / 2f;
        
        // Limitar la cámara a los bordes del mundo
        x = Math.max(0, Math.min(x, worldWidth - viewportWidth));
        y = Math.max(0, Math.min(y, worldHeight - viewportHeight));
    }
    
    /**
     * Obtiene el offset X de la cámara para renderizado.
     */
    public int getOffsetX() {
        return (int) x;
    }
    
    /**
     * Obtiene el offset Y de la cámara para renderizado.
     */
    public int getOffsetY() {
        return (int) y;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
}
