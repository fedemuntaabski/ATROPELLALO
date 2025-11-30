package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.sprite.Animation;
import com.atropellalo.game.sprite.AnimationState;
import com.atropellalo.game.sprite.ZombieSpriteGenerator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Zombie escupidor - Enemigo a distancia que dispara proyectiles de ácido.
 * 
 * Comportamiento:
 * - Mantiene distancia del jugador (no se acerca demasiado)
 * - Dispara proyectiles de ácido a intervalos regulares
 * - Los proyectiles dejan charcos corrosivos al impactar
 * - Los charcos hacen daño continuo al jugador que los pisa
 */
public class SpitterZombie extends Enemy {
    
    // Sistema de disparo
    private float fireTimer;
    private final float fireRate;
    private final float attackRange;
    private final float projectileSpeed;
    private final float projectileDamage;
    
    // Listas de proyectiles y charcos activos (gestionados externamente)
    private static final List<AcidProjectile> projectiles = new ArrayList<>();
    private static final List<CorrosivePuddle> puddles = new ArrayList<>();
    
    // Callback para daño al jugador
    private SpitterCallback callback;
    
    // Colores del escupidor
    private static final Color BODY_COLOR = new Color(60, 140, 60);      // Verde tóxico
    private static final Color BODY_DARK = new Color(40, 100, 40);       // Verde oscuro
    private static final Color ACID_COLOR = new Color(180, 255, 50);     // Verde ácido brillante
    private static final Color EYE_COLOR = new Color(255, 255, 100);     // Ojos amarillentos
    
    /**
     * Interface para notificar daño al jugador.
     */
    public interface SpitterCallback {
        void onAcidDamage(float damage);
    }
    
    /**
     * Constructor del zombie escupidor.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     */
    public SpitterZombie(float x, float y) {
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
    public SpitterZombie(float x, float y, float healthScale, float speedScale, float damageScale) {
        super(x, y, 
              GameConfig.SPITTER_ZOMBIE_HEALTH,
              GameConfig.SPITTER_ZOMBIE_SPEED,
              GameConfig.SPITTER_ZOMBIE_SIZE,
              GameConfig.SPITTER_ZOMBIE_DAMAGE,
              healthScale, speedScale, damageScale);
        
        this.fireTimer = 0;
        this.fireRate = GameConfig.SPITTER_FIRE_RATE;
        this.attackRange = GameConfig.SPITTER_ATTACK_RANGE;
        this.projectileSpeed = GameConfig.SPITTER_PROJECTILE_SPEED;
        this.projectileDamage = GameConfig.SPITTER_PROJECTILE_DAMAGE * damageScale;
        
        // Cargar animaciones
        loadAnimations();
    }
    
    /**
     * Carga las animaciones del escupidor.
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
     * Establece el callback para daño al jugador.
     */
    public void setCallback(SpitterCallback callback) {
        this.callback = callback;
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
        
        // Calcular distancia al jugador
        float distance = distanceToPlayer(playerX, playerY);
        
        // Actualizar timer de disparo
        fireTimer += deltaTime;
        
        // Disparar si está en rango y el cooldown terminó
        if (distance <= attackRange && fireTimer >= fireRate) {
            fireAcidProjectile(playerX, playerY);
            fireTimer = 0;
        }
        
        // Comportamiento de movimiento: mantener distancia
        float idealDistance = attackRange * 0.6f;
        float oldX = x;
        float oldY = y;
        
        if (distance < idealDistance * 0.7f) {
            // Demasiado cerca - retroceder
            float dx = getCenterX() - playerX;
            float dy = getCenterY() - playerY;
            float len = (float) Math.sqrt(dx * dx + dy * dy);
            if (len > 0) {
                x += (dx / len) * speed * deltaTime;
                y += (dy / len) * speed * deltaTime;
            }
        } else if (distance > attackRange * 0.9f) {
            // Demasiado lejos - acercarse usando pathfinding
            updatePath(deltaTime, playerX, playerY);
            moveTowardsWithAStar(playerX, playerY, deltaTime);
        }
        // Si está en rango óptimo, se queda quieto
        
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
     * Dispara un proyectil de ácido hacia el jugador.
     */
    private void fireAcidProjectile(float targetX, float targetY) {
        float dx = targetX - getCenterX();
        float dy = targetY - getCenterY();
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (len > 0) {
            float vx = (dx / len) * projectileSpeed;
            float vy = (dy / len) * projectileSpeed;
            
            AcidProjectile projectile = new AcidProjectile(
                getCenterX(), getCenterY(), vx, vy, projectileDamage);
            projectile.setCallback(callback);
            projectiles.add(projectile);
        }
    }
    
    /**
     * Actualiza todos los proyectiles y charcos (llamar desde EnemyManager).
     */
    public static void updateProjectilesAndPuddles(float deltaTime, float playerX, float playerY, SpitterCallback callback) {
        // Actualizar proyectiles
        Iterator<AcidProjectile> projIterator = projectiles.iterator();
        while (projIterator.hasNext()) {
            AcidProjectile proj = projIterator.next();
            proj.update(deltaTime);
            
            // Verificar impacto con jugador
            float dx = proj.x - playerX;
            float dy = proj.y - playerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (distance < 20) {
                // Impacto con jugador
                if (callback != null) {
                    callback.onAcidDamage(proj.damage);
                }
                // Crear charco en posición del jugador
                puddles.add(new CorrosivePuddle(proj.x, proj.y));
                projIterator.remove();
            } else if (proj.isExpired()) {
                // Proyectil expiró - crear charco
                puddles.add(new CorrosivePuddle(proj.x, proj.y));
                projIterator.remove();
            }
        }
        
        // Actualizar charcos
        Iterator<CorrosivePuddle> puddleIterator = puddles.iterator();
        while (puddleIterator.hasNext()) {
            CorrosivePuddle puddle = puddleIterator.next();
            puddle.update(deltaTime);
            
            // Verificar si el jugador está en el charco
            if (puddle.containsPoint(playerX, playerY)) {
                if (puddle.canDamage()) {
                    if (callback != null) {
                        callback.onAcidDamage(GameConfig.SPITTER_PUDDLE_DAMAGE * deltaTime);
                    }
                }
            }
            
            if (puddle.isExpired()) {
                puddleIterator.remove();
            }
        }
    }
    
    /**
     * Renderiza todos los proyectiles y charcos.
     */
    public static void renderProjectilesAndPuddles(Graphics2D g2d) {
        // Renderizar charcos primero (bajo los enemigos)
        for (CorrosivePuddle puddle : puddles) {
            puddle.render(g2d);
        }
        
        // Renderizar proyectiles
        for (AcidProjectile proj : projectiles) {
            proj.render(g2d);
        }
    }
    
    /**
     * Limpia todos los proyectiles y charcos.
     */
    public static void clearAll() {
        projectiles.clear();
        puddles.clear();
    }
    
    @Override
    public void render(Graphics2D g2d) {
        if (!alive && isDeathAnimationFinished()) {
            return;
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
        
        // Renderizar barra de vida
        if (alive) {
            renderHealthBar(g2d);
            
            // Indicador de carga de disparo
            if (fireTimer > 0 && fireTimer < fireRate) {
                float chargePercent = fireTimer / fireRate;
                int barWidth = size;
                int barHeight = 3;
                int barX = (int) x;
                int barY = (int) y - 12;
                
                g2d.setColor(Color.DARK_GRAY);
                g2d.fillRect(barX, barY, barWidth, barHeight);
                g2d.setColor(ACID_COLOR);
                g2d.fillRect(barX, barY, (int)(barWidth * chargePercent), barHeight);
            }
        }
    }
    
    /**
     * Renderizado de respaldo si no hay animación.
     */
    private void renderFallback(Graphics2D g2d) {
        // Cuerpo
        g2d.setColor(BODY_COLOR);
        g2d.fillOval((int) x, (int) y, size, size);
        
        // Manchas de ácido
        g2d.setColor(ACID_COLOR);
        g2d.fillOval((int) x + size/4, (int) y + size/4, size/4, size/4);
        g2d.fillOval((int) x + size/2, (int) y + size/3, size/5, size/5);
        
        // Ojos
        g2d.setColor(EYE_COLOR);
        g2d.fillOval((int) x + size/4, (int) y + size/4, size/5, size/5);
        g2d.fillOval((int) x + size/2, (int) y + size/4, size/5, size/5);
        
        // Boca goteando ácido
        g2d.setColor(ACID_COLOR);
        g2d.fillOval((int) x + size/3, (int) y + size*2/3, size/3, size/4);
    }
    
    @Override
    public EnemyType getType() {
        return EnemyType.SPITTER;
    }
    
    /**
     * Clase interna para proyectiles de ácido.
     */
    public static class AcidProjectile {
        float x, y;
        float vx, vy;
        float damage;
        float lifetime;
        private SpitterCallback callback;
        
        private static final float MAX_LIFETIME = 3.0f;
        private static final int SIZE = 10;
        
        public AcidProjectile(float x, float y, float vx, float vy, float damage) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.damage = damage;
            this.lifetime = 0;
        }
        
        public void setCallback(SpitterCallback callback) {
            this.callback = callback;
        }
        
        public void update(float deltaTime) {
            x += vx * deltaTime;
            y += vy * deltaTime;
            lifetime += deltaTime;
        }
        
        public boolean isExpired() {
            return lifetime >= MAX_LIFETIME ||
                   x < 0 || x > GameConfig.WORLD_WIDTH ||
                   y < 0 || y > GameConfig.WORLD_HEIGHT;
        }
        
        public void render(Graphics2D g2d) {
            // Gota de ácido
            g2d.setColor(ACID_COLOR);
            g2d.fillOval((int)(x - SIZE/2), (int)(y - SIZE/2), SIZE, SIZE);
            
            // Brillo
            g2d.setColor(new Color(220, 255, 100));
            g2d.fillOval((int)(x - SIZE/4), (int)(y - SIZE/4), SIZE/2, SIZE/2);
            
            // Estela
            g2d.setColor(new Color(180, 255, 50, 100));
            g2d.fillOval((int)(x - vx*0.05f - SIZE/3), (int)(y - vy*0.05f - SIZE/3), SIZE*2/3, SIZE*2/3);
        }
    }
    
    /**
     * Clase interna para charcos corrosivos.
     */
    public static class CorrosivePuddle {
        float x, y;
        float radius;
        float duration;
        float lifetime;
        float pulseTimer;
        
        private static final float PULSE_RATE = 0.3f;
        
        public CorrosivePuddle(float x, float y) {
            this.x = x;
            this.y = y;
            this.radius = GameConfig.SPITTER_PUDDLE_RADIUS;
            this.duration = GameConfig.SPITTER_PUDDLE_DURATION;
            this.lifetime = 0;
            this.pulseTimer = 0;
        }
        
        public void update(float deltaTime) {
            lifetime += deltaTime;
            pulseTimer += deltaTime;
            if (pulseTimer >= PULSE_RATE) {
                pulseTimer = 0;
            }
        }
        
        public boolean isExpired() {
            return lifetime >= duration;
        }
        
        public boolean canDamage() {
            return true; // Daño continuo
        }
        
        public boolean containsPoint(float px, float py) {
            float dx = px - x;
            float dy = py - y;
            return (dx * dx + dy * dy) <= (radius * radius);
        }
        
        public void render(Graphics2D g2d) {
            float fadeAlpha = 1.0f - (lifetime / duration);
            int alpha = (int)(fadeAlpha * 150);
            
            // Charco base
            g2d.setColor(new Color(100, 180, 30, alpha));
            g2d.fill(new Ellipse2D.Float(x - radius, y - radius, radius * 2, radius * 2));
            
            // Efecto de pulso
            float pulse = (float) Math.sin(pulseTimer / PULSE_RATE * Math.PI);
            float innerRadius = radius * (0.5f + pulse * 0.2f);
            g2d.setColor(new Color(180, 255, 50, (int)(alpha * 0.7f)));
            g2d.fill(new Ellipse2D.Float(x - innerRadius, y - innerRadius, innerRadius * 2, innerRadius * 2));
            
            // Burbujas
            g2d.setColor(new Color(200, 255, 100, (int)(alpha * 0.8f)));
            float bubbleOffset = (lifetime * 20) % (radius * 0.5f);
            g2d.fillOval((int)(x - radius/3 + bubbleOffset), (int)(y - radius/4), 6, 6);
            g2d.fillOval((int)(x + radius/4 - bubbleOffset), (int)(y + radius/4), 4, 4);
        }
    }
}
