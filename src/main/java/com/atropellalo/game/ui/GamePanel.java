package com.atropellalo.game.ui;

import com.atropellalo.game.camera.Camera;
import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.Enemy;
import com.atropellalo.game.enemy.EnemyManager;
import com.atropellalo.game.entity.Player;
import com.atropellalo.game.input.InputHandler;
import com.atropellalo.game.loot.Loot;
import com.atropellalo.game.loot.LootManager;
import com.atropellalo.game.loot.LootType;
import com.atropellalo.game.loot.XPOrb;
import com.atropellalo.game.map.CityMap;
import com.atropellalo.game.pathfinding.AStarPathfinder;
import com.atropellalo.game.upgrade.UpgradeManager;
import com.atropellalo.game.upgrade.UpgradeOption;
import com.atropellalo.game.weapon.WeaponManager;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel de renderizado del juego.
 * Responsable de dibujar el mapa de fondo y todos los elementos del juego.
 * Contiene el game loop principal.
 */
public class GamePanel extends JPanel implements Runnable, KeyListener {
    
    private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName());
    private static final int TARGET_FPS = 60;
    private static final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
    
    private Thread gameThread;
    private boolean running;
    private boolean paused;
    
    // Mapa de la ciudad
    private CityMap cityMap;
    
    // Pathfinder A*
    private AStarPathfinder pathfinder;
    
    private Player player;
    private Camera camera;
    private InputHandler inputHandler;
    private LootManager lootManager;
    private EnemyManager enemyManager;
    private WeaponManager weaponManager;
    private GameHUD gameHUD;
    
    // Sistema de mejoras
    private UpgradeManager upgradeManager;
    private UpgradeMenu upgradeMenu;
    
    public GamePanel() {
        setFocusable(true);
        initializeGame();
    }
    
    /**
     * Inicializa los componentes del juego.
     */
    private void initializeGame() {
        // Crear mapa de la ciudad (con semilla fija para consistencia durante desarrollo)
        cityMap = new CityMap(12345L);
        
        // Crear pathfinder A* y configurarlo para los enemigos
        pathfinder = new AStarPathfinder(cityMap);
        Enemy.setPathfinder(pathfinder);
        
        // Crear jugador en el centro del mundo (zona de spawn segura)
        player = new Player(GameConfig.WORLD_WIDTH / 2f, GameConfig.WORLD_HEIGHT / 2f);
        
        // Configurar callback de subida de nivel
        player.setLevelUpCallback(this::onPlayerLevelUp);
        
        // Configurar callback de colisión con el mapa de la ciudad
        player.setCollisionCallback(new Player.CollisionCallback() {
            @Override
            public boolean checkCollision(float x, float y, float width, float height) {
                return cityMap.checkCollision(x, y, width, height);
            }
            
            @Override
            public float[] resolveCollision(float oldX, float oldY, float newX, float newY, 
                                             float width, float height) {
                return cityMap.resolveCollision(oldX, oldY, newX, newY, width, height);
            }
        });
        
        // Crear cámara
        camera = new Camera(1280, 720, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        
        // Remover listeners anteriores antes de agregar nuevos
        for (java.awt.event.KeyListener listener : getKeyListeners()) {
            removeKeyListener(listener);
        }
        
        // Crear y configurar input handler (nuevo cada vez)
        inputHandler = new InputHandler();
        addKeyListener(inputHandler);
        addKeyListener(this); // Para el menú de mejoras y reinicio
        
        // Crear sistema de loot con referencia al mapa para spawn válido
        lootManager = new LootManager();
        lootManager.setCityMap(cityMap);
        
        // Crear sistema de enemigos con referencia al mapa
        enemyManager = new EnemyManager();
        enemyManager.setPlayer(player);
        enemyManager.setLootManager(lootManager);
        enemyManager.setCityMap(cityMap);
        
        // Crear sistema de armas
        weaponManager = new WeaponManager();
        
        // Crear HUD con callback de reinicio
        gameHUD = new GameHUD(1280, 720);
        gameHUD.setRestartCallback(this::restartGame);
        
        // Crear sistema de mejoras
        upgradeManager = new UpgradeManager();
        upgradeMenu = new UpgradeMenu(1280, 720);
        upgradeMenu.setCallback(this::onUpgradeSelected);
        
        paused = false;
        
        LOGGER.info("Juego inicializado - Mundo: " + GameConfig.WORLD_WIDTH + "x" + GameConfig.WORLD_HEIGHT);
        LOGGER.info("Mapa de ciudad generado con semilla: " + cityMap.getSeed());
    }
    
    /**
     * Reinicia el juego creando una nueva partida.
     */
    private void restartGame() {
        LOGGER.info("Reiniciando juego...");
        initializeGame();
    }
    
    /**
     * Callback cuando el jugador sube de nivel.
     */
    private void onPlayerLevelUp(int newLevel) {
        LOGGER.info("¡Jugador subió al nivel " + newLevel + "!");
        
        // Pausar el juego
        paused = true;
        
        // Generar opciones de mejora
        List<UpgradeOption> options = upgradeManager.generateOptions(player, weaponManager);
        
        // Mostrar menú de mejoras
        upgradeMenu.show(options, newLevel);
    }
    
    /**
     * Callback cuando se selecciona una mejora.
     */
    private void onUpgradeSelected(UpgradeOption option) {
        LOGGER.info("Mejora seleccionada: " + option.getTitle());
        
        // Aplicar la mejora
        boolean success = upgradeManager.applyUpgrade(option, player, weaponManager);
        
        if (success) {
            LOGGER.info("Mejora aplicada exitosamente");
        } else {
            LOGGER.warning("Error al aplicar mejora");
        }
        
        // Reanudar el juego
        paused = false;
    }
    
    /**
     * Inicia el game loop.
     */
    public void startGameLoop() {
        if (gameThread == null) {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
            LOGGER.info("Game loop iniciado");
        }
    }
    
    /**
     * Detiene el game loop.
     */
    public void stopGameLoop() {
        running = false;
    }
    
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        
        while (running) {
            long now = System.nanoTime();
            long updateLength = now - lastTime;
            lastTime = now;
            float deltaTime = updateLength / 1000000000f;
            
            update(deltaTime);
            repaint();
            
            try {
                long sleepTime = (OPTIMAL_TIME - (System.nanoTime() - now)) / 1000000;
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.WARNING, "Game loop interrumpido", e);
            }
        }
    }
    
    /**
     * Actualiza la lógica del juego.
     */
    private void update(float deltaTime) {
        // No actualizar si el juego terminó o está pausado
        if (!player.isAlive() || paused) {
            return;
        }
        
        // Actualizar movimiento del jugador
        int moveX = inputHandler.getHorizontalDirection();
        int moveY = inputHandler.getVerticalDirection();
        player.setMovement(moveX, moveY);
        player.update(deltaTime);
        
        // Actualizar sistema de loot (con posición del jugador para atracción de orbes)
        lootManager.update(deltaTime, player.getCenterX(), player.getCenterY());
        
        // Verificar colisiones con loot
        List<Loot> collected = lootManager.checkCollisions(player.getCenterX(), player.getCenterY());
        for (Loot loot : collected) {
            applyLootEffect(loot);
        }
        
        // Actualizar sistema de enemigos
        enemyManager.update(deltaTime, player.getCenterX(), player.getCenterY());
        
        // Actualizar sistema de armas (disparo automático)
        weaponManager.update(deltaTime, player.getCenterX(), player.getCenterY(), enemyManager.getEnemies());
        
        // Actualizar cámara para seguir al jugador
        camera.centerOn(player.getCenterX(), player.getCenterY());
    }
    
    /**
     * Aplica el efecto del loot recolectado al jugador.
     */
    private void applyLootEffect(Loot loot) {
        if (loot.getType() == LootType.FUEL) {
            player.addFuel(loot.getValue());
            LOGGER.fine("Combustible recolectado: +" + loot.getValue());
        } else if (loot.getType() == LootType.SCRAP) {
            player.heal(loot.getValue());
            LOGGER.fine("Chatarra recolectada: +" + loot.getValue() + " HP");
        } else if (loot.getType() == LootType.XP_ORB) {
            XPOrb orb = (XPOrb) loot;
            player.addXP(orb.getXPValue());
            LOGGER.fine("XP recolectado: +" + orb.getXPValue());
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Aplicar transformación de cámara
        g2d.translate(-camera.getOffsetX(), -camera.getOffsetY());
        
        drawMap(g2d);
        drawLoot(g2d);
        drawEnemies(g2d);
        drawProjectiles(g2d);
        drawPlayer(g2d);
        
        // Restaurar transformación para HUD (se dibuja en coordenadas de pantalla)
        g2d.translate(camera.getOffsetX(), camera.getOffsetY());
        
        // Dibujar HUD
        gameHUD.render(g2d, player);
        
        // Dibujar información de oleadas
        enemyManager.renderWaveInfo(g2d, 1280);
        
        // Dibujar menú de mejoras si está visible
        if (upgradeMenu.isVisible()) {
            upgradeMenu.render(g2d);
        }
    }
    
    /**
     * Dibuja el mapa de la ciudad.
     */
    private void drawMap(Graphics2D g2d) {
        cityMap.render(g2d);
    }
    
    /**
     * Dibuja todos los items de loot.
     */
    private void drawLoot(Graphics2D g2d) {
        lootManager.render(g2d);
    }
    
    /**
     * Dibuja todos los enemigos.
     */
    private void drawEnemies(Graphics2D g2d) {
        enemyManager.render(g2d);
    }
    
    /**
     * Dibuja todos los proyectiles y efectos de armas.
     */
    private void drawProjectiles(Graphics2D g2d) {
        weaponManager.render(g2d, player.getCenterX(), player.getCenterY());
    }
    
    /**
     * Dibuja el jugador.
     */
    private void drawPlayer(Graphics2D g2d) {
        player.render(g2d);
    }
    
    // KeyListener para el menú de mejoras y reinicio
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (upgradeMenu.isVisible()) {
            upgradeMenu.handleKeyPress(e.getKeyCode());
        } else if (!player.isAlive()) {
            // Cuando está muerto, delegar al HUD para reiniciar
            gameHUD.handleKeyPress(e.getKeyCode());
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // No necesario
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // No necesario
    }
}
