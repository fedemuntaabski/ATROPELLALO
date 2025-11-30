package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Proyectil penetrante que atraviesa múltiples enemigos.
 * Utilizado por el Rifle de Francotirador (Sniper Railgun).
 * 
 * A diferencia del proyectil normal, este:
 * - Atraviesa enemigos hasta alcanzar el límite de penetración
 * - Registra enemigos ya dañados para no dañarlos múltiples veces
 * - Se renderiza como un proyectil rápido con estela corta
 */
public class PenetratingProjectile extends Projectile {
    
    /** Cantidad máxima de enemigos que puede atravesar */
    private int maxPenetration;
    
    /** Cantidad actual de enemigos atravesados */
    private int currentPenetration;
    
    /** Enemigos ya dañados por este proyectil */
    private final Set<Enemy> damagedEnemies;
    
    /** Posiciones anteriores para la estela (corta) */
    private float prevX;
    private float prevY;
    
    /** Color del proyectil */
    private static final Color BULLET_COLOR = new Color(255, 230, 150);
    private static final Color TRAIL_COLOR = new Color(255, 200, 100, 150);
    
    /** Ancho del proyectil para colisiones */
    private final float bulletWidth;
    
    /** Timer para el efecto de rastro */
    private float trailTimer;
    private static final float TRAIL_LENGTH = 25f;
    
    /**
     * Crea un nuevo proyectil penetrante.
     * @param startX Posición X inicial
     * @param startY Posición Y inicial
     * @param targetX Posición X del objetivo
     * @param targetY Posición Y del objetivo
     * @param damage Daño por enemigo
     * @param maxRange Rango máximo
     * @param maxPenetration Número de enemigos que puede atravesar
     * @param speed Velocidad del proyectil
     * @param size Tamaño visual del proyectil
     */
    public PenetratingProjectile(float startX, float startY, float targetX, float targetY,
                                  float damage, float maxRange, int maxPenetration,
                                  float speed, int size) {
        super(startX, startY, targetX, targetY, damage, maxRange, 0, speed, BULLET_COLOR, size);
        
        this.maxPenetration = maxPenetration;
        this.currentPenetration = 0;
        this.damagedEnemies = new HashSet<>();
        this.prevX = startX;
        this.prevY = startY;
        this.bulletWidth = GameConfig.SNIPER_RAY_WIDTH;
        this.trailTimer = 0;
    }
    
    @Override
    public void update(float deltaTime) {
        // Guardar posición anterior para la estela
        prevX = getX();
        prevY = getY();
        
        // Actualizar posición normalmente
        super.update(deltaTime);
        
        trailTimer += deltaTime;
    }
    
    /**
     * Verifica colisiones con enemigos y aplica daño con penetración.
     * El proyectil continúa después de impactar hasta alcanzar el límite.
     * @param enemies Lista de enemigos
     * @return true si impactó a algún enemigo
     */
    @Override
    public boolean checkCollisions(List<Enemy> enemies) {
        if (!isActive()) {
            return false;
        }
        
        boolean hitAny = false;
        
        for (Enemy enemy : enemies) {
            // Ignorar enemigos muertos o ya dañados
            if (!enemy.isAlive() || damagedEnemies.contains(enemy)) {
                continue;
            }
            
            // Verificar colisión con el proyectil
            float dx = getX() - enemy.getCenterX();
            float dy = getY() - enemy.getCenterY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            // Colisión usando tamaño del proyectil + radio del enemigo
            float collisionDistance = (bulletWidth / 2f) + (enemy.getSize() / 2f);
            
            if (distance <= collisionDistance) {
                // Aplicar daño
                enemy.takeDamage(getDamage());
                damagedEnemies.add(enemy);
                currentPenetration++;
                hitAny = true;
                
                // Verificar si alcanzó el límite de penetración
                if (currentPenetration >= maxPenetration) {
                    deactivate();
                    return true;
                }
            }
        }
        
        return hitAny;
    }
    
    /**
     * Renderiza el proyectil como una bala rápida con estela corta.
     * @param g2d Contexto gráfico
     */
    @Override
    public void render(Graphics2D g2d) {
        if (!isActive()) {
            return;
        }
        
        float currentX = getX();
        float currentY = getY();
        
        Graphics2D g2dCopy = (Graphics2D) g2d.create();
        
        // Calcular dirección para la estela
        float dx = currentX - prevX;
        float dy = currentY - prevY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (dist > 0) {
            // Normalizar y calcular punto de inicio de estela
            float trailStartX = currentX - (dx / dist) * TRAIL_LENGTH;
            float trailStartY = currentY - (dy / dist) * TRAIL_LENGTH;
            
            // Estela (línea fina que se desvanece)
            g2dCopy.setColor(TRAIL_COLOR);
            g2dCopy.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2dCopy.draw(new Line2D.Float(trailStartX, trailStartY, currentX, currentY));
        }
        
        // Proyectil principal (pequeño y brillante)
        int bulletSize = (int) bulletWidth;
        g2dCopy.setColor(BULLET_COLOR);
        g2dCopy.fillOval((int)(currentX - bulletSize/2), (int)(currentY - bulletSize/2), 
                         bulletSize, bulletSize);
        
        // Núcleo brillante
        int coreSize = bulletSize / 2;
        g2dCopy.setColor(Color.WHITE);
        g2dCopy.fillOval((int)(currentX - coreSize/2), (int)(currentY - coreSize/2), 
                         coreSize, coreSize);
        
        g2dCopy.dispose();
    }
    
    /**
     * Obtiene la cantidad de enemigos atravesados.
     * @return Número de penetraciones realizadas
     */
    public int getCurrentPenetration() {
        return currentPenetration;
    }
    
    /**
     * Obtiene la penetración máxima.
     * @return Penetración máxima configurada
     */
    public int getMaxPenetration() {
        return maxPenetration;
    }
    
    /**
     * Aumenta la penetración máxima (para mejoras).
     * @param amount Cantidad a aumentar
     */
    public void upgradePenetration(int amount) {
        this.maxPenetration += amount;
    }
}
