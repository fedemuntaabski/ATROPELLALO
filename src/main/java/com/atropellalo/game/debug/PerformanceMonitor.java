package com.atropellalo.game.debug;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Monitor de rendimiento del juego.
 * Rastrea FPS, frame time, uso de memoria y otras métricas.
 */
public class PerformanceMonitor {
    
    private static final int SAMPLE_SIZE = 100;
    private static final long SECOND_IN_NANOS = 1_000_000_000L;
    
    // Métricas de FPS
    private int currentFPS;
    private int frameCount;
    private long lastFPSUpdate;
    private final Queue<Long> frameTimes;
    
    // Métricas de frame time
    private long lastFrameTime;
    private long averageFrameTime;
    private long minFrameTime;
    private long maxFrameTime;
    
    // Métricas de memoria
    private long usedMemory;
    private long totalMemory;
    private long maxMemory;
    private float memoryUsagePercent;
    
    // Métricas de juego
    private int entityCount;
    private int enemyCount;
    private int projectileCount;
    private int lootCount;
    
    public PerformanceMonitor() {
        this.frameTimes = new LinkedList<>();
        this.lastFPSUpdate = System.nanoTime();
        this.minFrameTime = Long.MAX_VALUE;
        this.maxFrameTime = 0;
    }
    
    /**
     * Actualiza las métricas de rendimiento.
     * Debe llamarse al inicio de cada frame.
     */
    public void update(long currentTime) {
        updateFPS(currentTime);
        updateFrameTime(currentTime);
        updateMemoryMetrics();
    }
    
    /**
     * Actualiza el contador de FPS.
     */
    private void updateFPS(long currentTime) {
        frameCount++;
        
        if (currentTime - lastFPSUpdate >= SECOND_IN_NANOS) {
            currentFPS = frameCount;
            frameCount = 0;
            lastFPSUpdate = currentTime;
        }
    }
    
    /**
     * Actualiza las métricas de frame time.
     */
    private void updateFrameTime(long currentTime) {
        if (lastFrameTime > 0) {
            long frameTime = currentTime - lastFrameTime;
            
            // Agregar a la cola de samples
            frameTimes.offer(frameTime);
            if (frameTimes.size() > SAMPLE_SIZE) {
                frameTimes.poll();
            }
            
            // Calcular promedio
            long sum = 0;
            for (long time : frameTimes) {
                sum += time;
            }
            averageFrameTime = sum / frameTimes.size();
            
            // Actualizar min/max
            minFrameTime = Math.min(minFrameTime, frameTime);
            maxFrameTime = Math.max(maxFrameTime, frameTime);
        }
        
        lastFrameTime = currentTime;
    }
    
    /**
     * Actualiza las métricas de memoria.
     */
    private void updateMemoryMetrics() {
        Runtime runtime = Runtime.getRuntime();
        totalMemory = runtime.totalMemory();
        maxMemory = runtime.maxMemory();
        usedMemory = totalMemory - runtime.freeMemory();
        memoryUsagePercent = (float) usedMemory / totalMemory * 100f;
    }
    
    /**
     * Actualiza el conteo de entidades del juego.
     */
    public void updateEntityCounts(int enemies, int projectiles, int loot) {
        this.enemyCount = enemies;
        this.projectileCount = projectiles;
        this.lootCount = loot;
        this.entityCount = enemies + projectiles + loot;
    }
    
    /**
     * Resetea las métricas min/max.
     */
    public void resetMinMax() {
        minFrameTime = Long.MAX_VALUE;
        maxFrameTime = 0;
    }
    
    // Getters
    
    public int getCurrentFPS() {
        return currentFPS;
    }
    
    public long getAverageFrameTime() {
        return averageFrameTime;
    }
    
    public long getMinFrameTime() {
        return minFrameTime == Long.MAX_VALUE ? 0 : minFrameTime;
    }
    
    public long getMaxFrameTime() {
        return maxFrameTime;
    }
    
    public long getUsedMemoryMB() {
        return usedMemory / (1024 * 1024);
    }
    
    public long getTotalMemoryMB() {
        return totalMemory / (1024 * 1024);
    }
    
    public long getMaxMemoryMB() {
        return maxMemory / (1024 * 1024);
    }
    
    public float getMemoryUsagePercent() {
        return memoryUsagePercent;
    }
    
    public int getEntityCount() {
        return entityCount;
    }
    
    public int getEnemyCount() {
        return enemyCount;
    }
    
    public int getProjectileCount() {
        return projectileCount;
    }
    
    public int getLootCount() {
        return lootCount;
    }
    
    /**
     * Convierte nanosegundos a milisegundos.
     */
    public static float nanosToMillis(long nanos) {
        return nanos / 1_000_000f;
    }
}
