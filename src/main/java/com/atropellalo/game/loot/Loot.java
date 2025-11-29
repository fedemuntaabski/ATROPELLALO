package com.atropellalo.game.loot;

import java.awt.Graphics2D;

/**
 * Clase abstracta base para todos los items de loot.
 * Define el comportamiento común para combustible y chatarra.
 */
public abstract class Loot {
    
    protected float x;
    protected float y;
    protected int size;
    protected boolean collected;
    
    /**
     * Constructor base para items de loot.
     * @param x Posición X en el mundo
     * @param y Posición Y en el mundo
     * @param size Tamaño del item en píxeles
     */
    protected Loot(float x, float y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.collected = false;
    }
    
    /**
     * Renderiza el item de loot.
     * @param g2d Contexto gráfico
     */
    public abstract void render(Graphics2D g2d);
    
    /**
     * Obtiene el valor que este loot proporciona.
     * @return Valor numérico del beneficio
     */
    public abstract float getValue();
    
    /**
     * Obtiene el tipo de loot para identificación.
     * @return Tipo del loot
     */
    public abstract LootType getType();
    
    /**
     * Calcula la distancia al centro del loot desde un punto.
     * @param targetX Posición X del objetivo
     * @param targetY Posición Y del objetivo
     * @return Distancia en píxeles
     */
    public float distanceTo(float targetX, float targetY) {
        float centerX = x + size / 2f;
        float centerY = y + size / 2f;
        float dx = centerX - targetX;
        float dy = centerY - targetY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Marca el loot como recolectado.
     */
    public void collect() {
        this.collected = true;
    }
    
    /**
     * Verifica si el loot ha sido recolectado.
     * @return true si fue recolectado
     */
    public boolean isCollected() {
        return collected;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public int getSize() {
        return size;
    }
    
    public float getCenterX() {
        return x + size / 2f;
    }
    
    public float getCenterY() {
        return y + size / 2f;
    }
}
