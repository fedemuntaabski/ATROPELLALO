package com.atropellalo.game.effect;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Gestor centralizado de efectos visuales del juego.
 * Maneja la creación, actualización y renderizado de todos los efectos.
 */
public class VisualEffectManager {
    
    /** Instancia singleton */
    private static VisualEffectManager instance;
    
    /** Lista de efectos activos */
    private final List<VisualEffect> effects;
    
    /** Efecto del lanzallamas (persistente) */
    private FlamethrowerEffect flamethrowerEffect;
    
    /**
     * Constructor privado (singleton).
     */
    private VisualEffectManager() {
        this.effects = new ArrayList<>();
        this.flamethrowerEffect = new FlamethrowerEffect();
    }
    
    /**
     * Obtiene la instancia del gestor.
     * @return Instancia singleton
     */
    public static synchronized VisualEffectManager getInstance() {
        if (instance == null) {
            instance = new VisualEffectManager();
        }
        return instance;
    }
    
    /**
     * Crea un efecto de explosión en la posición dada.
     * @param x Posición X central
     * @param y Posición Y central
     * @param radius Radio de la explosión
     */
    public void createExplosion(float x, float y, float radius) {
        effects.add(new ExplosionEffect(x, y, radius));
    }
    
    /**
     * Actualiza el efecto del lanzallamas.
     * @param x Posición X del origen
     * @param y Posición Y del origen
     * @param angle Ángulo en radianes
     * @param coneAngle Ángulo del cono en grados
     * @param range Alcance
     * @param active Si está activo
     */
    public void updateFlamethrower(float x, float y, float angle, float coneAngle, float range, boolean active) {
        flamethrowerEffect.setParameters(x, y, angle, coneAngle, range, active);
    }
    
    /**
     * Actualiza todos los efectos.
     * @param deltaTime Tiempo desde el último frame
     */
    public void update(float deltaTime) {
        // Actualizar efectos temporales
        Iterator<VisualEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            VisualEffect effect = iterator.next();
            effect.update(deltaTime);
            if (!effect.isAlive()) {
                iterator.remove();
            }
        }
        
        // Actualizar efecto del lanzallamas
        flamethrowerEffect.update(deltaTime);
    }
    
    /**
     * Renderiza todos los efectos.
     * @param g2d Contexto gráfico
     */
    public void render(Graphics2D g2d) {
        // Renderizar efectos temporales
        for (VisualEffect effect : effects) {
            effect.render(g2d);
        }
        
        // Renderizar efecto del lanzallamas
        flamethrowerEffect.render(g2d);
    }
    
    /**
     * Limpia todos los efectos.
     */
    public void clear() {
        effects.clear();
        flamethrowerEffect = new FlamethrowerEffect();
    }
    
    /**
     * Obtiene el número de efectos activos.
     * @return Cantidad de efectos
     */
    public int getActiveEffectCount() {
        return effects.size() + (flamethrowerEffect.isActive() ? 1 : 0);
    }
}
