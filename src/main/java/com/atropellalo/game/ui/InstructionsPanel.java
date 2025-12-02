package com.atropellalo.game.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Panel de instrucciones del juego.
 * Muestra los controles y objetivos del juego.
 */
public class InstructionsPanel extends JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color(15, 10, 20);
    private static final Color TITLE_COLOR = new Color(180, 30, 30);
    private static final Color TITLE_SHADOW_COLOR = new Color(60, 10, 10);
    private static final Color TEXT_COLOR = new Color(220, 220, 200);
    private static final Color SUBTITLE_COLOR = new Color(150, 150, 130);
    
    private InstructionsCallback callback;
    
    public InstructionsPanel(int width, int height) {
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
    }
    
    public void setCallback(InstructionsCallback callback) {
        this.callback = callback;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        drawTitle(g2d);
        drawInstructions(g2d);
        drawBackHint(g2d);
    }
    
    private void drawTitle(Graphics2D g2d) {
        String title = "INSTRUCCIONES";
        
        g2d.setFont(new Font("Impact", Font.BOLD, 60));
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        int titleX = (getWidth() - titleWidth) / 2;
        int titleY = 100;
        
        // Sombra del título
        g2d.setColor(TITLE_SHADOW_COLOR);
        g2d.drawString(title, titleX + 4, titleY + 4);
        
        // Título principal
        g2d.setColor(TITLE_COLOR);
        g2d.drawString(title, titleX, titleY);
    }
    
    private void drawInstructions(Graphics2D g2d) {
        int startY = 180;
        int lineHeight = 35;
        int currentY = startY;
        
        // Controles
        g2d.setColor(SUBTITLE_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        g2d.drawString("Controles:", 100, currentY);
        currentY += 50;
        
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        
        String[] controls = {
            "WASD o Flechas - Mover el camión",
            "ESC - Pausar el juego",
            "Las armas disparan automáticamente"
        };
        
        for (String control : controls) {
            g2d.drawString("• " + control, 120, currentY);
            currentY += lineHeight;
        }
        
        currentY += 30;
        
        // Objetivo
        g2d.setColor(SUBTITLE_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        g2d.drawString("Objetivo:", 100, currentY);
        currentY += 50;
        
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        
        String[] objectives = {
            "Sobrevive el mayor tiempo posible",
            "Elimina zombies para ganar experiencia",
            "Sube de nivel para mejorar tus habilidades",
            "Recolecta combustible y chatarra para recuperarte"
        };
        
        for (String objective : objectives) {
            g2d.drawString("• " + objective, 120, currentY);
            currentY += lineHeight;
        }
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
     * Interface para callbacks del panel de instrucciones.
     */
    public interface InstructionsCallback {
        void onBack();
    }
}
