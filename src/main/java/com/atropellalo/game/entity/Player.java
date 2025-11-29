package com.atropellalo.game.entity;

import com.atropellalo.game.config.GameConfig;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Representa al jugador en el juego.
 * Gestiona posición, movimiento, salud y combustible.
 */
public class Player {
    
    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    
    // Sistema de stats
    private float health;
    private float maxHealth;
    private float fuel;
    private float maxFuel;
    
    // Estado del jugador
    private boolean isMoving;
    private boolean isAlive;
    
    public Player(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.velocityX = 0;
        this.velocityY = 0;
        
        // Inicializar stats desde configuración
        this.maxHealth = GameConfig.PLAYER_MAX_HEALTH;
        this.health = GameConfig.PLAYER_INITIAL_HEALTH;
        this.maxFuel = GameConfig.PLAYER_MAX_FUEL;
        this.fuel = GameConfig.PLAYER_INITIAL_FUEL;
        
        this.isMoving = false;
        this.isAlive = true;
    }
    
    /**
     * Actualiza la posición del jugador según su velocidad.
     * Consume combustible si está en movimiento.
     * @param deltaTime Tiempo transcurrido desde el último update (en segundos)
     */
    public void update(float deltaTime) {
        if (!isAlive) {
            return;
        }
        
        // Consumir combustible si se está moviendo y hay combustible
        if (isMoving && fuel > 0) {
            fuel -= GameConfig.FUEL_CONSUMPTION_RATE * deltaTime;
            if (fuel < 0) {
                fuel = 0;
            }
            
            // Solo mover si hay combustible
            x += velocityX * deltaTime;
            y += velocityY * deltaTime;
            
            // Limitar al mundo
            x = Math.max(0, Math.min(x, GameConfig.WORLD_WIDTH - GameConfig.PLAYER_SIZE));
            y = Math.max(0, Math.min(y, GameConfig.WORLD_HEIGHT - GameConfig.PLAYER_SIZE));
        }
        
        // Verificar si el jugador muere
        if (health <= 0) {
            isAlive = false;
        }
    }
    
    /**
     * Renderiza el jugador como un cuadrado.
     */
    public void render(Graphics2D g2d) {
        // Color basado en estado de salud
        if (health > maxHealth * 0.5f) {
            g2d.setColor(Color.RED);
        } else if (health > maxHealth * 0.25f) {
            g2d.setColor(new Color(255, 140, 0)); // Naranja
        } else {
            g2d.setColor(new Color(139, 0, 0)); // Rojo oscuro
        }
        
        g2d.fillRect((int)x, (int)y, GameConfig.PLAYER_SIZE, GameConfig.PLAYER_SIZE);
        
        // Borde para mejor visibilidad
        g2d.setColor(Color.WHITE);
        g2d.drawRect((int)x, (int)y, GameConfig.PLAYER_SIZE, GameConfig.PLAYER_SIZE);
        
        // Indicador de combustible bajo
        if (fuel < maxFuel * 0.2f && fuel > 0) {
            g2d.setColor(Color.ORANGE);
            g2d.drawString("!", (int)x + GameConfig.PLAYER_SIZE / 2 - 3, (int)y - 5);
        } else if (fuel <= 0) {
            g2d.setColor(Color.RED);
            g2d.drawString("X", (int)x + GameConfig.PLAYER_SIZE / 2 - 4, (int)y - 5);
        }
    }
    
    /**
     * Establece la velocidad del jugador basado en input.
     * @param moveX Dirección horizontal (-1, 0, 1)
     * @param moveY Dirección vertical (-1, 0, 1)
     */
    public void setMovement(int moveX, int moveY) {
        // No puede moverse sin combustible
        if (fuel <= 0) {
            velocityX = 0;
            velocityY = 0;
            isMoving = false;
            return;
        }
        
        velocityX = moveX * GameConfig.PLAYER_SPEED;
        velocityY = moveY * GameConfig.PLAYER_SPEED;
        
        // Normalizar velocidad diagonal
        if (moveX != 0 && moveY != 0) {
            float diagonal = (float) (1.0 / Math.sqrt(2.0));
            velocityX *= diagonal;
            velocityY *= diagonal;
        }
        
        isMoving = (moveX != 0 || moveY != 0);
    }
    
    /**
     * Añade combustible al jugador.
     * @param amount Cantidad de combustible a añadir
     */
    public void addFuel(float amount) {
        fuel = Math.min(fuel + amount, maxFuel);
    }
    
    /**
     * Cura al jugador.
     * @param amount Cantidad de salud a restaurar
     */
    public void heal(float amount) {
        health = Math.min(health + amount, maxHealth);
    }
    
    /**
     * Inflige daño al jugador.
     * @param amount Cantidad de daño
     */
    public void damage(float amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            isAlive = false;
        }
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public int getSize() {
        return GameConfig.PLAYER_SIZE;
    }
    
    /**
     * Obtiene el centro X del jugador (para la cámara).
     */
    public float getCenterX() {
        return x + GameConfig.PLAYER_SIZE / 2f;
    }
    
    /**
     * Obtiene el centro Y del jugador (para la cámara).
     */
    public float getCenterY() {
        return y + GameConfig.PLAYER_SIZE / 2f;
    }
    
    // Getters para stats
    
    public float getHealth() {
        return health;
    }
    
    public float getMaxHealth() {
        return maxHealth;
    }
    
    public float getFuel() {
        return fuel;
    }
    
    public float getMaxFuel() {
        return maxFuel;
    }
    
    public boolean isAlive() {
        return isAlive;
    }
    
    public boolean isMoving() {
        return isMoving;
    }
    
    public boolean hasFuel() {
        return fuel > 0;
    }
}
