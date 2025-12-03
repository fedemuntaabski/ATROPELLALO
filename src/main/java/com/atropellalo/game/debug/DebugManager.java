package com.atropellalo.game.debug;

import com.atropellalo.game.entity.Player;
import com.atropellalo.game.camera.Camera;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * Gestor central del sistema de depuración.
 * Coordina el overlay, consola y monitor de rendimiento.
 */
public class DebugManager {
    
    private static final Logger LOGGER = Logger.getLogger(DebugManager.class.getName());
    
    private final PerformanceMonitor performanceMonitor;
    private final DebugOverlay debugOverlay;
    private final DebugConsole debugConsole;
    
    private boolean initialized;
    
    public DebugManager() {
        this.performanceMonitor = new PerformanceMonitor();
        this.debugOverlay = new DebugOverlay(performanceMonitor);
        this.debugConsole = new DebugConsole();
        this.initialized = false;
        
        // No registrar comandos aquí - se registrarán desde GamePanel
    }
    
    /**
     * Inicializa el sistema de debug.
     */
    public void initialize() {
        if (initialized) {
            LOGGER.warning("DebugManager already initialized");
            return;
        }
        
        // Debug desactivado por defecto - activar con F3
        DebugConfig.setDebugEnabled(false);
        
        debugConsole.addMessage("Debug System Initialized", DebugConsole.MessageType.INFO);
        debugConsole.addMessage("Press F3 to toggle debug overlay", DebugConsole.MessageType.INFO);
        debugConsole.addMessage("Press F1 to open console", DebugConsole.MessageType.INFO);
        debugConsole.addMessage("Type 'help' for available commands", DebugConsole.MessageType.INFO);
        
        initialized = true;
        LOGGER.info("Debug system initialized (disabled by default)");
    }
    
    /**
     * Actualiza el sistema de debug.
     * Debe llamarse cada frame.
     */
    public void update(long currentTime, int enemyCount, int projectileCount, int lootCount) {
        if (!DebugConfig.isDebugEnabled()) {
            return;
        }
        
        performanceMonitor.update(currentTime);
        performanceMonitor.updateEntityCounts(enemyCount, projectileCount, lootCount);
    }
    
    /**
     * Renderiza todos los elementos de debug.
     */
    public void render(Graphics2D g2d, Player player, Camera camera, int screenWidth, int screenHeight) {
        // Renderizar overlay solo si debug está activado
        if (DebugConfig.isDebugEnabled()) {
            // Renderizar grid si está activo
            debugOverlay.renderGrid(g2d, camera, 100);
            
            // Renderizar overlay de información
            debugOverlay.render(g2d, player, camera);
        }
        
        // Renderizar consola SIEMPRE (independiente de F3)
        debugConsole.render(g2d, screenWidth, screenHeight);
    }
    
    /**
     * Maneja eventos de teclado para debug.
     * 
     * @return true si el evento fue consumido por el sistema de debug
     */
    public boolean handleKeyPress(KeyEvent e) {
        // F3 togglea el debug overlay y el indicador de oleada
        if (e.getKeyCode() == KeyEvent.VK_F3) {
            boolean newState = !DebugConfig.isDebugEnabled();
            DebugConfig.setDebugEnabled(newState);
            DebugConfig.setShowWaveInfo(newState);
            LOGGER.info("Debug mode: " + (newState ? "ON" : "OFF"));
            return true;
        }
        
        // F1 togglea la consola (funciona siempre, no requiere debug activo)
        if (e.getKeyCode() == KeyEvent.VK_F1) {
            debugConsole.toggle();
            LOGGER.info("Debug console: " + (debugConsole.isVisible() ? "OPENED" : "CLOSED"));
            return true;
        }
        
        // Si la consola está visible, pasa todos los eventos a ella
        if (debugConsole.isVisible()) {
            return debugConsole.handleKeyPress(e);
        }
        
        return false;
    }
    
    /**
     * Agrega un mensaje a la consola de debug.
     */
    public void logMessage(String message, DebugConsole.MessageType type) {
        debugConsole.addMessage(message, type);
    }
    
    /**
     * Registra un comando personalizado en la consola.
     */
    public void registerCommand(String name, DebugConsole.DebugCommand command) {
        debugConsole.registerCommand(name, command);
    }
    
    /**
     * Registra un comando personalizado con descripción en la consola.
     */
    public void registerCommand(String name, String description, DebugConsole.DebugCommand command) {
        debugConsole.registerCommand(name, description, command);
    }
    
    // Getters
    
    public PerformanceMonitor getPerformanceMonitor() {
        return performanceMonitor;
    }
    
    public DebugConsole getConsole() {
        return debugConsole;
    }
    
    public boolean isConsoleVisible() {
        return debugConsole.isVisible();
    }
}
