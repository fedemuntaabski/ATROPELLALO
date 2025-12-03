package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Representa un proyectil disparado por un arma.
 * Se mueve en línea recta hacia una dirección y daña enemigos al contacto.
 */
public class Projectile {
    
    private static final String SPRITE_PATH = "/img/bullet.png";
    private static final float SPRITE_SCALE = 0.03f;
    private static BufferedImage sprite = null;
    private static int spriteWidth = 0;
    private static int spriteHeight = 0;
    
    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private float damage;
    private boolean active;
    private float distanceTraveled;
    private float maxRange;
    private float impactArea;
    private float speed;
    private Color color;
    private int size;
    
    /**
     * Crea un nuevo proyectil.
     * @param startX Posición X inicial
     * @param startY Posición Y inicial
     * @param targetX Posición X del objetivo
     * @param targetY Posición Y del objetivo
     * @param damage Daño que causa el proyectil
     * @param maxRange Rango máximo del proyectil
     */
    public Projectile(float startX, float startY, float targetX, float targetY, 
                     float damage, float maxRange) {
        this(startX, startY, targetX, targetY, damage, maxRange, 0, 
             GameConfig.PROJECTILE_SPEED, Color.YELLOW, GameConfig.PROJECTILE_SIZE);
        loadSprite();
    }
    
    /**
     * Crea un nuevo proyectil con área de impacto.
     * @param startX Posición X inicial
     * @param startY Posición Y inicial
     * @param targetX Posición X del objetivo
     * @param targetY Posición Y del objetivo
     * @param damage Daño que causa el proyectil
     * @param maxRange Rango máximo del proyectil
     * @param impactArea Radio de daño en área (0 para sin área)
     */
    public Projectile(float startX, float startY, float targetX, float targetY, 
                     float damage, float maxRange, float impactArea) {
        this(startX, startY, targetX, targetY, damage, maxRange, impactArea,
             GameConfig.PROJECTILE_SPEED, Color.YELLOW, GameConfig.PROJECTILE_SIZE);
        loadSprite();
    }
    
    /**
     * Constructor completo para proyectiles personalizados.
     */
    public Projectile(float startX, float startY, float targetX, float targetY, 
                     float damage, float maxRange, float impactArea,
                     float speed, Color color, int size) {
        this.x = startX;
        this.y = startY;
        this.damage = damage;
        this.active = true;
        this.distanceTraveled = 0;
        this.maxRange = maxRange;
        this.impactArea = impactArea;
        this.speed = speed;
        this.color = color;
        this.size = size;
        
        loadSprite();
        
        // Calcular dirección normalizada
        float dx = targetX - startX;
        float dy = targetY - startY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            this.velocityX = (dx / distance) * speed;
            this.velocityY = (dy / distance) * speed;
        } else {
            this.velocityX = 0;
            this.velocityY = 0;
            this.active = false;
        }
    }
    
    /**
     * Carga el sprite de la bala desde el archivo.
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
     * Calcula el tamaño de renderizado del sprite.
     */
    private int calculateScale() {
        if (sprite == null || spriteWidth == 0 || spriteHeight == 0) {
            return size;
        }
        int maxDimension = Math.max(spriteWidth, spriteHeight);
        return (int) (maxDimension * SPRITE_SCALE);
    }
    
    /**
     * Actualiza la posición del proyectil.
     * @param deltaTime Tiempo desde el último frame
     */
    public void update(float deltaTime) {
        if (!active) {
            return;
        }
        
        // Calcular movimiento
        float moveX = velocityX * deltaTime;
        float moveY = velocityY * deltaTime;
        
        x += moveX;
        y += moveY;
        
        // Actualizar distancia recorrida
        distanceTraveled += (float) Math.sqrt(moveX * moveX + moveY * moveY);
        
        // Desactivar si excede el rango
        if (distanceTraveled >= maxRange) {
            active = false;
        }
        
        // Desactivar si sale del mundo
        if (x < 0 || x > GameConfig.WORLD_WIDTH || y < 0 || y > GameConfig.WORLD_HEIGHT) {
            active = false;
        }
    }
    
    /**
     * Verifica colisiones con enemigos y aplica daño.
     * @param enemies Lista de enemigos
     * @return true si impactó a un enemigo
     */
    public boolean checkCollisions(List<Enemy> enemies) {
        if (!active) {
            return false;
        }
        
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            
            // Verificar colisión con el enemigo
            float dx = x - enemy.getCenterX();
            float dy = y - enemy.getCenterY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            // Colisión si la distancia es menor que la suma de los radios
            float collisionDistance = (size / 2f) + (enemy.getSize() / 2f);
            
            if (distance <= collisionDistance) {
                // Aplicar daño
                if (impactArea > 0) {
                    // Daño en área
                    applyAreaDamage(enemies);
                } else {
                    enemy.takeDamage(damage);
                }
                active = false;
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Aplica daño en área a todos los enemigos dentro del radio.
     * @param enemies Lista de enemigos
     */
    private void applyAreaDamage(List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            
            float dx = x - enemy.getCenterX();
            float dy = y - enemy.getCenterY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (distance <= impactArea) {
                // Daño disminuye con la distancia
                float damageMultiplier = 1.0f - (distance / impactArea) * 0.5f;
                enemy.takeDamage(damage * damageMultiplier);
            }
        }
    }
    
    /**
     * Renderiza el proyectil.
     * @param g2d Contexto gráfico
     */
    public void render(Graphics2D g2d) {
        if (!active) {
            return;
        }
        
        if (sprite != null) {
            int scale = calculateScale();
            int drawX = (int) (x - scale / 2);
            int drawY = (int) (y - scale / 2);
            
            // Calcular ángulo de rotación basado en la dirección del proyectil
            // Sumamos π/2 porque la imagen apunta hacia arriba (90 grados)
            double angle = Math.atan2(velocityY, velocityX) + Math.PI / 2;
            
            // Guardar transformación original
            AffineTransform oldTransform = g2d.getTransform();
            
            // Rotar el contexto gráfico
            g2d.rotate(angle, x, y);
            
            // Dibujar la bala rotada
            g2d.drawImage(sprite, drawX, drawY, scale, scale, null);
            
            // Restaurar transformación
            g2d.setTransform(oldTransform);
        } else {
            renderFallback(g2d);
        }
    }
    
    /**
     * Renderiza el proyectil usando gráficos procedurales como fallback.
     */
    private void renderFallback(Graphics2D g2d) {
        int drawX = (int)(x - size / 2);
        int drawY = (int)(y - size / 2);
        
        // Proyectil con color personalizado
        g2d.setColor(color);
        g2d.fillOval(drawX, drawY, size, size);
        
        // Borde más oscuro
        g2d.setColor(color.darker());
        g2d.drawOval(drawX, drawY, size, size);
    }
    
    /**
     * Verifica si el proyectil está activo.
     * @return true si está activo
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Desactiva el proyectil.
     */
    public void deactivate() {
        active = false;
    }
    
    // Getters
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getDamage() {
        return damage;
    }
    
    public float getImpactArea() {
        return impactArea;
    }
}
