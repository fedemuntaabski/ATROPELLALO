package com.atropellalo.game.debug;

import com.atropellalo.game.entity.Player;
import com.atropellalo.game.camera.Camera;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Overlay visual de depuración.
 * Muestra información en tiempo real sobre el estado del juego.
 */
public class DebugOverlay {
    
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 14);
    private static final Font INFO_FONT = new Font("Monospaced", Font.PLAIN, 12);
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 200);
    private static final Color TEXT_COLOR = new Color(0, 255, 0);
    private static final Color WARNING_COLOR = new Color(255, 255, 0);
    private static final Color ERROR_COLOR = new Color(255, 0, 0);
    private static final int PADDING = 8;
    private static final int LINE_HEIGHT = 16;
    
    private final PerformanceMonitor performanceMonitor;
    
    public DebugOverlay(PerformanceMonitor performanceMonitor) {
        this.performanceMonitor = performanceMonitor;
    }
    
    /**
     * Renderiza el overlay de debug.
     */
    public void render(Graphics2D g2d, Player player, Camera camera) {
        if (!DebugConfig.isDebugEnabled()) {
            return;
        }
        
        // Guardar el estado original del Graphics
        Composite originalComposite = g2d.getComposite();
        Font originalFont = g2d.getFont();
        
        // Recopilar líneas de información
        List<String> lines = collectDebugInfo(player, camera);
        
        if (lines.isEmpty()) {
            return;
        }
        
        // Calcular dimensiones del panel
        int panelWidth = calculatePanelWidth(g2d, lines);
        int panelHeight = PADDING * 2 + lines.size() * LINE_HEIGHT;
        
        int x = DebugConfig.getOverlayX();
        int y = DebugConfig.getOverlayY();
        
        // Dibujar fondo con transparencia
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, DebugConfig.getOverlayAlpha()));
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(x, y, panelWidth, panelHeight);
        
        // Dibujar borde
        g2d.setComposite(originalComposite);
        g2d.setColor(TEXT_COLOR);
        g2d.drawRect(x, y, panelWidth, panelHeight);
        
        // Dibujar texto
        int textY = y + PADDING + LINE_HEIGHT;
        for (String line : lines) {
            Color lineColor = getLineColor(line);
            g2d.setColor(lineColor);
            
            if (line.startsWith("=== ")) {
                g2d.setFont(TITLE_FONT);
            } else {
                g2d.setFont(INFO_FONT);
            }
            
            g2d.drawString(line, x + PADDING, textY);
            textY += LINE_HEIGHT;
        }
        
        // Restaurar estado
        g2d.setFont(originalFont);
        g2d.setComposite(originalComposite);
    }
    
    /**
     * Recopila la información de debug a mostrar.
     */
    private List<String> collectDebugInfo(Player player, Camera camera) {
        List<String> lines = new ArrayList<>();
        
        // Performance
        if (DebugConfig.isShowFPS() || DebugConfig.isShowMemoryUsage()) {
            lines.add("=== PERFORMANCE ===");
            
            if (DebugConfig.isShowFPS()) {
                int fps = performanceMonitor.getCurrentFPS();
                String fpsLine = "FPS: %d".formatted(fps);
                if (fps < 30) {
                    fpsLine += " [!]";
                }
                lines.add(fpsLine);
                
                float avgFrameTime = PerformanceMonitor.nanosToMillis(performanceMonitor.getAverageFrameTime());
                float minFrameTime = PerformanceMonitor.nanosToMillis(performanceMonitor.getMinFrameTime());
                float maxFrameTime = PerformanceMonitor.nanosToMillis(performanceMonitor.getMaxFrameTime());
                
                lines.add("Frame Time: %.2fms (avg)".formatted(avgFrameTime));
                lines.add("  Min: %.2fms | Max: %.2fms".formatted(minFrameTime, maxFrameTime));
            }
            
            if (DebugConfig.isShowMemoryUsage()) {
                long usedMB = performanceMonitor.getUsedMemoryMB();
                long totalMB = performanceMonitor.getTotalMemoryMB();
                long maxMB = performanceMonitor.getMaxMemoryMB();
                float usagePercent = performanceMonitor.getMemoryUsagePercent();
                
                String memLine = "Memory: %dMB / %dMB (%.1f%%)".formatted(usedMB, totalMB, usagePercent);
                if (usagePercent > 90) {
                    memLine += " [!]";
                }
                lines.add(memLine);
                lines.add("  Max Available: %dMB".formatted(maxMB));
            }
        }
        
        // Entidades
        if (DebugConfig.isShowEntityCount()) {
            lines.add("");
            lines.add("=== ENTITIES ===");
            lines.add("Total: %d".formatted(performanceMonitor.getEntityCount()));
            lines.add("  Enemies: %d".formatted(performanceMonitor.getEnemyCount()));
            lines.add("  Projectiles: %d".formatted(performanceMonitor.getProjectileCount()));
            lines.add("  Loot: %d".formatted(performanceMonitor.getLootCount()));
        }
        
        // Player
        if (DebugConfig.isShowPlayerStats() && player != null) {
            lines.add("");
            lines.add("=== PLAYER ===");
            lines.add("Position: (%.1f, %.1f)".formatted(player.getX(), player.getY()));
            lines.add("Health: %.1f / %.1f".formatted(player.getHealth(), player.getMaxHealth()));
            lines.add("Level: %d (XP: %d/%d)".formatted(
                player.getLevel(), player.getCurrentXP(), player.getXPForNextLevel()));
            lines.add("Speed: %.2f".formatted(player.getSpeed()));
        }
        
        // Camera
        if (DebugConfig.isShowCameraInfo() && camera != null) {
            lines.add("");
            lines.add("=== CAMERA ===");
            lines.add("Position: (%.1f, %.1f)".formatted(camera.getX(), camera.getY()));
            lines.add("Viewport: %dx%d".formatted((int)camera.getViewportWidth(), (int)camera.getViewportHeight()));
        }
        
        return lines;
    }
    
    /**
     * Calcula el ancho necesario del panel.
     */
    private int calculatePanelWidth(Graphics2D g2d, List<String> lines) {
        int maxWidth = 0;
        FontMetrics fmTitle = g2d.getFontMetrics(TITLE_FONT);
        FontMetrics fmInfo = g2d.getFontMetrics(INFO_FONT);
        
        for (String line : lines) {
            FontMetrics fm = line.startsWith("=== ") ? fmTitle : fmInfo;
            Rectangle2D bounds = fm.getStringBounds(line, g2d);
            maxWidth = Math.max(maxWidth, (int) bounds.getWidth());
        }
        
        return maxWidth + PADDING * 2;
    }
    
    /**
     * Determina el color de una línea según su contenido.
     */
    private Color getLineColor(String line) {
        if (line.contains("[!]")) {
            return ERROR_COLOR;
        } else if (line.startsWith("=== ")) {
            return WARNING_COLOR;
        }
        return TEXT_COLOR;
    }
    
    /**
     * Dibuja las cajas de colisión de las entidades.
     */
    public void renderCollisionBoxes(Graphics2D g2d, List<?> entities, Camera camera) {
        if (!DebugConfig.isShowCollisionBoxes()) {
            return;
        }
        
        g2d.setColor(new Color(255, 0, 0, 128));
        g2d.setStroke(new BasicStroke(2));
        
        for (Object entity : entities) {
            // Aquí se dibujarían las cajas de colisión
            // La implementación específica depende de tu interfaz de entidades
            // Por ahora dejamos el método preparado para futuras implementaciones
        }
    }
    
    /**
     * Dibuja una grilla sobre el mundo para ayudar con el posicionamiento.
     */
    public void renderGrid(Graphics2D g2d, Camera camera, int gridSize) {
        if (!DebugConfig.isShowGridOverlay()) {
            return;
        }
        
        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.setStroke(new BasicStroke(1));
        
        int startX = (int) (camera.getX() / gridSize) * gridSize;
        int startY = (int) (camera.getY() / gridSize) * gridSize;
        int endX = (int) (camera.getX() + camera.getViewportWidth());
        int endY = (int) (camera.getY() + camera.getViewportHeight());
        
        // Líneas verticales
        for (int x = startX; x <= endX; x += gridSize) {
            int screenX = (int) (x - camera.getX());
            g2d.drawLine(screenX, 0, screenX, (int) camera.getViewportHeight());
        }
        
        // Líneas horizontales
        for (int y = startY; y <= endY; y += gridSize) {
            int screenY = (int) (y - camera.getY());
            g2d.drawLine(0, screenY, (int) camera.getViewportWidth(), screenY);
        }
    }
}
