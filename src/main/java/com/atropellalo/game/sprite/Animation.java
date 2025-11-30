package com.atropellalo.game.sprite;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que maneja animaciones frame por frame.
 * Soporta múltiples estados de animación (idle, move, death).
 */
public class Animation {
    
    private final List<BufferedImage> frames;
    private int currentFrame;
    private float frameTimer;
    private float frameDuration;
    private boolean looping;
    private boolean finished;
    
    /**
     * Constructor de animación.
     * @param frameDuration Duración de cada frame en segundos
     * @param looping Si la animación debe repetirse
     */
    public Animation(float frameDuration, boolean looping) {
        this.frames = new ArrayList<>();
        this.currentFrame = 0;
        this.frameTimer = 0;
        this.frameDuration = frameDuration;
        this.looping = looping;
        this.finished = false;
    }
    
    /**
     * Constructor con frame único (para sprites estáticos).
     * @param singleFrame Imagen única
     */
    public Animation(BufferedImage singleFrame) {
        this(0.1f, true);
        this.frames.add(singleFrame);
    }
    
    /**
     * Agrega un frame a la animación.
     * @param frame Imagen del frame
     */
    public void addFrame(BufferedImage frame) {
        frames.add(frame);
    }
    
    /**
     * Agrega múltiples frames a la animación.
     * @param newFrames Lista de frames
     */
    public void addFrames(List<BufferedImage> newFrames) {
        frames.addAll(newFrames);
    }
    
    /**
     * Actualiza la animación.
     * @param deltaTime Tiempo desde el último frame
     */
    public void update(float deltaTime) {
        if (frames.isEmpty() || finished) {
            return;
        }
        
        frameTimer += deltaTime;
        
        if (frameTimer >= frameDuration) {
            frameTimer = 0;
            currentFrame++;
            
            if (currentFrame >= frames.size()) {
                if (looping) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.size() - 1;
                    finished = true;
                }
            }
        }
    }
    
    /**
     * Obtiene el frame actual de la animación.
     * @return Imagen del frame actual
     */
    public BufferedImage getCurrentFrame() {
        if (frames.isEmpty()) {
            return null;
        }
        return frames.get(currentFrame);
    }
    
    /**
     * Reinicia la animación al primer frame.
     */
    public void reset() {
        currentFrame = 0;
        frameTimer = 0;
        finished = false;
    }
    
    /**
     * Verifica si la animación ha terminado (solo para no-looping).
     * @return true si terminó
     */
    public boolean isFinished() {
        return finished;
    }
    
    /**
     * Obtiene el número de frames.
     * @return Cantidad de frames
     */
    public int getFrameCount() {
        return frames.size();
    }
    
    /**
     * Establece la duración de cada frame.
     * @param duration Duración en segundos
     */
    public void setFrameDuration(float duration) {
        this.frameDuration = duration;
    }
    
    /**
     * Renderiza el frame actual centrado en una posición.
     * @param g2d Contexto gráfico
     * @param x Posición X central
     * @param y Posición Y central
     */
    public void render(Graphics2D g2d, float x, float y) {
        BufferedImage frame = getCurrentFrame();
        if (frame != null) {
            int drawX = (int) (x - frame.getWidth() / 2f);
            int drawY = (int) (y - frame.getHeight() / 2f);
            g2d.drawImage(frame, drawX, drawY, null);
        }
    }
    
    /**
     * Renderiza el frame actual centrado y rotado.
     * @param g2d Contexto gráfico
     * @param x Posición X central
     * @param y Posición Y central
     * @param rotation Ángulo de rotación en radianes
     */
    public void render(Graphics2D g2d, float x, float y, double rotation) {
        BufferedImage frame = getCurrentFrame();
        if (frame != null) {
            Graphics2D g2dCopy = (Graphics2D) g2d.create();
            g2dCopy.translate(x, y);
            g2dCopy.rotate(rotation);
            g2dCopy.drawImage(frame, -frame.getWidth() / 2, -frame.getHeight() / 2, null);
            g2dCopy.dispose();
        }
    }
    
    /**
     * Renderiza el frame actual con escala.
     * @param g2d Contexto gráfico
     * @param x Posición X central
     * @param y Posición Y central
     * @param scale Factor de escala
     */
    public void renderScaled(Graphics2D g2d, float x, float y, float scale) {
        BufferedImage frame = getCurrentFrame();
        if (frame != null) {
            int scaledWidth = (int) (frame.getWidth() * scale);
            int scaledHeight = (int) (frame.getHeight() * scale);
            int drawX = (int) (x - scaledWidth / 2f);
            int drawY = (int) (y - scaledHeight / 2f);
            g2d.drawImage(frame, drawX, drawY, scaledWidth, scaledHeight, null);
        }
    }
    
    /**
     * Renderiza el frame actual con escala y rotación.
     * @param g2d Contexto gráfico
     * @param x Posición X central
     * @param y Posición Y central
     * @param rotation Ángulo de rotación en radianes
     * @param scale Factor de escala
     */
    public void renderScaled(Graphics2D g2d, float x, float y, double rotation, float scale) {
        BufferedImage frame = getCurrentFrame();
        if (frame != null) {
            int scaledWidth = (int) (frame.getWidth() * scale);
            int scaledHeight = (int) (frame.getHeight() * scale);
            
            Graphics2D g2dCopy = (Graphics2D) g2d.create();
            g2dCopy.translate(x, y);
            g2dCopy.rotate(rotation);
            g2dCopy.drawImage(frame, -scaledWidth / 2, -scaledHeight / 2, scaledWidth, scaledHeight, null);
            g2dCopy.dispose();
        }
    }
}
