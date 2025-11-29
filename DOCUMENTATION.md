# Documentación del Proyecto - Atropellalo Game

## Información General
- **Nombre del Proyecto**: Atropellalo
- **Tipo**: Juego estilo Survivor (inspirado en Vampire Survivors)
- **Lenguaje**: Java 11
- **Framework UI**: Swing
- **Build Tool**: Maven
- **Estado**: Fase 2 Completada - Jugador con Movimiento y Cámara

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
        │               ├── entity/
        │               │   └── Player.java            # Jugador
        │               ├── input/
        │               │   └── InputHandler.java      # Manejo de teclado
        │               ├── ui/
        │               │   ├── GameWindow.java        # Ventana del juego
        │               │   └── GamePanel.java         # Panel con game loop
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

**Responsabilidad**: Representa al jugador y maneja su estado y renderizado.

**Características**:
- Posición en el mundo (x, y)
- Velocidad de movimiento
- Renderizado como cuadrado rojo (prototipo)
- Normalización de velocidad diagonal
- Métodos para obtener centro (para cámara)

**Constantes**:
- `PLAYER_SIZE`: 32 píxeles
- `PLAYER_SPEED`: 200 píxeles/segundo

**Métodos Principales**:
- `update(float)`: Actualiza posición según velocidad
- `render(Graphics2D)`: Dibuja el jugador
- `setMovement(int, int)`: Establece dirección de movimiento
- `getCenterX()`, `getCenterY()`: Obtiene centro para cámara

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
✅ Representado como cuadrado rojo (prototipo)  
✅ Tamaño: 32x32 píxeles  
✅ Velocidad: 200 píxeles/segundo  
✅ Movimiento con WASD  
✅ Normalización de velocidad diagonal  

### Controles
- **W**: Mover arriba
- **A**: Mover izquierda
- **S**: Mover abajo
- **D**: Mover derecha
- Se pueden combinar teclas para movimiento diagonal

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
2. Actualizar input del jugador
3. Actualizar posición del jugador
4. Actualizar posición de la cámara
5. Renderizar escena
6. Sleep para mantener FPS objetivo

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
- **UI**: GameWindow, GamePanel
- **Entidades**: Player
- **Sistemas**: Camera, InputHandler
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

## Próximos Pasos Sugeridos (No Implementados)

Para continuar el desarrollo del juego estilo Survivor, se sugiere:

1. **Enemigos**: Sistema de spawn y comportamiento de enemigos
2. **Colisiones**: Detección de colisiones entre jugador y enemigos
3. **Armas/Ataques**: Sistema de combate automático (característica principal de Vampire Survivors)
4. **Experiencia/Nivel**: Sistema de progresión
5. **Power-ups**: Mejoras y habilidades
6. **Partículas**: Efectos visuales
7. **Audio**: Música y efectos de sonido
8. **UI**: HUD con vida, experiencia, etc.
9. **Sprite del jugador**: Reemplazar cuadrado con sprite animado
10. **Mapa mejorado**: Diseño de nivel más detallado

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

### Decisiones de Diseño
1. **Cuadrado para jugador**: Prototipo simple, fácil de reemplazar con sprite
2. **Mapa generado**: Solución temporal, puede reemplazarse con imagen artística
3. **Tamaño mundo 2x ventana**: Balance entre exploración y simplicidad
4. **60 FPS**: Estándar para juegos 2D, buen balance rendimiento/fluidez
5. **Velocidad 200px/s**: Valor ajustable, proporciona movimiento fluido

---

**Fecha de Creación**: 29/11/2025  
**Versión**: 1.0-SNAPSHOT  
**Estado**: Fase 2 Completada - Jugador con Movimiento, Cámara y Controles WASD
