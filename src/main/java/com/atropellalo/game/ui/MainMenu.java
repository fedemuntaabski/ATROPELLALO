package com.atropellalo.game.ui;

import com.atropellalo.game.sound.SoundManager;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Menú principal del juego.
 * Muestra las opciones: Jugar, Instrucciones, Opciones y Salir.
 */
public class MainMenu extends JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color(15, 10, 20);
    private static final Color TITLE_COLOR = new Color(180, 30, 30);
    private static final Color TITLE_SHADOW_COLOR = new Color(60, 10, 10);
    private static final Color BUTTON_COLOR = new Color(40, 35, 45);
    private static final Color BUTTON_HOVER_COLOR = new Color(70, 50, 50);
    private static final Color BUTTON_BORDER_COLOR = new Color(100, 40, 40);
    private static final Color BUTTON_TEXT_COLOR = new Color(220, 220, 200);
    
    private static final int BUTTON_WIDTH = 300;
    private static final int BUTTON_HEIGHT = 60;
    private static final int BUTTON_SPACING = 15;
    
    private final List<MenuButton> buttons;
    private int hoveredButtonIndex = -1;
    private MainMenuCallback callback;
    
    public MainMenu(int width, int height) {
        setPreferredSize(new java.awt.Dimension(width, height));
        setBackground(BACKGROUND_COLOR);
        
        buttons = new ArrayList<>();
        initializeButtons(width, height);
        setupMouseListeners();
    }
    
    private void initializeButtons(int width, int height) {
        int startY = height / 2 - 40;
        int centerX = width / 2;
        
        buttons.add(new MenuButton("Jugar", centerX, startY, BUTTON_WIDTH, BUTTON_HEIGHT));
        buttons.add(new MenuButton("Instrucciones", centerX, startY + (BUTTON_HEIGHT + BUTTON_SPACING), BUTTON_WIDTH, BUTTON_HEIGHT));
        buttons.add(new MenuButton("Opciones", centerX, startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 2, BUTTON_WIDTH, BUTTON_HEIGHT));
        buttons.add(new MenuButton("Salir", centerX, startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 3, BUTTON_WIDTH, BUTTON_HEIGHT));
    }
    
    private void setupMouseListeners() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHoveredButton(e.getX(), e.getY());
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }
    
    private void updateHoveredButton(int mouseX, int mouseY) {
        int previousHovered = hoveredButtonIndex;
        hoveredButtonIndex = -1;
        
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).contains(mouseX, mouseY)) {
                hoveredButtonIndex = i;
                break;
            }
        }
        
        if (previousHovered != hoveredButtonIndex) {
            repaint();
        }
    }
    
    private void handleClick(int mouseX, int mouseY) {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).contains(mouseX, mouseY)) {
                handleButtonAction(i);
                break;
            }
        }
    }
    
    private void handleButtonAction(int buttonIndex) {
        if (callback == null) {
            return;
        }
        
        switch (buttonIndex) {
            case 0: // Jugar
                callback.onPlay();
                break;
            case 1: // Instrucciones
                callback.onInstructions();
                break;
            case 2: // Opciones
                callback.onOptions();
                break;
            case 3: // Salir
                callback.onExit();
                break;
        }
    }
    
    public void setCallback(MainMenuCallback callback) {
        this.callback = callback;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        drawTitle(g2d);
        drawButtons(g2d);
    }
    
    private void drawTitle(Graphics2D g2d) {
        String title = "ATROPELLALO";
        
        // Sombra del título con efecto irregular
        g2d.setFont(new Font("Impact", Font.BOLD, 80));
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        int titleX = (getWidth() - titleWidth) / 2;
        int titleY = 140;
        
        g2d.setColor(TITLE_SHADOW_COLOR);
        g2d.drawString(title, titleX + 5, titleY + 5);
        
        // Título principal
        g2d.setColor(TITLE_COLOR);
        g2d.drawString(title, titleX, titleY);
        
        // Subtítulo con estilo cartoon
        g2d.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));
        g2d.setColor(new Color(150, 150, 130));
        String subtitle = "~ Survivor Game ~";
        int subtitleWidth = g2d.getFontMetrics().stringWidth(subtitle);
        int subtitleX = (getWidth() - subtitleWidth) / 2;
        
        g2d.drawString(subtitle, subtitleX, titleY + 45);
    }
    
    private void drawButtons(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(3));
        
        for (int i = 0; i < buttons.size(); i++) {
            MenuButton button = buttons.get(i);
            boolean isHovered = i == hoveredButtonIndex;
            
            int btnX = button.x - button.width / 2;
            int btnY = button.y - button.height / 2;
            
            // Sombra del botón
            g2d.setColor(new Color(10, 5, 10, 150));
            g2d.fillRoundRect(btnX + 4, btnY + 4, button.width, button.height, 12, 12);
            
            // Dibujar fondo del botón
            g2d.setColor(isHovered ? BUTTON_HOVER_COLOR : BUTTON_COLOR);
            g2d.fillRoundRect(btnX, btnY, button.width, button.height, 12, 12);
            
            // Borde grueso estilo cartoon
            g2d.setColor(isHovered ? new Color(150, 60, 60) : BUTTON_BORDER_COLOR);
            g2d.drawRoundRect(btnX, btnY, button.width, button.height, 12, 12);
            
            // Borde interno más claro
            g2d.setColor(new Color(80, 70, 80, 100));
            g2d.drawRoundRect(btnX + 3, btnY + 3, button.width - 6, button.height - 6, 8, 8);
            
            // Dibujar texto del botón con sombra
            g2d.setFont(new Font("Arial Black", Font.BOLD, 24));
            int textWidth = g2d.getFontMetrics().stringWidth(button.text);
            int textX = button.x - textWidth / 2;
            int textY = button.y + g2d.getFontMetrics().getAscent() / 2 - 5;
            
            // Sombra del texto
            g2d.setColor(new Color(20, 10, 10));
            g2d.drawString(button.text, textX + 2, textY + 2);
            
            // Texto principal
            g2d.setColor(isHovered ? new Color(255, 220, 200) : BUTTON_TEXT_COLOR);
            g2d.drawString(button.text, textX, textY);
        }
        
        g2d.setStroke(new BasicStroke(1));
    }
    
    /**
     * Clase interna para representar un botón del menú.
     */
    private static class MenuButton {
        final String text;
        final int x;
        final int y;
        final int width;
        final int height;
        
        MenuButton(String text, int x, int y, int width, int height) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        boolean contains(int mouseX, int mouseY) {
            int left = x - width / 2;
            int top = y - height / 2;
            return mouseX >= left && mouseX <= left + width &&
                   mouseY >= top && mouseY <= top + height;
        }
    }
    
    /**
     * Interface para callbacks del menú principal.
     */
    public interface MainMenuCallback {
        void onPlay();
        void onInstructions();
        void onOptions();
        void onExit();
    }
}
