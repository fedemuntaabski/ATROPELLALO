package com.atropellalo.game.loot;

import com.atropellalo.game.config.GameConfig;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Gestiona el spawn, actualización y colección de loot en el mundo.
 * Responsable de mantener el balance de items en el terreno.
 */
public class LootManager {
    
    private static final Logger LOGGER = Logger.getLogger(LootManager.class.getName());
    
    private final List<Loot> lootItems;
    private final List<XPOrb> xpOrbs;
    private final Random random;
    
    private float fuelSpawnTimer;
    private float scrapSpawnTimer;
    private float nextFuelSpawnTime;
    private float nextScrapSpawnTime;
    
    private int currentFuelCount;
    private int currentScrapCount;
    
    /**
     * Crea un nuevo gestor de loot.
     */
    public LootManager() {
        this.lootItems = new ArrayList<>();
        this.xpOrbs = new ArrayList<>();
        this.random = new Random();
        this.fuelSpawnTimer = 0;
        this.scrapSpawnTimer = 0;
        this.currentFuelCount = 0;
        this.currentScrapCount = 0;
        
        calculateNextSpawnTimes();
        spawnInitialLoot();
    }
    
    /**
     * Genera el loot inicial en el mapa.
     */
    private void spawnInitialLoot() {
        // Spawn inicial de combustible
        for (int i = 0; i < GameConfig.FUEL_INITIAL_SPAWN; i++) {
            spawnFuel();
        }
        
        // Spawn inicial de chatarra
        for (int i = 0; i < GameConfig.SCRAP_INITIAL_SPAWN; i++) {
            spawnScrap();
        }
        
        LOGGER.info("Loot inicial generado: " + currentFuelCount + " fuel, " + currentScrapCount + " scrap");
    }
    
    /**
     * Calcula los próximos tiempos de spawn aleatorios.
     */
    private void calculateNextSpawnTimes() {
        nextFuelSpawnTime = GameConfig.FUEL_SPAWN_INTERVAL_MIN + 
            random.nextFloat() * (GameConfig.FUEL_SPAWN_INTERVAL_MAX - GameConfig.FUEL_SPAWN_INTERVAL_MIN);
        
        nextScrapSpawnTime = GameConfig.SCRAP_SPAWN_INTERVAL_MIN + 
            random.nextFloat() * (GameConfig.SCRAP_SPAWN_INTERVAL_MAX - GameConfig.SCRAP_SPAWN_INTERVAL_MIN);
    }
    
    /**
     * Actualiza el sistema de loot.
     * @param deltaTime Tiempo transcurrido desde el último update
     * @param playerX Centro X del jugador (para atracción de orbes)
     * @param playerY Centro Y del jugador (para atracción de orbes)
     */
    public void update(float deltaTime, float playerX, float playerY) {
        // Actualizar timers de spawn
        fuelSpawnTimer += deltaTime;
        scrapSpawnTimer += deltaTime;
        
        // Spawn de combustible
        if (fuelSpawnTimer >= nextFuelSpawnTime && currentFuelCount < GameConfig.FUEL_MAX_ON_MAP) {
            spawnFuel();
            fuelSpawnTimer = 0;
            nextFuelSpawnTime = GameConfig.FUEL_SPAWN_INTERVAL_MIN + 
                random.nextFloat() * (GameConfig.FUEL_SPAWN_INTERVAL_MAX - GameConfig.FUEL_SPAWN_INTERVAL_MIN);
        }
        
        // Spawn de chatarra
        if (scrapSpawnTimer >= nextScrapSpawnTime && currentScrapCount < GameConfig.SCRAP_MAX_ON_MAP) {
            spawnScrap();
            scrapSpawnTimer = 0;
            nextScrapSpawnTime = GameConfig.SCRAP_SPAWN_INTERVAL_MIN + 
                random.nextFloat() * (GameConfig.SCRAP_SPAWN_INTERVAL_MAX - GameConfig.SCRAP_SPAWN_INTERVAL_MIN);
        }
        
        // Actualizar orbes de XP (atracción magnética)
        for (XPOrb orb : xpOrbs) {
            orb.update(deltaTime, playerX, playerY);
        }
        
        // Limpiar loot recolectado
        removeCollectedLoot();
    }
    
    /**
     * Actualiza el sistema de loot (sin posición del jugador - compatibilidad).
     * @param deltaTime Tiempo transcurrido desde el último update
     */
    public void update(float deltaTime) {
        update(deltaTime, 0, 0);
    }
    
    /**
     * Genera un orbe de XP en una posición específica.
     * Verifica que no colisione con edificios si hay mapa configurado.
     * @param x Posición X
     * @param y Posición Y
     * @param xpValue Cantidad de XP
     */
    public void spawnXPOrb(float x, float y, int xpValue) {
        // Pequeña variación aleatoria en la posición
        float offsetX = (random.nextFloat() - 0.5f) * 20;
        float offsetY = (random.nextFloat() - 0.5f) * 20;
        
        float finalX = x + offsetX;
        float finalY = y + offsetY;
        
        // Sin colisiones en el estacionamiento - spawns siempre válidos
        
        xpOrbs.add(new XPOrb(finalX, finalY, xpValue));
    }
    
    /**
     * Genera un nuevo item de combustible en posición aleatoria válida.
     */
    private void spawnFuel() {
        float x, y;
        
        x = GameConfig.LOOT_SPAWN_MARGIN + 
            random.nextFloat() * (GameConfig.WORLD_WIDTH - 2 * GameConfig.LOOT_SPAWN_MARGIN - GameConfig.FUEL_SIZE);
        y = GameConfig.LOOT_SPAWN_MARGIN + 
            random.nextFloat() * (GameConfig.WORLD_HEIGHT - 2 * GameConfig.LOOT_SPAWN_MARGIN - GameConfig.FUEL_SIZE);
        
        lootItems.add(new Fuel(x, y));
        currentFuelCount++;
    }
    
    /**
     * Genera un nuevo item de chatarra en posición aleatoria válida.
     */
    private void spawnScrap() {
        float x, y;
        
        x = GameConfig.LOOT_SPAWN_MARGIN + 
            random.nextFloat() * (GameConfig.WORLD_WIDTH - 2 * GameConfig.LOOT_SPAWN_MARGIN - GameConfig.SCRAP_SIZE);
        y = GameConfig.LOOT_SPAWN_MARGIN + 
            random.nextFloat() * (GameConfig.WORLD_HEIGHT - 2 * GameConfig.LOOT_SPAWN_MARGIN - GameConfig.SCRAP_SIZE);
        
        lootItems.add(new Scrap(x, y));
        currentScrapCount++;
    }
    
    /**
     * Elimina los items de loot que fueron recolectados.
     */
    private void removeCollectedLoot() {
        Iterator<Loot> iterator = lootItems.iterator();
        while (iterator.hasNext()) {
            Loot loot = iterator.next();
            if (loot.isCollected()) {
                if (loot.getType() == LootType.FUEL) {
                    currentFuelCount--;
                } else if (loot.getType() == LootType.SCRAP) {
                    currentScrapCount--;
                }
                iterator.remove();
            }
        }
        
        // Limpiar orbes de XP recolectados
        xpOrbs.removeIf(XPOrb::isCollected);
    }
    
    /**
     * Verifica colisiones con el jugador y retorna el loot recolectado.
     * @param playerCenterX Centro X del jugador
     * @param playerCenterY Centro Y del jugador
     * @return Lista de loot recolectado
     */
    public List<Loot> checkCollisions(float playerCenterX, float playerCenterY) {
        List<Loot> collected = new ArrayList<>();
        
        // Verificar loot normal
        for (Loot loot : lootItems) {
            if (!loot.isCollected()) {
                float distance = loot.distanceTo(playerCenterX, playerCenterY);
                if (distance <= GameConfig.LOOT_PICKUP_DISTANCE) {
                    loot.collect();
                    collected.add(loot);
                }
            }
        }
        
        // Verificar orbes de XP
        for (XPOrb orb : xpOrbs) {
            if (!orb.isCollected()) {
                float distance = orb.distanceTo(playerCenterX, playerCenterY);
                if (distance <= GameConfig.XP_ORB_PICKUP_DISTANCE) {
                    orb.collect();
                    collected.add(orb);
                }
            }
        }
        
        return collected;
    }
    
    /**
     * Renderiza todos los items de loot.
     * @param g2d Contexto gráfico
     */
    public void render(Graphics2D g2d) {
        // Renderizar loot normal (copia para evitar ConcurrentModificationException)
        List<Loot> lootCopy = new ArrayList<>(lootItems);
        for (Loot loot : lootCopy) {
            loot.render(g2d);
        }
        
        // Renderizar orbes de XP (copia para evitar ConcurrentModificationException)
        List<XPOrb> orbsCopy = new ArrayList<>(xpOrbs);
        for (XPOrb orb : orbsCopy) {
            orb.render(g2d);
        }
    }
    
    /**
     * Obtiene la cantidad actual de combustible en el mapa.
     * @return Cantidad de items de combustible
     */
    public int getFuelCount() {
        return currentFuelCount;
    }
    
    /**
     * Obtiene la cantidad actual de chatarra en el mapa.
     * @return Cantidad de items de chatarra
     */
    public int getScrapCount() {
        return currentScrapCount;
    }
    
    /**
     * Obtiene la cantidad de orbes de XP en el mapa.
     * @return Cantidad de orbes
     */
    public int getXPOrbCount() {
        return xpOrbs.size();
    }
}
