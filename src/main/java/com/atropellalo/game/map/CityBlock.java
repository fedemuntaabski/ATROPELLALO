package com.atropellalo.game.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representa una manzana de la ciudad que contiene múltiples edificios.
 * Las manzanas están separadas por calles.
 */
public class CityBlock {
    
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final List<Building> buildings;
    private final Random random;
    
    // Espaciado mínimo entre edificios
    private static final float BUILDING_SPACING = 8f;
    // Margen desde el borde de la manzana
    private static final float BLOCK_MARGIN = 10f;
    
    /**
     * Constructor de manzana urbana.
     * @param x Posición X de la manzana
     * @param y Posición Y de la manzana
     * @param width Ancho de la manzana
     * @param height Alto de la manzana
     * @param seed Semilla para generación procedural
     */
    public CityBlock(float x, float y, float width, float height, long seed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.buildings = new ArrayList<>();
        this.random = new Random(seed);
        
        generateBuildings();
    }
    
    /**
     * Genera edificios dentro de la manzana de forma procedural.
     */
    private void generateBuildings() {
        // Área disponible para edificios
        float availableWidth = width - (2 * BLOCK_MARGIN);
        float availableHeight = height - (2 * BLOCK_MARGIN);
        
        if (availableWidth < 40 || availableHeight < 40) {
            // Manzana muy pequeña, un solo edificio
            Building.BuildingType type = getRandomBuildingType();
            buildings.add(new Building(
                x + BLOCK_MARGIN,
                y + BLOCK_MARGIN,
                availableWidth,
                availableHeight,
                type
            ));
            return;
        }
        
        // Decidir layout de la manzana
        int layoutType = random.nextInt(4);
        
        switch (layoutType) {
            case 0:
                generateGridLayout(availableWidth, availableHeight);
                break;
            case 1:
                generateLShapeLayout(availableWidth, availableHeight);
                break;
            case 2:
                generateMixedLayout(availableWidth, availableHeight);
                break;
            default:
                generateSingleLargeBuilding(availableWidth, availableHeight);
                break;
        }
    }
    
    /**
     * Genera un layout de grilla con múltiples edificios.
     */
    private void generateGridLayout(float availableWidth, float availableHeight) {
        int cols = random.nextInt(2) + 2; // 2-3 columnas
        int rows = random.nextInt(2) + 2; // 2-3 filas
        
        float buildingWidth = (availableWidth - (cols - 1) * BUILDING_SPACING) / cols;
        float buildingHeight = (availableHeight - (rows - 1) * BUILDING_SPACING) / rows;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Ocasionalmente saltar un edificio para crear variedad
                if (random.nextFloat() < 0.1f) continue;
                
                float bx = x + BLOCK_MARGIN + col * (buildingWidth + BUILDING_SPACING);
                float by = y + BLOCK_MARGIN + row * (buildingHeight + BUILDING_SPACING);
                
                // Añadir algo de variación en el tamaño
                float varWidth = buildingWidth * (0.8f + random.nextFloat() * 0.2f);
                float varHeight = buildingHeight * (0.8f + random.nextFloat() * 0.2f);
                
                Building.BuildingType type = getRandomBuildingType();
                buildings.add(new Building(bx, by, varWidth, varHeight, type));
            }
        }
    }
    
    /**
     * Genera un layout en forma de L o U.
     */
    private void generateLShapeLayout(float availableWidth, float availableHeight) {
        // Edificio principal (parte vertical de la L)
        float mainWidth = availableWidth * 0.4f;
        float mainHeight = availableHeight * 0.9f;
        buildings.add(new Building(
            x + BLOCK_MARGIN,
            y + BLOCK_MARGIN,
            mainWidth,
            mainHeight,
            Building.BuildingType.OFFICE
        ));
        
        // Extensión (parte horizontal de la L)
        float extWidth = availableWidth * 0.5f;
        float extHeight = availableHeight * 0.35f;
        buildings.add(new Building(
            x + BLOCK_MARGIN + mainWidth + BUILDING_SPACING,
            y + BLOCK_MARGIN + mainHeight - extHeight,
            extWidth,
            extHeight,
            Building.BuildingType.COMMERCIAL
        ));
        
        // Edificio pequeño adicional
        if (random.nextBoolean()) {
            float smallWidth = availableWidth * 0.3f;
            float smallHeight = availableHeight * 0.3f;
            buildings.add(new Building(
                x + BLOCK_MARGIN + mainWidth + BUILDING_SPACING,
                y + BLOCK_MARGIN,
                smallWidth,
                smallHeight,
                Building.BuildingType.RESIDENTIAL
            ));
        }
    }
    
    /**
     * Genera un layout mixto con edificios de diferentes tamaños.
     */
    private void generateMixedLayout(float availableWidth, float availableHeight) {
        // Edificio grande (rascacielos o almacén)
        boolean isSkyscraper = random.nextBoolean();
        float largeWidth = availableWidth * (isSkyscraper ? 0.4f : 0.6f);
        float largeHeight = availableHeight * (isSkyscraper ? 0.5f : 0.4f);
        
        buildings.add(new Building(
            x + BLOCK_MARGIN,
            y + BLOCK_MARGIN,
            largeWidth,
            largeHeight,
            isSkyscraper ? Building.BuildingType.SKYSCRAPER : Building.BuildingType.WAREHOUSE
        ));
        
        // Edificios residenciales al lado
        float smallWidth = (availableWidth - largeWidth - BUILDING_SPACING) / 2;
        float smallHeight = (largeHeight - BUILDING_SPACING) / 2;
        
        if (smallWidth > 30) {
            buildings.add(new Building(
                x + BLOCK_MARGIN + largeWidth + BUILDING_SPACING,
                y + BLOCK_MARGIN,
                smallWidth,
                smallHeight,
                Building.BuildingType.RESIDENTIAL
            ));
            
            buildings.add(new Building(
                x + BLOCK_MARGIN + largeWidth + BUILDING_SPACING,
                y + BLOCK_MARGIN + smallHeight + BUILDING_SPACING,
                smallWidth,
                smallHeight,
                Building.BuildingType.RESIDENTIAL
            ));
        }
        
        // Fila inferior
        int numSmall = random.nextInt(2) + 2;
        float bottomWidth = (availableWidth - (numSmall - 1) * BUILDING_SPACING) / numSmall;
        float bottomHeight = availableHeight - largeHeight - BUILDING_SPACING;
        
        if (bottomHeight > 25) {
            for (int i = 0; i < numSmall; i++) {
                buildings.add(new Building(
                    x + BLOCK_MARGIN + i * (bottomWidth + BUILDING_SPACING),
                    y + BLOCK_MARGIN + largeHeight + BUILDING_SPACING,
                    bottomWidth,
                    bottomHeight,
                    getRandomBuildingType()
                ));
            }
        }
    }
    
    /**
     * Genera un único edificio grande que ocupa la mayor parte de la manzana.
     */
    private void generateSingleLargeBuilding(float availableWidth, float availableHeight) {
        Building.BuildingType type = random.nextBoolean() ? 
            Building.BuildingType.SKYSCRAPER : Building.BuildingType.OFFICE;
        
        buildings.add(new Building(
            x + BLOCK_MARGIN,
            y + BLOCK_MARGIN,
            availableWidth,
            availableHeight,
            type
        ));
    }
    
    /**
     * Obtiene un tipo de edificio aleatorio con probabilidades ponderadas.
     */
    private Building.BuildingType getRandomBuildingType() {
        int rand = random.nextInt(100);
        
        if (rand < 35) {
            return Building.BuildingType.RESIDENTIAL;
        } else if (rand < 55) {
            return Building.BuildingType.COMMERCIAL;
        } else if (rand < 75) {
            return Building.BuildingType.OFFICE;
        } else if (rand < 90) {
            return Building.BuildingType.WAREHOUSE;
        } else {
            return Building.BuildingType.SKYSCRAPER;
        }
    }
    
    /**
     * Verifica si un rectángulo colisiona con algún edificio de la manzana.
     * @param rx Coordenada X
     * @param ry Coordenada Y
     * @param rwidth Ancho
     * @param rheight Alto
     * @return true si hay colisión
     */
    public boolean checkCollision(float rx, float ry, float rwidth, float rheight) {
        for (Building building : buildings) {
            if (building.collidesWith(rx, ry, rwidth, rheight)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene el edificio con el que colisiona, si hay alguno.
     * @param rx Coordenada X
     * @param ry Coordenada Y
     * @param rwidth Ancho
     * @param rheight Alto
     * @return Edificio con colisión o null
     */
    public Building getCollidingBuilding(float rx, float ry, float rwidth, float rheight) {
        for (Building building : buildings) {
            if (building.collidesWith(rx, ry, rwidth, rheight)) {
                return building;
            }
        }
        return null;
    }
    
    /**
     * Renderiza la manzana (borde de acera) y sus edificios.
     * @param g2d Contexto gráfico
     */
    public void render(Graphics2D g2d) {
        // Dibujar acera/borde de la manzana
        g2d.setColor(new Color(169, 169, 169)); // Gris acera
        g2d.fillRect((int) x, (int) y, (int) width, (int) height);
        
        // Borde de la acera
        g2d.setColor(new Color(128, 128, 128));
        g2d.drawRect((int) x, (int) y, (int) width, (int) height);
        
        // Renderizar todos los edificios
        for (Building building : buildings) {
            building.render(g2d);
        }
    }
    
    // Getters
    
    public List<Building> getBuildings() {
        return buildings;
    }
    
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
}
