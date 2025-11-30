package com.atropellalo.game.pathfinding;

import com.atropellalo.game.map.CityMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Implementación del algoritmo A* para pathfinding en el mapa.
 * Optimizado para uso en tiempo real con múltiples enemigos.
 */
public class AStarPathfinder {
    
    private final CityMap map;
    private final int gridCellSize;
    private final int gridWidth;
    private final int gridHeight;
    
    // Direcciones de movimiento (8 direcciones)
    private static final int[][] DIRECTIONS = {
        {0, -1},   // Arriba
        {0, 1},    // Abajo
        {-1, 0},   // Izquierda
        {1, 0},    // Derecha
        {-1, -1},  // Diagonal arriba-izquierda
        {1, -1},   // Diagonal arriba-derecha
        {-1, 1},   // Diagonal abajo-izquierda
        {1, 1}     // Diagonal abajo-derecha
    };
    
    // Costos de movimiento
    private static final float STRAIGHT_COST = 1.0f;
    private static final float DIAGONAL_COST = 1.414f;  // √2
    
    // Límites para optimización
    private static final int MAX_ITERATIONS = 500;  // Máximo de iteraciones por búsqueda
    private static final int MAX_PATH_LENGTH = 100;  // Máximo de nodos en el path
    
    // Cache de paths para optimización
    private final Map<Long, CachedPath> pathCache;
    private static final long CACHE_EXPIRY_MS = 200;  // Los paths se invalidan después de 200ms
    
    /**
     * Constructor del pathfinder.
     * @param map Mapa de la ciudad
     */
    public AStarPathfinder(CityMap map) {
        this.map = map;
        this.gridCellSize = map.getGridCellSize();
        this.gridWidth = map.getGridWidth();
        this.gridHeight = map.getGridHeight();
        this.pathCache = new HashMap<>();
    }
    
    /**
     * Encuentra un path desde una posición inicial hasta una posición objetivo.
     * @param startX Posición X inicial en el mundo
     * @param startY Posición Y inicial en el mundo
     * @param targetX Posición X objetivo en el mundo
     * @param targetY Posición Y objetivo en el mundo
     * @return Lista de puntos del path (en coordenadas del mundo), o null si no hay path
     */
    public List<PathNode> findPath(float startX, float startY, float targetX, float targetY) {
        // Convertir a coordenadas de grid
        int startGridX = map.worldToGridX(startX);
        int startGridY = map.worldToGridY(startY);
        int targetGridX = map.worldToGridX(targetX);
        int targetGridY = map.worldToGridY(targetY);
        
        // Verificar cache
        long cacheKey = generateCacheKey(startGridX, startGridY, targetGridX, targetGridY);
        CachedPath cached = pathCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached.path;
        }
        
        // Ejecutar A*
        List<PathNode> path = executeAStar(startGridX, startGridY, targetGridX, targetGridY);
        
        // Guardar en cache
        pathCache.put(cacheKey, new CachedPath(path));
        
        // Limpiar cache viejo ocasionalmente
        if (pathCache.size() > 1000) {
            cleanCache();
        }
        
        return path;
    }
    
    /**
     * Ejecuta el algoritmo A*.
     */
    private List<PathNode> executeAStar(int startX, int startY, int targetX, int targetY) {
        // Si el inicio o el objetivo no son válidos, buscar alternativas
        if (!map.isWalkable(startX, startY)) {
            int[] nearest = findNearestWalkable(startX, startY);
            if (nearest == null) return null;
            startX = nearest[0];
            startY = nearest[1];
        }
        
        if (!map.isWalkable(targetX, targetY)) {
            int[] nearest = findNearestWalkable(targetX, targetY);
            if (nearest == null) return null;
            targetX = nearest[0];
            targetY = nearest[1];
        }
        
        // Si start == target, devolver path vacío
        if (startX == targetX && startY == targetY) {
            List<PathNode> singleNode = new ArrayList<>();
            singleNode.add(new PathNode(map.gridToWorldX(startX), map.gridToWorldY(startY)));
            return singleNode;
        }
        
        // Estructuras de datos para A*
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        Set<Long> closedSet = new HashSet<>();
        Map<Long, Node> allNodes = new HashMap<>();
        
        // Nodo inicial
        Node startNode = new Node(startX, startY, 0, heuristic(startX, startY, targetX, targetY), null);
        openSet.add(startNode);
        allNodes.put(nodeKey(startX, startY), startNode);
        
        int iterations = 0;
        final int finalTargetX = targetX;
        final int finalTargetY = targetY;
        
        while (!openSet.isEmpty() && iterations < MAX_ITERATIONS) {
            iterations++;
            
            Node current = openSet.poll();
            
            // ¿Llegamos al objetivo?
            if (current.x == finalTargetX && current.y == finalTargetY) {
                return reconstructPath(current);
            }
            
            closedSet.add(nodeKey(current.x, current.y));
            
            // Explorar vecinos
            for (int i = 0; i < DIRECTIONS.length; i++) {
                int nx = current.x + DIRECTIONS[i][0];
                int ny = current.y + DIRECTIONS[i][1];
                
                // Verificar límites
                if (nx < 0 || nx >= gridWidth || ny < 0 || ny >= gridHeight) {
                    continue;
                }
                
                // Verificar si ya fue visitado
                if (closedSet.contains(nodeKey(nx, ny))) {
                    continue;
                }
                
                // Verificar si es caminable
                if (!map.isWalkable(nx, ny)) {
                    continue;
                }
                
                // Para movimientos diagonales, verificar que no haya esquinas bloqueadas
                if (i >= 4) {  // Es diagonal
                    if (!map.isWalkable(current.x + DIRECTIONS[i][0], current.y) ||
                        !map.isWalkable(current.x, current.y + DIRECTIONS[i][1])) {
                        continue;  // No permitir cortar esquinas
                    }
                }
                
                // Calcular costos
                float moveCost = (i < 4) ? STRAIGHT_COST : DIAGONAL_COST;
                float newGCost = current.gCost + moveCost;
                
                long neighborKey = nodeKey(nx, ny);
                Node existingNode = allNodes.get(neighborKey);
                
                if (existingNode == null || newGCost < existingNode.gCost) {
                    float hCost = heuristic(nx, ny, finalTargetX, finalTargetY);
                    Node neighbor = new Node(nx, ny, newGCost, hCost, current);
                    
                    if (existingNode != null) {
                        openSet.remove(existingNode);
                    }
                    
                    openSet.add(neighbor);
                    allNodes.put(neighborKey, neighbor);
                }
            }
        }
        
        // No se encontró path - intentar devolver el mejor path parcial
        if (!allNodes.isEmpty()) {
            Node bestPartial = allNodes.values().stream()
                .min(Comparator.comparingDouble(n -> heuristic(n.x, n.y, finalTargetX, finalTargetY)))
                .orElse(null);
            if (bestPartial != null && bestPartial.parent != null) {
                return reconstructPath(bestPartial);
            }
        }
        
        return null;
    }
    
    /**
     * Encuentra la celda caminable más cercana.
     */
    private int[] findNearestWalkable(int x, int y) {
        int searchRadius = 5;
        
        for (int r = 1; r <= searchRadius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    if (Math.abs(dx) == r || Math.abs(dy) == r) {
                        int nx = x + dx;
                        int ny = y + dy;
                        if (map.isWalkable(nx, ny)) {
                            return new int[]{nx, ny};
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Reconstruye el path desde el nodo final.
     */
    private List<PathNode> reconstructPath(Node endNode) {
        List<PathNode> path = new ArrayList<>();
        Node current = endNode;
        
        while (current != null && path.size() < MAX_PATH_LENGTH) {
            float worldX = map.gridToWorldX(current.x);
            float worldY = map.gridToWorldY(current.y);
            path.add(new PathNode(worldX, worldY));
            current = current.parent;
        }
        
        Collections.reverse(path);
        
        // Suavizar el path eliminando puntos innecesarios
        return smoothPath(path);
    }
    
    /**
     * Suaviza el path eliminando waypoints innecesarios.
     */
    private List<PathNode> smoothPath(List<PathNode> path) {
        if (path.size() <= 2) {
            return path;
        }
        
        List<PathNode> smoothed = new ArrayList<>();
        smoothed.add(path.get(0));
        
        int current = 0;
        while (current < path.size() - 1) {
            int furthest = current + 1;
            
            // Buscar el punto más lejano al que podemos ir en línea recta
            for (int i = current + 2; i < path.size() && i < current + 10; i++) {
                if (hasLineOfSight(path.get(current), path.get(i))) {
                    furthest = i;
                }
            }
            
            smoothed.add(path.get(furthest));
            current = furthest;
        }
        
        return smoothed;
    }
    
    /**
     * Verifica si hay línea de vista entre dos puntos.
     */
    private boolean hasLineOfSight(PathNode from, PathNode to) {
        float dx = to.x - from.x;
        float dy = to.y - from.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance < gridCellSize) {
            return true;
        }
        
        int steps = (int) (distance / gridCellSize) + 1;
        float stepX = dx / steps;
        float stepY = dy / steps;
        
        for (int i = 1; i < steps; i++) {
            float checkX = from.x + stepX * i;
            float checkY = from.y + stepY * i;
            
            int gridX = map.worldToGridX(checkX);
            int gridY = map.worldToGridY(checkY);
            
            if (!map.isWalkable(gridX, gridY)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Heurística para A* (distancia Manhattan con diagonal).
     */
    private float heuristic(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        // Octile distance
        return STRAIGHT_COST * Math.max(dx, dy) + (DIAGONAL_COST - STRAIGHT_COST) * Math.min(dx, dy);
    }
    
    /**
     * Genera una clave única para un nodo.
     */
    private long nodeKey(int x, int y) {
        return ((long) x << 32) | (y & 0xFFFFFFFFL);
    }
    
    /**
     * Genera una clave de cache para un path.
     */
    private long generateCacheKey(int startX, int startY, int targetX, int targetY) {
        // Agrupa posiciones cercanas para mejor cache hit rate
        int sx = startX / 3;
        int sy = startY / 3;
        int tx = targetX / 3;
        int ty = targetY / 3;
        return ((long) sx << 48) | ((long) sy << 32) | ((long) tx << 16) | ty;
    }
    
    /**
     * Limpia entries del cache expirados.
     */
    private void cleanCache() {
        pathCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * Limpia todo el cache.
     */
    public void clearCache() {
        pathCache.clear();
    }
    
    /**
     * Obtiene el siguiente punto del path como dirección de movimiento.
     * Útil para enemigos que solo necesitan saber hacia dónde moverse.
     * @return float[] con {dirX, dirY} normalizado, o null si no hay path
     */
    public float[] getMovementDirection(float startX, float startY, float targetX, float targetY) {
        List<PathNode> path = findPath(startX, startY, targetX, targetY);
        
        if (path == null || path.size() < 2) {
            // Fallback: dirección directa al objetivo
            float dx = targetX - startX;
            float dy = targetY - startY;
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            if (length > 0) {
                return new float[]{dx / length, dy / length};
            }
            return new float[]{0, 0};
        }
        
        // Obtener el siguiente waypoint
        PathNode next = path.get(1);
        float dx = next.x - startX;
        float dy = next.y - startY;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (length > 0) {
            return new float[]{dx / length, dy / length};
        }
        
        return new float[]{0, 0};
    }
    
    /**
     * Nodo interno para A*.
     */
    private static class Node {
        final int x;
        final int y;
        final float gCost;  // Costo desde el inicio
        final float fCost;  // gCost + heurística
        final Node parent;
        
        Node(int x, int y, float gCost, float hCost, Node parent) {
            this.x = x;
            this.y = y;
            this.gCost = gCost;
            this.fCost = gCost + hCost;
            this.parent = parent;
        }
    }
    
    /**
     * Path cacheado con timestamp.
     */
    private static class CachedPath {
        final List<PathNode> path;
        final long timestamp;
        
        CachedPath(List<PathNode> path) {
            this.path = path;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_MS;
        }
    }
    
    /**
     * Punto del path en coordenadas del mundo.
     */
    public static class PathNode {
        public final float x;
        public final float y;
        
        public PathNode(float x, float y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public String toString() {
            return "PathNode(" + x + ", " + y + ")";
        }
    }
}
