package com.atropellalo;

import com.atropellalo.game.ui.GameWindow;
import javax.swing.SwingUtilities;

/**
 * Clase principal del juego estilo Survivor.
 * Punto de entrada de la aplicación.
 */
public class Game {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow gameWindow = new GameWindow();
            gameWindow.setVisible(true);
        });
    }
}
