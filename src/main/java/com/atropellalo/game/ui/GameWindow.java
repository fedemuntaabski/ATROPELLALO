package com.atropellalo.game.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

/**
 * Ventana principal del juego.
 * Configura el JFrame y gestiona la navegación entre menús.
 */
public class GameWindow extends JFrame {
    
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final String GAME_TITLE = "Atropellalo - Survivor Game";
    
    private static final String MAIN_MENU = "MAIN_MENU";
    private static final String INSTRUCTIONS = "INSTRUCTIONS";
    private static final String OPTIONS = "OPTIONS";
    private static final String GAME = "GAME";
    
    private final CardLayout cardLayout;
    private final JPanel containerPanel;
    
    private MainMenu mainMenu;
    private InstructionsPanel instructionsPanel;
    private OptionsPanel optionsPanel;
    private GamePanel gamePanel;
    
    public GameWindow() {
        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);
        initializeWindow();
        createPanels();
    }
    
    private void initializeWindow() {
        setTitle(GAME_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        add(containerPanel);
    }
    
    private void createPanels() {
        // Crear menú principal
        mainMenu = new MainMenu(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainMenu.setCallback(new MainMenu.MainMenuCallback() {
            @Override
            public void onPlay() {
                startGame();
            }
            
            @Override
            public void onInstructions() {
                showInstructions();
            }
            
            @Override
            public void onOptions() {
                showOptions();
            }
            
            @Override
            public void onExit() {
                System.exit(0);
            }
        });
        
        // Crear panel de instrucciones
        instructionsPanel = new InstructionsPanel(WINDOW_WIDTH, WINDOW_HEIGHT);
        instructionsPanel.setCallback(this::showMainMenu);
        
        // Crear panel de opciones
        optionsPanel = new OptionsPanel(WINDOW_WIDTH, WINDOW_HEIGHT);
        optionsPanel.setCallback(this::showMainMenu);
        
        // Agregar paneles al contenedor
        containerPanel.add(mainMenu, MAIN_MENU);
        containerPanel.add(instructionsPanel, INSTRUCTIONS);
        containerPanel.add(optionsPanel, OPTIONS);
        
        // Mostrar menú principal
        cardLayout.show(containerPanel, MAIN_MENU);
    }
    
    private void showMainMenu() {
        cardLayout.show(containerPanel, MAIN_MENU);
        mainMenu.requestFocusInWindow();
    }
    
    private void showInstructions() {
        cardLayout.show(containerPanel, INSTRUCTIONS);
        instructionsPanel.requestFocusInWindow();
    }
    
    private void showOptions() {
        cardLayout.show(containerPanel, OPTIONS);
        optionsPanel.requestFocusInWindow();
    }
    
    private void startGame() {
        // Crear el panel del juego solo cuando se va a jugar
        if (gamePanel == null) {
            gamePanel = new GamePanel();
            gamePanel.setMainMenuCallback(this::showMainMenu);
            containerPanel.add(gamePanel, GAME);
            
            // Configurar listeners del game panel
            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    if (gamePanel != null) {
                        gamePanel.stopGameLoop();
                    }
                }
            });
        } else {
            // Si ya existe, reiniciar el juego
            gamePanel.stopGameLoop();
            containerPanel.remove(gamePanel);
            gamePanel = new GamePanel();
            gamePanel.setMainMenuCallback(this::showMainMenu);
            containerPanel.add(gamePanel, GAME);
        }
        
        cardLayout.show(containerPanel, GAME);
        gamePanel.requestFocusInWindow();
        gamePanel.startGameLoop();
    }
}
