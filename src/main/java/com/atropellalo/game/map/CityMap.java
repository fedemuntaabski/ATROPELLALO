package com.atropellalo.game.map;

import com.atropellalo.game.config.GameConfig;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Representa el mapa de la ciudad con calles y manzanas.
 * Gestiona la generación procedural del layout urbano y las colisiones.
 */
public class CityMap {
    
    private static final Logger LOGGER = Logger.getLogger(CityMap.class.getName());
    
    // Configuración del mapa urbano
    private static final float STREET_WIDTH = 120f;          // Ancho de calles principales (más anchas)
    private static final float ALLEY_WIDTH = 80f;            // Ancho de callejones (más anchos)
    private static final int MIN_BLOCK_SIZE = 250;           // Tamaño mínimo de manzana
    private static final int MAX_BLOCK_SIZE = 500;           // Tamaño máximo de manzana
    private static final float BLOCK_SKIP_CHANCE = 0.55f;    // 55% de probabilidad de omitir un bloque
    
    // Colores de la ciudad
    private static final Color ASPHALT_COLOR = new Color(50, 50, 55);
    private static final Color ASPHALT_DARK = new Color(35, 35, 40);
    private static final Color LANE_MARKING = new Color(255, 255, 200);
    private static final Color CROSSWALK_COLOR = new Color(245, 245, 245);
    private static final Color CURB_COLOR = new Color(180, 180, 180);
    
    private final int worldWidth;
    private final int worldHeight;
    private final List<CityBlock> blocks;
    private final List<Street> streets;
    private final Random random;
    private final long seed;
    
    // Zona de spawn segura (sin edificios)
    private Rectangle2D.Float spawnZone;
    
    /**
     * Constructor del mapa de ciudad.
     * @param seed Semilla para generación procedural (permite regenerar el mismo mapa)
     */
    public CityMap(long seed) {
        this.worldWidth = GameConfig.WORLD_WIDTH;
        this.worldHeight = GameConfig.WORLD_HEIGHT;
        this.blocks = new ArrayList<>();
        this.streets = new ArrayList<>();
        this.random = new Random(seed);
        this.seed = seed;
        
        generateCity();
        LOGGER.info("Mapa de ciudad generado con semilla: " + seed + " - " + 
                    blocks.size() + " manzanas, " + streets.size() + " calles");
    }
    
    /**
     * Constructor con semilla aleatoria.
     */
    public CityMap() {
        this(System.currentTimeMillis());
    }
    
    /**
     * Genera la estructura de la ciudad de forma procedural.
     */
    private void generateCity() {
        // Definir zona de spawn segura en el centro (más grande para más espacio)
        float spawnSize = 350f;
        spawnZone = new Rectangle2D.Float(
            worldWidth / 2f - spawnSize / 2,
            worldHeight / 2f - spawnSize / 2,
            spawnSize,
            spawnSize
        );
        
        // Generar calles principales (grid)
        generateStreetGrid();
        
        // Generar manzanas entre las calles
        generateBlocks();
        
        // Generar grid de navegación para A* pathfinding
        generateNavigationGrid();
    }
    
    /**
     * Genera la red de calles principal.
     */
    private void generateStreetGrid() {
        // Calles horizontales
        float currentY = STREET_WIDTH;
        while (currentY < worldHeight - STREET_WIDTH) {
            float streetWidth = random.nextBoolean() ? STREET_WIDTH : ALLEY_WIDTH;
            streets.add(new Street(0, currentY, worldWidth, streetWidth, true));
            
            // Siguiente calle a una distancia variable
            currentY += streetWidth + MIN_BLOCK_SIZE + random.nextInt(MAX_BLOCK_SIZE - MIN_BLOCK_SIZE);
        }
        
        // Calles verticales
        float currentX = STREET_WIDTH;
        while (currentX < worldWidth - STREET_WIDTH) {
            float streetWidth = random.nextBoolean() ? STREET_WIDTH : ALLEY_WIDTH;
            streets.add(new Street(currentX, 0, streetWidth, worldHeight, false));
            
            // Siguiente calle a una distancia variable
            currentX += streetWidth + MIN_BLOCK_SIZE + random.nextInt(MAX_BLOCK_SIZE - MIN_BLOCK_SIZE);
        }
    }
    
    /**
     * Genera las manzanas de la ciudad basándose en la red de calles.
     */
    private void generateBlocks() {
        // Ordenar calles por posición
        List<Float> horizontalStreets = new ArrayList<>();
        List<Float> verticalStreets = new ArrayList<>();
        
        horizontalStreets.add(0f); // Borde superior
        verticalStreets.add(0f);   // Borde izquierdo
        
        for (Street street : streets) {
            if (street.isHorizontal()) {
                horizontalStreets.add(street.getY());
                horizontalStreets.add(street.getY() + street.getWidth());
            } else {
                verticalStreets.add(street.getX());
                verticalStreets.add(street.getX() + street.getHeight());
            }
        }
        
        horizontalStreets.add((float) worldHeight); // Borde inferior
        verticalStreets.add((float) worldWidth);    // Borde derecho
        
        // Ordenar las listas
        horizontalStreets.sort(Float::compare);
        verticalStreets.sort(Float::compare);
        
        // Crear manzanas en cada espacio entre calles
        long blockSeed = seed;
        for (int i = 0; i < horizontalStreets.size() - 1; i += 2) {
            for (int j = 0; j < verticalStreets.size() - 1; j += 2) {
                float blockX = verticalStreets.get(j);
                float blockY = horizontalStreets.get(i);
                float blockWidth = verticalStreets.get(j + 1) - blockX;
                float blockHeight = horizontalStreets.get(i + 1) - blockY;
                
                // Solo crear manzana si es lo suficientemente grande
                if (blockWidth >= MIN_BLOCK_SIZE * 0.7f && blockHeight >= MIN_BLOCK_SIZE * 0.7f) {
                    // No crear manzana en la zona de spawn
                    if (!intersectsSpawnZone(blockX, blockY, blockWidth, blockHeight)) {
                        // Probabilidad de omitir bloque para reducir densidad
                        if (random.nextFloat() >= BLOCK_SKIP_CHANCE) {
                            blocks.add(new CityBlock(blockX, blockY, blockWidth, blockHeight, blockSeed++));
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Verifica si un área intersecta con la zona de spawn.
     */
    private boolean intersectsSpawnZone(float x, float y, float width, float height) {
        // Ampliar zona de spawn para dar más espacio
        float margin = STREET_WIDTH;
        return x < spawnZone.x + spawnZone.width + margin &&
               x + width > spawnZone.x - margin &&
               y < spawnZone.y + spawnZone.height + margin &&
               y + height > spawnZone.y - margin;
    }
    
    /**
     * Verifica si una posición colisiona con algún edificio.
     * @param x Coordenada X
     * @param y Coordenada Y
     * @param width Ancho del rectángulo
     * @param height Alto del rectángulo
     * @return true si hay colisión
     */
    public boolean checkCollision(float x, float y, float width, float height) {
        // Verificar colisión con cada manzana
        for (CityBlock block : blocks) {
            if (block.checkCollision(x, y, width, height)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene la posición corregida después de resolver una colisión.
     * Usa un enfoque de separación para sacar al jugador del edificio.
     * @param oldX Posición X anterior
     * @param oldY Posición Y anterior
     * @param newX Posición X nueva (con colisión)
     * @param newY Posición Y nueva (con colisión)
     * @param width Ancho del objeto
     * @param height Alto del objeto
     * @return Array con [x, y] corregidos
     */
    public float[] resolveCollision(float oldX, float oldY, float newX, float newY, 
                                     float width, float height) {
        float[] result = new float[]{newX, newY};
        
        // Intentar movimiento solo en X
        if (!checkCollision(newX, oldY, width, height)) {
            result[1] = oldY;
            return result;
        }
        
        // Intentar movimiento solo en Y
        if (!checkCollision(oldX, newY, width, height)) {
            result[0] = oldX;
            return result;
        }
        
        // Si ambos fallan, quedarse en la posición anterior
        result[0] = oldX;
        result[1] = oldY;
        return result;
    }
    
    /**
     * Verifica si una posición está dentro de la zona de spawn segura.
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return true si está en la zona de spawn
     */
    public boolean isInSpawnZone(float x, float y) {
        return spawnZone.contains(x, y);
    }
    
    /**
     * Obtiene una posición válida para spawn de enemigos (en las calles).
     * @param minDistanceFromPlayer Distancia mínima desde el jugador
     * @param playerX Posición X del jugador
     * @param playerY Posición Y del jugador
     * @return Array con [x, y] de la posición de spawn
     */
    public float[] getValidEnemySpawnPosition(float minDistanceFromPlayer, 
                                               float playerX, float playerY) {
        int maxAttempts = 50;
        
        for (int i = 0; i < maxAttempts; i++) {
            // Generar posición aleatoria
            float x = random.nextFloat() * (worldWidth - 50) + 25;
            float y = random.nextFloat() * (worldHeight - 50) + 25;
            
            // Verificar distancia del jugador
            float dx = x - playerX;
            float dy = y - playerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (distance < minDistanceFromPlayer) {
                continue;
            }
            
            // Verificar que no esté en un edificio
            if (!checkCollision(x - 15, y - 15, 30, 30)) {
                return new float[]{x, y};
            }
        }
        
        // Fallback: posición en el borde del mapa
        return new float[]{50, 50};
    }
    
    /**
     * Renderiza el fondo de la ciudad (calles).
     * @param g2d Contexto gráfico
     */
    public void renderBackground(Graphics2D g2d) {
        // Fondo de asfalto base
        g2d.setColor(ASPHALT_COLOR);
        g2d.fillRect(0, 0, worldWidth, worldHeight);
        
        // Renderizar calles con detalles
        for (Street street : streets) {
            renderStreet(g2d, street);
        }
        
        // Renderizar intersecciones
        renderIntersections(g2d);
    }
    
    /**
     * Renderiza una calle individual.
     */
    private void renderStreet(Graphics2D g2d, Street street) {
        int x = (int) street.getX();
        int y = (int) street.getY();
        int w = (int) (street.isHorizontal() ? street.getLength() : street.getWidth());
        int h = (int) (street.isHorizontal() ? street.getWidth() : street.getLength());
        
        // Asfalto de la calle (un poco más oscuro para contraste)
        g2d.setColor(ASPHALT_DARK);
        g2d.fillRect(x, y, w, h);
        
        // Bordillos
        g2d.setColor(CURB_COLOR);
        if (street.isHorizontal()) {
            g2d.fillRect(x, y, w, 3);
            g2d.fillRect(x, y + h - 3, w, 3);
        } else {
            g2d.fillRect(x, y, 3, h);
            g2d.fillRect(x + w - 3, y, 3, h);
        }
        
        // Líneas de carril (solo en calles principales)
        if (street.getWidth() >= STREET_WIDTH) {
            renderLaneMarkings(g2d, street);
        }
    }
    
    /**
     * Renderiza las marcas de carril de una calle.
     */
    private void renderLaneMarkings(Graphics2D g2d, Street street) {
        Stroke oldStroke = g2d.getStroke();
        g2d.setColor(LANE_MARKING);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
                                       10.0f, new float[]{20.0f, 20.0f}, 0.0f));
        
        if (street.isHorizontal()) {
            int centerY = (int) (street.getY() + street.getWidth() / 2);
            g2d.drawLine(0, centerY, worldWidth, centerY);
        } else {
            int centerX = (int) (street.getX() + street.getWidth() / 2);
            g2d.drawLine(centerX, 0, centerX, worldHeight);
        }
        
        g2d.setStroke(oldStroke);
    }
    
    /**
     * Renderiza detalles en las intersecciones.
     */
    private void renderIntersections(Graphics2D g2d) {
        // Encontrar intersecciones entre calles horizontales y verticales
        for (Street hStreet : streets) {
            if (!hStreet.isHorizontal()) continue;
            
            for (Street vStreet : streets) {
                if (vStreet.isHorizontal()) continue;
                
                // Dibujar paso de peatones
                float ix = vStreet.getX();
                float iy = hStreet.getY();
                float iw = vStreet.getWidth();
                float ih = hStreet.getWidth();
                
                renderCrosswalk(g2d, ix, iy, iw, ih);
            }
        }
    }
    
    /**
     * Renderiza un paso de peatones en una intersección.
     */
    private void renderCrosswalk(Graphics2D g2d, float x, float y, float width, float height) {
        g2d.setColor(CROSSWALK_COLOR);
        
        // Líneas del paso de peatones (horizontal)
        int stripeWidth = 8;
        int stripeGap = 8;
        
        for (float cx = x + 5; cx < x + width - 5; cx += stripeWidth + stripeGap) {
            g2d.fillRect((int) cx, (int) (y + 5), stripeWidth, (int) (height - 10));
        }
    }
    
    /**
     * Renderiza las manzanas y edificios.
     * @param g2d Contexto gráfico
     */
    public void renderBuildings(Graphics2D g2d) {
        for (CityBlock block : blocks) {
            block.render(g2d);
        }
        
        // Marcar zona de spawn (debug - comentar en producción)
        // g2d.setColor(new Color(0, 255, 0, 50));
        // g2d.fillRect((int)spawnZone.x, (int)spawnZone.y, 
        //              (int)spawnZone.width, (int)spawnZone.height);
    }
    
    /**
     * Renderiza todo el mapa (fondo + edificios).
     * @param g2d Contexto gráfico
     */
    public void render(Graphics2D g2d) {
        renderBackground(g2d);
        renderBuildings(g2d);
    }
    
    // Getters
    
    public List<CityBlock> getBlocks() {
        return blocks;
    }
    
    public List<Street> getStreets() {
        return streets;
    }
    
    public Rectangle2D.Float getSpawnZone() {
        return spawnZone;
    }
    
    public long getSeed() {
        return seed;
    }
    
    // ==================== SISTEMA DE NAVEGACIÓN PARA A* ====================
    
    /** Tamaño de celda para la cuadrícula de navegación */
    private static final int GRID_CELL_SIZE = 20;
    
    private boolean[][] navigationGrid;
    private int gridWidth;
    private int gridHeight;
    
    /**
     * Genera la cuadrícula de navegación para el pathfinder A*.
     * Debe llamarse después de generar las manzanas.
     */
    private void generateNavigationGrid() {
        gridWidth = worldWidth / GRID_CELL_SIZE;
        gridHeight = worldHeight / GRID_CELL_SIZE;
        navigationGrid = new boolean[gridWidth][gridHeight];
        
        // Marcar todas las celdas como caminables inicialmente
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                float worldX = x * GRID_CELL_SIZE;
                float worldY = y * GRID_CELL_SIZE;
                
                // Verificar si la celda colisiona con algún edificio
                boolean walkable = !checkCollision(worldX, worldY, GRID_CELL_SIZE, GRID_CELL_SIZE);
                navigationGrid[x][y] = walkable;
            }
        }
        
        LOGGER.fine("Grid de navegación generado: " + gridWidth + "x" + gridHeight);
    }
    
    /**
     * Verifica si una celda del grid es caminable.
     * @param gridX Coordenada X en el grid
     * @param gridY Coordenada Y en el grid
     * @return true si es caminable
     */
    public boolean isWalkable(int gridX, int gridY) {
        if (gridX < 0 || gridX >= gridWidth || gridY < 0 || gridY >= gridHeight) {
            return false;
        }
        return navigationGrid[gridX][gridY];
    }
    
    /**
     * Convierte coordenada X del mundo a coordenada del grid.
     * @param worldX Coordenada X en el mundo
     * @return Coordenada X en el grid
     */
    public int worldToGridX(float worldX) {
        return (int) (worldX / GRID_CELL_SIZE);
    }
    
    /**
     * Convierte coordenada Y del mundo a coordenada del grid.
     * @param worldY Coordenada Y en el mundo
     * @return Coordenada Y en el grid
     */
    public int worldToGridY(float worldY) {
        return (int) (worldY / GRID_CELL_SIZE);
    }
    
    /**
     * Convierte coordenada X del grid a coordenada del mundo (centro de la celda).
     * @param gridX Coordenada X en el grid
     * @return Coordenada X en el mundo
     */
    public float gridToWorldX(int gridX) {
        return gridX * GRID_CELL_SIZE + GRID_CELL_SIZE / 2f;
    }
    
    /**
     * Convierte coordenada Y del grid a coordenada del mundo (centro de la celda).
     * @param gridY Coordenada Y en el grid
     * @return Coordenada Y en el mundo
     */
    public float gridToWorldY(int gridY) {
        return gridY * GRID_CELL_SIZE + GRID_CELL_SIZE / 2f;
    }
    
    /**
     * Obtiene el tamaño de celda del grid.
     * @return Tamaño de celda en píxeles
     */
    public int getGridCellSize() {
        return GRID_CELL_SIZE;
    }
    
    /**
     * Obtiene el ancho del grid.
     * @return Ancho en celdas
     */
    public int getGridWidth() {
        return gridWidth;
    }
    
    /**
     * Obtiene el alto del grid.
     * @return Alto en celdas
     */
    public int getGridHeight() {
        return gridHeight;
    }
    
    /**
     * Obtiene el grid de navegación completo.
     * @return Matriz booleana donde true = caminable
     */
    public boolean[][] getNavigationGrid() {
        return navigationGrid;
    }
    
    /**
     * Clase interna para representar una calle.
     */
    public static class Street {
        private final float x;
        private final float y;
        private final float width;  // Ancho de la calle
        private final float length; // Longitud de la calle
        private final boolean horizontal;
        
        public Street(float x, float y, float widthOrLength, float lengthOrWidth, boolean horizontal) {
            this.x = x;
            this.y = y;
            this.horizontal = horizontal;
            
            if (horizontal) {
                this.length = widthOrLength;
                this.width = lengthOrWidth;
            } else {
                this.width = widthOrLength;
                this.length = lengthOrWidth;
            }
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
        
        public float getLength() {
            return length;
        }
        
        public float getHeight() {
            return horizontal ? width : length;
        }
        
        public boolean isHorizontal() {
            return horizontal;
        }
    }
}
