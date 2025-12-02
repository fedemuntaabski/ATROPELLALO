package com.atropellalo.game.ui;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.sound.SoundManager;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Panel de opciones del juego.
 * Muestra configuraciones de volumen.
 */
public class OptionsPanel extends JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color(15, 10, 20);
    private static final Color TITLE_COLOR = new Color(180, 30, 30);
    private static final Color TITLE_SHADOW_COLOR = new Color(60, 10, 10);
    private static final Color TEXT_COLOR = new Color(220, 220, 200);
    private static final Color SUBTITLE_COLOR = new Color(150, 150, 130);
    private static final Color SLIDER_BG_COLOR = new Color(40, 35, 45);
    private static final Color SLIDER_FILL_COLOR = new Color(100, 40, 40);
    private static final Color SLIDER_HANDLE_COLOR = new Color(150, 60, 60);
    private static final Color SLIDER_HANDLE_HOVER_COLOR = new Color(180, 80, 80);
    
    private static final int SLIDER_WIDTH = 400;
    private static final int SLIDER_HEIGHT = 20;
    private static final int HANDLE_SIZE = 30;
    
    private OptionsCallback callback;
    private boolean draggingWeaponVolume = false;
    private boolean hoveringWeaponHandle = false;
    private boolean draggingMusicVolume = false;
    private boolean hoveringMusicHandle = false;
    
    public OptionsPanel(int width, int height) {
        setPreferredSize(new java.awt.Dimension(width, height));
        setBackground(BACKGROUND_COLOR);
        setFocusable(true);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && callback != null) {
                    callback.onBack();
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePress(e.getX(), e.getY());
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                draggingWeaponVolume = false;
                draggingMusicVolume = false;
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDrag(e.getX(), e.getY());
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e.getX(), e.getY());
            }
        });
    }
    
    private void handleMousePress(int mouseX, int mouseY) {
        int centerX = getWidth() / 2;
        int weaponSliderY = getHeight() / 2 - 80;
        int musicSliderY = getHeight() / 2 + 40;
        int sliderX = centerX - SLIDER_WIDTH / 2;
        
        // Verificar si se hizo clic en el slider de volumen de armas
        if (mouseX >= sliderX && mouseX <= sliderX + SLIDER_WIDTH &&
            mouseY >= weaponSliderY - HANDLE_SIZE / 2 && mouseY <= weaponSliderY + SLIDER_HEIGHT + HANDLE_SIZE / 2) {
            draggingWeaponVolume = true;
            updateWeaponVolume(mouseX, sliderX);
        }
        
        // Verificar si se hizo clic en el slider de volumen de música
        if (mouseX >= sliderX && mouseX <= sliderX + SLIDER_WIDTH &&
            mouseY >= musicSliderY - HANDLE_SIZE / 2 && mouseY <= musicSliderY + SLIDER_HEIGHT + HANDLE_SIZE / 2) {
            draggingMusicVolume = true;
            updateMusicVolume(mouseX, sliderX);
        }
    }
    
    private void handleMouseDrag(int mouseX, int mouseY) {
        if (draggingWeaponVolume) {
            int centerX = getWidth() / 2;
            int sliderX = centerX - SLIDER_WIDTH / 2;
            updateWeaponVolume(mouseX, sliderX);
        }
        
        if (draggingMusicVolume) {
            int centerX = getWidth() / 2;
            int sliderX = centerX - SLIDER_WIDTH / 2;
            updateMusicVolume(mouseX, sliderX);
        }
    }
    
    private void handleMouseMove(int mouseX, int mouseY) {
        int centerX = getWidth() / 2;
        int weaponSliderY = getHeight() / 2 - 80;
        int musicSliderY = getHeight() / 2 + 40;
        int sliderX = centerX - SLIDER_WIDTH / 2;
        
        int weaponHandleX = sliderX + (int)(GameConfig.SOUND_WEAPON_VOLUME * SLIDER_WIDTH) - HANDLE_SIZE / 2;
        int musicHandleX = sliderX + (int)(SoundManager.getInstance().getMusicVolume() * SLIDER_WIDTH) - HANDLE_SIZE / 2;
        
        boolean wasHoveringWeapon = hoveringWeaponHandle;
        boolean wasHoveringMusic = hoveringMusicHandle;
        
        hoveringWeaponHandle = mouseX >= weaponHandleX && mouseX <= weaponHandleX + HANDLE_SIZE &&
                               mouseY >= weaponSliderY - HANDLE_SIZE / 2 && mouseY <= weaponSliderY + SLIDER_HEIGHT + HANDLE_SIZE / 2;
        
        hoveringMusicHandle = mouseX >= musicHandleX && mouseX <= musicHandleX + HANDLE_SIZE &&
                              mouseY >= musicSliderY - HANDLE_SIZE / 2 && mouseY <= musicSliderY + SLIDER_HEIGHT + HANDLE_SIZE / 2;
        
        if (wasHoveringWeapon != hoveringWeaponHandle || wasHoveringMusic != hoveringMusicHandle) {
            repaint();
        }
    }
    
    private void updateWeaponVolume(int mouseX, int sliderX) {
        float newVolume = Math.max(0, Math.min(1, (float)(mouseX - sliderX) / SLIDER_WIDTH));
        GameConfig.SOUND_WEAPON_VOLUME = newVolume;
        SoundManager.getInstance().setWeaponVolume(newVolume);
        repaint();
    }
    
    private void updateMusicVolume(int mouseX, int sliderX) {
        float newVolume = Math.max(0, Math.min(1, (float)(mouseX - sliderX) / SLIDER_WIDTH));
        SoundManager.getInstance().setMusicVolume(newVolume);
        repaint();
    }
    
    public void setCallback(OptionsCallback callback) {
        this.callback = callback;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        drawTitle(g2d);
        drawVolumeControl(g2d);
        drawBackHint(g2d);
    }
    
    private void drawTitle(Graphics2D g2d) {
        String title = "OPCIONES";
        
        g2d.setFont(new Font("Impact", Font.BOLD, 64));
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        int titleX = (getWidth() - titleWidth) / 2;
        int titleY = 120;
        
        // Sombra del título
        g2d.setColor(TITLE_SHADOW_COLOR);
        g2d.drawString(title, titleX + 4, titleY + 4);
        
        // Título principal
        g2d.setColor(TITLE_COLOR);
        g2d.drawString(title, titleX, titleY);
    }
    
    private void drawVolumeControl(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Dibujar slider de volumen de armas
        drawSlider(g2d, centerX, centerY - 80, "Volumen de Armas", 
                   GameConfig.SOUND_WEAPON_VOLUME, 
                   hoveringWeaponHandle, draggingWeaponVolume);
        
        // Dibujar slider de volumen de música
        drawSlider(g2d, centerX, centerY + 40, "Volumen de Música", 
                   SoundManager.getInstance().getMusicVolume(), 
                   hoveringMusicHandle, draggingMusicVolume);
    }
    
    private void drawSlider(Graphics2D g2d, int centerX, int sliderY, String label, 
                           float volume, boolean hovering, boolean dragging) {
        // Etiqueta
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        int labelWidth = g2d.getFontMetrics().stringWidth(label);
        g2d.drawString(label, centerX - labelWidth / 2, sliderY - 20);
        
        // Slider
        int sliderX = centerX - SLIDER_WIDTH / 2;
        
        // Sombra del slider
        g2d.setColor(new Color(10, 5, 10, 150));
        g2d.fillRoundRect(sliderX + 3, sliderY + 3, SLIDER_WIDTH, SLIDER_HEIGHT, 10, 10);
        
        // Fondo del slider
        g2d.setColor(SLIDER_BG_COLOR);
        g2d.fillRoundRect(sliderX, sliderY, SLIDER_WIDTH, SLIDER_HEIGHT, 10, 10);
        
        // Borde del slider
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(80, 30, 30));
        g2d.drawRoundRect(sliderX, sliderY, SLIDER_WIDTH, SLIDER_HEIGHT, 10, 10);
        
        // Relleno del slider según el volumen
        int fillWidth = (int)(volume * SLIDER_WIDTH);
        if (fillWidth > 0) {
            g2d.setColor(SLIDER_FILL_COLOR);
            g2d.fillRoundRect(sliderX, sliderY, fillWidth, SLIDER_HEIGHT, 10, 10);
        }
        
        // Handle del slider
        int handleX = sliderX + fillWidth - HANDLE_SIZE / 2;
        int handleY = sliderY + SLIDER_HEIGHT / 2 - HANDLE_SIZE / 2;
        
        // Sombra del handle
        g2d.setColor(new Color(10, 5, 10, 180));
        g2d.fillOval(handleX + 3, handleY + 3, HANDLE_SIZE, HANDLE_SIZE);
        
        // Handle
        g2d.setColor(hovering || dragging ? 
                     SLIDER_HANDLE_HOVER_COLOR : SLIDER_HANDLE_COLOR);
        g2d.fillOval(handleX, handleY, HANDLE_SIZE, HANDLE_SIZE);
        
        // Borde del handle
        g2d.setColor(new Color(200, 100, 100));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(handleX, handleY, HANDLE_SIZE, HANDLE_SIZE);
        
        // Valor del volumen
        g2d.setColor(SUBTITLE_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        String value = "%d%%".formatted((int)(volume * 100));
        int valueWidth = g2d.getFontMetrics().stringWidth(value);
        g2d.drawString(value, centerX - valueWidth / 2, sliderY + SLIDER_HEIGHT + 30);
        
        g2d.setStroke(new BasicStroke(1));
    }
    
    private void drawBackHint(Graphics2D g2d) {
        g2d.setColor(SUBTITLE_COLOR);
        g2d.setFont(new Font("Arial", Font.ITALIC, 18));
        
        String hint = "Presiona ESC para volver";
        int hintWidth = g2d.getFontMetrics().stringWidth(hint);
        int hintX = (getWidth() - hintWidth) / 2;
        
        g2d.drawString(hint, hintX, getHeight() - 40);
    }
    
    /**
     * Interface para callbacks del panel de opciones.
     */
    public interface OptionsCallback {
        void onBack();
    }
}
