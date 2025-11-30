package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.sprite.Animation;
import com.atropellalo.game.sprite.AnimationState;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Map;

/**
 * Clase abstracta base para todos los enemigos.
 * Implementa comportamiento de persecución hacia el jugador con A* pathfinding.
 */
public abstract class Enemy {
    
    protected float x;
    protected float y;
    protected float health;
    protected float maxHealth;
    protected float speed;
    protected int size;
    protected float damage;
    protected boolean alive;
    protected float damageCooldown;
    protected float xpMultiplier;
    
    // Sistema de animaciones
    protected Map<AnimationState, Animation> animations;
    protected AnimationState currentAnimState;
    protected float rotation; // Rotación basada en dirección de movimiento
    
    // Callback para verificar colisiones con el mapa
    protected CollisionChecker collisionChecker;
    
    // Sistema de fallback (movimiento directo sin pathfinding)
    protected float stuckTimer;                    // Tiempo que lleva bloqueado
    protected float avoidanceAngle;                // Ángulo actual de evasión
    protected boolean isAvoiding;                  // Si está en modo evasión
    protected float avoidanceTimer;                // Timer para modo evasión
    protected int avoidanceAttempts;               // Intentos de evasión
    protected float lastValidAngle;                // Último ángulo válido
    protected static final float STUCK_THRESHOLD = 0.2f;    // Tiempo antes de considerar "stuck"
    protected static final float AVOIDANCE_DURATION = 0.3f;  // Duración del movimiento de evasión
    protected static final int MAX_AVOIDANCE_ATTEMPTS = 8;   // Máximo de intentos
    protected float lastX, lastY;                  // Posición anterior para detectar bloqueo
    protected float totalStuckTime;                // Tiempo total bloqueado para teleport de emergencia
    
    /**
     * Interface para verificar colisiones con el entorno.
     */
    public interface CollisionChecker {
        boolean checkCollision(float x, float y, float width, float height);
    }
    
    /**
     * Constructor base para enemigos.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param health Salud del enemigo
     * @param speed Velocidad de movimiento
     * @param size Tamaño del enemigo
     * @param damage Daño al jugador por contacto
     */
    protected Enemy(float x, float y, float health, float speed, int size, float damage) {
        this(x, y, health, speed, size, damage, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Constructor con factores de escalado.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param health Salud base del enemigo
     * @param speed Velocidad base de movimiento
     * @param size Tamaño del enemigo
     * @param damage Daño base al jugador por contacto
     * @param healthScale Factor de escalado de salud
     * @param speedScale Factor de escalado de velocidad
     * @param damageScale Factor de escalado de daño
     */
    protected Enemy(float x, float y, float health, float speed, int size, float damage,
                   float healthScale, float speedScale, float damageScale) {
        this.x = x;
        this.y = y;
        this.health = health * healthScale;
        this.maxHealth = health * healthScale;
        this.speed = speed * speedScale;
        this.size = size;
        this.damage = damage * damageScale;
        this.alive = true;
        this.damageCooldown = 0;
        this.xpMultiplier = (healthScale + speedScale + damageScale) / 3.0f;
        this.collisionChecker = null;
        
        // Inicializar sistema de movimiento directo (sin pathfinding en estacionamiento)
        this.stuckTimer = 0;
        this.avoidanceAngle = 0;
        this.isAvoiding = false;
        this.avoidanceTimer = 0;
        this.avoidanceAttempts = 0;
        this.lastValidAngle = 0;
        this.lastX = x;
        this.lastY = y;
        this.totalStuckTime = 0;
        
        // Inicializar sistema de animaciones (las subclases deben cargar sus animaciones)
        this.animations = null;
        this.currentAnimState = AnimationState.IDLE;
        this.rotation = 0;
    }
    
    /**
     * Establece el verificador de colisiones con el mapa.
     * @param checker Verificador de colisiones
     */
    public void setCollisionChecker(CollisionChecker checker) {
        this.collisionChecker = checker;
    }
    
    /**
     * Actualiza el estado del enemigo.
     * @param deltaTime Tiempo desde el último frame
     * @param playerX Posición X del jugador
     * @param playerY Posición Y del jugador
     */
    public void update(float deltaTime, float playerX, float playerY) {
        if (!alive) {
            // Actualizar animación de muerte
            updateDeathAnimation(deltaTime);
            return;
        }
        
        // Actualizar cooldown de daño
        if (damageCooldown > 0) {
            damageCooldown -= deltaTime;
        }
        
        // Guardar posición anterior para calcular movimiento
        float oldX = x;
        float oldY = y;
        
        // Perseguir al jugador directamente (sin pathfinding en el estacionamiento abierto)
        moveTowardsDirect(playerX, playerY, deltaTime);
        
        // Actualizar estado de animación basado en movimiento
        updateAnimationState(deltaTime, oldX, oldY, playerX, playerY);
        
        // Limitar al mundo
        x = Math.max(0, Math.min(x, GameConfig.WORLD_WIDTH - size));
        y = Math.max(0, Math.min(y, GameConfig.WORLD_HEIGHT - size));
    }
    
    /**
     * Actualiza el estado de la animación y la rotación.
     */
    protected void updateAnimationState(float deltaTime, float oldX, float oldY, float playerX, float playerY) {
        // Calcular si se está moviendo
        float dx = x - oldX;
        float dy = y - oldY;
        boolean moving = (Math.abs(dx) > 0.01f || Math.abs(dy) > 0.01f);
        
        // Actualizar rotación basada en dirección hacia el jugador
        float dirX = playerX - getCenterX();
        float dirY = playerY - getCenterY();
        if (dirX != 0 || dirY != 0) {
            rotation = (float) Math.atan2(dirX, -dirY); // Ajuste para que "arriba" sea 0
        }
        
        // Cambiar estado de animación
        AnimationState newState = moving ? AnimationState.MOVING : AnimationState.IDLE;
        if (newState != currentAnimState) {
            currentAnimState = newState;
            if (animations != null && animations.containsKey(currentAnimState)) {
                animations.get(currentAnimState).reset();
            }
        }
        
        // Actualizar animación actual
        if (animations != null && animations.containsKey(currentAnimState)) {
            animations.get(currentAnimState).update(deltaTime);
        }
    }
    
    /**
     * Actualiza la animación de muerte.
     */
    protected void updateDeathAnimation(float deltaTime) {
        if (animations != null && animations.containsKey(AnimationState.DEATH)) {
            if (currentAnimState != AnimationState.DEATH) {
                currentAnimState = AnimationState.DEATH;
                animations.get(AnimationState.DEATH).reset();
            }
            animations.get(AnimationState.DEATH).update(deltaTime);
        }
    }
    
    /**
     * Verifica si la animación de muerte ha terminado.
     */
    public boolean isDeathAnimationFinished() {
        if (animations != null && animations.containsKey(AnimationState.DEATH)) {
            return animations.get(AnimationState.DEATH).isFinished();
        }
        return true; // Si no hay animación, considerar terminada
    }
    
    /**
     * Mueve al enemigo directamente hacia el objetivo (sin pathfinding).
     * El estacionamiento es un espacio abierto sin obstáculos.
     */
    protected void moveTowardsDirect(float targetX, float targetY, float deltaTime) {
        float dx = targetX - getCenterX();
        float dy = targetY - getCenterY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 1) {
            // Normalizar dirección y aplicar velocidad
            float normalizedDx = dx / distance;
            float normalizedDy = dy / distance;
            
            float moveSpeed = speed * deltaTime;
            x += normalizedDx * moveSpeed;
            y += normalizedDy * moveSpeed;
        }
    }
    
    /**
     * Actualiza el path A* hacia el jugador.
     * Renderiza el enemigo.
     * @param g2d Contexto gráfico
     */
    public abstract void render(Graphics2D g2d);
    
    /**
     * Método auxiliar para renderizar usando el sistema de animaciones.
     * Las subclases pueden llamar a este método o implementar su propio render.
     * @param g2d Contexto gráfico
     * @param scale Factor de escala para el sprite
     */
    protected void renderWithAnimation(Graphics2D g2d, float scale) {
        if (animations != null && animations.containsKey(currentAnimState)) {
            Animation anim = animations.get(currentAnimState);
            if (anim.getCurrentFrame() != null) {
                anim.renderScaled(g2d, getCenterX(), getCenterY(), rotation, scale);
                return;
            }
        }
        // Si no hay animación, no renderizar nada (las subclases deben manejar el fallback)
    }
    
    /**
     * Renderiza la barra de vida sobre el enemigo.
     * @param g2d Contexto gráfico
     */
    protected void renderHealthBar(Graphics2D g2d) {
        if (health >= maxHealth) {
            return; // No mostrar si tiene vida completa
        }
        
        int barWidth = size;
        int barHeight = 4;
        int barX = (int) x;
        int barY = (int) y - 8;
        
        // Fondo
        g2d.setColor(java.awt.Color.DARK_GRAY);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        // Vida actual
        float healthPercent = health / maxHealth;
        g2d.setColor(healthPercent > 0.5f ? java.awt.Color.GREEN : 
                    (healthPercent > 0.25f ? java.awt.Color.YELLOW : java.awt.Color.RED));
        g2d.fillRect(barX, barY, (int)(barWidth * healthPercent), barHeight);
    }
    
    /**
     * Obtiene el tipo de enemigo.
     * @return Tipo del enemigo
     */
    public abstract EnemyType getType();
    
    /**
     * Llamado cuando el enemigo muere.
     * Permite comportamiento especial (como explosiones).
     */
    public void onDeath() {
        alive = false;
    }
    
    /**
     * Inflige daño al enemigo.
     * @param amount Cantidad de daño
     */
    public void takeDamage(float amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            onDeath();
        }
    }
    
    /**
     * Verifica si puede hacer daño (cooldown).
     * @return true si puede dañar
     */
    public boolean canDamage() {
        return damageCooldown <= 0;
    }
    
    /**
     * Reinicia el cooldown de daño.
     */
    public void resetDamageCooldown() {
        damageCooldown = GameConfig.ENEMY_DAMAGE_COOLDOWN;
    }
    
    /**
     * Calcula la distancia al jugador.
     * @param playerX Centro X del jugador
     * @param playerY Centro Y del jugador
     * @return Distancia en píxeles
     */
    public float distanceToPlayer(float playerX, float playerY) {
        float dx = getCenterX() - playerX;
        float dy = getCenterY() - playerY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Calcula la distancia a otro enemigo.
     * @param other Otro enemigo
     * @return Distancia en píxeles
     */
    public float distanceToEnemy(Enemy other) {
        float dx = getCenterX() - other.getCenterX();
        float dy = getCenterY() - other.getCenterY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    // Getters
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getCenterX() {
        return x + size / 2f;
    }
    
    public float getCenterY() {
        return y + size / 2f;
    }
    
    public float getHealth() {
        return health;
    }
    
    public float getMaxHealth() {
        return maxHealth;
    }
    
    public int getSize() {
        return size;
    }
    
    public float getDamage() {
        return damage;
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public float getSpeed() {
        return speed;
    }
    
    /**
     * Obtiene el multiplicador de XP basado en el escalado del enemigo.
     * @return Multiplicador de XP
     */
    public float getXPMultiplier() {
        return xpMultiplier;
    }
}
