package com.atropellalo.game.ui;

import com.atropellalo.game.upgrade.UpgradeOption;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Menú de selección de mejoras que aparece al subir de nivel.
 * Muestra 3 opciones y permite al jugador seleccionar una.
 */
public class UpgradeMenu {
    
    // Colores del menú
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 220);
    private static final Color CARD_COLOR = new Color(40, 40, 50);
    private static final Color CARD_HOVER_COLOR = new Color(60, 60, 80);
    private static final Color CARD_BORDER_COLOR = new Color(100, 100, 120);
    private static final Color CARD_SELECTED_BORDER = new Color(255, 215, 0);
    private static final Color TITLE_COLOR = new Color(255, 215, 0);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color DESC_COLOR = new Color(180, 180, 180);
    
    // Fuentes
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 36);
    private static final Font LEVEL_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font OPTION_TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font OPTION_DESC_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font HINT_FONT = new Font("Arial", Font.ITALIC, 14);
    
    // Dimensiones de las tarjetas
    private static final int CARD_WIDTH = 280;
    private static final int CARD_HEIGHT = 150;
    private static final int CARD_SPACING = 30;
    
    private final int screenWidth;
    private final int screenHeight;
    
    private List<UpgradeOption> options;
    private int selectedIndex;
    private int newLevel;
    private boolean visible;
    
    // Callback cuando se selecciona una opción
    private UpgradeSelectedCallback callback;
    
    /**
     * Interface para notificar la selección de una mejora.
     */
    public interface UpgradeSelectedCallback {
        void onUpgradeSelected(UpgradeOption option);
    }
    
    public UpgradeMenu(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.selectedIndex = 0;
        this.visible = false;
    }
    
    /**
     * Establece el callback para cuando se seleccione una mejora.
     */
    public void setCallback(UpgradeSelectedCallback callback) {
        this.callback = callback;
    }
    
    /**
     * Muestra el menú con las opciones dadas.
     * @param options Lista de 3 opciones de mejora
     * @param newLevel Nuevo nivel del jugador
     */
    public void show(List<UpgradeOption> options, int newLevel) {
        this.options = options;
        this.newLevel = newLevel;
        this.selectedIndex = 0;
        this.visible = true;
    }
    
    /**
     * Oculta el menú.
     */
    public void hide() {
        this.visible = false;
        this.options = null;
    }
    
    /**
     * Procesa input del teclado.
     * @param keyCode Código de tecla presionada
     */
    public void handleKeyPress(int keyCode) {
        if (!visible || options == null) {
            return;
        }
        
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                selectedIndex = Math.max(0, selectedIndex - 1);
                break;
                
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                selectedIndex = Math.min(options.size() - 1, selectedIndex + 1);
                break;
                
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                selectCurrentOption();
                break;
                
            case KeyEvent.VK_1:
                if (options.size() > 0) {
                    selectedIndex = 0;
                    selectCurrentOption();
                }
                break;
                
            case KeyEvent.VK_2:
                if (options.size() > 1) {
                    selectedIndex = 1;
                    selectCurrentOption();
                }
                break;
                
            case KeyEvent.VK_3:
                if (options.size() > 2) {
                    selectedIndex = 2;
                    selectCurrentOption();
                }
                break;
        }
    }
    
    /**
     * Selecciona la opción actual y notifica al callback.
     */
    private void selectCurrentOption() {
        if (callback != null && options != null && selectedIndex < options.size()) {
            UpgradeOption selected = options.get(selectedIndex);
            hide();
            callback.onUpgradeSelected(selected);
        }
    }
    
    /**
     * Renderiza el menú.
     * @param g2d Contexto gráfico
     */
    public void render(Graphics2D g2d) {
        if (!visible || options == null) {
            return;
        }
        
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Fondo oscuro
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, screenWidth, screenHeight);
        
        // Título "¡SUBISTE DE NIVEL!"
        g2d.setFont(TITLE_FONT);
        String title = "¡SUBISTE DE NIVEL!";
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int titleX = (screenWidth - titleWidth) / 2;
        int titleY = 100;
        
        // Sombra del título
        g2d.setColor(Color.BLACK);
        g2d.drawString(title, titleX + 2, titleY + 2);
        g2d.setColor(TITLE_COLOR);
        g2d.drawString(title, titleX, titleY);
        
        // Nivel actual
        g2d.setFont(LEVEL_FONT);
        String levelText = "Nivel " + newLevel;
        fm = g2d.getFontMetrics();
        int levelWidth = fm.stringWidth(levelText);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(levelText, (screenWidth - levelWidth) / 2, titleY + 40);
        
        // Instrucciones
        g2d.setFont(HINT_FONT);
        String hint = "Usa ← → o A/D para navegar, ENTER o 1-2-3 para seleccionar";
        fm = g2d.getFontMetrics();
        int hintWidth = fm.stringWidth(hint);
        g2d.setColor(DESC_COLOR);
        g2d.drawString(hint, (screenWidth - hintWidth) / 2, titleY + 70);
        
        // Calcular posición inicial de las tarjetas
        int totalWidth = CARD_WIDTH * options.size() + CARD_SPACING * (options.size() - 1);
        int startX = (screenWidth - totalWidth) / 2;
        int cardY = screenHeight / 2 - CARD_HEIGHT / 2;
        
        // Renderizar cada tarjeta de opción
        for (int i = 0; i < options.size(); i++) {
            int cardX = startX + i * (CARD_WIDTH + CARD_SPACING);
            boolean isSelected = (i == selectedIndex);
            renderCard(g2d, options.get(i), cardX, cardY, i + 1, isSelected);
        }
    }
    
    /**
     * Renderiza una tarjeta de opción.
     */
    private void renderCard(Graphics2D g2d, UpgradeOption option, int x, int y, 
                           int number, boolean selected) {
        // Fondo de la tarjeta
        g2d.setColor(selected ? CARD_HOVER_COLOR : CARD_COLOR);
        g2d.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 15, 15);
        
        // Borde de la tarjeta
        g2d.setColor(selected ? CARD_SELECTED_BORDER : CARD_BORDER_COLOR);
        g2d.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 15, 15);
        
        if (selected) {
            // Borde extra para destacar
            g2d.drawRoundRect(x - 1, y - 1, CARD_WIDTH + 2, CARD_HEIGHT + 2, 15, 15);
        }
        
        // Número de opción
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(selected ? TITLE_COLOR : DESC_COLOR);
        g2d.drawString("[" + number + "]", x + 10, y + 25);
        
        // Título de la opción
        g2d.setFont(OPTION_TITLE_FONT);
        g2d.setColor(TEXT_COLOR);
        
        // Centrar título
        FontMetrics fm = g2d.getFontMetrics();
        String title = option.getTitle();
        if (fm.stringWidth(title) > CARD_WIDTH - 20) {
            // Truncar si es muy largo
            while (fm.stringWidth(title + "...") > CARD_WIDTH - 20 && title.length() > 0) {
                title = title.substring(0, title.length() - 1);
            }
            title += "...";
        }
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, x + (CARD_WIDTH - titleWidth) / 2, y + 65);
        
        // Descripción
        g2d.setFont(OPTION_DESC_FONT);
        g2d.setColor(DESC_COLOR);
        fm = g2d.getFontMetrics();
        String desc = option.getDescription();
        int descWidth = fm.stringWidth(desc);
        
        // Dividir descripción en líneas si es necesario
        if (descWidth > CARD_WIDTH - 20) {
            String[] words = desc.split(" ");
            StringBuilder line1 = new StringBuilder();
            StringBuilder line2 = new StringBuilder();
            
            for (String word : words) {
                if (fm.stringWidth(line1.toString() + word + " ") < CARD_WIDTH - 20) {
                    line1.append(word).append(" ");
                } else {
                    line2.append(word).append(" ");
                }
            }
            
            g2d.drawString(line1.toString().trim(), 
                x + (CARD_WIDTH - fm.stringWidth(line1.toString().trim())) / 2, y + 100);
            g2d.drawString(line2.toString().trim(), 
                x + (CARD_WIDTH - fm.stringWidth(line2.toString().trim())) / 2, y + 118);
        } else {
            g2d.drawString(desc, x + (CARD_WIDTH - descWidth) / 2, y + 100);
        }
        
        // Indicador de tipo
        g2d.setFont(new Font("Arial", Font.ITALIC, 12));
        String typeText = getTypeIndicator(option);
        g2d.setColor(getTypeColor(option));
        fm = g2d.getFontMetrics();
        g2d.drawString(typeText, x + (CARD_WIDTH - fm.stringWidth(typeText)) / 2, y + CARD_HEIGHT - 15);
    }
    
    /**
     * Obtiene el texto indicador del tipo de mejora.
     */
    private String getTypeIndicator(UpgradeOption option) {
        if (option.isNewWeapon()) {
            return "★ Nueva Arma";
        } else if (option.isWeaponUpgrade()) {
            return "⚔ Mejora de Arma";
        } else {
            return "🚗 Mejora de Vehículo";
        }
    }
    
    /**
     * Obtiene el color según el tipo de mejora.
     */
    private Color getTypeColor(UpgradeOption option) {
        if (option.isNewWeapon()) {
            return new Color(255, 215, 0); // Dorado
        } else if (option.isWeaponUpgrade()) {
            return new Color(255, 100, 100); // Rojo
        } else {
            return new Color(100, 200, 255); // Azul
        }
    }
    
    /**
     * Verifica si el menú está visible.
     */
    public boolean isVisible() {
        return visible;
    }
}
