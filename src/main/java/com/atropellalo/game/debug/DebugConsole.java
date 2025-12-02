package com.atropellalo.game.debug;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Consola de comandos para debugging en tiempo real.
 * Permite ejecutar comandos para modificar el estado del juego.
 */
public class DebugConsole {
    
    private static final int MAX_HISTORY = 100;
    private static final Font CONSOLE_FONT = new Font("Monospaced", Font.PLAIN, 12);
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 220);
    private static final Color TEXT_COLOR = new Color(0, 255, 0);
    private static final Color INPUT_COLOR = new Color(255, 255, 255);
    private static final Color ERROR_COLOR = new Color(255, 100, 100);
    private static final int PADDING = 10;
    private static final int LINE_HEIGHT = 16;
    private static final int MAX_VISIBLE_LINES = 20;
    
    private boolean visible;
    private final StringBuilder currentInput;
    private final List<String> history;
    private final List<ConsoleMessage> messages;
    private final Map<String, DebugCommand> commands;
    private final Map<String, String> commandDescriptions;
    private int historyIndex;
    
    public DebugConsole() {
        this.visible = false;
        this.currentInput = new StringBuilder();
        this.history = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.commands = new HashMap<>();
        this.commandDescriptions = new HashMap<>();
        this.historyIndex = -1;
        
        registerDefaultCommands();
    }
    
    /**
     * Registra los comandos por defecto.
     */
    private void registerDefaultCommands() {
        // Comando help
        registerCommand("help", "Lista todos los comandos disponibles", args -> {
            addMessage("Available commands:", MessageType.INFO);
            commands.keySet().stream().sorted().forEach(cmd -> {
                String desc = commandDescriptions.get(cmd);
                addMessage("  " + cmd + " - " + desc, MessageType.INFO);
            });
        });
        
        // Comando clear
        registerCommand("clear", "Limpia la consola", args -> messages.clear());
        
        // Comandos de debug flags
        registerCommand("fps", "Alterna visualización de FPS", args -> {
            DebugConfig.setShowFPS(!DebugConfig.isShowFPS());
            addMessage("FPS display: " + DebugConfig.isShowFPS(), MessageType.INFO);
        });
        
        registerCommand("memory", "Alterna visualización de memoria", args -> {
            DebugConfig.setShowMemoryUsage(!DebugConfig.isShowMemoryUsage());
            addMessage("Memory display: " + DebugConfig.isShowMemoryUsage(), MessageType.INFO);
        });
        
        registerCommand("entities", "Alterna visualización de entidades", args -> {
            DebugConfig.setShowEntityCount(!DebugConfig.isShowEntityCount());
            addMessage("Entity count display: " + DebugConfig.isShowEntityCount(), MessageType.INFO);
        });
        
        registerCommand("player", "Alterna estadísticas del jugador", args -> {
            DebugConfig.setShowPlayerStats(!DebugConfig.isShowPlayerStats());
            addMessage("Player stats display: " + DebugConfig.isShowPlayerStats(), MessageType.INFO);
        });
        
        registerCommand("collision", "Alterna cajas de colisión", args -> {
            DebugConfig.setShowCollisionBoxes(!DebugConfig.isShowCollisionBoxes());
            addMessage("Collision boxes: " + DebugConfig.isShowCollisionBoxes(), MessageType.INFO);
        });
        
        registerCommand("grid", "Alterna grilla de posicionamiento", args -> {
            DebugConfig.setShowGridOverlay(!DebugConfig.isShowGridOverlay());
            addMessage("Grid overlay: " + DebugConfig.isShowGridOverlay(), MessageType.INFO);
        });
        
        registerCommand("all", "Activa todas las opciones de debug", args -> {
            DebugConfig.enableAll();
            addMessage("All debug options enabled", MessageType.INFO);
        });
        
        registerCommand("none", "Desactiva todas las opciones de debug", args -> {
            DebugConfig.disableAll();
            addMessage("All debug options disabled", MessageType.INFO);
        });
        
        registerCommand("gc", "Fuerza garbage collection", args -> {
            System.gc();
            addMessage("Garbage collection requested", MessageType.INFO);
        });
    }
    
    /**
     * Registra un comando personalizado.
     */
    public void registerCommand(String name, DebugCommand command) {
        registerCommand(name, "No description", command);
    }
    
    /**
     * Registra un comando personalizado con descripción.
     */
    public void registerCommand(String name, String description, DebugCommand command) {
        commands.put(name.toLowerCase(), command);
        commandDescriptions.put(name.toLowerCase(), description);
    }
    
    /**
     * Alterna la visibilidad de la consola.
     */
    public void toggle() {
        visible = !visible;
        if (!visible) {
            currentInput.setLength(0);
            historyIndex = -1;
        }
    }
    
    /**
     * Muestra la consola.
     */
    public void show() {
        visible = true;
    }
    
    /**
     * Oculta la consola.
     */
    public void hide() {
        visible = false;
        currentInput.setLength(0);
        historyIndex = -1;
    }
    
    /**
     * Procesa una tecla presionada.
     */
    public boolean handleKeyPress(KeyEvent e) {
        if (!visible) {
            return false;
        }
        
        int keyCode = e.getKeyCode();
        
        switch (keyCode) {
            case KeyEvent.VK_ENTER:
                executeCurrentInput();
                return true;
                
            case KeyEvent.VK_BACK_SPACE:
                if (currentInput.length() > 0) {
                    currentInput.deleteCharAt(currentInput.length() - 1);
                }
                return true;
                
            case KeyEvent.VK_UP:
                navigateHistory(-1);
                return true;
                
            case KeyEvent.VK_DOWN:
                navigateHistory(1);
                return true;
                
            case KeyEvent.VK_ESCAPE:
                hide();
                return true;
                
            default:
                char keyChar = e.getKeyChar();
                if (Character.isDefined(keyChar) && !Character.isISOControl(keyChar)) {
                    currentInput.append(keyChar);
                    return true;
                }
        }
        
        return false;
    }
    
    /**
     * Ejecuta el comando actual.
     */
    private void executeCurrentInput() {
        String input = currentInput.toString().trim();
        
        if (input.isEmpty()) {
            return;
        }
        
        // Agregar al historial
        history.addFirst(input);
        if (history.size() > MAX_HISTORY) {
            history.removeLast();
        }
        
        // Mostrar comando en la consola
        addMessage("> " + input, MessageType.COMMAND);
        
        // Parsear y ejecutar
        String[] parts = input.split("\\s+");
        String commandName = parts[0].toLowerCase();
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        
        DebugCommand command = commands.get(commandName);
        if (command != null) {
            try {
                command.execute(args);
            } catch (Exception ex) {
                addMessage("Error: " + ex.getMessage(), MessageType.ERROR);
            }
        } else {
            addMessage("Unknown command: " + commandName, MessageType.ERROR);
            addMessage("Type 'help' for available commands", MessageType.INFO);
        }
        
        // Limpiar input
        currentInput.setLength(0);
        historyIndex = -1;
    }
    
    /**
     * Navega por el historial de comandos.
     */
    private void navigateHistory(int direction) {
        if (history.isEmpty()) {
            return;
        }
        
        historyIndex += direction;
        historyIndex = Math.max(-1, Math.min(history.size() - 1, historyIndex));
        
        if (historyIndex >= 0) {
            currentInput.setLength(0);
            currentInput.append(history.get(historyIndex));
        } else {
            currentInput.setLength(0);
        }
    }
    
    /**
     * Agrega un mensaje a la consola.
     */
    public void addMessage(String message, MessageType type) {
        messages.addFirst(new ConsoleMessage(message, type));
        
        // Limitar tamaño del historial de mensajes
        if (messages.size() > MAX_HISTORY) {
            messages.removeLast();
        }
    }
    
    /**
     * Renderiza la consola.
     */
    public void render(Graphics2D g2d, int screenWidth, int screenHeight) {
        if (!visible) {
            return;
        }
        
        // Guardar estado
        Composite originalComposite = g2d.getComposite();
        Font originalFont = g2d.getFont();
        
        int consoleHeight = (MAX_VISIBLE_LINES + 2) * LINE_HEIGHT + PADDING * 2;
        int consoleY = screenHeight - consoleHeight;
        
        // Fondo
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, consoleY, screenWidth, consoleHeight);
        
        // Borde
        g2d.setComposite(originalComposite);
        g2d.setColor(TEXT_COLOR);
        g2d.drawRect(0, consoleY, screenWidth - 1, consoleHeight - 1);
        
        // Font
        g2d.setFont(CONSOLE_FONT);
        
        // Input actual
        int textY = screenHeight - PADDING;
        g2d.setColor(INPUT_COLOR);
        String inputText = "> " + currentInput.toString() + "_";
        g2d.drawString(inputText, PADDING, textY);
        
        // Mensajes históricos
        textY -= LINE_HEIGHT * 2;
        int visibleMessages = Math.min(MAX_VISIBLE_LINES, messages.size());
        
        for (int i = 0; i < visibleMessages; i++) {
            ConsoleMessage msg = messages.get(i);
            g2d.setColor(msg.getColor());
            g2d.drawString(msg.getText(), PADDING, textY);
            textY -= LINE_HEIGHT;
        }
        
        // Restaurar estado
        g2d.setFont(originalFont);
        g2d.setComposite(originalComposite);
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Interfaz para comandos de debug.
     */
    @FunctionalInterface
    public interface DebugCommand {
        void execute(String[] args) throws Exception;
    }
    
    /**
     * Tipo de mensaje de consola.
     */
    public enum MessageType {
        INFO(TEXT_COLOR),
        COMMAND(INPUT_COLOR),
        ERROR(ERROR_COLOR),
        WARNING(new Color(255, 255, 0));
        
        private final Color color;
        
        MessageType(Color color) {
            this.color = color;
        }
        
        public Color getColor() {
            return color;
        }
    }
    
    /**
     * Mensaje de consola.
     */
    private static class ConsoleMessage {
        private final String text;
        private final MessageType type;
        
        public ConsoleMessage(String text, MessageType type) {
            this.text = text;
            this.type = type;
        }
        
        public String getText() {
            return text;
        }
        
        public Color getColor() {
            return type.getColor();
        }
    }
}
