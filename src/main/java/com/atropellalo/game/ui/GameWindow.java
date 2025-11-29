package com.atropellalo.game.ui;

import javax.swing.JFrame;

/**
 * Ventana principal del juego.
 * Configura el JFrame y contiene el panel de renderizado.
 */
public class GameWindow extends JFrame {
    
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final String GAME_TITLE = "Atropellalo - Survivor Game";
    
    private final GamePanel gamePanel;
    
    public GameWindow() {
        this.gamePanel = new GamePanel();
        initializeWindow();
    }
    
    private void initializeWindow() {
        setTitle(GAME_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        add(gamePanel);
        
        // Iniciar el game loop cuando la ventana está lista
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                gamePanel.startGameLoop();
            }
            
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                gamePanel.stopGameLoop();
            }
        });
    }
}
