package com.atropellalo.game.ui;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.entity.Player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Heads-Up Display (HUD) del juego.
 * Muestra información vital del jugador: salud, combustible, XP y nivel.
 */
public class GameHUD {
    
    private static final String HUD_SPRITE_PATH = "/images/HUD.png";
    private static BufferedImage hudSprite = null;
    
    // Posiciones y dimensiones del HUD y las barras
    private static final int HUD_X = 20;
    private static final int HUD_Y = 20;
    private static final float HUD_SCALE = 0.3f;
    
    // Escala independiente para las barras dinámicas
    private static final float BAR_SCALE = 0.5f;
    
    // Coordenadas relativas de las barras dentro del HUD (en píxeles de la imagen original)
    private static final int HEALTH_BAR_X = 145;
    private static final int HEALTH_BAR_Y = 170;
    private static final int HEALTH_BAR_WIDTH = 328;
    private static final int HEALTH_BAR_HEIGHT = 40;
    
    private static final int FUEL_BAR_X = 157;
    private static final int FUEL_BAR_Y = 296;
    private static final int FUEL_BAR_WIDTH = 319;
    private static final int FUEL_BAR_HEIGHT = 40;
    
    private static final int LEVEL_BAR_X = 145;
    private static final int LEVEL_BAR_Y = 420;
    private static final int LEVEL_BAR_WIDTH = 320;
    private static final int LEVEL_BAR_HEIGHT = 40;
    
    // Colores para las barras dinámicas
    private static final Color HEALTH_BAR_COLOR = new Color(139, 37, 37, 220);
    private static final Color FUEL_BAR_COLOR = new Color(139, 90, 43, 220);
    private static final Color LEVEL_BAR_COLOR = new Color(100, 100, 100, 220);
    
    // Colores para la barra de salud
    private static final Color HEALTH_BAR_BG = new Color(60, 60, 60, 200);
    private static final Color HEALTH_BAR_FULL = new Color(50, 205, 50);
    private static final Color HEALTH_BAR_MEDIUM = new Color(255, 165, 0);
    private static final Color HEALTH_BAR_LOW = new Color(220, 20, 60);
    private static final Color HEALTH_BAR_BORDER = new Color(40, 40, 40);
    
    // Colores para la barra de combustible
    private static final Color FUEL_BAR_BG = new Color(60, 60, 60, 200);
    private static final Color FUEL_BAR_FULL = new Color(255, 165, 0);
    private static final Color FUEL_BAR_MEDIUM = new Color(255, 100, 0);
    private static final Color FUEL_BAR_LOW = new Color(255, 50, 50);
    private static final Color FUEL_BAR_BORDER = new Color(40, 40, 40);
    
    // Colores para la barra de XP
    private static final Color XP_BAR_BG = new Color(60, 60, 60, 200);
    private static final Color XP_BAR_FILL = new Color(100, 180, 255);
    private static final Color XP_BAR_GLOW = new Color(150, 200, 255);
    private static final Color XP_BAR_BORDER = new Color(40, 40, 40);
    private static final Color LEVEL_COLOR = new Color(255, 215, 0);
    
    // Colores de texto
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color TEXT_SHADOW = new Color(0, 0, 0, 150);
    
    // Fuentes
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font VALUE_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final Font LEVEL_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font GAME_OVER_FONT = new Font("Arial", Font.BOLD, 48);
    private static final Font GAME_OVER_SUB_FONT = new Font("Arial", Font.PLAIN, 18);
    private static final Font RESTART_FONT = new Font("Arial", Font.BOLD, 20);
    
    private final int screenWidth;
    private final int screenHeight;
    
    // Callback para reiniciar el juego
    private RestartCallback restartCallback;
    
    /**
     * Interface para callback de reinicio del juego.
     */
    public interface RestartCallback {
        void onRestart();
    }
    
    /**
     * Crea un nuevo HUD.
     * @param screenWidth Ancho de la pantalla
     * @param screenHeight Alto de la pantalla
     */
    public GameHUD(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.restartCallback = null;
        loadHudSprite();
    }
    
    /**
     * Carga el sprite del HUD desde el archivo.
     */
    private void loadHudSprite() {
        if (hudSprite == null) {
            try {
                hudSprite = ImageIO.read(getClass().getResourceAsStream(HUD_SPRITE_PATH));
            } catch (IOException e) {
                System.err.println("Error cargando " + HUD_SPRITE_PATH + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Establece el callback para reiniciar el juego.
     * @param callback Callback a ejecutar cuando se quiera reiniciar
     */
    public void setRestartCallback(RestartCallback callback) {
        this.restartCallback = callback;
    }
    
    /**
     * Maneja la tecla presionada para reiniciar.
     * @param keyCode Código de la tecla
     */
    public void handleKeyPress(int keyCode) {
        // R para reiniciar cuando está en Game Over
        if (keyCode == java.awt.event.KeyEvent.VK_R && restartCallback != null) {
            restartCallback.onRestart();
        }
    }
    
    /**
     * Renderiza el HUD completo.
     * @param g2d Contexto gráfico
     * @param player Jugador para obtener stats
     */
    public void render(Graphics2D g2d, Player player) {
        // Activar antialiasing para texto suave
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Dibujar el sprite del HUD como base
        if (hudSprite != null) {
            int scaledWidth = (int) (hudSprite.getWidth() * HUD_SCALE);
            int scaledHeight = (int) (hudSprite.getHeight() * HUD_SCALE);
            g2d.drawImage(hudSprite, HUD_X, HUD_Y, scaledWidth, scaledHeight, null);
            
            // Dibujar las barras dinámicas encima
            renderHealthBar(g2d, player);
            renderFuelBar(g2d, player);
            renderLevelBar(g2d, player);
        } else {
            // Fallback al HUD antiguo si no se puede cargar el sprite
            renderHealthBarOld(g2d, player);
            renderFuelBarOld(g2d, player);
            renderXPBarOld(g2d, player);
        }
        
        // Mostrar Game Over si el jugador murió
        if (!player.isAlive()) {
            renderGameOver(g2d);
        }
        
        // Mostrar advertencia de sin combustible
        if (!player.hasFuel() && player.isAlive()) {
            renderNoFuelWarning(g2d);
        }
    }
    
    /**
     * Renderiza la barra de salud sobre el HUD sprite.
     */
    private void renderHealthBar(Graphics2D g2d, Player player) {
        float healthPercent = player.getHealth() / player.getMaxHealth();
        
        int scaledX = (int) (HEALTH_BAR_X * BAR_SCALE);
        int scaledY = (int) (HEALTH_BAR_Y * BAR_SCALE);
        int scaledWidth = (int) (HEALTH_BAR_WIDTH * BAR_SCALE);
        int scaledHeight = (int) (HEALTH_BAR_HEIGHT * BAR_SCALE);
        
        int barWidth = (int) (healthPercent * scaledWidth);
        
        if (barWidth > 0) {
            g2d.setColor(HEALTH_BAR_COLOR);
            g2d.fillRect(HUD_X + scaledX, HUD_Y + scaledY, barWidth, scaledHeight);
        }
    }
    
    /**
     * Renderiza la barra de combustible sobre el HUD sprite.
     */
    private void renderFuelBar(Graphics2D g2d, Player player) {
        float fuelPercent = player.getFuel() / player.getMaxFuel();
        
        int scaledX = (int) (FUEL_BAR_X * BAR_SCALE);
        int scaledY = (int) (FUEL_BAR_Y * BAR_SCALE);
        int scaledWidth = (int) (FUEL_BAR_WIDTH * BAR_SCALE);
        int scaledHeight = (int) (FUEL_BAR_HEIGHT * BAR_SCALE);
        
        int barWidth = (int) (fuelPercent * scaledWidth);
        
        if (barWidth > 0) {
            g2d.setColor(FUEL_BAR_COLOR);
            g2d.fillRect(HUD_X + scaledX, HUD_Y + scaledY, barWidth, scaledHeight);
        }
    }
    
    /**
     * Renderiza la barra de nivel sobre el HUD sprite.
     */
    private void renderLevelBar(Graphics2D g2d, Player player) {
        float xpPercent = player.getXPProgress();
        
        int scaledX = (int) (LEVEL_BAR_X * BAR_SCALE);
        int scaledY = (int) (LEVEL_BAR_Y * BAR_SCALE);
        int scaledWidth = (int) (LEVEL_BAR_WIDTH * BAR_SCALE);
        int scaledHeight = (int) (LEVEL_BAR_HEIGHT * BAR_SCALE);
        
        int barWidth = (int) (xpPercent * scaledWidth);
        
        if (barWidth > 0) {
            g2d.setColor(LEVEL_BAR_COLOR);
            g2d.fillRect(HUD_X + scaledX, HUD_Y + scaledY, barWidth, scaledHeight);
        }
    }
    
    /**
     * Renderiza la barra de salud (versión antigua, fallback).
     */
    private void renderHealthBarOld(Graphics2D g2d, Player player) {
        int x = GameConfig.HUD_MARGIN;
        int y = GameConfig.HUD_MARGIN;
        
        // Etiqueta
        drawTextWithShadow(g2d, "SALUD", x, y - 5, LABEL_FONT);
        
        // Fondo de la barra
        g2d.setColor(HEALTH_BAR_BG);
        g2d.fillRoundRect(x, y, GameConfig.HUD_BAR_WIDTH, GameConfig.HUD_BAR_HEIGHT, 5, 5);
        
        // Calcular porcentaje y color
        float healthPercent = player.getHealth() / player.getMaxHealth();
        Color barColor = getHealthColor(healthPercent);
        
        // Barra de salud actual
        int barWidth = (int) (healthPercent * (GameConfig.HUD_BAR_WIDTH - 4));
        if (barWidth > 0) {
            g2d.setColor(barColor);
            g2d.fillRoundRect(x + 2, y + 2, barWidth, GameConfig.HUD_BAR_HEIGHT - 4, 3, 3);
        }
        
        // Borde
        g2d.setColor(HEALTH_BAR_BORDER);
        g2d.drawRoundRect(x, y, GameConfig.HUD_BAR_WIDTH, GameConfig.HUD_BAR_HEIGHT, 5, 5);
        
        // Valor numérico
        String healthText = (int) player.getHealth() + "/" + (int) player.getMaxHealth();
        drawCenteredText(g2d, healthText, x, y, GameConfig.HUD_BAR_WIDTH, GameConfig.HUD_BAR_HEIGHT, VALUE_FONT);
    }
    
    /**
     * Renderiza la barra de combustible (versión antigua, fallback).
     */
    private void renderFuelBarOld(Graphics2D g2d, Player player) {
        int x = GameConfig.HUD_MARGIN;
        int y = GameConfig.HUD_MARGIN + GameConfig.HUD_BAR_HEIGHT + GameConfig.HUD_SPACING + 15;
        
        // Etiqueta
        drawTextWithShadow(g2d, "COMBUSTIBLE", x, y - 5, LABEL_FONT);
        
        // Fondo de la barra
        g2d.setColor(FUEL_BAR_BG);
        g2d.fillRoundRect(x, y, GameConfig.HUD_BAR_WIDTH, GameConfig.HUD_BAR_HEIGHT, 5, 5);
        
        // Calcular porcentaje y color
        float fuelPercent = player.getFuel() / player.getMaxFuel();
        Color barColor = getFuelColor(fuelPercent);
        
        // Barra de combustible actual
        int barWidth = (int) (fuelPercent * (GameConfig.HUD_BAR_WIDTH - 4));
        if (barWidth > 0) {
            g2d.setColor(barColor);
            g2d.fillRoundRect(x + 2, y + 2, barWidth, GameConfig.HUD_BAR_HEIGHT - 4, 3, 3);
        }
        
        // Borde
        g2d.setColor(FUEL_BAR_BORDER);
        g2d.drawRoundRect(x, y, GameConfig.HUD_BAR_WIDTH, GameConfig.HUD_BAR_HEIGHT, 5, 5);
        
        // Valor numérico
        String fuelText = (int) player.getFuel() + "/" + (int) player.getMaxFuel();
        drawCenteredText(g2d, fuelText, x, y, GameConfig.HUD_BAR_WIDTH, GameConfig.HUD_BAR_HEIGHT, VALUE_FONT);
        
        // Icono de advertencia si el combustible es bajo
        if (fuelPercent < 0.2f && fuelPercent > 0) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("⚠", x + GameConfig.HUD_BAR_WIDTH + 5, y + 15);
        }
    }
    
    /**
     * Renderiza la barra de experiencia y nivel (versión antigua, fallback).
     */
    private void renderXPBarOld(Graphics2D g2d, Player player) {
        int x = GameConfig.HUD_MARGIN;
        int y = GameConfig.HUD_MARGIN + (GameConfig.HUD_BAR_HEIGHT + GameConfig.HUD_SPACING + 15) * 2;
        
        // Indicador de nivel
        g2d.setFont(LEVEL_FONT);
        String levelText = "Nv." + player.getLevel();
        g2d.setColor(TEXT_SHADOW);
        g2d.drawString(levelText, x + 1, y - 4);
        g2d.setColor(LEVEL_COLOR);
        g2d.drawString(levelText, x, y - 5);
        
        // Calcular ancho del texto de nivel para ajustar la barra
        int levelTextWidth = g2d.getFontMetrics().stringWidth(levelText) + 10;
        int barX = x + levelTextWidth;
        int barWidth = GameConfig.HUD_BAR_WIDTH - levelTextWidth;
        int barHeight = 12; // Barra más pequeña para XP
        
        // Fondo de la barra
        g2d.setColor(XP_BAR_BG);
        g2d.fillRoundRect(barX, y - 2, barWidth, barHeight, 4, 4);
        
        // Calcular porcentaje de XP
        float xpPercent = player.getXPProgress();
        
        // Barra de XP actual
        int fillWidth = (int) (xpPercent * (barWidth - 4));
        if (fillWidth > 0) {
            g2d.setColor(XP_BAR_FILL);
            g2d.fillRoundRect(barX + 2, y, fillWidth, barHeight - 4, 3, 3);
            
            // Efecto de brillo
            g2d.setColor(XP_BAR_GLOW);
            g2d.fillRoundRect(barX + 2, y, fillWidth, 3, 3, 3);
        }
        
        // Borde
        g2d.setColor(XP_BAR_BORDER);
        g2d.drawRoundRect(barX, y - 2, barWidth, barHeight, 4, 4);
        
        // Texto de XP
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        String xpText = player.getCurrentXP() + "/" + player.getXpToNextLevel();
        int textWidth = g2d.getFontMetrics().stringWidth(xpText);
        int textX = barX + (barWidth - textWidth) / 2;
        
        g2d.setColor(TEXT_SHADOW);
        g2d.drawString(xpText, textX + 1, y + 8);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(xpText, textX, y + 7);
    }
    
    /**
     * Renderiza pantalla de Game Over con opción de reiniciar.
     */
    private void renderGameOver(Graphics2D g2d) {
        // Fondo semi-transparente
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, screenWidth, screenHeight);
        
        // Texto de Game Over
        g2d.setFont(GAME_OVER_FONT);
        String gameOverText = "GAME OVER";
        int textWidth = g2d.getFontMetrics().stringWidth(gameOverText);
        int x = (screenWidth - textWidth) / 2;
        int y = screenHeight / 2 - 40;
        
        // Sombra
        g2d.setColor(Color.BLACK);
        g2d.drawString(gameOverText, x + 3, y + 3);
        
        // Texto principal
        g2d.setColor(Color.RED);
        g2d.drawString(gameOverText, x, y);
        
        // Subtexto
        g2d.setFont(GAME_OVER_SUB_FONT);
        String subText = "Te has quedado sin salud";
        textWidth = g2d.getFontMetrics().stringWidth(subText);
        x = (screenWidth - textWidth) / 2;
        g2d.setColor(Color.WHITE);
        g2d.drawString(subText, x, y + 40);
        
        // Texto de reinicio (parpadeante)
        g2d.setFont(RESTART_FONT);
        String restartText = "Presiona R para reiniciar";
        textWidth = g2d.getFontMetrics().stringWidth(restartText);
        x = (screenWidth - textWidth) / 2;
        
        // Efecto de parpadeo
        if ((System.currentTimeMillis() / 500) % 2 == 0) {
            // Fondo para el botón de reinicio
            g2d.setColor(new Color(50, 50, 50, 200));
            g2d.fillRoundRect(x - 15, y + 60, textWidth + 30, 40, 10, 10);
            g2d.setColor(new Color(100, 200, 100));
            g2d.drawRoundRect(x - 15, y + 60, textWidth + 30, 40, 10, 10);
            
            g2d.setColor(new Color(100, 255, 100));
            g2d.drawString(restartText, x, y + 88);
        }
    }
    
    /**
     * Renderiza advertencia de sin combustible.
     */
    private void renderNoFuelWarning(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String warningText = "¡SIN COMBUSTIBLE!";
        int textWidth = g2d.getFontMetrics().stringWidth(warningText);
        int x = (screenWidth - textWidth) / 2;
        int y = screenHeight - 100;
        
        // Fondo para mejor legibilidad
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(x - 10, y - 25, textWidth + 20, 35, 10, 10);
        
        // Texto parpadeante (usando el tiempo del sistema)
        if ((System.currentTimeMillis() / 500) % 2 == 0) {
            g2d.setColor(Color.RED);
            g2d.drawString(warningText, x, y);
        }
    }
    
    /**
     * Obtiene el color de la barra de salud según el porcentaje.
     */
    private Color getHealthColor(float percent) {
        if (percent > 0.5f) {
            return HEALTH_BAR_FULL;
        } else if (percent > 0.25f) {
            return HEALTH_BAR_MEDIUM;
        }
        return HEALTH_BAR_LOW;
    }
    
    /**
     * Obtiene el color de la barra de combustible según el porcentaje.
     */
    private Color getFuelColor(float percent) {
        if (percent > 0.5f) {
            return FUEL_BAR_FULL;
        } else if (percent > 0.2f) {
            return FUEL_BAR_MEDIUM;
        }
        return FUEL_BAR_LOW;
    }
    
    /**
     * Dibuja texto con sombra.
     */
    private void drawTextWithShadow(Graphics2D g2d, String text, int x, int y, Font font) {
        g2d.setFont(font);
        g2d.setColor(TEXT_SHADOW);
        g2d.drawString(text, x + 1, y + 1);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(text, x, y);
    }
    
    /**
     * Dibuja texto centrado en un área.
     */
    private void drawCenteredText(Graphics2D g2d, String text, int x, int y, int width, int height, Font font) {
        g2d.setFont(font);
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int textHeight = g2d.getFontMetrics().getAscent();
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height + textHeight) / 2 - 2;
        
        g2d.setColor(TEXT_SHADOW);
        g2d.drawString(text, textX + 1, textY + 1);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(text, textX, textY);
    }
}
