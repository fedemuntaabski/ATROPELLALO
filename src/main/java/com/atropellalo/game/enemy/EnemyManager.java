package com.atropellalo.game.enemy;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.entity.Player;
import com.atropellalo.game.loot.LootManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Gestiona el spawn, actualización y colisiones de enemigos.
 * Implementa sistema de oleadas progresivas con jefes en oleadas especiales.
 */
public class EnemyManager implements ExplosiveZombie.ExplosionCallback, 
                                     BruiserBoss.BossDamageCallback, 
                                     InfectorBoss.BossDamageCallback {
    
    private static final Logger LOGGER = Logger.getLogger(EnemyManager.class.getName());
    
    private final List<Enemy> enemies;
    private final Random random;
    
    // Sistema de oleadas
    private int currentWave;
    private float waveTimer;
    private int enemiesRemainingInWave;
    private float spawnTimer;
    private boolean waveInProgress;
    
    // Control de jefes
    private boolean bossSpawnedThisWave;
    private Enemy currentBoss;
    
    // Referencia al jugador para daño por explosión
    private Player player;
    
    // Referencia al LootManager para generar XP orbs
    private LootManager lootManager;
    
    // Estadísticas
    private int totalKills;
    
    /**
     * Crea un nuevo gestor de enemigos.
     */
    public EnemyManager() {
        this.enemies = new ArrayList<>();
        this.random = new Random();
        this.currentWave = 0;
        this.waveTimer = 3.0f; // Primera oleada después de 3 segundos
        this.enemiesRemainingInWave = 0;
        this.spawnTimer = 0;
        this.waveInProgress = false;
        this.totalKills = 0;
        this.bossSpawnedThisWave = false;
        this.currentBoss = null;
    }
    
    /**
     * Establece la referencia al jugador.
     * @param player Jugador
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    /**
     * Establece la referencia al LootManager para generar XP orbs.
     * @param lootManager Gestor de loot
     */
    public void setLootManager(LootManager lootManager) {
        this.lootManager = lootManager;
    }
    
    /**
     * Actualiza todos los enemigos y el sistema de oleadas.
     * @param deltaTime Tiempo desde el último frame
     * @param playerX Posición X del jugador
     * @param playerY Posición Y del jugador
     */
    public void update(float deltaTime, float playerX, float playerY) {
        // Actualizar sistema de oleadas
        updateWaveSystem(deltaTime, playerX, playerY);
        
        // Actualizar cada enemigo
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime, playerX, playerY);
            
            // Actualizar contexto de explosivos
            if (enemy instanceof ExplosiveZombie) {
                ((ExplosiveZombie) enemy).updatePlayerPosition(playerX, playerY);
            }
        }
        
        // Verificar colisiones con jugador
        checkPlayerCollisions(playerX, playerY);
        
        // Limpiar enemigos muertos y generar XP
        cleanupDeadEnemies();
    }
    
    /**
     * Actualiza el sistema de oleadas.
     */
    private void updateWaveSystem(float deltaTime, float playerX, float playerY) {
        if (waveInProgress) {
            // Spawn de enemigos de la oleada actual
            if (enemiesRemainingInWave > 0) {
                spawnTimer += deltaTime;
                if (spawnTimer >= GameConfig.ENEMY_SPAWN_INTERVAL) {
                    spawnTimer = 0;
                    if (enemies.size() < GameConfig.MAX_ENEMIES_ON_MAP) {
                        spawnRandomEnemy(playerX, playerY);
                        enemiesRemainingInWave--;
                    }
                }
            } else if (enemies.isEmpty()) {
                // Oleada completada
                waveInProgress = false;
                waveTimer = GameConfig.WAVE_INTERVAL;
                LOGGER.info("Oleada " + currentWave + " completada!");
            }
        } else {
            // Esperar siguiente oleada
            waveTimer -= deltaTime;
            if (waveTimer <= 0) {
                startNextWave();
            }
        }
    }
    
    /**
     * Inicia la siguiente oleada.
     * En oleadas especiales (10, 20) genera jefes.
     */
    private void startNextWave() {
        currentWave++;
        waveInProgress = true;
        bossSpawnedThisWave = false;
        currentBoss = null;
        
        // Calcular enemigos para esta oleada
        enemiesRemainingInWave = GameConfig.WAVE_BASE_ENEMIES + 
                                 (currentWave - 1) * GameConfig.WAVE_ENEMY_INCREMENT;
        
        LOGGER.info("Iniciando oleada " + currentWave + " con " + enemiesRemainingInWave + " enemigos");
        
        // Verificar si es oleada de jefe
        if (currentWave == GameConfig.BRUISER_BOSS_WAVE) {
            LOGGER.info("¡OLEADA DE JEFE! El Aplastador aparecerá...");
        } else if (currentWave == GameConfig.INFECTOR_BOSS_WAVE) {
            LOGGER.info("¡OLEADA DE JEFE! El Infectador aparecerá...");
        }
    }
    
    /**
     * Genera un enemigo aleatorio en una posición válida.
     * En oleadas de jefe, genera el jefe primero.
     */
    private void spawnRandomEnemy(float playerX, float playerY) {
        // Verificar si debe spawnearse un jefe en esta oleada
        if (!bossSpawnedThisWave) {
            if (currentWave == GameConfig.BRUISER_BOSS_WAVE) {
                spawnBoss(EnemyType.BOSS_BRUISER, playerX, playerY);
                bossSpawnedThisWave = true;
                return;
            } else if (currentWave == GameConfig.INFECTOR_BOSS_WAVE) {
                spawnBoss(EnemyType.BOSS_INFECTOR, playerX, playerY);
                bossSpawnedThisWave = true;
                return;
            }
        }
        
        // Determinar tipo de enemigo normal
        EnemyType type = getRandomEnemyType();
        
        // Encontrar posición válida (lejos del jugador)
        float x, y;
        int attempts = 0;
        do {
            x = GameConfig.ENEMY_SPAWN_MARGIN + 
                random.nextFloat() * (GameConfig.WORLD_WIDTH - 2 * GameConfig.ENEMY_SPAWN_MARGIN);
            y = GameConfig.ENEMY_SPAWN_MARGIN + 
                random.nextFloat() * (GameConfig.WORLD_HEIGHT - 2 * GameConfig.ENEMY_SPAWN_MARGIN);
            attempts++;
        } while (distanceToPoint(x, y, playerX, playerY) < GameConfig.ENEMY_MIN_SPAWN_DISTANCE 
                 && attempts < 50);
        
        // Crear enemigo
        Enemy enemy = createEnemy(type, x, y);
        enemies.add(enemy);
    }
    
    /**
     * Genera un jefe en una posición válida.
     */
    private void spawnBoss(EnemyType bossType, float playerX, float playerY) {
        // Encontrar posición válida para el jefe (lejos del jugador)
        float x, y;
        int attempts = 0;
        do {
            x = GameConfig.ENEMY_SPAWN_MARGIN + 
                random.nextFloat() * (GameConfig.WORLD_WIDTH - 2 * GameConfig.ENEMY_SPAWN_MARGIN);
            y = GameConfig.ENEMY_SPAWN_MARGIN + 
                random.nextFloat() * (GameConfig.WORLD_HEIGHT - 2 * GameConfig.ENEMY_SPAWN_MARGIN);
            attempts++;
        } while (distanceToPoint(x, y, playerX, playerY) < GameConfig.ENEMY_MIN_SPAWN_DISTANCE * 1.5f 
                 && attempts < 50);
        
        Enemy boss;
        if (bossType == EnemyType.BOSS_BRUISER) {
            BruiserBoss bruiser = new BruiserBoss(x, y);
            bruiser.setDamageCallback(this);
            boss = bruiser;
            LOGGER.info("¡El Aplastador ha aparecido en (" + (int)x + ", " + (int)y + ")!");
        } else {
            InfectorBoss infector = new InfectorBoss(x, y);
            infector.setDamageCallback(this);
            boss = infector;
            LOGGER.info("¡El Infectador ha aparecido en (" + (int)x + ", " + (int)y + ")!");
        }
        
        currentBoss = boss;
        enemies.add(boss);
    }
    
    /**
     * Obtiene un tipo de enemigo aleatorio según las probabilidades configuradas.
     */
    private EnemyType getRandomEnemyType() {
        int roll = random.nextInt(100);
        
        if (roll < GameConfig.FAST_ZOMBIE_SPAWN_CHANCE) {
            return EnemyType.FAST;
        } else if (roll < GameConfig.FAST_ZOMBIE_SPAWN_CHANCE + GameConfig.SLOW_ZOMBIE_SPAWN_CHANCE) {
            return EnemyType.SLOW;
        } else {
            return EnemyType.EXPLOSIVE;
        }
    }
    
    /**
     * Crea un enemigo del tipo especificado con escalado por oleada.
     */
    private Enemy createEnemy(EnemyType type, float x, float y) {
        // Calcular factores de escalado según la oleada actual
        float healthScale = (float) Math.pow(GameConfig.WAVE_HEALTH_SCALING, currentWave - 1);
        float speedScale = (float) Math.pow(GameConfig.WAVE_SPEED_SCALING, currentWave - 1);
        float damageScale = (float) Math.pow(GameConfig.WAVE_DAMAGE_SCALING, currentWave - 1);
        
        Enemy enemy;
        switch (type) {
            case FAST:
                enemy = new FastZombie(x, y, healthScale, speedScale, damageScale);
                break;
            case SLOW:
                enemy = new SlowZombie(x, y, healthScale, speedScale, damageScale);
                break;
            case EXPLOSIVE:
                ExplosiveZombie explosive = new ExplosiveZombie(x, y, healthScale, speedScale, damageScale);
                explosive.setExplosionContext(enemies, this);
                enemy = explosive;
                break;
            default:
                enemy = new FastZombie(x, y, healthScale, speedScale, damageScale);
        }
        
        return enemy;
    }
    
    /**
     * Verifica colisiones entre enemigos y jugador.
     * Solo los enemigos dañan al jugador por contacto.
     */
    private void checkPlayerCollisions(float playerX, float playerY) {
        if (player == null || !player.isAlive()) {
            return;
        }
        
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            
            float distance = enemy.distanceToPlayer(playerX, playerY);
            
            if (distance <= GameConfig.ENEMY_COLLISION_DISTANCE) {
                // Enemigo daña al jugador por contacto
                if (enemy.canDamage()) {
                    player.damage(enemy.getDamage());
                    enemy.resetDamageCooldown();
                }
            }
        }
    }
    
    /**
     * Callback cuando un zombie explosivo daña al jugador.
     */
    @Override
    public void onExplosionDamagePlayer(float damage) {
        if (player != null && player.isAlive()) {
            player.damage(damage);
            LOGGER.fine("Jugador dañado por explosión: " + damage);
        }
    }
    
    /**
     * Callback cuando un jefe daña al jugador con habilidad especial.
     */
    @Override
    public void onBossDamage(float damage) {
        if (player != null && player.isAlive()) {
            player.damage(damage);
            LOGGER.info("Jugador dañado por habilidad de jefe: " + damage);
        }
    }
    
    /**
     * Elimina enemigos muertos de la lista y genera XP orbs.
     * Maneja la secuencia de muerte especial de los jefes.
     */
    private void cleanupDeadEnemies() {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (!enemy.isAlive()) {
                // Verificar si es explosivo y ya terminó su animación
                if (enemy instanceof ExplosiveZombie) {
                    ExplosiveZombie explosive = (ExplosiveZombie) enemy;
                    if (!explosive.hasExploded()) {
                        continue; // Esperar a que explote
                    }
                }
                
                // Manejar muerte especial del Infectador
                if (enemy instanceof InfectorBoss) {
                    InfectorBoss infector = (InfectorBoss) enemy;
                    if (!infector.isDying()) {
                        infector.startDeathSequence();
                        continue; // Esperar a que complete la explosión de muerte
                    }
                    if (!infector.isDeathComplete()) {
                        continue; // Aún no termina la animación
                    }
                }
                
                // Limpiar referencia al jefe si es necesario
                if (enemy == currentBoss) {
                    LOGGER.info("¡JEFE DERROTADO! " + enemy.getType());
                    currentBoss = null;
                }
                
                // Generar XP orb en la posición del enemigo
                spawnXPForEnemy(enemy);
                
                // Incrementar contador de kills
                totalKills++;
                
                iterator.remove();
            }
        }
    }
    
    /**
     * Genera un orbe de XP basado en el tipo de enemigo.
     * El XP escala con el multiplicador del enemigo (basado en oleada).
     * Los jefes dan XP fijo alto.
     * @param enemy Enemigo que murió
     */
    private void spawnXPForEnemy(Enemy enemy) {
        if (lootManager == null) {
            return;
        }
        
        int baseXP;
        switch (enemy.getType()) {
            case FAST:
                baseXP = GameConfig.XP_FAST_ZOMBIE;
                break;
            case SLOW:
                baseXP = GameConfig.XP_SLOW_ZOMBIE;
                break;
            case EXPLOSIVE:
                baseXP = GameConfig.XP_EXPLOSIVE_ZOMBIE;
                break;
            case BOSS_BRUISER:
                baseXP = GameConfig.XP_BRUISER_BOSS;
                // Los jefes no escalan XP - ya dan mucho
                lootManager.spawnXPOrb(enemy.getCenterX(), enemy.getCenterY(), baseXP);
                return;
            case BOSS_INFECTOR:
                baseXP = GameConfig.XP_INFECTOR_BOSS;
                // Los jefes no escalan XP - ya dan mucho
                lootManager.spawnXPOrb(enemy.getCenterX(), enemy.getCenterY(), baseXP);
                return;
            default:
                baseXP = GameConfig.XP_FAST_ZOMBIE;
        }
        
        // Aplicar escalado de XP por oleada
        float xpScale = (float) Math.pow(GameConfig.WAVE_XP_SCALING, currentWave - 1);
        int scaledXP = Math.round(baseXP * xpScale * enemy.getXPMultiplier());
        
        lootManager.spawnXPOrb(enemy.getCenterX(), enemy.getCenterY(), scaledXP);
    }
    
    /**
     * Renderiza todos los enemigos.
     * @param g2d Contexto gráfico
     */
    public void render(Graphics2D g2d) {
        for (Enemy enemy : enemies) {
            enemy.render(g2d);
        }
    }
    
    /**
     * Renderiza información de oleada en el HUD.
     * Muestra alerta especial cuando hay un jefe activo.
     */
    public void renderWaveInfo(Graphics2D g2d, int screenWidth) {
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        
        String waveText = "Oleada: " + currentWave;
        String killsText = "Kills: " + totalKills;
        
        int x = screenWidth - 150;
        int y = 30;
        
        // Sombra
        g2d.setColor(Color.BLACK);
        g2d.drawString(waveText, x + 1, y + 1);
        g2d.drawString(killsText, x + 1, y + 21);
        
        // Texto
        g2d.setColor(Color.WHITE);
        g2d.drawString(waveText, x, y);
        g2d.drawString(killsText, x, y + 20);
        
        // Mostrar countdown si hay espera
        if (!waveInProgress && waveTimer > 0) {
            g2d.setColor(Color.YELLOW);
            String nextWave = "Siguiente oleada: " + (int)Math.ceil(waveTimer) + "s";
            g2d.drawString(nextWave, x - 30, y + 40);
        }
        
        // Mostrar enemigos restantes o alerta de jefe
        if (waveInProgress) {
            if (currentBoss != null && currentBoss.isAlive()) {
                // Alerta de jefe activo
                g2d.setColor(new Color(218, 165, 32)); // Dorado
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                String bossAlert = "★ ¡JEFE ACTIVO! ★";
                g2d.drawString(bossAlert, x - 40, y + 40);
                
                // Mostrar salud del jefe
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                int bossHealthPercent = (int) ((currentBoss.getHealth() / currentBoss.getMaxHealth()) * 100);
                String bossHealth = "Salud: " + bossHealthPercent + "%";
                g2d.drawString(bossHealth, x - 10, y + 58);
            } else {
                g2d.setColor(Color.ORANGE);
                String remaining = "Enemigos: " + enemies.size();
                g2d.drawString(remaining, x, y + 40);
            }
        }
    }
    
    /**
     * Calcula distancia entre dos puntos.
     */
    private float distanceToPoint(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    // Getters
    
    public int getCurrentWave() {
        return currentWave;
    }
    
    public int getTotalKills() {
        return totalKills;
    }
    
    public int getEnemyCount() {
        return enemies.size();
    }
    
    public boolean isWaveInProgress() {
        return waveInProgress;
    }
    
    /**
     * Obtiene la lista de enemigos para el sistema de armas.
     * @return Lista de enemigos
     */
    public List<Enemy> getEnemies() {
        return enemies;
    }
    
    /**
     * Incrementa el contador de kills (usado por WeaponManager).
     */
    public void addKill() {
        totalKills++;
    }
}
