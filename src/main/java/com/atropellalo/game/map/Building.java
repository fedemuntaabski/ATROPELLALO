package com.atropellalo.game.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Representa un edificio en el mapa de la ciudad.
 * Los edificios son obstáculos sólidos con los que el jugador colisiona.
 */
public class Building {
    
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final Rectangle2D.Float bounds;
    private final BuildingType type;
    private final Color baseColor;
    private final Color roofColor;
    
    /**
     * Tipos de edificios para variedad visual.
     */
    public enum BuildingType {
        RESIDENTIAL,    // Edificio residencial (bajo)
        COMMERCIAL,     // Edificio comercial (medio)
        OFFICE,         // Edificio de oficinas (alto)
        WAREHOUSE,      // Almacén/depósito (ancho, bajo)
        SKYSCRAPER      // Rascacielos (muy alto)
    }
    
    /**
     * Constructor del edificio.
     * @param x Posición X
     * @param y Posición Y
     * @param width Ancho del edificio
     * @param height Alto del edificio
     * @param type Tipo de edificio
     */
    public Building(float x, float y, float width, float height, BuildingType type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.bounds = new Rectangle2D.Float(x, y, width, height);
        
        // Asignar colores según tipo
        switch (type) {
            case RESIDENTIAL:
                this.baseColor = new Color(139, 119, 101); // Marrón claro
                this.roofColor = new Color(101, 67, 33);   // Marrón oscuro
                break;
            case COMMERCIAL:
                this.baseColor = new Color(176, 196, 222); // Azul acero claro
                this.roofColor = new Color(70, 130, 180);  // Azul acero
                break;
            case OFFICE:
                this.baseColor = new Color(192, 192, 192); // Gris plata
                this.roofColor = new Color(105, 105, 105); // Gris oscuro
                break;
            case WAREHOUSE:
                this.baseColor = new Color(160, 82, 45);   // Siena
                this.roofColor = new Color(139, 69, 19);   // Marrón silla
                break;
            case SKYSCRAPER:
            default:
                this.baseColor = new Color(70, 130, 180);  // Azul acero
                this.roofColor = new Color(25, 25, 112);   // Azul medianoche
                break;
        }
    }
    
    /**
     * Verifica si un punto está dentro del edificio.
     * @param px Coordenada X del punto
     * @param py Coordenada Y del punto
     * @return true si el punto está dentro del edificio
     */
    public boolean contains(float px, float py) {
        return bounds.contains(px, py);
    }
    
    /**
     * Verifica si un rectángulo colisiona con el edificio.
     * @param rx Coordenada X del rectángulo
     * @param ry Coordenada Y del rectángulo
     * @param rwidth Ancho del rectángulo
     * @param rheight Alto del rectángulo
     * @return true si hay colisión
     */
    public boolean collidesWith(float rx, float ry, float rwidth, float rheight) {
        return bounds.intersects(rx, ry, rwidth, rheight);
    }
    
    /**
     * Verifica colisión con otro rectángulo.
     * @param other Rectángulo a verificar
     * @return true si hay colisión
     */
    public boolean collidesWith(Rectangle2D other) {
        return bounds.intersects(other);
    }
    
    /**
     * Renderiza el edificio en el mapa.
     * Vista cenital (desde arriba) para estilo survivor.
     * @param g2d Contexto gráfico
     */
    public void render(Graphics2D g2d) {
        // Base del edificio (techo visible desde arriba)
        g2d.setColor(baseColor);
        g2d.fillRect((int) x, (int) y, (int) width, (int) height);
        
        // Borde del edificio (sombra/contorno)
        g2d.setColor(baseColor.darker().darker());
        g2d.drawRect((int) x, (int) y, (int) width, (int) height);
        
        // Detalles del techo según tipo
        renderRoofDetails(g2d);
    }
    
    /**
     * Renderiza detalles del techo según el tipo de edificio.
     */
    private void renderRoofDetails(Graphics2D g2d) {
        int ix = (int) x;
        int iy = (int) y;
        int iw = (int) width;
        int ih = (int) height;
        
        g2d.setColor(roofColor);
        
        switch (type) {
            case RESIDENTIAL:
                // Techo a dos aguas (línea central)
                g2d.drawLine(ix + iw / 2, iy + 2, ix + iw / 2, iy + ih - 2);
                // Pequeños detalles de tejas
                for (int row = 0; row < ih; row += 8) {
                    g2d.drawLine(ix + 2, iy + row, ix + iw - 2, iy + row);
                }
                break;
                
            case COMMERCIAL:
                // Aire acondicionado en el techo
                int acSize = Math.min(iw, ih) / 4;
                g2d.fillRect(ix + iw / 2 - acSize / 2, iy + ih / 2 - acSize / 2, acSize, acSize);
                // Ventilación
                g2d.setColor(Color.DARK_GRAY);
                g2d.fillOval(ix + iw / 4 - 4, iy + ih / 4 - 4, 8, 8);
                break;
                
            case OFFICE:
                // Múltiples unidades de AC
                g2d.fillRect(ix + 5, iy + 5, 10, 10);
                g2d.fillRect(ix + iw - 15, iy + 5, 10, 10);
                g2d.fillRect(ix + 5, iy + ih - 15, 10, 10);
                g2d.fillRect(ix + iw - 15, iy + ih - 15, 10, 10);
                // Helipuerto en edificios grandes
                if (iw > 80 && ih > 80) {
                    g2d.setColor(Color.WHITE);
                    g2d.drawOval(ix + iw / 2 - 15, iy + ih / 2 - 15, 30, 30);
                    g2d.drawString("H", ix + iw / 2 - 4, iy + ih / 2 + 5);
                }
                break;
                
            case WAREHOUSE:
                // Líneas de techo industrial
                for (int i = 0; i < iw; i += 15) {
                    g2d.drawLine(ix + i, iy, ix + i, iy + ih);
                }
                // Puertas de carga (indicador)
                g2d.setColor(Color.DARK_GRAY);
                g2d.fillRect(ix + iw / 2 - 10, iy + ih - 5, 20, 5);
                break;
                
            case SKYSCRAPER:
                // Antena/torre en el centro
                g2d.fillRect(ix + iw / 2 - 3, iy + ih / 2 - 15, 6, 30);
                // Estructura central del techo
                g2d.drawRect(ix + iw / 4, iy + ih / 4, iw / 2, ih / 2);
                // Luces de advertencia
                g2d.setColor(Color.RED);
                g2d.fillOval(ix + iw / 2 - 2, iy + ih / 2 - 17, 4, 4);
                break;
        }
    }
    
    // Getters
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public Rectangle2D.Float getBounds() {
        return bounds;
    }
    
    public BuildingType getType() {
        return type;
    }
    
    public Color getBaseColor() {
        return baseColor;
    }
}
