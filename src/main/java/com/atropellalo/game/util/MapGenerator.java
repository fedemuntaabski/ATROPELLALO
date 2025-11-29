package com.atropellalo.game.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Utilidad para generar una imagen de mapa temporal más grande.
 * Esta es una solución temporal hasta que se tenga una imagen real del mapa.
 */
public class MapGenerator {
    
    public static void main(String[] args) {
        int width = 2560;
        int height = 1440;
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Fondo base - tono verdoso oscuro
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(0, 0, width, height);
        
        // Agregar variación con diferentes tonos de verde
        Color[] greens = {
            new Color(46, 125, 50),
            new Color(56, 142, 60),
            new Color(27, 94, 32),
            new Color(51, 105, 30)
        };
        
        // Dibujar parches de diferentes tonos
        for (int i = 0; i < 200; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            int size = (int) (Math.random() * 150 + 50);
            
            g2d.setColor(greens[(int) (Math.random() * greens.length)]);
            g2d.fillOval(x, y, size, size);
        }
        
        // Agregar algunos círculos más oscuros para dar textura
        g2d.setColor(new Color(21, 71, 52, 80));
        for (int i = 0; i < 100; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            int size = (int) (Math.random() * 80 + 20);
            g2d.fillOval(x, y, size, size);
        }
        
        g2d.dispose();
        
        // Guardar la imagen
        try {
            File outputFile = new File("src/main/resources/images/map.jpg");
            outputFile.getParentFile().mkdirs();
            ImageIO.write(image, "jpg", outputFile);
            System.out.println("Mapa generado exitosamente: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error al guardar la imagen: " + e.getMessage());
        }
    }
}
