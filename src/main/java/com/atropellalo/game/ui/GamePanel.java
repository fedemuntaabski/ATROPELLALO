package com.atropellalo.game.ui;

import com.atropellalo.game.camera.Camera;
import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.debug.DebugManager;
import com.atropellalo.game.debug.DebugConsole;
import com.atropellalo.game.effect.VisualEffectManager;
import com.atropellalo.game.enemy.EnemyManager;
import com.atropellalo.game.entity.Player;
import com.atropellalo.game.input.InputHandler;
import com.atropellalo.game.loot.Loot;
import com.atropellalo.game.loot.LootManager;
import com.atropellalo.game.loot.LootType;
import com.atropellalo.game.loot.XPOrb;
import com.atropellalo.game.upgrade.UpgradeManager;
import com.atropellalo.game.upgrade.UpgradeOption;
import com.atropellalo.game.weapon.WeaponManager;

import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
    private static final String MAP_IMAGE_PATH = "/images/Map.png";
    private static final int TARGET_FPS = 60;
    private static final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
    
    private Thread gameThread;
    private boolean running;
    private boolean paused;
    private boolean pausedByMenu;
    
    // Imagen del mapa de estacionamiento
    private BufferedImage mapImage;
    
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
    
    // Menú de pausa
    private PauseMenu pauseMenu;
    
    // Sistema de debug
    private DebugManager debugManager;
    private boolean debugCommandsRegistered = false;
    
    public GamePanel() {
        setFocusable(true);
        loadMapImage();
        initializeDebugSystem();
        initializeGame();
    }
    
    /**
     * Carga la imagen del mapa de estacionamiento.
     */
    private void loadMapImage() {
        try {
            mapImage = ImageIO.read(getClass().getResourceAsStream(MAP_IMAGE_PATH));
            LOGGER.info("Mapa de estacionamiento cargado exitosamente: " + MAP_IMAGE_PATH);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la imagen del mapa: " + MAP_IMAGE_PATH, e);
        }
    }
    
    /**
     * Inicializa el sistema de debug UNA SOLA VEZ.
     * No se debe llamar en restartGame().
     */
    private void initializeDebugSystem() {
        // Crear sistema de debug
        debugManager = new DebugManager();
        debugManager.initialize();
    }
    
    /**
     * Inicializa los componentes del juego.
     */
    private void initializeGame() {
        // Crear jugador en el centro del mundo
        player = new Player(GameConfig.WORLD_WIDTH / 2f, GameConfig.WORLD_HEIGHT / 2f);
        
        // Configurar callback de subida de nivel
        player.setLevelUpCallback(this::onPlayerLevelUp);
        
        // Sin callback de colisión - el estacionamiento no tiene obstáculos
        player.setCollisionCallback(null);
        
        // Crear cámara
        camera = new Camera(1280, 720, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        
        // Remover listeners anteriores antes de agregar nuevos
        for (java.awt.event.KeyListener listener : getKeyListeners()) {
            removeKeyListener(listener);
        }
        
        // Crear y configurar input handler
        inputHandler = new InputHandler();
        
        // IMPORTANTE: Registrar listeners en orden inverso de prioridad
        // Los últimos registrados se procesan primero
        addKeyListener(inputHandler);  // Primero (menor prioridad)
        addKeyListener(this);           // Último (mayor prioridad - incluye debugManager)
        
        // Crear sistema de loot (sin mapa de colisiones)
        lootManager = new LootManager();
        
        // Crear sistema de enemigos (sin mapa de colisiones)
        enemyManager = new EnemyManager();
        enemyManager.setPlayer(player);
        enemyManager.setLootManager(lootManager);
        
        // Crear sistema de armas
        weaponManager = new WeaponManager();
        
        // Registrar comandos de debug SOLO la primera vez
        if (!debugCommandsRegistered) {
            registerDebugCommands();
            debugCommandsRegistered = true;
        }
        
        // Crear HUD con callback de reinicio
        gameHUD = new GameHUD(1280, 720);
        gameHUD.setRestartCallback(this::restartGame);
        
        // Crear sistema de mejoras
        upgradeManager = new UpgradeManager();
        upgradeMenu = new UpgradeMenu(1280, 720);
        upgradeMenu.setCallback(this::onUpgradeSelected);
        
        // Crear menú de pausa
        pauseMenu = new PauseMenu(1280, 720);
        pauseMenu.setCallback(new PauseMenu.PauseMenuCallback() {
            @Override
            public void onResume() {
                resumeGame();
            }
            
            @Override
            public void onQuit() {
                quitToDesktop();
            }
        });
        
        // Agregar listener para scroll con rueda del mouse
        addMouseWheelListener(pauseMenu);
        
        // Agregar listeners para click y hover del mouse
        addMouseListener(pauseMenu);
        addMouseMotionListener(pauseMenu);
        
        paused = false;
        pausedByMenu = false;
        
        LOGGER.info("Juego inicializado - Mundo: " + GameConfig.WORLD_WIDTH + "x" + GameConfig.WORLD_HEIGHT);
        LOGGER.info("Mapa de estacionamiento cargado - Sin obstáculos");
    }
    
    /**
     * Reinicia el juego creando una nueva partida.
     */
    private void restartGame() {
        LOGGER.info("Reiniciando juego...");
        initializeGame();
    }
    
    /**
     * Registra comandos de debug con referencias al juego.
     */
    private void registerDebugCommands() {
        LOGGER.info("Registrando comandos de debug personalizados...");
        
        // Comando para agregar XP al jugador
        debugManager.registerCommand("addxp", "Agrega XP al jugador (uso: addxp <cantidad>)", args -> {
            if (args.length < 1) {
                debugManager.logMessage("Usage: addxp <amount>", DebugConsole.MessageType.ERROR);
                return;
            }
            
            try {
                int amount = Integer.parseInt(args[0]);
                player.addXP(amount);
                debugManager.logMessage("Added " + amount + " XP to player", DebugConsole.MessageType.INFO);
            } catch (NumberFormatException e) {
                debugManager.logMessage("Invalid amount: " + args[0], DebugConsole.MessageType.ERROR);
            }
        });
        
        // Comando para curar al jugador
        debugManager.registerCommand("heal", "Restaura la vida del jugador al máximo", args -> {
            player.heal(player.getMaxHealth());
            debugManager.logMessage("Player healed to full health", DebugConsole.MessageType.INFO);
        });
        
        // Comando para subir de nivel
        debugManager.registerCommand("levelup", "Sube de nivel al jugador", args -> {
            int xpNeeded = player.getXPForNextLevel() - player.getCurrentXP();
            player.addXP(xpNeeded);
            debugManager.logMessage("Player leveled up!", DebugConsole.MessageType.INFO);
        });
        
        // Comando para detener spawn de enemigos
        debugManager.registerCommand("stopspawn", "Detiene el spawn de enemigos", args -> {
            enemyManager.setSpawnEnabled(false);
            debugManager.logMessage("Enemy spawning disabled", DebugConsole.MessageType.INFO);
        });
        
        // Comando para reactivar spawn de enemigos
        debugManager.registerCommand("startspawn", "Reactiva el spawn de enemigos", args -> {
            enemyManager.setSpawnEnabled(true);
            debugManager.logMessage("Enemy spawning enabled", DebugConsole.MessageType.INFO);
        });
        
        // Comando para eliminar todos los enemigos
        debugManager.registerCommand("clearwave", "Elimina todos los enemigos actuales", args -> {
            int count = enemyManager.getEnemies().size();
            enemyManager.clearAllEnemies();
            debugManager.logMessage("Cleared " + count + " enemies", DebugConsole.MessageType.INFO);
        });
        
        // Comando para dar combustible
        debugManager.registerCommand("addfuel", "Agrega combustible (uso: addfuel [cantidad], default: 100)", args -> {
            if (args.length < 1) {
                player.addFuel(100);
                debugManager.logMessage("Added 100 fuel", DebugConsole.MessageType.INFO);
            } else {
                try {
                    float amount = Float.parseFloat(args[0]);
                    player.addFuel(amount);
                    debugManager.logMessage("Added " + amount + " fuel", DebugConsole.MessageType.INFO);
                } catch (NumberFormatException e) {
                    debugManager.logMessage("Invalid amount: " + args[0], DebugConsole.MessageType.ERROR);
                }
            }
        });
        
        // Comando para modo god (invencibilidad)
        debugManager.registerCommand("god", "Alterna modo invulnerable (no implementado aún)", args -> {
            // Este comando requeriría agregar un flag de invencibilidad al Player
            debugManager.logMessage("God mode not yet implemented", DebugConsole.MessageType.WARNING);
        });
        
        LOGGER.info("Comandos de debug registrados exitosamente");
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
     * Muestra el menú de pausa.
     */
    private void showPauseMenu() {
        if (!player.isAlive() || paused) {
            return; // No pausar si está muerto o ya pausado por nivel
        }
        
        pausedByMenu = true;
        pauseMenu.show();
    }
    
    /**
     * Reanuda el juego desde el menú de pausa.
     */
    private void resumeGame() {
        LOGGER.info("Reanudando juego desde menú de pausa");
        pausedByMenu = false;
        pauseMenu.hide();
    }
    
    /**
     * Sale al escritorio.
     */
    private void quitToDesktop() {
        LOGGER.info("Saliendo del juego...");
        System.exit(0);
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
            
            // Actualizar debug antes del update del juego
            int totalLoot = lootManager.getFuelCount() + lootManager.getScrapCount() + lootManager.getXPOrbCount();
            debugManager.update(now, 
                enemyManager.getEnemies().size(), 
                weaponManager.getActiveProjectiles(), 
                totalLoot);
            
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
        if (!player.isAlive() || paused || pausedByMenu) {
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
        
        // Actualizar efectos visuales
        VisualEffectManager.getInstance().update(deltaTime);
        
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
        
        // Guardar el offset de la cámara para restauración exacta
        int offsetX = camera.getOffsetX();
        int offsetY = camera.getOffsetY();
        
        // Aplicar transformación de cámara
        g2d.translate(-offsetX, -offsetY);
        
        drawMap(g2d);
        drawLoot(g2d);
        drawEnemies(g2d);
        drawProjectiles(g2d);
        drawVisualEffects(g2d);
        drawPlayer(g2d);
        
        // Restaurar transformación usando los mismos valores para evitar temblor del HUD
        g2d.translate(offsetX, offsetY);
        
        // Dibujar HUD
        gameHUD.render(g2d, player);
        
        // Dibujar información de oleadas
        enemyManager.renderWaveInfo(g2d, 1280);
        
        // Dibujar menú de mejoras si está visible
        if (upgradeMenu.isVisible()) {
            upgradeMenu.render(g2d);
        }
        
        // Dibujar menú de pausa si está visible
        if (pauseMenu.isVisible()) {
            pauseMenu.render(g2d, player, weaponManager);
        }
        
        // Dibujar debug overlay (siempre al final para que esté encima de todo)
        debugManager.render(g2d, player, camera, getWidth(), getHeight());
    }
    
    /**
     * Dibuja el mapa de estacionamiento.
     */
    private void drawMap(Graphics2D g2d) {
        if (mapImage != null) {
            g2d.drawImage(mapImage, 0, 0, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, null);
        }
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
     * Dibuja todos los efectos visuales (explosiones, llamas, etc.).
     */
    private void drawVisualEffects(Graphics2D g2d) {
        VisualEffectManager.getInstance().render(g2d);
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
        // Primero intentar con debug (máxima prioridad)
        if (debugManager.handleKeyPress(e)) {
            return; // El debug consumió el evento
        }
        
        // Si la consola está visible, no procesar otros inputs
        if (debugManager.isConsoleVisible()) {
            return;
        }
        
        // Prioridad al menú de pausa
        if (pauseMenu.isVisible()) {
            pauseMenu.handleKeyPress(e.getKeyCode());
            return;
        }
        
        // ESC abre el menú de pausa
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            showPauseMenu();
            return;
        }
        
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
