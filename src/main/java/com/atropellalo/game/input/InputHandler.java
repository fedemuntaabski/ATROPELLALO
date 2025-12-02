package com.atropellalo.game.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Maneja la entrada del teclado para el control del jugador.
 * Captura las teclas WASD para movimiento.
 */
public class InputHandler implements KeyListener {
    
    private boolean wPressed;
    private boolean aPressed;
    private boolean sPressed;
    private boolean dPressed;
    
    public InputHandler() {
        this.wPressed = false;
        this.aPressed = false;
        this.sPressed = false;
        this.dPressed = false;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        // No procesar teclas de debug (F1, F3) para que GamePanel las maneje
        if (key == KeyEvent.VK_F1 || key == KeyEvent.VK_F3) {
            return;
        }
        
        switch (key) {
            case KeyEvent.VK_W:
                wPressed = true;
                break;
            case KeyEvent.VK_A:
                aPressed = true;
                break;
            case KeyEvent.VK_S:
                sPressed = true;
                break;
            case KeyEvent.VK_D:
                dPressed = true;
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        switch (key) {
            case KeyEvent.VK_W:
                wPressed = false;
                break;
            case KeyEvent.VK_A:
                aPressed = false;
                break;
            case KeyEvent.VK_S:
                sPressed = false;
                break;
            case KeyEvent.VK_D:
                dPressed = false;
                break;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // No se utiliza
    }
    
    /**
     * Obtiene la dirección horizontal del movimiento.
     * @return -1 (izquierda), 0 (sin movimiento), 1 (derecha)
     */
    public int getHorizontalDirection() {
        int direction = 0;
        if (aPressed) direction -= 1;
        if (dPressed) direction += 1;
        return direction;
    }
    
    /**
     * Obtiene la dirección vertical del movimiento.
     * @return -1 (arriba), 0 (sin movimiento), 1 (abajo)
     */
    public int getVerticalDirection() {
        int direction = 0;
        if (wPressed) direction -= 1;
        if (sPressed) direction += 1;
        return direction;
    }
}
