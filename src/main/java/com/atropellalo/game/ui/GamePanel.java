package com.atropellalo.game.ui;

import com.atropellalo.game.camera.Camera;
import com.atropellalo.game.entity.Player;
import com.atropellalo.game.input.InputHandler;

import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
    
    // Tamaño del mundo (mapa más grande que la ventana)
    private static final int WORLD_WIDTH = 2560;  // 2x el ancho de la ventana
    private static final int WORLD_HEIGHT = 1440; // 2x el alto de la ventana
    
    private BufferedImage mapImage;
    private Thread gameThread;
    private boolean running;
    
    private Player player;
    private Camera camera;
    private InputHandler inputHandler;
    
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
        player = new Player(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
        
        // Crear cámara
        camera = new Camera(1280, 720, WORLD_WIDTH, WORLD_HEIGHT);
        
        // Crear y configurar input handler
        inputHandler = new InputHandler();
        addKeyListener(inputHandler);
        
        LOGGER.info("Juego inicializado - Mundo: " + WORLD_WIDTH + "x" + WORLD_HEIGHT);
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
        // Actualizar movimiento del jugador
        int moveX = inputHandler.getHorizontalDirection();
        int moveY = inputHandler.getVerticalDirection();
        player.setMovement(moveX, moveY);
        player.update(deltaTime);
        
        // Actualizar cámara para seguir al jugador
        camera.centerOn(player.getCenterX(), player.getCenterY());
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
        drawPlayer(g2d);
        
        // Restaurar transformación
        g2d.translate(camera.getOffsetX(), camera.getOffsetY());
    }
    
    /**
     * Dibuja el mapa de fondo al tamaño del mundo.
     */
    private void drawMap(Graphics2D g2d) {
        if (mapImage != null) {
            g2d.drawImage(mapImage, 0, 0, WORLD_WIDTH, WORLD_HEIGHT, null);
        }
    }
    
    /**
     * Dibuja el jugador.
     */
    private void drawPlayer(Graphics2D g2d) {
        player.render(g2d);
    }
}
