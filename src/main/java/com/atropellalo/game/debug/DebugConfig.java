package com.atropellalo.game.debug;

/**
 * Configuración centralizada de flags de depuración.
 * Permite activar/desactivar diferentes aspectos de debugging.
 */
public class DebugConfig {
    
    // Flag maestro de debug
    private static boolean debugEnabled = false;
    
    // Flags individuales
    private static boolean showWaveInfo = false;  // Oculto por defecto, se muestra con F3
    private static boolean showFPS = true;
    private static boolean showEntityCount = true;
    private static boolean showPlayerStats = true;
    private static boolean showMemoryUsage = true;
    private static boolean showCollisionBoxes = false;
    private static boolean showEnemyHealth = false;
    private static boolean showProjectileInfo = false;
    private static boolean showCameraInfo = false;
    private static boolean showPerformanceGraph = false;
    private static boolean enableConsole = true;
    private static boolean logInputEvents = false;
    private static boolean showGridOverlay = false;
    
    // Configuración de overlay
    private static int overlayX = 10;
    private static int overlayY = 10;
    private static float overlayAlpha = 0.85f;
    
    /**
     * Activa/desactiva el modo debug completo.
     */
    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }
    
    public static boolean isDebugEnabled() {
        return debugEnabled;
    }
    
    public static boolean isShowWaveInfo() {
        return showWaveInfo;
    }
    
    public static void setShowWaveInfo(boolean show) {
        showWaveInfo = show;
    }
    
    // Getters y setters para flags individuales
    
    public static boolean isShowFPS() {
        return debugEnabled && showFPS;
    }
    
    public static void setShowFPS(boolean show) {
        showFPS = show;
    }
    
    public static boolean isShowEntityCount() {
        return debugEnabled && showEntityCount;
    }
    
    public static void setShowEntityCount(boolean show) {
        showEntityCount = show;
    }
    
    public static boolean isShowPlayerStats() {
        return debugEnabled && showPlayerStats;
    }
    
    public static void setShowPlayerStats(boolean show) {
        showPlayerStats = show;
    }
    
    public static boolean isShowMemoryUsage() {
        return debugEnabled && showMemoryUsage;
    }
    
    public static void setShowMemoryUsage(boolean show) {
        showMemoryUsage = show;
    }
    
    public static boolean isShowCollisionBoxes() {
        return debugEnabled && showCollisionBoxes;
    }
    
    public static void setShowCollisionBoxes(boolean show) {
        showCollisionBoxes = show;
    }
    
    public static boolean isShowEnemyHealth() {
        return debugEnabled && showEnemyHealth;
    }
    
    public static void setShowEnemyHealth(boolean show) {
        showEnemyHealth = show;
    }
    
    public static boolean isShowProjectileInfo() {
        return debugEnabled && showProjectileInfo;
    }
    
    public static void setShowProjectileInfo(boolean show) {
        showProjectileInfo = show;
    }
    
    public static boolean isShowCameraInfo() {
        return debugEnabled && showCameraInfo;
    }
    
    public static void setShowCameraInfo(boolean show) {
        showCameraInfo = show;
    }
    
    public static boolean isShowPerformanceGraph() {
        return debugEnabled && showPerformanceGraph;
    }
    
    public static void setShowPerformanceGraph(boolean show) {
        showPerformanceGraph = show;
    }
    
    public static boolean isEnableConsole() {
        return debugEnabled && enableConsole;
    }
    
    public static void setEnableConsole(boolean enable) {
        enableConsole = enable;
    }
    
    public static boolean isLogInputEvents() {
        return debugEnabled && logInputEvents;
    }
    
    public static void setLogInputEvents(boolean log) {
        logInputEvents = log;
    }
    
    public static boolean isShowGridOverlay() {
        return debugEnabled && showGridOverlay;
    }
    
    public static void setShowGridOverlay(boolean show) {
        showGridOverlay = show;
    }
    
    // Configuración de overlay
    
    public static int getOverlayX() {
        return overlayX;
    }
    
    public static void setOverlayX(int x) {
        overlayX = x;
    }
    
    public static int getOverlayY() {
        return overlayY;
    }
    
    public static void setOverlayY(int y) {
        overlayY = y;
    }
    
    public static float getOverlayAlpha() {
        return overlayAlpha;
    }
    
    public static void setOverlayAlpha(float alpha) {
        overlayAlpha = Math.max(0f, Math.min(1f, alpha));
    }
    
    /**
     * Activa todas las opciones de debug.
     */
    public static void enableAll() {
        showWaveInfo = true;
        showFPS = true;
        showEntityCount = true;
        showPlayerStats = true;
        showMemoryUsage = true;
        showCollisionBoxes = true;
        showEnemyHealth = true;
        showProjectileInfo = true;
        showCameraInfo = true;
        showPerformanceGraph = true;
        enableConsole = true;
        showGridOverlay = true;
    }
    
    /**
     * Desactiva todas las opciones de debug.
     */
    public static void disableAll() {
        showWaveInfo = false;
        showFPS = false;
        showEntityCount = false;
        showPlayerStats = false;
        showMemoryUsage = false;
        showCollisionBoxes = false;
        showEnemyHealth = false;
        showProjectileInfo = false;
        showCameraInfo = false;
        showPerformanceGraph = false;
        enableConsole = false;
        showGridOverlay = false;
    }
    
    /**
     * Restaura la configuración por defecto.
     */
    public static void resetToDefaults() {
        showWaveInfo = false;
        showFPS = true;
        showEntityCount = true;
        showPlayerStats = true;
        showMemoryUsage = true;
        showCollisionBoxes = false;
        showEnemyHealth = false;
        showProjectileInfo = false;
        showCameraInfo = false;
        showPerformanceGraph = false;
        enableConsole = true;
        logInputEvents = false;
        showGridOverlay = false;
        overlayX = 10;
        overlayY = 10;
        overlayAlpha = 0.85f;
    }
}
