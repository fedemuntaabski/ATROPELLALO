# Documentación del Proyecto - Atropellalo Game

## Información General
- **Nombre del Proyecto**: Atropellalo
- **Tipo**: Juego estilo Survivor (inspirado en Vampire Survivors)
- **Lenguaje**: Java 11
- **Framework UI**: Swing
- **Build Tool**: Maven
- **Estado**: Fase 7 Completada - Escopeta, Sierras, Escalado de Enemigos

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
        │               ├── enemy/
        │               │   ├── Enemy.java             # Clase base de enemigos
        │               │   ├── EnemyType.java         # Tipos de enemigos (enum)
        │               │   ├── FastZombie.java        # Zombie rápido
        │               │   ├── SlowZombie.java        # Zombie lento
        │               │   ├── ExplosiveZombie.java   # Zombie explosivo
        │               │   └── EnemyManager.java      # Gestor de enemigos y oleadas
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
        │               ├── util/
        │               │   └── MapGenerator.java      # Generador de mapa
        │               └── weapon/
        │                   ├── Weapon.java            # Clase base de armas
        │                   ├── Pistol.java            # Pistola automática
        │                   ├── Projectile.java        # Proyectil
        │                   └── WeaponManager.java     # Gestor de armas
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
- **Enemigos - General**: Daño, colisiones, spawn
- **Enemigos - Zombie Rápido**: Velocidad, salud, tamaño, daño
- **Enemigos - Zombie Lento**: Velocidad, salud, tamaño, daño
- **Enemigos - Zombie Explosivo**: Velocidad, salud, radio de explosión, daño de explosión
- **Sistema de Oleadas**: Intervalo, enemigos base, incremento, máximo de enemigos
- **Armas - Pistola**: Daño, rango, delay entre disparos, velocidad y tamaño de proyectil

### 15. EnemyType (NUEVO - Fase 4)
**Ubicación**: `com.atropellalo.game.enemy.EnemyType`

**Responsabilidad**: Enum que define los tipos de enemigos.

**Valores**:
- `FAST`: Zombie rápido
- `SLOW`: Zombie lento
- `EXPLOSIVE`: Zombie explosivo

### 16. Enemy (NUEVO - Fase 4)
**Ubicación**: `com.atropellalo.game.enemy.Enemy`

**Responsabilidad**: Clase abstracta base para todos los enemigos.

**Características**:
- Posición, salud, velocidad, tamaño, daño
- Comportamiento de persecución hacia el jugador
- Sistema de cooldown de daño
- Cálculo de distancias
- Métodos abstractos para renderizado y tipo

**Métodos Principales**:
- `update(float, float, float)`: Actualiza posición persiguiendo al jugador
- `moveTowards(float, float, float)`: Movimiento hacia objetivo
- `takeDamage(float)`: Recibe daño
- `onDeath()`: Comportamiento al morir (override en subclases)
- `distanceToPlayer(float, float)`: Calcula distancia al jugador

### 17. FastZombie (NUEVO - Fase 4)
**Ubicación**: `com.atropellalo.game.enemy.FastZombie`

**Responsabilidad**: Enemigo rápido con poca vida.

**Características**:
- Alta velocidad (150 px/s)
- Baja salud (30 HP)
- Tamaño pequeño (24 px)
- Color verde claro
- Ojos rojos
- Barra de vida cuando recibe daño

### 18. SlowZombie (NUEVO - Fase 4)
**Ubicación**: `com.atropellalo.game.enemy.SlowZombie`

**Responsabilidad**: Enemigo lento con mucha vida.

**Características**:
- Baja velocidad (50 px/s)
- Alta salud (100 HP)
- Tamaño grande (36 px)
- Color púrpura/índigo
- Ojos amarillos con pupilas
- Apariencia más pesada

### 19. ExplosiveZombie (NUEVO - Fase 4)
**Ubicación**: `com.atropellalo.game.enemy.ExplosiveZombie`

**Responsabilidad**: Enemigo que explota al morir.

**Características**:
- Velocidad media (80 px/s)
- Salud media (40 HP)
- Tamaño medio (28 px)
- Color naranja/rojo con símbolo "!"
- Al morir:
  - Explosión con radio de 80 px
  - Daña al jugador si está en rango (25 HP máx)
  - Daña a otros enemigos cercanos
  - Animación de explosión con círculos concéntricos

### 20. EnemyManager (NUEVO - Fase 4)
**Ubicación**: `com.atropellalo.game.enemy.EnemyManager`

**Responsabilidad**: Gestiona enemigos y sistema de oleadas.

**Características**:
- Lista de enemigos activos
- Sistema de oleadas progresivas
- Spawn aleatorio lejos del jugador
- Colisiones jugador-enemigo
- Contador de kills
- Implementa callback para explosiones

**Sistema de Oleadas**:
- Primera oleada después de 3 segundos
- Cada oleada tiene más enemigos
- Intervalo configurable entre oleadas
- Spawn gradual durante la oleada

**Métodos Principales**:
- `update(float, float, float)`: Actualiza oleadas y enemigos
- `checkPlayerCollisions()`: Detecta colisiones (enemigos dañan al jugador)
- `render(Graphics2D)`: Dibuja todos los enemigos
- `renderWaveInfo(Graphics2D, int)`: Dibuja info de oleada en HUD
- `getEnemies()`: Expone lista de enemigos para sistema de armas
- `addKill()`: Incrementa contador de kills

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

### 21. Weapon (NUEVO - Fase 5)
**Ubicación**: `com.atropellalo.game.weapon.Weapon`

**Responsabilidad**: Clase abstracta base para todas las armas.

**Características**:
- Daño, rango y delay configurables
- Sistema de cooldown entre disparos
- Búsqueda de enemigo más cercano en rango
- Métodos abstractos para disparo

**Métodos Principales**:
- `update(float)`: Actualiza cooldown del arma
- `canFire()`: Verifica si puede disparar
- `tryFire(float, float, List<Enemy>)`: Intenta disparar (abstracto)
- `findClosestEnemy()`: Encuentra enemigo más cercano en rango

### 22. Pistol (NUEVO - Fase 5)
**Ubicación**: `com.atropellalo.game.weapon.Pistol`

**Responsabilidad**: Pistola automática del jugador.

**Características**:
- Dispara automáticamente al enemigo más cercano
- Rango configurable (250 px por defecto)
- Delay entre disparos (0.5 segundos)
- Daño por disparo (15 HP)

**Comportamiento**:
1. Busca enemigo más cercano dentro del rango
2. Si hay enemigo y el cooldown terminó, dispara
3. Crea proyectil hacia el centro del enemigo
4. Reinicia cooldown

### 23. Projectile (NUEVO - Fase 5)
**Ubicación**: `com.atropellalo.game.weapon.Projectile`

**Responsabilidad**: Proyectil disparado por armas.

**Características**:
- Movimiento en línea recta hacia objetivo
- Velocidad configurable (400 px/s)
- Rango máximo (se desactiva al excederlo)
- Colisión con enemigos
- Renderizado como círculo amarillo/naranja

**Métodos Principales**:
- `update(float)`: Actualiza posición, verifica límites
- `checkCollisions(List<Enemy>)`: Detecta impactos con enemigos
- `render(Graphics2D)`: Dibuja el proyectil

### 24. WeaponManager (NUEVO - Fase 5)
**Ubicación**: `com.atropellalo.game.weapon.WeaponManager`

**Responsabilidad**: Gestiona armas y proyectiles del jugador.

**Características**:
- Maneja arma actual (pistola por defecto)
- Lista de proyectiles activos
- Estadísticas de disparos y precisión
- Disparo automático integrado

**Métodos Principales**:
- `update(float, float, float, List<Enemy>)`: Actualiza arma y proyectiles
- `render(Graphics2D)`: Dibuja todos los proyectiles
- `getAccuracy()`: Calcula porcentaje de precisión

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

## Sistema de Enemigos 

### Tipos de Zombies

#### Zombie Rápido (FastZombie)
- **Apariencia**: Círculo verde claro con ojos rojos
- **Velocidad**: 150 px/s (rápido)
- **Salud**: 30 HP (baja)
- **Daño**: 8 HP por contacto
- **Tamaño**: 24 px (pequeño)
- **Probabilidad de spawn**: 50%

#### Zombie Lento (SlowZombie)
- **Apariencia**: Cuadrado redondeado púrpura con ojos amarillos
- **Velocidad**: 50 px/s (lento)
- **Salud**: 100 HP (alta)
- **Daño**: 15 HP por contacto
- **Tamaño**: 36 px (grande)
- **Probabilidad de spawn**: 30%

#### Zombie Explosivo (ExplosiveZombie)
- **Apariencia**: Triángulo naranja con símbolo "!"
- **Velocidad**: 80 px/s (media)
- **Salud**: 40 HP (media)
- **Daño contacto**: 10 HP
- **Daño explosión**: 25 HP máximo (disminuye con distancia)
- **Radio de explosión**: 80 px
- **Tamaño**: 28 px (medio)
- **Probabilidad de spawn**: 20%
- **Especial**: Al morir explota, dañando al jugador y otros enemigos

### Sistema de Oleadas

- **Primera oleada**: 3 segundos después de iniciar
- **Enemigos base**: 5 por oleada
- **Incremento**: +2 enemigos por oleada
- **Intervalo entre oleadas**: 15 segundos
- **Máximo enemigos**: 50 simultáneos
- **Spawn gradual**: 0.5 segundos entre cada enemigo
- **Distancia mínima spawn**: 300 px del jugador

### Mecánicas de Combate

#### Disparo Automático (NUEVO - Fase 5)
- El jugador dispara automáticamente al enemigo más cercano
- No requiere input del jugador
- El arma apunta y dispara sola
- Daño por proyectil: 15 HP
- Rango de disparo: 250 píxeles
- Delay entre disparos: 0.5 segundos
- Velocidad del proyectil: 400 px/s

#### Contacto (Enemigo → Jugador)
- Si el jugador colisiona con un enemigo, recibe daño
- Cooldown de 0.5 segundos entre daños
- Cada tipo de zombie tiene daño diferente

**Nota**: La mecánica de "atropellar" enemigos ha sido removida. El jugador ahora solo puede dañar enemigos con disparos.

#### Explosiones
- Solo el ExplosiveZombie explota al morir
- Radio de 80 px
- Daño decrece con la distancia
- Afecta al jugador Y a otros enemigos
- Puede causar reacciones en cadena

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
6. Actualizar sistema de loot (spawn, timers)
7. Verificar colisiones con loot
8. Aplicar efectos de loot recolectado
9. Actualizar sistema de enemigos (oleadas, movimiento, colisiones)
10. Actualizar sistema de armas (disparo automático al enemigo más cercano)
11. Actualizar posición de la cámara
12. Renderizar escena (mapa, loot, enemigos, proyectiles, jugador)
13. Renderizar HUD e info de oleadas
14. Sleep para mantener FPS objetivo

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
- **Enemigos**: Enemy (abstract), FastZombie, SlowZombie, ExplosiveZombie
- **Sistemas**: Camera, InputHandler, LootManager, EnemyManager, WeaponManager
- **Configuración**: GameConfig
- **Loot**: Loot (abstract), Fuel, Scrap, LootType
- **Armas**: Weapon (abstract), Pistol, Projectile, WeaponManager
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

### Template Method Pattern (Loot/Enemy)
Loot y Enemy definen el esqueleto para items/enemigos, las subclases implementan detalles.

### Manager Pattern (LootManager/EnemyManager)
Centraliza la gestión de entidades relacionadas.

### Callback Pattern (Explosiones)
ExplosiveZombie usa callback para notificar daño al jugador.

## Próximos Pasos Sugeridos (No Implementados)

Para continuar el desarrollo del juego estilo Survivor, se sugiere:

1. ~~**Enemigos**: Sistema de spawn y comportamiento de enemigos~~ ✅ COMPLETADO
2. ~~**Colisiones**: Detección de colisiones entre jugador y enemigos~~ ✅ COMPLETADO
3. ~~**Armas/Ataques**: Sistema de combate automático~~ ✅ COMPLETADO
4. **Más Armas**: Agregar variedad de armas (escopeta, rifle, etc.)
5. **Experiencia/Nivel**: Sistema de progresión
6. **Power-ups adicionales**: Mejoras temporales y habilidades
7. **Partículas**: Efectos visuales al recolectar loot y disparar
8. **Audio**: Música y efectos de sonido
9. **Sprite del jugador**: Reemplazar cuadrado con sprite animado
10. **Mapa mejorado**: Diseño de nivel más detallado
11. **Reinicio de partida**: Opción para reiniciar después del Game Over
12. **Mejoras de armas**: Sistema de upgrades para las armas

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

### Enemigos (NUEVO - Fase 4)
```java
// General
PLAYER_DAMAGE_TO_ENEMY = 50.0f;      // Daño al atropellar
ENEMY_DAMAGE_TO_PLAYER = 10.0f;      // Daño base de contacto
ENEMY_DAMAGE_COOLDOWN = 0.5f;        // Cooldown de daño (segundos)
ENEMY_COLLISION_DISTANCE = 25.0f;    // Distancia de colisión
ENEMY_MIN_SPAWN_DISTANCE = 300.0f;   // Distancia mínima de spawn

// Zombie Rápido
FAST_ZOMBIE_SPEED = 150.0f;          // Velocidad (px/s)
FAST_ZOMBIE_HEALTH = 30.0f;          // Salud
FAST_ZOMBIE_SIZE = 24;               // Tamaño
FAST_ZOMBIE_DAMAGE = 8.0f;           // Daño al jugador

// Zombie Lento
SLOW_ZOMBIE_SPEED = 50.0f;           // Velocidad (px/s)
SLOW_ZOMBIE_HEALTH = 100.0f;         // Salud
SLOW_ZOMBIE_SIZE = 36;               // Tamaño
SLOW_ZOMBIE_DAMAGE = 15.0f;          // Daño al jugador

// Zombie Explosivo
EXPLOSIVE_ZOMBIE_SPEED = 80.0f;      // Velocidad (px/s)
EXPLOSIVE_ZOMBIE_HEALTH = 40.0f;     // Salud
EXPLOSIVE_ZOMBIE_SIZE = 28;          // Tamaño
EXPLOSIVE_ZOMBIE_DAMAGE = 10.0f;     // Daño contacto
EXPLOSIVE_ZOMBIE_RADIUS = 80.0f;     // Radio de explosión
EXPLOSIVE_ZOMBIE_EXPLOSION_DAMAGE = 25.0f; // Daño explosión

// Sistema de Oleadas
WAVE_INTERVAL = 15.0f;               // Segundos entre oleadas
WAVE_BASE_ENEMIES = 5;               // Enemigos base por oleada
WAVE_ENEMY_INCREMENT = 2;            // Incremento por oleada
MAX_ENEMIES_ON_MAP = 50;             // Máximo simultáneo
ENEMY_SPAWN_INTERVAL = 0.5f;         // Segundos entre spawns

// Probabilidades de spawn (deben sumar 100)
FAST_ZOMBIE_SPAWN_CHANCE = 50;       // 50% rápidos
SLOW_ZOMBIE_SPAWN_CHANCE = 30;       // 30% lentos
EXPLOSIVE_ZOMBIE_SPAWN_CHANCE = 20;  // 20% explosivos
```

### Armas - Pistola (NUEVO - Fase 5)
```java
PISTOL_DAMAGE = 15.0f;          // Daño por disparo
PISTOL_RANGE = 250.0f;          // Rango de alcance (px)
PISTOL_FIRE_DELAY = 0.5f;       // Segundos entre disparos
PROJECTILE_SPEED = 400.0f;      // Velocidad del proyectil (px/s)
PROJECTILE_SIZE = 8;            // Tamaño del proyectil (px)
```

**Nota**: Las configuraciones de enemigos usan `static` (no `static final`) para permitir modificación en tiempo real durante pruebas.

## Fase 6: Sistema de XP, Niveles y Mejoras (NUEVO)

### Resumen
Implementación completa del sistema estilo Vampire Survivors con orbes de XP, subida de nivel con menú de mejoras y nuevas armas.

### Sistema de XP y Niveles

#### XPOrb (NUEVO)
**Ubicación**: `com.atropellalo.game.loot.XPOrb`

**Responsabilidad**: Orbes de experiencia que caen de enemigos.

**Características**:
- Hereda de `Loot`
- Atracción magnética hacia el jugador
- Distancia de atracción: 100 px
- Velocidad de atracción: 300 px/s
- Renderizado con gradiente radial (verde/amarillo)
- Cada tipo de enemigo da diferente XP

#### Sistema de Niveles (Player)
**Nuevos campos en Player**:
- `currentXP`: Experiencia actual
- `level`: Nivel del jugador (comienza en 1)
- `xpToNextLevel`: XP necesario para subir (100 base, +50 por nivel)
- `speed`: Velocidad del jugador (mejora con upgrades)

**Callback de nivel**:
```java
public interface LevelUpCallback {
    void onLevelUp(int newLevel);
}
```

**Fórmula de XP**:
- Nivel 1 → 2: 100 XP
- Nivel 2 → 3: 150 XP
- Nivel 3 → 4: 200 XP
- Fórmula: `100 + (level - 1) * 50`

#### XP por Enemigo (GameConfig)
```java
XP_FAST_ZOMBIE = 10;      // Zombie rápido
XP_SLOW_ZOMBIE = 25;      // Zombie lento
XP_EXPLOSIVE_ZOMBIE = 15; // Zombie explosivo
```

### Sistema de Mejoras (Upgrades)

#### UpgradeType (NUEVO)
**Ubicación**: `com.atropellalo.game.upgrade.UpgradeType`

**Valores**:
- `VEHICLE_HEALTH`: Mejora salud máxima (+20 HP)
- `VEHICLE_SPEED`: Mejora velocidad (+15%)
- `NEW_WEAPON`: Añade nueva arma (máx 3)
- `WEAPON_DAMAGE`: Mejora daño del arma (+20%)
- `WEAPON_FIRE_RATE`: Mejora cadencia (+15%)
- `WEAPON_IMPACT_AREA`: Mejora área de impacto (+25%)
- `WEAPON_PROJECTILE_COUNT`: Añade proyectil (+1)

#### UpgradeOption (NUEVO)
**Ubicación**: `com.atropellalo.game.upgrade.UpgradeOption`

**Responsabilidad**: Representa una opción de mejora.

**Campos**:
- `type`: Tipo de mejora
- `title`: Título para mostrar
- `description`: Descripción detallada
- `weaponType`: Tipo de arma (opcional, para NEW_WEAPON o upgrades de arma)

#### UpgradeManager (NUEVO)
**Ubicación**: `com.atropellalo.game.upgrade.UpgradeManager`

**Responsabilidad**: Genera y aplica mejoras.

**Métodos Principales**:
- `generateOptions(Player, WeaponManager)`: Genera 3 opciones aleatorias
- `applyUpgrade(UpgradeOption, Player, WeaponManager)`: Aplica mejora seleccionada

**Lógica de Generación**:
1. Siempre incluye mejoras de vehículo (salud, velocidad)
2. Si hay espacio para armas (< 3), incluye nuevas armas
3. Incluye mejoras para armas equipadas
4. Selecciona 3 aleatorias sin repetir

#### UpgradeMenu (NUEVO)
**Ubicación**: `com.atropellalo.game.ui.UpgradeMenu`

**Responsabilidad**: Menú visual de selección de mejoras.

**Características**:
- Overlay semi-transparente
- 3 tarjetas de opción
- Navegación con teclado (A/D o ←/→)
- Selección con Enter, Espacio o 1/2/3
- Tarjeta seleccionada resaltada en verde
- Pausa el juego mientras está visible

**Callback**:
```java
public interface UpgradeSelectedCallback {
    void onUpgradeSelected(UpgradeOption option);
}
```

### Nuevas Armas

#### WeaponType (NUEVO)
**Ubicación**: `com.atropellalo.game.weapon.WeaponType`

**Valores**:
- `PISTOL`: Pistola base
- `LIGHT_MACHINE_GUN`: Ametralladora ligera
- `GRENADE_LAUNCHER`: Lanzagranadas
- `SPIKES`: Púas alrededor del vehículo
- `FLAMETHROWER`: Lanzallamas

#### LightMachineGun (NUEVO)
**Ubicación**: `com.atropellalo.game.weapon.LightMachineGun`

**Características**:
- Alta cadencia de fuego (5 disparos/s)
- Daño bajo por proyectil (8 HP)
- 2 proyectiles por disparo
- Proyectiles naranjas/rojos
- Rango: 300 px

**Configuración** (GameConfig):
```java
LMG_DAMAGE = 8.0f;
LMG_RANGE = 300.0f;
LMG_FIRE_DELAY = 0.2f;
LMG_PROJECTILE_COUNT = 2;
LMG_PROJECTILE_SPEED = 500.0f;
```

#### GrenadeLauncher (NUEVO)
**Ubicación**: `com.atropellalo.game.weapon.GrenadeLauncher`

**Características**:
- Bajo ratio de fuego (0.5 disparos/s)
- Alto daño (30 HP directo)
- Daño en área (50 px de radio)
- Proyectiles grandes y lentos
- Color gris/verde oscuro

**Configuración** (GameConfig):
```java
GRENADE_DAMAGE = 30.0f;
GRENADE_RANGE = 350.0f;
GRENADE_FIRE_DELAY = 2.0f;
GRENADE_IMPACT_AREA = 50.0f;
GRENADE_PROJECTILE_SPEED = 250.0f;
```

#### Spikes (NUEVO)
**Ubicación**: `com.atropellalo.game.weapon.Spikes`

**Características**:
- Arma pasiva (no dispara proyectiles)
- Daño continuo a enemigos cercanos
- 8 púas alrededor del jugador
- Radio de daño: 60 px
- Daño por segundo: 20 HP

**Configuración** (GameConfig):
```java
SPIKES_DAMAGE = 20.0f;
SPIKES_RANGE = 60.0f;
SPIKES_DAMAGE_INTERVAL = 0.5f;
```

**Métodos Especiales**:
- `processContinuousDamage(List<Enemy>, float, float)`: Daña enemigos en rango
- `render(Graphics2D, float, float)`: Dibuja púas alrededor del jugador

#### Flamethrower (NUEVO)
**Ubicación**: `com.atropellalo.game.weapon.Flamethrower`

**Características**:
- Arma de daño continuo en cono
- No dispara proyectiles tradicionales
- Daño en área cónica frente al jugador
- Partículas de fuego animadas
- Ángulo de cono: 45°
- Rango: 120 px

**Configuración** (GameConfig):
```java
FLAMETHROWER_DAMAGE = 25.0f;
FLAMETHROWER_RANGE = 120.0f;
FLAMETHROWER_DAMAGE_INTERVAL = 0.1f;
FLAMETHROWER_CONE_ANGLE = 45.0f;
```

**Métodos Especiales**:
- `processContinuousDamage(List<Enemy>, float, float)`: Daña enemigos en cono
- `render(Graphics2D, float, float)`: Dibuja llamas con partículas

### Actualizaciones a Clases Existentes

#### Weapon (Actualizado)
**Nuevos campos**:
- `weaponType`: Tipo de arma
- `projectileCount`: Proyectiles por disparo
- `impactArea`: Radio de daño en área

**Nuevos métodos**:
- `upgradeDamage()`: Mejora daño 20%
- `upgradeFireRate()`: Mejora cadencia 15%
- `upgradeImpactArea()`: Mejora área 25%
- `upgradeProjectileCount()`: Añade 1 proyectil

**Cambio en tryFire()**:
- Ahora retorna `List<Projectile>` en lugar de un solo proyectil

#### Projectile (Actualizado)
**Nuevos campos**:
- `impactArea`: Radio de explosión
- `speed`: Velocidad personalizable
- `color`: Color del proyectil
- `size`: Tamaño del proyectil

**Nuevo método**:
- `applyAreaDamage(List<Enemy>)`: Aplica daño en área al impactar

**Nuevos constructores**:
- Constructor completo con todos los parámetros
- Constructor simplificado compatible con versión anterior

#### WeaponManager (Actualizado)
**Cambios**:
- De `Weapon weapon` a `List<Weapon> weapons`
- Máximo 3 armas simultáneas

**Nuevos métodos**:
- `addWeapon(Weapon)`: Añade arma si hay espacio
- `hasWeapon(WeaponType)`: Verifica si tiene un tipo de arma
- `getWeapon(WeaponType)`: Obtiene arma por tipo
- `canAddWeapon()`: Verifica si puede añadir más armas
- `getAvailableWeaponTypes()`: Lista armas no equipadas

**Actualización de render()**:
- Nueva firma: `render(Graphics2D, float, float)` para armas que dibujan en posición del jugador

#### Player (Actualizado)
**Nuevos campos**:
- `currentXP`: Experiencia actual
- `level`: Nivel (comienza en 1)
- `xpToNextLevel`: XP para siguiente nivel
- `speed`: Velocidad (mejora con upgrades)
- `levelUpCallback`: Callback al subir de nivel

**Nuevos métodos**:
- `addXP(int)`: Añade XP, retorna true si sube de nivel
- `setLevelUpCallback(LevelUpCallback)`: Configura callback
- `upgradeMaxHealth()`: Mejora salud máxima
- `upgradeSpeed()`: Mejora velocidad
- Getters para XP, nivel, etc.

#### GameHUD (Actualizado)
**Nuevo elemento**:
- Barra de XP encima de las barras existentes
- Muestra nivel actual
- Barra de progreso hacia siguiente nivel
- Color púrpura/magenta

#### LootManager (Actualizado)
**Nuevos campos**:
- `List<XPOrb> xpOrbs`: Lista separada de orbes de XP

**Nuevos métodos**:
- `spawnXPOrb(float, float, int)`: Crea orbe de XP en posición

**Actualización de update()**:
- Nueva firma: `update(float, float, float)` para atracción magnética
- Actualiza orbes de XP con posición del jugador

#### EnemyManager (Actualizado)
**Nuevos campos**:
- `LootManager lootManager`: Referencia para spawn de XP

**Nuevos métodos**:
- `setLootManager(LootManager)`: Configura el loot manager
- `spawnXPForEnemy(Enemy)`: Genera XP según tipo de enemigo

**Actualización de cleanupDeadEnemies()**:
- Llama a `spawnXPForEnemy()` antes de eliminar enemigo muerto

#### LootType (Actualizado)
**Nuevo valor**:
- `XP_ORB`: Orbe de experiencia

#### GamePanel (Actualizado)
**Nuevos campos**:
- `UpgradeManager upgradeManager`
- `UpgradeMenu upgradeMenu`
- `boolean paused`

**Nuevas integraciones**:
- Conecta `enemyManager.setLootManager(lootManager)`
- Configura `player.setLevelUpCallback()`
- Configura `upgradeMenu.setCallback()`
- Implementa `KeyListener` para menú de mejoras

**Nuevos métodos**:
- `onPlayerLevelUp(int)`: Pausa juego, muestra menú
- `onUpgradeSelected(UpgradeOption)`: Aplica mejora, reanuda juego

**Actualización de update()**:
- No actualiza si `paused` es true
- Pasa posición del jugador a `lootManager.update()`

**Actualización de paintComponent()**:
- Renderiza `upgradeMenu` si está visible
- Pasa posición del jugador a `weaponManager.render()`

**Manejo de XP en applyLootEffect()**:
- Detecta `LootType.XP_ORB`
- Extrae valor de XP del orbe
- Llama a `player.addXP()`

### Flujo del Sistema

```
Enemigo muere
    ↓
EnemyManager.cleanupDeadEnemies()
    ↓
spawnXPForEnemy() → LootManager.spawnXPOrb()
    ↓
XPOrb aparece en el mapa
    ↓
LootManager.update() → XPOrb atracción magnética
    ↓
LootManager.checkCollisions() → Jugador recoge orbe
    ↓
GamePanel.applyLootEffect() → player.addXP()
    ↓
[Si alcanza XP necesario]
    ↓
Player.addXP() retorna true + callback
    ↓
GamePanel.onPlayerLevelUp()
    ↓
paused = true
    ↓
UpgradeManager.generateOptions()
    ↓
UpgradeMenu.show()
    ↓
[Jugador selecciona opción]
    ↓
UpgradeMenu callback → GamePanel.onUpgradeSelected()
    ↓
UpgradeManager.applyUpgrade()
    ↓
paused = false
    ↓
Juego continúa
```

### Configuración de Mejoras (GameConfig)
```java
// Mejoras de vehículo
UPGRADE_HEALTH_AMOUNT = 20.0f;      // +20 HP máximo
UPGRADE_SPEED_AMOUNT = 0.15f;       // +15% velocidad

// Mejoras de armas (multiplicadores)
UPGRADE_WEAPON_DAMAGE_FACTOR = 0.2f;         // +20% daño
UPGRADE_WEAPON_FIRE_RATE_FACTOR = 0.15f;     // +15% cadencia
UPGRADE_WEAPON_IMPACT_AREA_FACTOR = 0.25f;   // +25% área
UPGRADE_WEAPON_PROJECTILE_COUNT = 1;         // +1 proyectil

// Límites
MAX_WEAPONS = 3;                    // Máximo de armas
```

### Controles del Menú de Mejoras
- **A / ←**: Mover selección a la izquierda
- **D / →**: Mover selección a la derecha
- **Enter / Espacio**: Confirmar selección
- **1 / 2 / 3**: Selección directa de opción

---

## Fase 7: Mejoras de Armas y Escalado de Enemigos (NUEVO)

### Resumen
Implementación de nueva arma escopeta, cambio de púas por sierras circulares, lanzagranadas aleatorio con efectos de explosión, escalado de enemigos por oleada, y bonificación de combustible al subir de nivel.

### Nueva Arma: Escopeta (Shotgun)

**Ubicación**: `com.atropellalo.game.weapon.Shotgun`

**Características**:
- Alto daño por proyectil (25 HP)
- Rango corto (150 px)
- 5 proyectiles por disparo
- Dispersión en área del 15% (~54°)
- Cadencia media (1 disparo/segundo)

**Configuración** (GameConfig):
```java
SHOTGUN_DAMAGE = 25.0f;           // Daño alto
SHOTGUN_RANGE = 150.0f;           // Rango corto
SHOTGUN_FIRE_DELAY = 1.0f;        // Cadencia media
SHOTGUN_PROJECTILE_COUNT = 5;     // 5 perdigones
SHOTGUN_SPREAD_ANGLE = 54.0f;     // 15% de 360°
SHOTGUN_PROJECTILE_SPEED = 350.0f;
```

### Sierras Circulares (Reemplazo de Púas)

**Ubicación**: `com.atropellalo.game.weapon.CircularSaw`

**Cambio**: Las púas (Spikes) fueron reemplazadas por sierras circulares.

**Características**:
- Dos sierras, una a cada lado del jugador
- Rotación constante visual
- Daño por contacto continuo
- Dientes triangulares animados

**Configuración** (GameConfig):
```java
SAW_DAMAGE = 12.0f;              // Daño por contacto
SAW_RADIUS = 25.0f;              // Radio de cada sierra
SAW_DISTANCE = 45.0f;            // Distancia desde el jugador
SAW_DAMAGE_COOLDOWN = 0.25f;     // Cooldown de daño
SAW_ROTATION_SPEED = 10.0f;      // Velocidad de rotación (rad/s)
```

**Renderizado**:
- Disco base metálico
- 12 dientes triangulares
- Centro oscuro con agujero
- Rotación continua animada

### Lanzagranadas Aleatorio

**Cambios en GrenadeLauncher**:
- Ya NO apunta automáticamente a enemigos
- Dispara en direcciones ALEATORIAS
- Solo dispara si hay enemigos en el mapa

**Nueva clase GrenadeProjectile**:
**Ubicación**: `com.atropellalo.game.weapon.GrenadeProjectile`

**Efecto de explosión visual**:
- Fase de expansión (0-50% duración)
  - Círculos concéntricos de colores
  - Amarillo → Naranja → Rojo → Humo
  - Destello blanco central
- Fase de disipación (50-100% duración)
  - Humo que se expande
  - Fuego residual que se desvanece
- Duración total: 0.5 segundos

### Escalado de Enemigos por Oleada

**Sistema de escalado progresivo**:
Los enemigos se vuelven más fuertes con cada oleada. Los 4 atributos escalan simultáneamente:

```java
WAVE_HEALTH_SCALING = 1.05f;   // +5% vida por oleada
WAVE_SPEED_SCALING = 1.02f;    // +2% velocidad por oleada
WAVE_DAMAGE_SCALING = 1.03f;   // +3% daño por oleada
WAVE_XP_SCALING = 1.05f;       // +5% XP por oleada
```

**Fórmula de escalado**:
```
statFinal = statBase × (factorEscalado ^ (oleada - 1))
```

**Ejemplo oleada 5**:
- Vida: base × 1.05^4 = base × 1.216 (+21.6%)
- Velocidad: base × 1.02^4 = base × 1.082 (+8.2%)
- Daño: base × 1.03^4 = base × 1.126 (+12.6%)
- XP: base × 1.05^4 = base × 1.216 (+21.6%)

**Implementación**:
- `Enemy` tiene nuevo campo `xpMultiplier`
- Constructor con factores de escalado
- `EnemyManager.createEnemy()` calcula escalado
- `spawnXPForEnemy()` aplica escalado de XP

### Proyectiles Múltiples Sin Dispersión

**Cambio importante**: El multi-disparo ya NO dispersa proyectiles.

Armas afectadas:
- **Pistola**: Todos los proyectiles van al mismo objetivo
- **Ametralladora Ligera**: Todos en la misma dirección
- **Escopeta**: Mantiene su dispersión intencional (es su característica)

**Motivo**: Aumentar la cantidad de proyectiles debe aumentar el DPS, no dispersar el daño.

### Bonificación de Combustible al Subir de Nivel

**Configuración**:
```java
LEVEL_UP_FUEL_BONUS = 10.0f;   // +10 combustible
```

**Implementación en Player.levelUp()**:
```java
// Bonificación de combustible al subir de nivel
addFuel(GameConfig.LEVEL_UP_FUEL_BONUS);
```

### Actualizaciones de Archivos

#### Nuevos Archivos
- `Shotgun.java` - Nueva arma escopeta
- `CircularSaw.java` - Reemplazo de Spikes
- `GrenadeProjectile.java` - Proyectil con explosión visual

#### Archivos Eliminados
- `Spikes.java` - Reemplazado por CircularSaw

#### Archivos Modificados

**GameConfig.java**:
- Añadidas configs de escopeta (SHOTGUN_*)
- Añadidas configs de sierras (SAW_*)
- Añadidos factores de escalado (WAVE_*_SCALING)
- Añadido LEVEL_UP_FUEL_BONUS

**WeaponType.java**:
- Eliminado: `SPIKES`
- Añadido: `CIRCULAR_SAW`, `SHOTGUN`

**Enemy.java**:
- Nuevo campo: `xpMultiplier`
- Nuevo constructor con factores de escalado
- Nuevo getter: `getXPMultiplier()`

**FastZombie.java, SlowZombie.java, ExplosiveZombie.java**:
- Nuevo constructor con factores de escalado

**EnemyManager.java**:
- `createEnemy()` aplica factores de escalado
- `spawnXPForEnemy()` escala XP por oleada

**Pistol.java, LightMachineGun.java**:
- Proyectiles múltiples van en la misma dirección

**GrenadeLauncher.java**:
- Disparo aleatorio (no apunta)
- Usa GrenadeProjectile

**Player.java**:
- `levelUp()` otorga combustible bonus

**WeaponManager.java**:
- Crea CircularSaw y Shotgun

**UpgradeManager.java**:
- Referencias actualizadas de SPIKES a CIRCULAR_SAW

### Configuración Completa de Escalado

```java
// ==================== ESCALADO DE OLEADAS ====================
WAVE_HEALTH_SCALING = 1.05f;    // Factor de escalado de vida
WAVE_SPEED_SCALING = 1.02f;     // Factor de escalado de velocidad
WAVE_DAMAGE_SCALING = 1.03f;    // Factor de escalado de daño
WAVE_XP_SCALING = 1.05f;        // Factor de escalado de XP

// ==================== BONIFICACIONES DE NIVEL ====================
LEVEL_UP_FUEL_BONUS = 10.0f;    // Combustible al subir de nivel
```

---

**Fecha de Creación**: 29/11/2025  
**Última Actualización**: 29/11/2025  
**Versión**: 1.0-SNAPSHOT  
**Estado**: Fase 7 Completada - Escopeta, Sierras, Escalado de Enemigos
