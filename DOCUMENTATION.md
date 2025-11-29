# Documentación del Proyecto - Atropellalo Game

## Información General
- **Nombre del Proyecto**: Atropellalo
- **Tipo**: Juego estilo Survivor (inspirado en Vampire Survivors)
- **Lenguaje**: Java 11
- **Framework UI**: Swing
- **Build Tool**: Maven
- **Estado**: Fase 3 Completada - Sistema de Loot y HUD

## Pautas de Desarrollo Aplicadas

### 1. Mejores Prácticas de Programación
- Uso de convenciones de nombres Java estándar
- Encapsulamiento adecuado de propiedades
- Manejo de excepciones con logging
- Uso de constantes para valores configurables

### 2. Bajo Acoplamiento y Alta Cohesión
- **Game**: Clase principal con única responsabilidad de iniciar la aplicación
- **GameWindow**: Responsable solo de la configuración de la ventana
- **GamePanel**: Responsable solo del renderizado
- Cada clase tiene una responsabilidad específica y bien definida

### 3. KISS (Keep It Simple, Stupid)
- Implementación directa sin complejidad innecesaria
- Uso de Swing estándar sin frameworks adicionales
- Carga simple de recursos

### 4. DRY (Don't Repeat Yourself)
- Constantes definidas para valores reutilizables
- Métodos separados para cada funcionalidad específica
- Logging centralizado

### 5. Definition of Done (DoD)
- Solo se implementó lo solicitado: ventana ejecutable con mapa de fondo
- No se agregaron características adicionales
- Código listo para ejecutar

### 6. Nivel Profesional
- Código documentado con JavaDoc
- Manejo de errores
- Logging apropiado
- Estructura de proyecto Maven estándar

## Estructura del Proyecto

```
atropellalo/
├── pom.xml                                    # Configuración Maven
├── DOCUMENTATION.md                           # Este archivo
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── atropellalo/
        │           ├── Game.java              # Clase principal (main)
        │           └── game/
        │               ├── camera/
        │               │   └── Camera.java            # Sistema de cámara
        │               ├── config/
        │               │   └── GameConfig.java        # Configuración del juego
        │               ├── entity/
        │               │   └── Player.java            # Jugador con stats
        │               ├── input/
        │               │   └── InputHandler.java      # Manejo de teclado
        │               ├── loot/
        │               │   ├── Loot.java              # Clase base de loot
        │               │   ├── LootType.java          # Tipos de loot (enum)
        │               │   ├── Fuel.java              # Item de combustible
        │               │   ├── Scrap.java             # Item de chatarra
        │               │   └── LootManager.java       # Gestor de loot
        │               ├── ui/
        │               │   ├── GameWindow.java        # Ventana del juego
        │               │   ├── GamePanel.java         # Panel con game loop
        │               │   └── GameHUD.java           # HUD del jugador
        │               └── util/
        │                   └── MapGenerator.java      # Generador de mapa
        └── resources/
            └── images/
                └── map.jpg                    # Imagen del mapa (2560x1440)
```

## Clases Implementadas

### 1. Game (Clase Principal)
**Ubicación**: `com.atropellalo.Game`

**Responsabilidad**: Punto de entrada de la aplicación.

**Características**:
- Método `main()` que inicia la aplicación
- Usa `SwingUtilities.invokeLater()` para thread-safety de Swing
- Crea e inicializa la ventana del juego

### 2. GameWindow
**Ubicación**: `com.atropellalo.game.ui.GameWindow`

**Responsabilidad**: Configuración y gestión de la ventana principal del juego.

**Características**:
- Extiende `JFrame`
- Dimensiones: 1280x720 píxeles
- Ventana centrada en pantalla
- No redimensionable
- Cierra la aplicación al cerrar ventana
- Contiene el GamePanel
- Inicia y detiene el game loop

**Constantes**:
- `WINDOW_WIDTH`: 1280
- `WINDOW_HEIGHT`: 720
- `GAME_TITLE`: "Atropellalo - Survivor Game"

### 3. GamePanel
**Ubicación**: `com.atropellalo.game.ui.GamePanel`

**Responsabilidad**: Renderizado del mapa, elementos visuales y game loop principal.

**Características**:
- Extiende `JPanel` e implementa `Runnable`
- Carga imagen del mapa desde recursos
- Game loop a 60 FPS
- Actualiza lógica del juego (jugador, cámara)
- Renderiza el mapa y jugador con transformación de cámara
- Usa renderizado de calidad (antialiasing)
- Manejo de errores con logging

**Constantes**:
- `TARGET_FPS`: 60
- `WORLD_WIDTH`: 2560 (2x ventana)
- `WORLD_HEIGHT`: 1440 (2x ventana)

**Métodos Principales**:
- `startGameLoop()`: Inicia el loop del juego
- `stopGameLoop()`: Detiene el loop del juego
- `run()`: Loop principal con control de FPS
- `update(float)`: Actualiza lógica del juego
- `loadMapImage()`: Carga la imagen desde `/images/map.jpg`
- `paintComponent()`: Método de renderizado con transformación de cámara
- `drawMap()`: Dibuja el mapa de fondo
- `drawPlayer()`: Dibuja el jugador

### 4. Player
**Ubicación**: `com.atropellalo.game.entity.Player`

**Responsabilidad**: Representa al jugador y maneja su estado, movimiento, salud y combustible.

**Características**:
- Posición en el mundo (x, y)
- Velocidad de movimiento
- Sistema de salud (health/maxHealth)
- Sistema de combustible (fuel/maxFuel)
- Renderizado con indicadores visuales de estado
- Normalización de velocidad diagonal
- Restricción de movimiento sin combustible
- Límites del mundo aplicados

**Stats del Jugador** (configurables en GameConfig):
- `PLAYER_MAX_HEALTH`: 100 puntos
- `PLAYER_INITIAL_HEALTH`: 100 puntos
- `PLAYER_MAX_FUEL`: 100 unidades
- `PLAYER_INITIAL_FUEL`: 100 unidades
- `FUEL_CONSUMPTION_RATE`: 5 unidades/segundo
- `PLAYER_SPEED`: 200 píxeles/segundo
- `PLAYER_SIZE`: 32 píxeles

**Métodos Principales**:
- `update(float)`: Actualiza posición, consume combustible
- `render(Graphics2D)`: Dibuja jugador con indicadores de estado
- `setMovement(int, int)`: Establece dirección (verifica combustible)
- `addFuel(float)`: Añade combustible al recolectar
- `heal(float)`: Restaura salud al recolectar chatarra
- `damage(float)`: Recibe daño
- `isAlive()`: Verifica si el jugador está vivo
- `hasFuel()`: Verifica si tiene combustible

### 5. InputHandler
**Ubicación**: `com.atropellalo.game.input.InputHandler`

**Responsabilidad**: Captura y procesa entrada del teclado.

**Características**:
- Implementa `KeyListener`
- Captura teclas WASD
- Estado de teclas presionadas
- Métodos para obtener dirección de movimiento

**Métodos Principales**:
- `keyPressed(KeyEvent)`: Registra tecla presionada
- `keyReleased(KeyEvent)`: Registra tecla liberada
- `getHorizontalDirection()`: Retorna -1, 0, o 1
- `getVerticalDirection()`: Retorna -1, 0, o 1

### 6. Camera
**Ubicación**: `com.atropellalo.game.camera.Camera`

**Responsabilidad**: Controla qué parte del mundo se muestra en pantalla.

**Características**:
- Posición de la cámara (x, y)
- Dimensiones del viewport (ventana visible)
- Dimensiones del mundo
- Centra en el jugador
- Limita movimiento a bordes del mundo

**Métodos Principales**:
- `centerOn(float, float)`: Centra cámara en objetivo
- `getOffsetX()`, `getOffsetY()`: Obtiene offset para renderizado

### 7. MapGenerator
**Ubicación**: `com.atropellalo.game.util.MapGenerator`

**Responsabilidad**: Genera imagen de mapa proceduralmente (utilidad).

**Características**:
- Genera imagen de 2560x1440 píxeles
- Tonos de verde variados
- Textura procedural simple
- Guarda como JPG en resources/images/

**Nota**: Esta es una clase utilitaria para generar el mapa inicial. No se usa durante el juego.

### 8. GameConfig (NUEVO)
**Ubicación**: `com.atropellalo.game.config.GameConfig`

**Responsabilidad**: Centraliza toda la configuración del juego en un solo lugar.

**Características**:
- Clase final no instanciable
- Constantes estáticas organizadas por categoría
- Fácil de modificar para ajustar balance del juego
- Documentación clara de cada parámetro

**Categorías de Configuración**:
- **Jugador**: Salud, combustible, velocidad, tamaño
- **Loot - Combustible**: Restauración, tamaño, intervalos de spawn, cantidad máxima
- **Loot - Chatarra**: Curación, tamaño, intervalos de spawn, cantidad máxima
- **Colisiones**: Distancia de pickup
- **HUD**: Dimensiones de barras, márgenes
- **Mundo**: Dimensiones, márgenes de spawn

### 9. Loot (NUEVO)
**Ubicación**: `com.atropellalo.game.loot.Loot`

**Responsabilidad**: Clase abstracta base para todos los items de loot.

**Características**:
- Posición y tamaño
- Estado de recolección
- Cálculo de distancia
- Métodos abstractos para renderizado y valores

### 10. LootType (NUEVO)
**Ubicación**: `com.atropellalo.game.loot.LootType`

**Responsabilidad**: Enum que define los tipos de loot disponibles.

**Valores**:
- `FUEL`: Combustible
- `SCRAP`: Chatarra

### 11. Fuel (NUEVO)
**Ubicación**: `com.atropellalo.game.loot.Fuel`

**Responsabilidad**: Item de loot que restaura combustible.

**Características**:
- Hereda de Loot
- Renderizado como bidón naranja
- Valor configurable en GameConfig

### 12. Scrap (NUEVO)
**Ubicación**: `com.atropellalo.game.loot.Scrap`

**Responsabilidad**: Item de loot que restaura salud.

**Características**:
- Hereda de Loot
- Renderizado como pieza metálica gris con cruz verde
- Valor configurable en GameConfig

### 13. LootManager (NUEVO)
**Ubicación**: `com.atropellalo.game.loot.LootManager`

**Responsabilidad**: Gestiona el spawn, actualización y colección de loot.

**Características**:
- Spawn inicial de loot al comenzar
- Spawn periódico con intervalos aleatorios
- Límites máximos por tipo de loot
- Detección de colisiones con jugador
- Limpieza de loot recolectado

**Métodos Principales**:
- `update(float)`: Actualiza timers y spawn
- `checkCollisions(float, float)`: Verifica colisiones con jugador
- `render(Graphics2D)`: Dibuja todos los items

### 14. GameHUD (NUEVO)
**Ubicación**: `com.atropellalo.game.ui.GameHUD`

**Responsabilidad**: Muestra información vital del jugador en pantalla.

**Características**:
- Barra de salud con colores según nivel (verde/naranja/rojo)
- Barra de combustible con colores según nivel
- Valores numéricos en las barras
- Advertencia de combustible bajo
- Pantalla de Game Over

**Elementos Visuales**:
- Barras con esquinas redondeadas
- Texto con sombra para mejor legibilidad
- Animación de parpadeo en advertencias
- Overlay semi-transparente en Game Over

## Configuración Maven (pom.xml)

### Propiedades
- Java Version: 11
- Encoding: UTF-8
- Packaging: JAR

### Plugins Configurados
1. **maven-jar-plugin**: Genera JAR ejecutable con manifest
   - Main-Class: `com.atropellalo.Game`

2. **maven-compiler-plugin**: Compila código Java 11

### Build
Los recursos en `src/main/resources` se incluyen automáticamente en el JAR.

## Características del Mapa

### Requisitos Cumplidos
✅ Tamaño fijo mayor que la ventana (2560x1440)  
✅ Imagen fija (no procedural en runtime)  
✅ Sin cuadrículas  
✅ Estilo similar a Vampire Survivors  
✅ Imagen estática (`map.jpg`)

### Implementación
- La imagen se carga desde `/images/map.jpg`
- Tamaño: 2560x1440 (2x el tamaño de la ventana)
- Se renderiza completa, pero solo se ve la porción visible según la cámara
- Renderizado con alta calidad (antialiasing activado)
- Generada proceduralmente en tonos verdes (temporal)

## Sistema de Jugador

### Características
✅ Representado como cuadrado (color según salud)  
✅ Tamaño: 32x32 píxeles  
✅ Velocidad: 200 píxeles/segundo  
✅ Movimiento con WASD  
✅ Normalización de velocidad diagonal  
✅ Sistema de salud con indicadores visuales  
✅ Sistema de combustible con consumo al moverse  
✅ Sin combustible = Sin movimiento  

### Controles
- **W**: Mover arriba
- **A**: Mover izquierda
- **S**: Mover abajo
- **D**: Mover derecha
- Se pueden combinar teclas para movimiento diagonal

### Mecánicas de Supervivencia
- **Combustible**: Se consume a 5 unidades/segundo mientras se mueve
- **Movimiento**: Bloqueado si el combustible llega a 0
- **Salud**: Si llega a 0, el juego termina (Game Over)

## Sistema de Loot (NUEVO)

### Combustible (Fuel)
- **Apariencia**: Bidón naranja con gota blanca
- **Efecto**: Restaura 25 unidades de combustible
- **Spawn inicial**: 5 items en el mapa
- **Spawn máximo**: 10 items simultáneos
- **Intervalo de spawn**: 3-6 segundos

### Chatarra (Scrap)
- **Apariencia**: Pieza metálica gris con cruz verde
- **Efecto**: Restaura 15 puntos de salud
- **Spawn inicial**: 3 items en el mapa
- **Spawn máximo**: 8 items simultáneos
- **Intervalo de spawn**: 5-10 segundos

### Mecánicas de Recolección
- Distancia de pickup: 30 píxeles desde el centro del jugador
- Los items desaparecen al ser recolectados
- Nuevos items aparecen periódicamente en posiciones aleatorias

## Sistema de HUD (NUEVO)

### Barra de Salud
- **Posición**: Esquina superior izquierda
- **Colores**:
  - Verde: > 50% salud
  - Naranja: 25-50% salud
  - Rojo: < 25% salud
- Muestra valores numéricos (ej: "75/100")

### Barra de Combustible
- **Posición**: Debajo de la barra de salud
- **Colores**:
  - Naranja: > 50% combustible
  - Naranja oscuro: 20-50% combustible
  - Rojo: < 20% combustible
- Icono de advertencia cuando el combustible es bajo
- Muestra valores numéricos

### Pantalla de Game Over
- Aparece cuando la salud llega a 0
- Overlay oscuro semi-transparente
- Texto "GAME OVER" en rojo
- Mensaje explicativo

## Sistema de Cámara

### Características
✅ Centrada en el jugador  
✅ Viewport de 1280x720 (tamaño de ventana)  
✅ Limitada a los bordes del mundo  
✅ Movimiento suave siguiendo al jugador  

### Implementación
- La cámara siempre mantiene al jugador centrado
- Cuando el jugador está cerca de los bordes, la cámara se detiene
- Usa transformación de Graphics2D para renderizado eficiente

## Game Loop

### Características
✅ 60 FPS objetivo  
✅ Delta time para movimiento independiente del framerate  
✅ Actualización de lógica separada del renderizado  
✅ Control de timing preciso  

### Flujo
1. Calcular delta time
2. Verificar si el juego terminó
3. Actualizar input del jugador
4. Actualizar posición del jugador (consumir combustible)
5. Actualizar sistema de loot (spawn, timers)
6. Verificar colisiones con loot
7. Aplicar efectos de loot recolectado
8. Actualizar posición de la cámara
9. Renderizar escena (mapa, loot, jugador)
10. Renderizar HUD
11. Sleep para mantener FPS objetivo

## Cómo Ejecutar

### Compilar el Proyecto
```powershell
mvn clean compile
```

### Empaquetar como JAR
```powershell
mvn clean package
```

### Ejecutar desde Maven
```powershell
mvn exec:java "-Dexec.mainClass=com.atropellalo.Game"
```

### Ejecutar JAR Generado
```powershell
java -jar target/atropellalo-game-1.0-SNAPSHOT.jar
```

**Nota**: El juego se ejecutará y mostrará una ventana de 1280x720 píxeles con el mapa de fondo cargado desde `src/main/resources/images/map.jpg`.

## Tecnologías Utilizadas

- **Java 11**: Lenguaje de programación
- **Swing**: Framework para interfaz gráfica
- **ImageIO**: Carga de imágenes
- **Maven**: Gestión de dependencias y build
- **Java Logging**: Sistema de logs
- **Threading**: Game loop en hilo separado
- **AWT Graphics2D**: Renderizado con transformaciones

## Arquitectura y Patrones

### Separación de Responsabilidades
- **UI**: GameWindow, GamePanel, GameHUD
- **Entidades**: Player
- **Sistemas**: Camera, InputHandler, LootManager
- **Configuración**: GameConfig
- **Loot**: Loot (abstract), Fuel, Scrap, LootType
- **Utilidades**: MapGenerator

### Game Loop Pattern
El GamePanel implementa el patrón de game loop:
1. **Update**: Actualiza lógica del juego
2. **Render**: Dibuja los elementos
3. **Timing**: Controla FPS

### Observer Pattern (Input)
InputHandler actúa como observer del teclado, manteniendo estado de teclas.

### Camera Pattern
Sistema de cámara desacoplado que puede seguir cualquier objetivo.

### Template Method Pattern (Loot)
Loot define el esqueleto para items coleccionables, Fuel y Scrap implementan los detalles.

### Manager Pattern (LootManager)
Centraliza la gestión de todos los items de loot en el juego.

## Próximos Pasos Sugeridos (No Implementados)

Para continuar el desarrollo del juego estilo Survivor, se sugiere:

1. **Enemigos**: Sistema de spawn y comportamiento de enemigos
2. **Colisiones**: Detección de colisiones entre jugador y enemigos (daño al jugador)
3. **Armas/Ataques**: Sistema de combate automático (característica principal de Vampire Survivors)
4. **Experiencia/Nivel**: Sistema de progresión
5. **Power-ups adicionales**: Mejoras temporales y habilidades
6. **Partículas**: Efectos visuales al recolectar loot
7. **Audio**: Música y efectos de sonido
8. **Sprite del jugador**: Reemplazar cuadrado con sprite animado
9. **Mapa mejorado**: Diseño de nivel más detallado
10. **Reinicio de partida**: Opción para reiniciar después del Game Over

## Notas Técnicas

### Rendimiento
- Game loop optimizado a 60 FPS
- Delta time asegura movimiento consistente
- Transformación de cámara eficiente con Graphics2D

### Escalabilidad
- Arquitectura preparada para múltiples entidades
- Sistema de cámara reutilizable
- Input handler extensible para más controles

### Mejores Prácticas Aplicadas
- El proyecto usa Swing por simplicidad (KISS)
- La separación UI/lógica/sistemas permite futura expansión
- El uso de constantes facilita ajustes de configuración
- El logging permite debugging efectivo
- La estructura permite agregar nuevas clases sin modificar existentes (Open/Closed)
- Game loop en hilo separado mantiene UI responsiva
- Delta time hace el juego independiente del framerate
- Configuración centralizada en GameConfig (fácil de modificar)
- Herencia para loot reduce duplicación de código (DRY)
- Bajo acoplamiento entre sistemas (loot, HUD, jugador)

### Decisiones de Diseño
1. **Cuadrado para jugador**: Prototipo simple, color indica salud
2. **Mapa generado**: Solución temporal, puede reemplazarse con imagen artística
3. **Tamaño mundo 2x ventana**: Balance entre exploración y simplicidad
4. **60 FPS**: Estándar para juegos 2D, buen balance rendimiento/fluidez
5. **Velocidad 200px/s**: Valor ajustable en GameConfig
6. **GameConfig separado**: Facilita ajustes de balance sin tocar lógica
7. **Loot abstracto**: Permite agregar nuevos tipos fácilmente
8. **HUD separado**: Mantiene GamePanel enfocado en el mundo del juego

## Guía de Configuración (GameConfig)

Para ajustar el balance del juego, modifica los valores en `GameConfig.java`:

### Jugador
```java
PLAYER_MAX_HEALTH = 100.0f;      // Salud máxima
PLAYER_INITIAL_HEALTH = 100.0f;  // Salud inicial
PLAYER_MAX_FUEL = 100.0f;        // Combustible máximo
PLAYER_INITIAL_FUEL = 100.0f;    // Combustible inicial
FUEL_CONSUMPTION_RATE = 5.0f;    // Consumo por segundo
PLAYER_SPEED = 200.0f;           // Velocidad en píxeles/s
```

### Combustible
```java
FUEL_RESTORE_AMOUNT = 25.0f;     // Cantidad restaurada
FUEL_SPAWN_INTERVAL_MIN = 3.0f;  // Intervalo mínimo de spawn
FUEL_SPAWN_INTERVAL_MAX = 6.0f;  // Intervalo máximo de spawn
FUEL_MAX_ON_MAP = 10;            // Máximo simultáneo en mapa
FUEL_INITIAL_SPAWN = 5;          // Cantidad inicial
```

### Chatarra
```java
SCRAP_HEAL_AMOUNT = 15.0f;       // Cantidad de curación
SCRAP_SPAWN_INTERVAL_MIN = 5.0f; // Intervalo mínimo de spawn
SCRAP_SPAWN_INTERVAL_MAX = 10.0f;// Intervalo máximo de spawn
SCRAP_MAX_ON_MAP = 8;            // Máximo simultáneo en mapa
SCRAP_INITIAL_SPAWN = 3;         // Cantidad inicial
```

---

**Fecha de Creación**: 29/11/2025  
**Última Actualización**: 29/11/2025  
**Versión**: 1.0-SNAPSHOT  
**Estado**: Fase 3 Completada - Sistema de Loot, HUD y Mecánicas de Supervivencia
