package com.atropellalo.game.weapon;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sierras Circulares - Arma defensiva pasiva.
 * Dos sierras giratorias, una a cada lado del jugador.
 * Dañan a los enemigos que se acercan demasiado.
 */
public class CircularSaw extends Weapon {
    
    private static final Color SAW_COLOR = new Color(180, 180, 190);
    private static final Color SAW_EDGE_COLOR = new Color(220, 220, 230);
    private static final Color SAW_CENTER_COLOR = new Color(80, 80, 90);
    private static final int TEETH_COUNT = 12;
    
    private final Map<Enemy, Float> damageCooldowns;
    private float rotationAngle;
    
    /**
     * Crea nuevas sierras circulares con valores de configuración.
     */
    public CircularSaw() {
        super(
            GameConfig.SAW_DAMAGE,
            GameConfig.SAW_RADIUS,
            GameConfig.SAW_DAMAGE_COOLDOWN,
            2, // Dos sierras
            0,
            WeaponType.CIRCULAR_SAW
        );
        this.damageCooldowns = new HashMap<>();
        this.rotationAngle = 0;
    }
    
    @Override
    public List<Projectile> tryFire(float playerX, float playerY, List<Enemy> enemies) {
        // Las sierras no disparan proyectiles
        return new ArrayList<>();
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        rotationAngle += GameConfig.SAW_ROTATION_SPEED * deltaTime;
        
        // Actualizar cooldowns individuales
        damageCooldowns.entrySet().removeIf(entry -> {
            entry.setValue(entry.getValue() - deltaTime);
            return entry.getValue() <= 0 || !entry.getKey().isAlive();
        });
    }
    
    @Override
    public void processContinuousDamage(float deltaTime, float playerX, float playerY, List<Enemy> enemies) {
        // Calcular posiciones de las sierras (una a cada lado)
        float leftSawX = playerX - GameConfig.SAW_DISTANCE;
        float leftSawY = playerY;
        float rightSawX = playerX + GameConfig.SAW_DISTANCE;
        float rightSawY = playerY;
        
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            
            // Verificar colisión con sierra izquierda
            float dxLeft = enemy.getCenterX() - leftSawX;
            float dyLeft = enemy.getCenterY() - leftSawY;
            float distLeft = (float) Math.sqrt(dxLeft * dxLeft + dyLeft * dyLeft);
            
            // Verificar colisión con sierra derecha
            float dxRight = enemy.getCenterX() - rightSawX;
            float dyRight = enemy.getCenterY() - rightSawY;
            float distRight = (float) Math.sqrt(dxRight * dxRight + dyRight * dyRight);
            
            float collisionDist = GameConfig.SAW_RADIUS + enemy.getSize() / 2f;
            
            if (distLeft <= collisionDist || distRight <= collisionDist) {
                // Verificar cooldown individual para este enemigo
                if (!damageCooldowns.containsKey(enemy)) {
                    enemy.takeDamage(damage);
                    damageCooldowns.put(enemy, fireDelay);
                }
            }
        }
    }
    
    @Override
    public void render(Graphics2D g2d, float playerX, float playerY) {
        // Dibujar sierra izquierda
        renderSaw(g2d, playerX - GameConfig.SAW_DISTANCE, playerY);
        
        // Dibujar sierra derecha
        renderSaw(g2d, playerX + GameConfig.SAW_DISTANCE, playerY);
    }
    
    /**
     * Renderiza una sierra individual.
     */
    private void renderSaw(Graphics2D g2d, float x, float y) {
        float radius = GameConfig.SAW_RADIUS;
        
        Stroke oldStroke = g2d.getStroke();
        
        // Dibujar disco base
        g2d.setColor(SAW_COLOR);
        g2d.fillOval((int)(x - radius), (int)(y - radius), 
                     (int)(radius * 2), (int)(radius * 2));
        
        // Dibujar dientes de la sierra
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(SAW_EDGE_COLOR);
        
        for (int i = 0; i < TEETH_COUNT; i++) {
            float angle = rotationAngle + (float)(i * 2 * Math.PI / TEETH_COUNT);
            
            // Diente triangular
            float innerRadius = radius * 0.7f;
            float outerRadius = radius;
            float toothWidth = (float)(Math.PI / TEETH_COUNT);
            
            float x1 = x + (float)Math.cos(angle - toothWidth/2) * innerRadius;
            float y1 = y + (float)Math.sin(angle - toothWidth/2) * innerRadius;
            float x2 = x + (float)Math.cos(angle) * outerRadius;
            float y2 = y + (float)Math.sin(angle) * outerRadius;
            float x3 = x + (float)Math.cos(angle + toothWidth/2) * innerRadius;
            float y3 = y + (float)Math.sin(angle + toothWidth/2) * innerRadius;
            
            int[] xPoints = {(int)x1, (int)x2, (int)x3};
            int[] yPoints = {(int)y1, (int)y2, (int)y3};
            
            g2d.setColor(SAW_EDGE_COLOR);
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(SAW_COLOR.darker());
            g2d.drawPolygon(xPoints, yPoints, 3);
        }
        
        // Centro de la sierra
        float centerRadius = radius * 0.3f;
        g2d.setColor(SAW_CENTER_COLOR);
        g2d.fillOval((int)(x - centerRadius), (int)(y - centerRadius), 
                     (int)(centerRadius * 2), (int)(centerRadius * 2));
        
        // Agujero central
        float holeRadius = radius * 0.1f;
        g2d.setColor(Color.BLACK);
        g2d.fillOval((int)(x - holeRadius), (int)(y - holeRadius), 
                     (int)(holeRadius * 2), (int)(holeRadius * 2));
        
        // Borde externo
        g2d.setColor(SAW_COLOR.darker());
        g2d.drawOval((int)(x - radius), (int)(y - radius), 
                     (int)(radius * 2), (int)(radius * 2));
        
        g2d.setStroke(oldStroke);
    }
}
