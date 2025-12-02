package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.sprite.Animation;
import com.atropellalo.game.sprite.AnimationState;
import com.atropellalo.game.sprite.ZombieSpriteGenerator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.List;

/**
 * Zombie Buffer (War Crier) - Enemigo de soporte que mejora a sus aliados.
 * 
 * Comportamiento:
 * - Se mantiene cerca de grupos de zombies
 * - Emite un aura que mejora velocidad (+20%) y daño (+15%) de aliados cercanos
 * - Daño por contacto muy bajo
 * - Tiene una animación visual de pulso cuando el aura está activa
 */
public class BufferZombie extends Enemy {
    
    // Sistema de aura
    private final float auraRadius;
    private final float speedBonus;
    private final float damageBonus;
    private float auraPulseTimer;
    private float auraVisualPulse;
    
    // Referencia a la lista de enemigos para aplicar buffs
    private List<Enemy> allEnemies;
    
    // Colores del buffer
    private static final Color BODY_COLOR = new Color(130, 80, 180);      // Morado
    private static final Color BODY_DARK = new Color(90, 50, 140);        // Morado oscuro
    private static final Color AURA_COLOR = new Color(180, 120, 255, 80); // Aura morada translúcida
    private static final Color AURA_PULSE = new Color(220, 180, 255);     // Pulso brillante
    private static final Color EYE_COLOR = new Color(255, 100, 255);      // Ojos rosa brillante
    private static final Color RUNE_COLOR = new Color(255, 200, 255);     // Runas mágicas
    
    /**
     * Constructor del zombie buffer.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     */
    public BufferZombie(float x, float y) {
        this(x, y, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Constructor con escalado.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param healthScale Factor de escalado de salud
     * @param speedScale Factor de escalado de velocidad
     * @param damageScale Factor de escalado de daño
     */
    public BufferZombie(float x, float y, float healthScale, float speedScale, float damageScale) {
        super(x, y, 
              GameConfig.BUFFER_ZOMBIE_HEALTH,
              GameConfig.BUFFER_ZOMBIE_SPEED,
              GameConfig.BUFFER_ZOMBIE_SIZE,
              GameConfig.BUFFER_ZOMBIE_DAMAGE,
              healthScale, speedScale, damageScale);
        
        this.auraRadius = GameConfig.BUFFER_AURA_RADIUS;
        this.speedBonus = GameConfig.BUFFER_SPEED_BONUS;
        this.damageBonus = GameConfig.BUFFER_DAMAGE_BONUS;
        this.auraPulseTimer = 0;
        this.auraVisualPulse = 0;
        
        // Cargar animaciones
        loadAnimations();
    }
    
    /**
     * Carga las animaciones del buffer.
     */
    private void loadAnimations() {
        animations = new HashMap<>();
        
        // Generar sprites usando ZombieSpriteGenerator con colores personalizados
        animations.put(AnimationState.IDLE, ZombieSpriteGenerator.generateZombieAnimation(
            AnimationState.IDLE, size, BODY_COLOR, BODY_DARK, EYE_COLOR));
        animations.put(AnimationState.MOVING, ZombieSpriteGenerator.generateZombieAnimation(
            AnimationState.MOVING, size, BODY_COLOR, BODY_DARK, EYE_COLOR));
        animations.put(AnimationState.DEATH, ZombieSpriteGenerator.generateZombieAnimation(
            AnimationState.DEATH, size, BODY_COLOR, BODY_DARK, EYE_COLOR));
        
        currentAnimState = AnimationState.IDLE;
    }
    
    /**
     * Establece la referencia a todos los enemigos para aplicar buffs.
     * @param enemies Lista de enemigos
     */
    public void setEnemyList(List<Enemy> enemies) {
        this.allEnemies = enemies;
    }
    
    @Override
    public void update(float deltaTime, float playerX, float playerY) {
        if (!alive) {
            updateDeathAnimation(deltaTime);
            return;
        }
        
        // Actualizar cooldown de daño
        if (damageCooldown > 0) {
            damageCooldown -= deltaTime;
        }
        
        // Actualizar pulso del aura
        auraPulseTimer += deltaTime;
        auraVisualPulse = (float) Math.sin(auraPulseTimer * 3) * 0.5f + 0.5f;
        
        // Aplicar buffs a enemigos cercanos
        if (auraPulseTimer >= GameConfig.BUFFER_AURA_PULSE_RATE) {
            auraPulseTimer = 0;
            applyAuraBuffs();
        }
        
        // Guardar posición anterior
        float oldX = x;
        float oldY = y;
        
        // El buffer intenta mantenerse cerca de grupos de zombies pero también persigue al jugador
        float targetX = playerX;
        float targetY = playerY;
        
        // Buscar centro de masa de zombies cercanos para agruparse
        if (allEnemies != null && allEnemies.size() > 2) {
            float groupX = 0, groupY = 0;
            int nearbyCount = 0;
            
            for (Enemy enemy : allEnemies) {
                if (enemy != this && enemy.isAlive() && !(enemy instanceof BufferZombie)) {
                    float dist = distanceToEnemy(enemy);
                    if (dist < auraRadius * 2) {
                        groupX += enemy.getCenterX();
                        groupY += enemy.getCenterY();
                        nearbyCount++;
                    }
                }
            }
            
            // Si hay zombies cerca, moverse hacia su centro pero también hacia el jugador
            if (nearbyCount > 0) {
                groupX /= nearbyCount;
                groupY /= nearbyCount;
                
                // Punto intermedio entre grupo y jugador
                targetX = groupX * 0.3f + playerX * 0.7f;
                targetY = groupY * 0.3f + playerY * 0.7f;
            }
        }
        
        // Moverse directamente hacia el objetivo
        moveTowardsDirect(targetX, targetY, deltaTime);
        
        // Verificar colisiones
        if (collisionChecker != null && collisionChecker.checkCollision(x, y, size, size)) {
            x = oldX;
            y = oldY;
        }
        
        // Limitar al mundo
        x = Math.max(0, Math.min(x, GameConfig.WORLD_WIDTH - size));
        y = Math.max(0, Math.min(y, GameConfig.WORLD_HEIGHT - size));
        
        // Actualizar animación
        updateAnimationState(deltaTime, oldX, oldY, playerX, playerY);
    }
    
    /**
     * Aplica los buffs del aura a enemigos cercanos.
     */
    private void applyAuraBuffs() {
        if (allEnemies == null) return;
        
        for (Enemy enemy : allEnemies) {
            if (enemy != this && enemy.isAlive()) {
                float dist = distanceToEnemy(enemy);
                if (dist <= auraRadius) {
                    // Aplicar buff temporal
                    applyBuff(enemy);
                }
            }
        }
    }
    
    /**
     * Aplica un buff temporal a un enemigo.
     */
    private void applyBuff(Enemy enemy) {
        // El sistema de buff usa multiplicadores temporales
        // Implementamos un sistema simple donde el buff se aplica cada pulso
        // Los enemigos tienen buffed flag que dura buffDuration
        if (enemy instanceof BuffableEnemy buffableEnemy) {
            buffableEnemy.applyBuff(speedBonus, damageBonus, GameConfig.BUFFER_BUFF_DURATION);
        }
    }
    
    /**
     * Verifica si un punto está dentro del aura.
     */
    public boolean isInAura(float px, float py) {
        float dx = px - getCenterX();
        float dy = py - getCenterY();
        return (dx * dx + dy * dy) <= (auraRadius * auraRadius);
    }
    
    /**
     * Obtiene el radio del aura.
     */
    public float getAuraRadius() {
        return auraRadius;
    }
    
    @Override
    public void render(Graphics2D g2d) {
        if (!alive && isDeathAnimationFinished()) {
            return;
        }
        
        // Renderizar aura antes del cuerpo
        if (alive) {
            renderAura(g2d);
        }
        
        // Renderizar con animación
        if (animations != null && animations.containsKey(currentAnimState)) {
            Animation anim = animations.get(currentAnimState);
            if (anim.getCurrentFrame() != null) {
                anim.renderScaled(g2d, getCenterX(), getCenterY(), rotation, 1.0f);
            } else {
                renderFallback(g2d);
            }
        } else {
            renderFallback(g2d);
        }
        
        // Renderizar runas mágicas flotantes
        if (alive) {
            renderRunes(g2d);
            renderHealthBar(g2d);
        }
    }
    
    /**
     * Renderiza el aura visual.
     */
    private void renderAura(Graphics2D g2d) {
        // Aura exterior
        float pulseRadius = auraRadius * (0.9f + auraVisualPulse * 0.2f);
        int alpha = (int)(60 + auraVisualPulse * 40);
        
        g2d.setColor(new Color(180, 120, 255, alpha));
        g2d.fill(new Ellipse2D.Float(
            getCenterX() - pulseRadius, 
            getCenterY() - pulseRadius, 
            pulseRadius * 2, 
            pulseRadius * 2));
        
        // Borde del aura
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                       0, new float[]{10, 10}, auraPulseTimer * 30));
        g2d.setColor(new Color(220, 180, 255, (int)(100 + auraVisualPulse * 50)));
        g2d.draw(new Ellipse2D.Float(
            getCenterX() - auraRadius, 
            getCenterY() - auraRadius, 
            auraRadius * 2, 
            auraRadius * 2));
        g2d.setStroke(oldStroke);
        
        // Partículas internas
        g2d.setColor(new Color(255, 200, 255, (int)(80 + auraVisualPulse * 70)));
        for (int i = 0; i < 6; i++) {
            float angle = (float)(auraPulseTimer * 2 + i * Math.PI / 3);
            float dist = auraRadius * 0.5f;
            float px = getCenterX() + (float)Math.cos(angle) * dist;
            float py = getCenterY() + (float)Math.sin(angle) * dist;
            g2d.fillOval((int)px - 3, (int)py - 3, 6, 6);
        }
    }
    
    /**
     * Renderiza runas mágicas flotantes alrededor del buffer.
     */
    private void renderRunes(Graphics2D g2d) {
        g2d.setColor(new Color(255, 200, 255, (int)(150 + auraVisualPulse * 100)));
        
        // Runas orbitando
        for (int i = 0; i < 3; i++) {
            float angle = (float)(auraPulseTimer * 1.5 + i * Math.PI * 2 / 3);
            float dist = size * 0.8f;
            float rx = getCenterX() + (float)Math.cos(angle) * dist;
            float ry = getCenterY() + (float)Math.sin(angle) * dist;
            
            // Runa simple (forma de diamante)
            int[] xPoints = {(int)rx, (int)rx + 4, (int)rx, (int)rx - 4};
            int[] yPoints = {(int)ry - 6, (int)ry, (int)ry + 6, (int)ry};
            g2d.fillPolygon(xPoints, yPoints, 4);
        }
    }
    
    /**
     * Renderizado de respaldo si no hay animación.
     */
    private void renderFallback(Graphics2D g2d) {
        // Cuerpo
        g2d.setColor(BODY_COLOR);
        g2d.fillOval((int) x, (int) y, size, size);
        
        // Marcas mágicas
        g2d.setColor(RUNE_COLOR);
        g2d.drawOval((int) x + size/4, (int) y + size/3, size/3, size/3);
        
        // Ojos brillantes
        g2d.setColor(EYE_COLOR);
        int eyeGlow = (int)(3 + auraVisualPulse * 2);
        g2d.fillOval((int) x + size/4 - eyeGlow/2, (int) y + size/4 - eyeGlow/2, 
                     size/5 + eyeGlow, size/5 + eyeGlow);
        g2d.fillOval((int) x + size/2 - eyeGlow/2, (int) y + size/4 - eyeGlow/2, 
                     size/5 + eyeGlow, size/5 + eyeGlow);
    }
    
    @Override
    public EnemyType getType() {
        return EnemyType.BUFFER;
    }
    
    /**
     * Interface para enemigos que pueden recibir buffs.
     * Los enemigos base no la implementan, pero puede extenderse.
     */
    public interface BuffableEnemy {
        void applyBuff(float speedBonus, float damageBonus, float duration);
    }
}
