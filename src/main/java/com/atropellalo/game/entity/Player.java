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
    
    private static final String SPRITE_PATH = "/img/player.png";
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
    private boolean godMode;
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
    
    // Hitbox rectangular rotable
    private float[] hitboxCorners; // 8 valores: x1,y1, x2,y2, x3,y3, x4,y4
    
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
        this.godMode = false;
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
        this.hitboxCorners = new float[8];
        updateHitbox();
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
        AnimationState newState = isMoving ? AnimationState.MOVING : AnimationState.IDLE;
        if (newState != currentAnimState && currentAnimState != AnimationState.DEATH) {
            currentAnimState = newState;
            animations.get(currentAnimState).reset();
        }
        
        // Actualizar animación actual
        animations.get(currentAnimState).update(deltaTime);
        
        // Consumir combustible solo si hay y se está moviendo
        if (isMoving && fuel > 0) {
            fuel -= GameConfig.FUEL_CONSUMPTION_RATE * deltaTime;
            if (fuel < 0) {
                fuel = 0;
            }
        }
        
        // El jugador puede moverse siempre (con o sin combustible)
        if (isMoving) {
            // Calcular nueva posición
            float newX = x + velocityX * deltaTime;
            float newY = y + velocityY * deltaTime;
            
            // Limitar al mundo (usando las dimensiones rectangulares)
            newX = Math.max(0, Math.min(newX, GameConfig.WORLD_WIDTH - GameConfig.PLAYER_WIDTH));
            newY = Math.max(0, Math.min(newY, GameConfig.WORLD_HEIGHT - GameConfig.PLAYER_HEIGHT));
            
            // Verificar colisiones si hay callback configurado
            if (collisionCallback != null) {
                if (collisionCallback.checkCollision(newX, newY, GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT)) {
                    // Resolver colisión
                    float[] resolved = collisionCallback.resolveCollision(
                        x, y, newX, newY, GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT
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
            
            // Actualizar hitbox
            updateHitbox();
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
            
            // Renderizar sprite con rotación (centrado en el mismo punto que la hitbox)
            currentAnim.renderScaled(g2d, 
                                     getHitboxCenterX(), 
                                     getHitboxCenterY(), 
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
     * Si no hay combustible, la velocidad se reduce según NO_FUEL_SPEED_PENALTY.
     * @param moveX Dirección horizontal (-1, 0, 1)
     * @param moveY Dirección vertical (-1, 0, 1)
     */
    public void setMovement(int moveX, int moveY) {
        // Calcular velocidad efectiva (reducida si no hay combustible)
        float effectiveSpeed = speed;
        if (fuel <= 0) {
            effectiveSpeed = speed * (1.0f - GameConfig.NO_FUEL_SPEED_PENALTY);
        }
        
        velocityX = moveX * effectiveSpeed;
        velocityY = moveY * effectiveSpeed;
        
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
        if (godMode) {
            return; // No recibir daño en modo dios
        }
        
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
     * Obtiene el ancho del jugador.
     */
    public int getWidth() {
        return GameConfig.PLAYER_WIDTH;
    }
    
    /**
     * Obtiene el alto del jugador.
     */
    public int getHeight() {
        return GameConfig.PLAYER_HEIGHT;
    }
    
    /**
     * Obtiene la rotación actual del jugador en radianes.
     */
    public float getRotation() {
        return rotation;
    }
    
    /**
     * Actualiza las esquinas de la hitbox rectangular rotada.
     * La hitbox rota alrededor del centro del sprite visual.
     */
    private void updateHitbox() {
        // Centro basado en el sprite visual (32x32)
        float centerX = getHitboxCenterX();
        float centerY = getHitboxCenterY();
        
        // Dimensiones de la hitbox (70x145)
        float halfWidth = GameConfig.PLAYER_WIDTH / 2f;
        float halfHeight = GameConfig.PLAYER_HEIGHT / 2f;
        
        float cos = (float) Math.cos(rotation);
        float sin = (float) Math.sin(rotation);
        
        // Esquina superior izquierda (en orientación sin rotar)
        float localX = -halfWidth;
        float localY = -halfHeight;
        hitboxCorners[0] = centerX + (localX * cos - localY * sin);
        hitboxCorners[1] = centerY + (localX * sin + localY * cos);
        
        // Esquina superior derecha
        localX = halfWidth;
        localY = -halfHeight;
        hitboxCorners[2] = centerX + (localX * cos - localY * sin);
        hitboxCorners[3] = centerY + (localX * sin + localY * cos);
        
        // Esquina inferior derecha
        localX = halfWidth;
        localY = halfHeight;
        hitboxCorners[4] = centerX + (localX * cos - localY * sin);
        hitboxCorners[5] = centerY + (localX * sin + localY * cos);
        
        // Esquina inferior izquierda
        localX = -halfWidth;
        localY = halfHeight;
        hitboxCorners[6] = centerX + (localX * cos - localY * sin);
        hitboxCorners[7] = centerY + (localX * sin + localY * cos);
    }
    
    /**
     * Obtiene las esquinas de la hitbox rotada.
     * @return Array de 8 floats: [x1,y1, x2,y2, x3,y3, x4,y4]
     */
    public float[] getHitboxCorners() {
        return hitboxCorners;
    }
    
    /**
     * Verifica si un punto está dentro de la hitbox del jugador.
     * Usa el algoritmo de ray casting para detección de punto en polígono.
     * @param px Coordenada X del punto
     * @param py Coordenada Y del punto
     * @return true si el punto está dentro de la hitbox
     */
    public boolean containsPoint(float px, float py) {
        int intersections = 0;
        
        // Ray casting algorithm - cuenta cuántas veces un rayo horizontal cruza los bordes
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4;
            
            float x1 = hitboxCorners[i * 2];
            float y1 = hitboxCorners[i * 2 + 1];
            float x2 = hitboxCorners[j * 2];
            float y2 = hitboxCorners[j * 2 + 1];
            
            // Verifica si el rayo horizontal desde el punto cruza este borde
            if (((y1 > py) != (y2 > py)) &&
                (px < (x2 - x1) * (py - y1) / (y2 - y1) + x1)) {
                intersections++;
            }
        }
        
        // Si el número de intersecciones es impar, el punto está dentro
        return (intersections % 2) == 1;
    }
    
    /**
     * Verifica si un círculo colisiona con la hitbox del jugador.
     * @param cx Centro X del círculo
     * @param cy Centro Y del círculo
     * @param radius Radio del círculo
     * @return true si hay colisión
     */
    public boolean collidesWithCircle(float cx, float cy, float radius) {
        // Primero verifica si el centro está dentro
        if (containsPoint(cx, cy)) {
            return true;
        }
        
        // Verifica si el círculo intersecta con algún borde del rectángulo
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4;
            
            float x1 = hitboxCorners[i * 2];
            float y1 = hitboxCorners[i * 2 + 1];
            float x2 = hitboxCorners[j * 2];
            float y2 = hitboxCorners[j * 2 + 1];
            
            // Distancia del centro del círculo al segmento de línea
            float dist = distanceToSegment(cx, cy, x1, y1, x2, y2);
            if (dist <= radius) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Calcula la distancia mínima de un punto a un segmento de línea.
     */
    private float distanceToSegment(float px, float py, float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float lengthSquared = dx * dx + dy * dy;
        
        if (lengthSquared == 0) {
            // El segmento es un punto
            return (float) Math.sqrt((px - x1) * (px - x1) + (py - y1) * (py - y1));
        }
        
        // Proyección del punto en la línea
        float t = Math.max(0, Math.min(1, ((px - x1) * dx + (py - y1) * dy) / lengthSquared));
        
        float projX = x1 + t * dx;
        float projY = y1 + t * dy;
        
        float distX = px - projX;
        float distY = py - projY;
        
        return (float) Math.sqrt(distX * distX + distY * distY);
    }
    
    /**
     * Obtiene la distancia mínima de un punto a la hitbox.
     * @param px Coordenada X del punto
     * @param py Coordenada Y del punto
     * @return Distancia mínima a la hitbox
     */
    public float distanceToHitbox(float px, float py) {
        // Si el punto está dentro, la distancia es 0
        if (containsPoint(px, py)) {
            return 0;
        }
        
        // Buscar la distancia mínima a todos los bordes
        float minDistance = Float.MAX_VALUE;
        
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4;
            
            float x1 = hitboxCorners[i * 2];
            float y1 = hitboxCorners[i * 2 + 1];
            float x2 = hitboxCorners[j * 2];
            float y2 = hitboxCorners[j * 2 + 1];
            
            float dist = distanceToSegment(px, py, x1, y1, x2, y2);
            minDistance = Math.min(minDistance, dist);
        }
        
        return minDistance;
    }
    
    /**
     * Calcula el vector de empuje para separar un círculo que colisiona con la hitbox.
     * @param cx Centro X del círculo
     * @param cy Centro Y del círculo
     * @param radius Radio del círculo
     * @return Array [dx, dy] con el vector de empuje para alejar el círculo
     */
    public float[] getPushVector(float cx, float cy, float radius) {
        float[] result = new float[2];
        
        // Encontrar el punto más cercano en la hitbox al centro del círculo
        float minDist = Float.MAX_VALUE;
        float closestX = 0, closestY = 0;
        
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4;
            
            float x1 = hitboxCorners[i * 2];
            float y1 = hitboxCorners[i * 2 + 1];
            float x2 = hitboxCorners[j * 2];
            float y2 = hitboxCorners[j * 2 + 1];
            
            // Encontrar punto más cercano en el segmento
            float dx = x2 - x1;
            float dy = y2 - y1;
            float lengthSquared = dx * dx + dy * dy;
            
            if (lengthSquared == 0) {
                // El segmento es un punto
                float dist = (float) Math.sqrt((cx - x1) * (cx - x1) + (cy - y1) * (cy - y1));
                if (dist < minDist) {
                    minDist = dist;
                    closestX = x1;
                    closestY = y1;
                }
                continue;
            }
            
            float t = Math.max(0, Math.min(1, ((cx - x1) * dx + (cy - y1) * dy) / lengthSquared));
            
            float projX = x1 + t * dx;
            float projY = y1 + t * dy;
            
            float distX = cx - projX;
            float distY = cy - projY;
            float dist = (float) Math.sqrt(distX * distX + distY * distY);
            
            if (dist < minDist) {
                minDist = dist;
                closestX = projX;
                closestY = projY;
            }
        }
        
        // Vector desde el punto más cercano hacia el centro del círculo
        float dx = cx - closestX;
        float dy = cy - closestY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        
        // Calcular cuánto debe moverse el círculo para no colisionar
        float overlap = radius - dist;
        
        if (overlap > 0 && dist > 0.001f) {
            // Normalizar el vector y multiplicar por la distancia de penetración + margen
            float pushAmount = overlap + 2; // +2 pixeles de margen
            result[0] = (dx / dist) * pushAmount;
            result[1] = (dy / dist) * pushAmount;
        } else if (containsPoint(cx, cy)) {
            // El centro está dentro de la hitbox, empujar hacia afuera con más fuerza
            if (dist > 0.001f) {
                float pushAmount = radius + 5; // Empujar completamente afuera
                result[0] = (dx / dist) * pushAmount;
                result[1] = (dy / dist) * pushAmount;
            } else {
                // Caso extremo: centro exactamente en el borde, empujar hacia afuera del player
                float toCenterX = cx - (x + GameConfig.PLAYER_SIZE / 2f);
                float toCenterY = cy - (y + GameConfig.PLAYER_SIZE / 2f);
                float toCenterDist = (float) Math.sqrt(toCenterX * toCenterX + toCenterY * toCenterY);
                if (toCenterDist > 0.001f) {
                    result[0] = (toCenterX / toCenterDist) * (radius + 5);
                    result[1] = (toCenterY / toCenterDist) * (radius + 5);
                }
            }
        }
        
        return result;
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
    
    /**
     * Obtiene el centro X de la hitbox.
     */
    private float getHitboxCenterX() {
        return x + GameConfig.PLAYER_SIZE / 2f;
    }
    
    /**
     * Obtiene el centro Y de la hitbox.
     */
    private float getHitboxCenterY() {
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
     * Obtiene la cantidad de XP necesaria para el siguiente nivel.
     * Alias para compatibilidad con el sistema de debug.
     * @return XP necesaria para el siguiente nivel
     */
    public int getXPForNextLevel() {
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
    
    public boolean isGodMode() {
        return godMode;
    }
    
    public void setGodMode(boolean godMode) {
        this.godMode = godMode;
    }
}
