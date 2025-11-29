package com.atropellalo.game.ui;

import com.atropellalo.game.camera.Camera;
import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.enemy.EnemyManager;
import com.atropellalo.game.entity.Player;
import com.atropellalo.game.input.InputHandler;
import com.atropellalo.game.loot.Loot;
import com.atropellalo.game.loot.LootManager;
import com.atropellalo.game.loot.LootType;
import com.atropellalo.game.weapon.WeaponManager;

import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
public class GamePanel extends JPanel implements Runnable {
    
    private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName());
    private static final String MAP_IMAGE_PATH = "/images/map.jpg";
    private static final int TARGET_FPS = 60;
    private static final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
    
    private BufferedImage mapImage;
    private Thread gameThread;
    private boolean running;
    
    private Player player;
    private Camera camera;
    private InputHandler inputHandler;
    private LootManager lootManager;
    private EnemyManager enemyManager;
    private WeaponManager weaponManager;
    private GameHUD gameHUD;
    
    public GamePanel() {
        loadMapImage();
        setFocusable(true);
        initializeGame();
    }
    
    /**
     * Inicializa los componentes del juego.
     */
    private void initializeGame() {
        // Crear jugador en el centro del mundo
        player = new Player(GameConfig.WORLD_WIDTH / 2f, GameConfig.WORLD_HEIGHT / 2f);
        
        // Crear cámara
        camera = new Camera(1280, 720, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        
        // Crear y configurar input handler
        inputHandler = new InputHandler();
        addKeyListener(inputHandler);
        
        // Crear sistema de loot
        lootManager = new LootManager();
        
        // Crear sistema de enemigos
        enemyManager = new EnemyManager();
        enemyManager.setPlayer(player);
        
        // Crear sistema de armas
        weaponManager = new WeaponManager();
        
        // Crear HUD
        gameHUD = new GameHUD(1280, 720);
        
        LOGGER.info("Juego inicializado - Mundo: " + GameConfig.WORLD_WIDTH + "x" + GameConfig.WORLD_HEIGHT);
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
        // No actualizar si el juego terminó
        if (!player.isAlive()) {
            return;
        }
        
        // Actualizar movimiento del jugador
        int moveX = inputHandler.getHorizontalDirection();
        int moveY = inputHandler.getVerticalDirection();
        player.setMovement(moveX, moveY);
        player.update(deltaTime);
        
        // Actualizar sistema de loot
        lootManager.update(deltaTime);
        
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
        }
    }
    
    /**
     * Carga la imagen del mapa desde los recursos.
     */
    private void loadMapImage() {
        try {
            mapImage = ImageIO.read(getClass().getResourceAsStream(MAP_IMAGE_PATH));
            LOGGER.info("Mapa cargado exitosamente: " + MAP_IMAGE_PATH);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la imagen del mapa: " + MAP_IMAGE_PATH, e);
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
    }
    
    /**
     * Dibuja el mapa de fondo al tamaño del mundo.
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
     * Dibuja todos los proyectiles.
     */
    private void drawProjectiles(Graphics2D g2d) {
        weaponManager.render(g2d);
    }
    
    /**
     * Dibuja el jugador.
     */
    private void drawPlayer(Graphics2D g2d) {
        player.render(g2d);
    }
}
