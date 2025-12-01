package com.atropellalo.game.ui;

import com.atropellalo.game.entity.Player;
import com.atropellalo.game.weapon.Weapon;
import com.atropellalo.game.weapon.WeaponManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

/**
 * Menú de pausa del juego activado con ESC.
 * Muestra estadísticas del jugador y armas con scroll.
 * Incluye botones "Reanudar" y "Salir".
 */
public class PauseMenu implements MouseWheelListener, MouseListener, MouseMotionListener {
    
    private static final int MENU_WIDTH = 600;
    private static final int MENU_HEIGHT = 650;
    private static final int STATS_HEIGHT = 450;
    private static final int BUTTON_WIDTH = 250;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_SPACING = 20;
    
    private static final Color OVERLAY_COLOR = new Color(0, 0, 0, 180);
    private static final Color MENU_BG_COLOR = new Color(30, 30, 40);
    private static final Color STATS_BG_COLOR = new Color(20, 20, 30);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color BUTTON_HOVER_COLOR = new Color(100, 160, 210);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color STAT_VALUE_COLOR = new Color(100, 255, 100);
    
    private final int screenWidth;
    private final int screenHeight;
    
    private boolean visible;
    private int selectedButton; // 0 = Reanudar, 1 = Salir
    private int scrollOffset;
    private int maxScroll;
    
    private Rectangle resumeButton;
    private Rectangle quitButton;
    
    private PauseMenuCallback callback;
    
    /**
     * Interfaz para callbacks del menú de pausa.
     */
    public interface PauseMenuCallback {
        void onResume();
        void onQuit();
    }
    
    public PauseMenu(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.visible = false;
        this.selectedButton = 0;
        this.scrollOffset = 0;
        this.maxScroll = 0;
        
        calculateButtonPositions();
    }
    
    /**
     * Calcula las posiciones de los botones.
     */
    private void calculateButtonPositions() {
        int menuX = (screenWidth - MENU_WIDTH) / 2;
        int menuY = (screenHeight - MENU_HEIGHT) / 2;
        
        int buttonY = menuY + MENU_HEIGHT - BUTTON_HEIGHT - 20;
        int totalButtonWidth = BUTTON_WIDTH * 2 + BUTTON_SPACING;
        int buttonStartX = menuX + (MENU_WIDTH - totalButtonWidth) / 2;
        
        resumeButton = new Rectangle(buttonStartX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        quitButton = new Rectangle(buttonStartX + BUTTON_WIDTH + BUTTON_SPACING, buttonY, 
                                   BUTTON_WIDTH, BUTTON_HEIGHT);
    }
    
    /**
     * Establece el callback del menú.
     */
    public void setCallback(PauseMenuCallback callback) {
        this.callback = callback;
    }
    
    /**
     * Muestra el menú de pausa.
     */
    public void show() {
        visible = true;
        selectedButton = 0;
        scrollOffset = 0;
    }
    
    /**
     * Oculta el menú de pausa.
     */
    public void hide() {
        visible = false;
        scrollOffset = 0;
    }
    
    /**
     * Verifica si el menú está visible.
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Renderiza el menú de pausa.
     */
    public void render(Graphics2D g2d, Player player, WeaponManager weaponManager) {
        if (!visible) {
            return;
        }
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Overlay oscuro
        g2d.setColor(OVERLAY_COLOR);
        g2d.fillRect(0, 0, screenWidth, screenHeight);
        
        int menuX = (screenWidth - MENU_WIDTH) / 2;
        int menuY = (screenHeight - MENU_HEIGHT) / 2;
        
        // Panel del menú
        g2d.setColor(MENU_BG_COLOR);
        g2d.fillRoundRect(menuX, menuY, MENU_WIDTH, MENU_HEIGHT, 20, 20);
        g2d.setColor(BUTTON_HOVER_COLOR);
        g2d.drawRoundRect(menuX, menuY, MENU_WIDTH, MENU_HEIGHT, 20, 20);
        
        // Título
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        String title = "PAUSA";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, menuX + (MENU_WIDTH - titleWidth) / 2, menuY + 50);
        
        // Área de estadísticas scrolleable
        renderStatsArea(g2d, menuX + 20, menuY + 80, MENU_WIDTH - 40, STATS_HEIGHT, 
                       player, weaponManager);
        
        // Botones
        renderButtons(g2d);
    }
    
    /**
     * Renderiza el área de estadísticas con scroll.
     */
    private void renderStatsArea(Graphics2D g2d, int x, int y, int width, int height,
                                  Player player, WeaponManager weaponManager) {
        // Fondo del área de stats
        g2d.setColor(STATS_BG_COLOR);
        g2d.fillRoundRect(x, y, width, height, 10, 10);
        g2d.setColor(BUTTON_HOVER_COLOR);
        g2d.drawRoundRect(x, y, width, height, 10, 10);
        
        // Clip para scroll
        g2d.setClip(x + 5, y + 5, width - 10, height - 10);
        
        int currentY = y + 20 - scrollOffset;
        int contentStartY = currentY;
        
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Estadísticas del jugador
        currentY = renderPlayerStats(g2d, x + 20, currentY, player);
        currentY += 20;
        
        // Estadísticas de armas
        currentY = renderWeaponStats(g2d, x + 20, currentY, width - 40, weaponManager);
        
        // Calcular scroll máximo
        int contentHeight = currentY - contentStartY;
        maxScroll = Math.max(0, contentHeight - height + 40);
        
        // Resetear clip
        g2d.setClip(null);
        
        // Indicadores de scroll mejorados
        if (scrollOffset > 0) {
            // Indicador superior con gradiente
            g2d.setColor(new Color(100, 160, 210, 200));
            int[] xPoints = {x + width / 2, x + width / 2 - 10, x + width / 2 + 10};
            int[] yPoints = {y + 10, y + 20, y + 20};
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(TEXT_COLOR);
            g2d.drawPolygon(xPoints, yPoints, 3);
        }
        if (scrollOffset < maxScroll) {
            // Indicador inferior con gradiente
            g2d.setColor(new Color(100, 160, 210, 200));
            int[] xPoints = {x + width / 2, x + width / 2 - 10, x + width / 2 + 10};
            int[] yPoints = {y + height - 10, y + height - 20, y + height - 20};
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(TEXT_COLOR);
            g2d.drawPolygon(xPoints, yPoints, 3);
        }
    }
    
    /**
     * Renderiza las estadísticas del jugador.
     */
    private int renderPlayerStats(Graphics2D g2d, int x, int y, Player player) {
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("JUGADOR", x, y);
        y += 25;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Nivel
        y = renderStat(g2d, x, y, "Nivel:", String.valueOf(player.getLevel()));
        
        // Experiencia
        y = renderStat(g2d, x, y, "Experiencia:", 
                      player.getCurrentXP() + " / " + player.getXpToNextLevel());
        
        // Salud
        y = renderStat(g2d, x, y, "Salud:", 
                      String.format("%.0f / %.0f", player.getHealth(), player.getMaxHealth()));
        
        // Combustible
        y = renderStat(g2d, x, y, "Combustible:", 
                      String.format("%.0f / %.0f", player.getFuel(), player.getMaxFuel()));
        
        // Velocidad
        y = renderStat(g2d, x, y, "Velocidad:", 
                      String.format("%.0f", player.getSpeed()));
        
        return y;
    }
    
    /**
     * Renderiza las estadísticas de armas.
     */
    private int renderWeaponStats(Graphics2D g2d, int x, int y, int width, 
                                   WeaponManager weaponManager) {
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("ARMAS", x, y);
        y += 25;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        
        List<Weapon> weapons = weaponManager.getWeapons();
        
        if (weapons.isEmpty()) {
            g2d.setColor(STAT_VALUE_COLOR);
            g2d.drawString("Sin armas equipadas", x, y);
            y += 20;
        } else {
            for (Weapon weapon : weapons) {
                y = renderWeaponDetails(g2d, x, y, width, weapon);
                y += 15; // Espacio entre armas
            }
        }
        
        // Estadísticas globales
        y += 10;
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("ESTADÍSTICAS", x, y);
        y += 25;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        y = renderStat(g2d, x, y, "Disparos realizados:", 
                      String.valueOf(weaponManager.getShotsFired()));
        y = renderStat(g2d, x, y, "Disparos acertados:", 
                      String.valueOf(weaponManager.getShotsHit()));
        y = renderStat(g2d, x, y, "Precisión:", 
                      String.format("%.1f%%", weaponManager.getAccuracy()));
        y = renderStat(g2d, x, y, "Proyectiles activos:", 
                      String.valueOf(weaponManager.getActiveProjectiles()));
        
        return y;
    }
    
    /**
     * Renderiza los detalles de un arma.
     */
    private int renderWeaponDetails(Graphics2D g2d, int x, int y, int width, Weapon weapon) {
        // Nombre del arma
        g2d.setColor(BUTTON_HOVER_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(weapon.getName(), x, y);
        y += 20;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Daño
        y = renderStat(g2d, x + 10, y, "Daño:", String.format("%.1f", weapon.getDamage()));
        
        // Cadencia de fuego
        y = renderStat(g2d, x + 10, y, "Cadencia:", 
                      String.format("%.2f/s", 1.0f / weapon.getFireRate()));
        
        // Velocidad de proyectil (si aplica)
        if (weapon.getProjectileSpeed() > 0) {
            y = renderStat(g2d, x + 10, y, "Vel. Proyectil:", 
                          String.format("%.0f", weapon.getProjectileSpeed()));
        }
        
        // Rango
        y = renderStat(g2d, x + 10, y, "Rango:", String.format("%.0f", weapon.getRange()));
        
        // Nivel
        y = renderStat(g2d, x + 10, y, "Nivel:", String.valueOf(weapon.getLevel()));
        
        return y;
    }
    
    /**
     * Renderiza una estadística individual.
     */
    private int renderStat(Graphics2D g2d, int x, int y, String label, String value) {
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(label, x, y);
        
        g2d.setColor(STAT_VALUE_COLOR);
        int labelWidth = g2d.getFontMetrics().stringWidth(label);
        g2d.drawString(value, x + labelWidth + 10, y);
        
        return y + 20;
    }
    
    /**
     * Renderiza los botones.
     */
    private void renderButtons(Graphics2D g2d) {
        // Botón Reanudar
        renderButton(g2d, resumeButton, "REANUDAR", selectedButton == 0);
        
        // Botón Salir
        renderButton(g2d, quitButton, "SALIR", selectedButton == 1);
    }
    
    /**
     * Renderiza un botón individual.
     */
    private void renderButton(Graphics2D g2d, Rectangle bounds, String text, boolean selected) {
        Color buttonColor = selected ? BUTTON_HOVER_COLOR : BUTTON_COLOR;
        
        g2d.setColor(buttonColor);
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
        
        g2d.setColor(TEXT_COLOR);
        g2d.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int textX = bounds.x + (bounds.width - textWidth) / 2;
        int textY = bounds.y + (bounds.height + g2d.getFontMetrics().getAscent()) / 2 - 2;
        g2d.drawString(text, textX, textY);
    }
    
    /**
     * Maneja las entradas del teclado.
     */
    public void handleKeyPress(int keyCode) {
        if (!visible) {
            return;
        }
        
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                selectedButton = Math.max(0, selectedButton - 1);
                break;
                
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                selectedButton = Math.min(1, selectedButton + 1);
                break;
                
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                scrollOffset = Math.max(0, scrollOffset - 30);
                break;
                
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                scrollOffset = Math.min(maxScroll, scrollOffset + 30);
                break;
                
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                executeSelectedButton();
                break;
                
            case KeyEvent.VK_ESCAPE:
                // ESC también cierra el menú (resume)
                if (callback != null) {
                    callback.onResume();
                }
                break;
        }
    }
    
    /**
     * Ejecuta la acción del botón seleccionado.
     */
    private void executeSelectedButton() {
        if (callback == null) {
            return;
        }
        
        switch (selectedButton) {
            case 0: // Reanudar
                callback.onResume();
                break;
            case 1: // Salir
                callback.onQuit();
                break;
        }
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!visible) {
            return;
        }
        
        // Scroll con la rueda del mouse
        int rotation = e.getWheelRotation();
        int scrollAmount = rotation * 40; // 40 píxeles por notch
        
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset + scrollAmount));
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (!visible) {
            return;
        }
        
        int x = e.getX();
        int y = e.getY();
        
        // Verificar click en botón Reanudar
        if (resumeButton.contains(x, y)) {
            selectedButton = 0;
            executeSelectedButton();
        }
        // Verificar click en botón Salir
        else if (quitButton.contains(x, y)) {
            selectedButton = 1;
            executeSelectedButton();
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        if (!visible) {
            return;
        }
        
        int x = e.getX();
        int y = e.getY();
        
        // Actualizar botón seleccionado según posición del mouse
        if (resumeButton.contains(x, y)) {
            selectedButton = 0;
        } else if (quitButton.contains(x, y)) {
            selectedButton = 1;
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        // No necesario
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        // No necesario
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        // No necesario
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        // No necesario
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        // No necesario
    }
}
