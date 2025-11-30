package com.atropellalo.game.entity;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.sprite.Animation;
import com.atropellalo.game.sprite.AnimationState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Representa al jugador en el juego.
 * Gestiona posición, movimiento, salud, combustible y experiencia.
 * Usa sprite personalizado Player.png.
 */
public class Player {
    
    private static final String SPRITE_PATH = "/images/Player.png";
    private static final float SPRITE_SCALE = 4.5f;
    
    // Sprite compartido
    private static BufferedImage sprite = null;
    private static int spriteWidth = 0;
    private static int spriteHeight = 0;
    
    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    
    // Sistema de stats
    private float health;
    private float maxHealth;
    private float fuel;
    private float maxFuel;
    private float speed;
    
    // Sistema de experiencia y niveles
    private int currentXP;
    private int level;
    private int xpToNextLevel;
    
    // Estado del jugador
    private boolean isMoving;
    private boolean isAlive;
    
    // Sistema de animaciones
    private Map<AnimationState, Animation> animations;
    private AnimationState currentAnimState;
    private float rotation; // Rotación en radianes
    
    // Callback para notificar subida de nivel
    private LevelUpCallback levelUpCallback;
    
    // Callback para verificar colisiones con el mapa
    private CollisionCallback collisionCallback;
    
    /**
     * Interface para notificar cuando el jugador sube de nivel.
     */
    public interface LevelUpCallback {
        void onLevelUp(int newLevel);
    }
    
    /**
     * Interface para verificar colisiones con el entorno.
     */
    public interface CollisionCallback {
        /**
         * Verifica si hay colisión en una posición.
         * @param x Coordenada X
         * @param y Coordenada Y
         * @param width Ancho del objeto
         * @param height Alto del objeto
         * @return true si hay colisión
         */
        boolean checkCollision(float x, float y, float width, float height);
        
        /**
         * Resuelve una colisión y devuelve la posición corregida.
         * @param oldX Posición X anterior
         * @param oldY Posición Y anterior
         * @param newX Posición X nueva
         * @param newY Posición Y nueva
         * @param width Ancho del objeto
         * @param height Alto del objeto
         * @return Array con [x, y] corregidos
         */
        float[] resolveCollision(float oldX, float oldY, float newX, float newY, 
                                  float width, float height);
    }
    
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
        this.speed = GameConfig.PLAYER_SPEED;
        
        // Inicializar sistema de niveles
        this.currentXP = 0;
        this.level = 1;
        this.xpToNextLevel = GameConfig.XP_BASE_TO_LEVEL_UP;
        
        this.isMoving = false;
        this.isAlive = true;
        
        loadSprite();
        initializeAnimations();
        this.currentAnimState = AnimationState.IDLE;
        this.rotation = 0; // Mirando hacia arriba por defecto
    }
    
    /**
     * Carga el sprite desde recursos (lazy loading).
     */
    private void loadSprite() {
        if (sprite == null) {
            try {
                sprite = ImageIO.read(getClass().getResourceAsStream(SPRITE_PATH));
                if (sprite != null) {
                    spriteWidth = sprite.getWidth();
                    spriteHeight = sprite.getHeight();
                }
            } catch (IOException e) {
                System.err.println("Error cargando " + SPRITE_PATH + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Inicializa las animaciones con el sprite cargado.
     */
    private void initializeAnimations() {
        if (sprite != null) {
            this.animations = new java.util.HashMap<>();
            this.animations.put(AnimationState.IDLE, new Animation(sprite));
            this.animations.put(AnimationState.MOVING, new Animation(sprite));
            this.animations.put(AnimationState.DEATH, new Animation(sprite));
        }
    }
    
    /**
     * Establece el callback para notificaciones de subida de nivel.
     * @param callback Callback a invocar
     */
    public void setLevelUpCallback(LevelUpCallback callback) {
        this.levelUpCallback = callback;
    }
    
    /**
     * Establece el callback para verificar colisiones con el entorno.
     * @param callback Callback de colisión
     */
    public void setCollisionCallback(CollisionCallback callback) {
        this.collisionCallback = callback;
    }
    
    /**
     * Actualiza la posición del jugador según su velocidad.
     * Consume combustible si está en movimiento.
     * Verifica colisiones con el entorno.
     * @param deltaTime Tiempo transcurrido desde el último update (en segundos)
     */
    public void update(float deltaTime) {
        if (!isAlive) {
            // Actualizar animación de muerte
            if (currentAnimState != AnimationState.DEATH) {
                currentAnimState = AnimationState.DEATH;
                animations.get(AnimationState.DEATH).reset();
            }
            animations.get(currentAnimState).update(deltaTime);
            return;
        }
        
        // Determinar estado de animación
        AnimationState newState = isMoving && fuel > 0 ? AnimationState.MOVING : AnimationState.IDLE;
        if (newState != currentAnimState && currentAnimState != AnimationState.DEATH) {
            currentAnimState = newState;
            animations.get(currentAnimState).reset();
        }
        
        // Actualizar animación actual
        animations.get(currentAnimState).update(deltaTime);
        
        // Consumir combustible si se está moviendo y hay combustible
        if (isMoving && fuel > 0) {
            fuel -= GameConfig.FUEL_CONSUMPTION_RATE * deltaTime;
            if (fuel < 0) {
                fuel = 0;
            }
            
            // Calcular nueva posición
            float newX = x + velocityX * deltaTime;
            float newY = y + velocityY * deltaTime;
            
            // Limitar al mundo
            newX = Math.max(0, Math.min(newX, GameConfig.WORLD_WIDTH - GameConfig.PLAYER_SIZE));
            newY = Math.max(0, Math.min(newY, GameConfig.WORLD_HEIGHT - GameConfig.PLAYER_SIZE));
            
            // Verificar colisiones si hay callback configurado
            if (collisionCallback != null) {
                if (collisionCallback.checkCollision(newX, newY, GameConfig.PLAYER_SIZE, GameConfig.PLAYER_SIZE)) {
                    // Resolver colisión
                    float[] resolved = collisionCallback.resolveCollision(
                        x, y, newX, newY, GameConfig.PLAYER_SIZE, GameConfig.PLAYER_SIZE
                    );
                    newX = resolved[0];
                    newY = resolved[1];
                }
            }
            
            // Actualizar rotación basada en velocidad
            if (velocityX != 0 || velocityY != 0) {
                rotation = (float) Math.atan2(velocityX, -velocityY); // Ajuste para que "arriba" sea 0
            }
            
            // Aplicar nueva posición
            x = newX;
            y = newY;
        }
        
        // Verificar si el jugador muere
        if (health <= 0) {
            isAlive = false;
        }
    }
    
    /**
     * Renderiza el jugador con sprites animados.
     */
    public void render(Graphics2D g2d) {
        Animation currentAnim = animations.get(currentAnimState);
        
        if (currentAnim != null && currentAnim.getCurrentFrame() != null) {
            // Calcular escala del sprite
            float scale = calculateScale();
            
            // Renderizar sprite con rotación (el sprite mantiene rotación)
            currentAnim.renderScaled(g2d, 
                                     x + GameConfig.PLAYER_SIZE / 2f, 
                                     y + GameConfig.PLAYER_SIZE / 2f, 
                                     rotation, 
                                     scale);
        } else {
            renderFallback(g2d);
        }
        
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
     * Calcula el factor de escala del sprite.
     * @return Factor de escala
     */
    private float calculateScale() {
        if (spriteWidth > 0 && spriteHeight > 0) {
            float maxDimension = Math.max(spriteWidth, spriteHeight);
            return ((float) GameConfig.PLAYER_SIZE / maxDimension) * SPRITE_SCALE;
        }
        return SPRITE_SCALE;
    }
    
    /**
     * Renderizado de respaldo cuando no hay sprites disponibles.
     */
    private void renderFallback(Graphics2D g2d) {
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
        
        velocityX = moveX * speed;
        velocityY = moveY * speed;
        
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
    
    /**
     * Añade experiencia al jugador.
     * @param amount Cantidad de XP a añadir
     * @return true si el jugador subió de nivel
     */
    public boolean addXP(int amount) {
        currentXP += amount;
        
        if (currentXP >= xpToNextLevel) {
            levelUp();
            return true;
        }
        
        return false;
    }
    
    /**
     * Procesa la subida de nivel.
     * Otorga combustible bonus y calcula XP para el siguiente nivel.
     */
    private void levelUp() {
        currentXP -= xpToNextLevel;
        level++;
        
        // Calcular XP requerido para el siguiente nivel (escalado exponencial)
        xpToNextLevel = (int) (GameConfig.XP_BASE_TO_LEVEL_UP * 
                               Math.pow(GameConfig.XP_LEVEL_SCALING, level - 1));
        
        // Bonificación de combustible al subir de nivel
        addFuel(GameConfig.LEVEL_UP_FUEL_BONUS);
        
        // Notificar callback
        if (levelUpCallback != null) {
            levelUpCallback.onLevelUp(level);
        }
    }
    
    // ==================== MÉTODOS DE MEJORA ====================
    
    /**
     * Aumenta la salud máxima del jugador.
     * @param amount Cantidad a aumentar
     */
    public void upgradeMaxHealth(float amount) {
        maxHealth += amount;
        health = Math.min(health + amount, maxHealth); // También restaura algo de vida
    }
    
    /**
     * Aumenta la velocidad del jugador.
     * @param amount Cantidad a aumentar
     */
    public void upgradeSpeed(float amount) {
        speed += amount;
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
    
    public float getSpeed() {
        return speed;
    }
    
    public int getCurrentXP() {
        return currentXP;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getXpToNextLevel() {
        return xpToNextLevel;
    }
    
    /**
     * Obtiene el porcentaje de progreso hacia el siguiente nivel.
     * @return Valor entre 0.0 y 1.0
     */
    public float getXPProgress() {
        return (float) currentXP / xpToNextLevel;
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
