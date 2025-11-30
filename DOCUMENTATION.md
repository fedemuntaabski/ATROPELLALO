# Documentación del Proyecto - Atropellalo Game

## Información General
- **Nombre del Proyecto**: Atropellalo
- **Tipo**: Juego estilo Survivor (inspirado en Vampire Survivors)
- **Lenguaje**: Java 11
- **Framework UI**: Swing
- **Build Tool**: Maven
- **Estado**: Fase 14 Completada - Oleadas Continuas, Multi-Objetivo y Mejora de Ángulo

---

## Tabla de Contenidos
1. [Pautas de Desarrollo](#pautas-de-desarrollo)
2. [Estructura del Proyecto](#estructura-del-proyecto)
3. [Arquitectura del Sistema](#arquitectura-del-sistema)
4. [Clases Principales](#clases-principales)
5. [Sistema de Juego](#sistema-de-juego)
6. [Sistema de Enemigos y Jefes](#sistema-de-enemigos-y-jefes)
7. [Sistema de Armas](#sistema-de-armas)
8. [Sistema de Mejoras](#sistema-de-mejoras)
9. [Mapa Urbano](#mapa-urbano)
10. [Configuración](#configuración)
11. [Cómo Ejecutar](#cómo-ejecutar)

---

## Pautas de Desarrollo

### 1. Mejores Prácticas de Programación
- Uso de convenciones de nombres Java estándar (camelCase para métodos/variables, PascalCase para clases)
- Encapsulamiento adecuado de propiedades (campos privados con getters/setters)
- Manejo de excepciones con logging estructurado
- Uso de constantes para valores configurables (centralizados en GameConfig)
- Documentación JavaDoc en todas las clases públicas

### 2. Bajo Acoplamiento y Alta Cohesión
- **Game**: Punto de entrada con única responsabilidad de iniciar la aplicación
- **GameWindow**: Responsable solo de la configuración de la ventana
- **GamePanel**: Responsable del game loop y coordinación de sistemas
- **Managers**: Cada manager gestiona un dominio específico (armas, enemigos, loot, upgrades)
- Cada clase tiene una responsabilidad específica y bien definida
- Comunicación entre sistemas mediante interfaces y callbacks

### 3. KISS (Keep It Simple, Stupid)
- Implementación directa sin complejidad innecesaria
- Uso de Swing estándar sin frameworks adicionales
- Carga simple de recursos desde classpath
- Pathfinding basado en detección de bloqueo y evasión simple

### 4. DRY (Don't Repeat Yourself)
- Constantes definidas para valores reutilizables en GameConfig
- Métodos separados para cada funcionalidad específica
- Logging centralizado mediante java.util.logging
- Clase base Enemy abstracta para todos los tipos de enemigos
- Clase base Weapon abstracta para todas las armas
- Clase base Loot abstracta para todos los items

### 5. Definition of Done (DoD)
- Solo se implementa lo solicitado en cada fase
- No se agregan características adicionales no requeridas
- Código listo para ejecutar y probar
- Documentación actualizada después de cada fase

### 6. Nivel Profesional
- Código documentado con JavaDoc
- Manejo de errores con logging apropiado
- Estructura de proyecto Maven estándar
- Patrones de diseño aplicados (Template Method, Observer, Manager, Callback)

---

## Estructura del Proyecto

```
atropellalo/
├── pom.xml                                    # Configuración Maven
├── DOCUMENTATION.md                           # Este archivo
├── README.md                                  # Descripción del proyecto
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
        │               │   └── GameConfig.java        # Configuración centralizada
        │               ├── enemy/
        │               │   ├── Enemy.java             # Clase base abstracta
        │               │   ├── EnemyType.java         # Enum de tipos
        │               │   ├── FastZombie.java        # Zombie rápido
        │               │   ├── SlowZombie.java        # Zombie lento
        │               │   ├── ExplosiveZombie.java   # Zombie explosivo
        │               │   ├── BruiserBoss.java       # Jefe Oleada 5
        │               │   ├── InfectorBoss.java      # Jefe Oleada 10
        │               │   └── EnemyManager.java      # Gestor de oleadas (spawn en bordes)
        │               ├── entity/
        │               │   └── Player.java            # Jugador
        │               ├── input/
        │               │   └── InputHandler.java      # Manejo de teclado
        │               ├── loot/
        │               │   ├── Loot.java              # Clase base abstracta
        │               │   ├── LootType.java          # Enum de tipos
        │               │   ├── Fuel.java              # Combustible
        │               │   ├── Scrap.java             # Chatarra (curación)
        │               │   ├── XPOrb.java             # Orbe de experiencia
        │               │   └── LootManager.java       # Gestor de loot
        │               ├── map/
        │               │   ├── Building.java          # Edificio con colisión
        │               │   ├── CityBlock.java         # Manzana urbana
        │               │   └── CityMap.java           # Mapa procedural
        │               ├── sprite/                    # (Reservado para sprites)
        │               ├── ui/
        │               │   ├── GameWindow.java        # Ventana principal
        │               │   ├── GamePanel.java         # Panel con game loop
        │               │   ├── GameHUD.java           # HUD del jugador
        │               │   └── UpgradeMenu.java       # Menú de mejoras
        │               ├── upgrade/
        │               │   ├── UpgradeType.java       # Enum de mejoras
        │               │   ├── UpgradeOption.java     # Opción de mejora
        │               │   └── UpgradeManager.java    # Gestor de mejoras
        │               ├── util/
        │               │   └── MapGenerator.java      # Generador de mapas
        │               └── weapon/
        │                   ├── Weapon.java            # Clase base abstracta (multi-target)
        │                   ├── WeaponType.java        # Enum de armas (6 tipos)
        │                   ├── Pistol.java            # Pistola (inicial)
        │                   ├── LightMachineGun.java   # Ametralladora
        │                   ├── Shotgun.java           # Escopeta
        │                   ├── GrenadeLauncher.java   # Lanzagranadas
        │                   ├── Flamethrower.java      # Lanzallamas (con ángulo mejorable)
        │                   ├── SniperRailgun.java     # Rifle de francotirador
        │                   ├── Projectile.java        # Proyectil base
        │                   ├── PenetratingProjectile.java # Proyectil penetrante
        │                   ├── GrenadeProjectile.java # Granada con explosión
        │                   └── WeaponManager.java     # Gestor de armas
        └── resources/
            └── images/
                └── (recursos gráficos)
```

---

## Arquitectura del Sistema

### Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────┐
│                          Game (Main)                             │
│                              │                                   │
│                        GameWindow                                │
│                              │                                   │
│  ┌───────────────────────────┴───────────────────────────────┐  │
│  │                       GamePanel                            │  │
│  │  ┌─────────────────────────────────────────────────────┐  │  │
│  │  │                    Game Loop                         │  │  │
│  │  │  update(deltaTime) → repaint()                       │  │  │
│  │  └─────────────────────────────────────────────────────┘  │  │
│  │                           │                                │  │
│  │  ┌────────────┬────────────┼────────────┬────────────┐    │  │
│  │  │            │            │            │            │    │  │
│  │  ▼            ▼            ▼            ▼            ▼    │  │
│  │ Player    Camera     InputHandler   CityMap      GameHUD  │  │
│  │  │                                     │                  │  │
│  │  └──────────┬──────────────────────────┘                  │  │
│  │             │                                              │  │
│  │  ┌──────────┴──────────┐                                  │  │
│  │  ▼                     ▼                                  │  │
│  │ EnemyManager      LootManager                             │  │
│  │  │                     │                                  │  │
│  │  │                     ├── Fuel                           │  │
│  │  │                     ├── Scrap                          │  │
│  │  │                     └── XPOrb                          │  │
│  │  │                                                        │  │
│  │  ├── FastZombie                                           │  │
│  │  ├── SlowZombie                                           │  │
│  │  ├── ExplosiveZombie                                      │  │
│  │  ├── BruiserBoss                                          │  │
│  │  └── InfectorBoss                                         │  │
│  │                                                            │  │
│  │  WeaponManager ─────────────────────────────────────────  │  │
│  │  │                                                        │  │
│  │  ├── Pistol          ├── Shotgun        ├── SniperRailgun  │  │
│  │  ├── LightMachineGun ├── GrenadeLauncher└── Flamethrower   │  │
│  │  └── Projectiles                                          │  │
│  │                                                            │  │
│  │  UpgradeManager + UpgradeMenu                             │  │
│  └────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Patrones de Diseño Utilizados

| Patrón | Uso |
|--------|-----|
| **Template Method** | `Enemy`, `Weapon`, `Loot` - clases abstractas con comportamiento común |
| **Observer/Callback** | `LevelUpCallback`, `RestartCallback`, `BossDamageCallback`, `ExplosionCallback` |
| **Manager** | `EnemyManager`, `WeaponManager`, `LootManager`, `UpgradeManager` |
| **Factory** | `WeaponManager.createWeapon()`, `EnemyManager.createEnemy()` |
| **Game Loop** | `GamePanel.run()` con delta time |
| **Strategy** | Diferentes comportamientos de armas y enemigos |

---

## Clases Principales

### Game.java
**Ubicación**: `com.atropellalo.Game`

Punto de entrada de la aplicación. Usa `SwingUtilities.invokeLater()` para thread-safety de Swing.

### GameWindow.java
**Ubicación**: `com.atropellalo.game.ui.GameWindow`

Ventana principal del juego.

| Propiedad | Valor |
|-----------|-------|
| Dimensiones | 1280x720 píxeles |
| Redimensionable | No |
| Close Operation | EXIT_ON_CLOSE |

### GamePanel.java
**Ubicación**: `com.atropellalo.game.ui.GamePanel`

Panel principal con el game loop.

**Responsabilidades**:
- Game loop a 60 FPS con delta time
- Coordinación de todos los sistemas
- Renderizado con transformación de cámara
- Manejo de input para menús y reinicio

**Flujo del Game Loop**:
```
1. Calcular deltaTime
2. Si jugador vivo y no pausado:
   a. Actualizar input → Player.setMovement()
   b. Player.update()
   c. LootManager.update()
   d. Verificar colisiones con loot
   e. EnemyManager.update()
   f. WeaponManager.update()
   g. Camera.centerOn(player)
3. repaint() → paintComponent()
4. Sleep para mantener FPS
```

### Player.java
**Ubicación**: `com.atropellalo.game.entity.Player`

Representa al jugador con sistemas de salud, combustible y experiencia.

**Stats Principales**:

| Stat | Valor Default | Descripción |
|------|---------------|-------------|
| Salud Máxima | 100 | Vida del jugador |
| Combustible Máximo | 100 | Recurso para moverse |
| Velocidad | 200 px/s | Velocidad de movimiento |
| Tamaño | 32 px | Tamaño del sprite |
| Consumo Combustible | 3/s | Gasto al moverse |

**Sistema de Niveles**:
- XP base para nivel 2: 100
- Escalado: `XP_requerido = 100 * 1.2^(nivel-1)`
- Al subir de nivel: +10 combustible bonus + menú de mejoras

**Callbacks**:
- `LevelUpCallback`: Notifica cuando sube de nivel
- `CollisionCallback`: Verifica colisiones con edificios

### Camera.java
**Ubicación**: `com.atropellalo.game.camera.Camera`

Sistema de cámara que sigue al jugador.

**Características**:
- Viewport: 1280x720
- Centrada en el jugador
- Limitada a bordes del mundo
- Offset calculado para renderizado

### InputHandler.java
**Ubicación**: `com.atropellalo.game.input.InputHandler`

Manejo de teclado WASD.

**Controles**:

| Tecla | Acción | Contexto |
|-------|--------|----------|
| W | Mover arriba | Juego |
| A | Mover izquierda | Juego |
| S | Mover abajo | Juego |
| D | Mover derecha | Juego |
| 1/2/3 | Seleccionar mejora | Menú de nivel |
| Enter/Espacio | Confirmar mejora | Menú de nivel |
| R | Reiniciar partida | Game Over |

---

## Sistema de Enemigos y Jefes

### EnemyManager.java
**Ubicación**: `com.atropellalo.game.enemy.EnemyManager`

Gestiona oleadas y spawn de enemigos.

**Sistema de Oleadas**:

| Parámetro | Valor |
|-----------|-------|
| Primera oleada | 3 segundos |
| Intervalo entre oleadas | 5 segundos |
| Enemigos base | 10 por oleada |
| Incremento | +5 por oleada |
| Máximo simultáneo | 100 enemigos |
| Spawn interval | 0.3 segundos |

**Escalado por Oleada**:

| Factor | Valor | Efecto |
|--------|-------|--------|
| Salud | 1.05x | +5% por oleada |
| Velocidad | 1.02x | +2% por oleada |
| Daño | 1.03x | +3% por oleada |
| XP | 1.05x | +5% por oleada |

### Tipos de Enemigos

#### FastZombie (Zombie Rápido)

| Stat | Valor |
|------|-------|
| Velocidad | 150 px/s |
| Salud | 30 HP |
| Daño | 8 HP |
| Tamaño | 24 px |
| Spawn Chance | 50% |
| XP | 10 |

**Visual**: Círculo verde claro con ojos rojos.

#### SlowZombie (Zombie Lento)

| Stat | Valor |
|------|-------|
| Velocidad | 50 px/s |
| Salud | 100 HP |
| Daño | 15 HP |
| Tamaño | 36 px |
| Spawn Chance | 30% |
| XP | 25 |

**Visual**: Cuadrado redondeado púrpura con ojos amarillos.

#### ExplosiveZombie (Zombie Explosivo)

| Stat | Valor |
|------|-------|
| Velocidad | 80 px/s |
| Salud | 40 HP |
| Daño contacto | 10 HP |
| Daño explosión | 25 HP |
| Radio explosión | 80 px |
| Tamaño | 28 px |
| Spawn Chance | 20% |
| XP | 20 |

**Visual**: Triángulo naranja con símbolo "!".
**Especial**: Al morir explota, dañando al jugador y otros enemigos (reacciones en cadena).

### Jefes

#### BruiserBoss (El Aplastador) - Oleada 5

| Stat | Valor |
|------|-------|
| Salud | 800 HP |
| Velocidad | 60 px/s |
| Daño contacto | 35 HP |
| Tamaño | 74 px |
| XP | 550 |

**Habilidades**:

| Habilidad | Descripción | Cooldown |
|-----------|-------------|----------|
| Terremoto | Onda de choque circular (150 px, 30 daño) | 4 segundos |
| Carga | Embiste al jugador (400 px/s, 40 daño, 2s duración) | 6 segundos |

**Visual**: Zombie gigante con torso acorazado y brazo hipertrofiado.

#### InfectorBoss (El Infectador) - Oleada 10

| Stat | Valor |
|------|-------|
| Salud | 650 HP |
| Velocidad | 80 px/s |
| Daño contacto | 25 HP |
| Tamaño | 66 px |
| XP | 750 |

**Habilidades**:

| Habilidad | Descripción | Cooldown |
|-----------|-------------|----------|
| Nube Tóxica | Aura de veneno (100 px, 10 daño/s) | Pasiva |
| Bomba Química | Crea poza tóxica (70 px, 8 daño/s, 6s duración) | 3 segundos |
| Explosión Final | Al morir explota (100 px, 50 daño) | Al morir |

**Visual**: Zombie hinchado verdoso con ampollas químicas.

### Sistema de Pathfinding (Enemy.java)

Sistema avanzado de evasión de obstáculos:

**Campos de Pathfinding**:
```java
protected float stuckTimer;                    // Tiempo bloqueado
protected float avoidanceAngle;                // Ángulo de evasión
protected boolean isAvoiding;                  // Modo evasión activo
protected float avoidanceTimer;                // Timer de evasión
protected int avoidanceAttempts;               // Intentos de evasión
protected float lastValidAngle;                // Último ángulo exitoso
protected float totalStuckTime;                // Para teleport emergencia
```

**Constantes**:

| Constante | Valor | Descripción |
|-----------|-------|-------------|
| STUCK_THRESHOLD | 0.15s | Tiempo para detectar bloqueo |
| AVOIDANCE_DURATION | 0.4s | Duración de evasión |
| MAX_AVOIDANCE_ATTEMPTS | 8 | Intentos antes de cambiar estrategia |

**Algoritmo**:
1. Detectar si está bloqueado (movimiento < 10% velocidad * deltaTime)
2. Si bloqueado > STUCK_THRESHOLD → activar evasión
3. Probar 16 ángulos para encontrar camino libre
4. Si ninguno funciona → usar perpendicular al objetivo
5. Si totalStuckTime > 3 segundos → teleport de emergencia

---

## Sistema de Armas

### WeaponManager.java
**Ubicación**: `com.atropellalo.game.weapon.WeaponManager`

Gestiona armas y proyectiles del jugador.

**Características**:
- Máximo 3 armas simultáneas
- Disparo automático al enemigo más cercano
- Estadísticas de precisión

### Tipos de Armas

#### Pistol (Arma Inicial)

| Stat | Valor |
|------|-------|
| Daño | 15 HP |
| Rango | 250 px |
| Cadencia | 0.5s |
| Proyectiles | 1 |

**Comportamiento**: Disparo preciso al enemigo más cercano.

#### LightMachineGun (Ametralladora Ligera)

| Stat | Valor |
|------|-------|
| Daño | 3 HP |
| Rango | 200 px |
| Cadencia | 0.15s |
| Proyectiles | 1 |

**Comportamiento**: Alta cadencia, bajo daño individual.

#### Shotgun (Escopeta)

| Stat | Valor |
|------|-------|
| Daño | 25 HP |
| Rango | 150 px |
| Cadencia | 1.0s |
| Proyectiles | 5 |
| Dispersión | 54° |

**Comportamiento**: Alto daño, rango corto, múltiples proyectiles.

#### GrenadeLauncher (Lanzagranadas)

| Stat | Valor |
|------|-------|
| Daño | 50 HP |
| Rango | 200 px |
| Cadencia | 2.0s |
| Radio explosión | 60 px |

**Comportamiento**: Daño en área, disparo hacia dirección aleatoria en rango.

#### CircularSaw (Sierras Circulares)

| Stat | Valor |
|------|-------|
| Daño | 12 HP |
| Radio sierra | 25 px |
| Distancia | 45 px del jugador |
| Cooldown | 0.25s |

**Comportamiento**: Sierras giratorias que rotan alrededor del jugador.

#### Flamethrower (Lanzallamas)

| Stat | Valor |
|------|-------|
| Daño | 15 HP/s |
| Rango | 100 px |
| Ángulo cono | 45° |
| Tick rate | 0.1s |

**Comportamiento**: Daño continuo en cono hacia el enemigo más cercano.

#### SniperRailgun (Rifle de Francotirador)

| Stat | Valor |
|------|-------|
| Daño | 120 HP |
| Rango | 500 px |
| Cadencia | 2.5s |
| Penetración | 3 enemigos |
| Velocidad proyectil | 800 px/s |

**Comportamiento**: Disparo de alta precisión con penetración. El proyectil atraviesa hasta 3 enemigos en línea recta. Prioriza objetivos peligrosos (Spitters, Buffers, Explosivos, Jefes).

**Visual**: Proyectil dorado brillante con estela corta (25px). Disparo rápido y realista.

**Clase del proyectil**: `PenetratingProjectile` - Registra enemigos dañados para evitar daño múltiple al mismo objetivo.

---

## Sistema de Mejoras

### UpgradeManager.java
**Ubicación**: `com.atropellalo.game.upgrade.UpgradeManager`

Genera y aplica mejoras al subir de nivel.

### Tipos de Mejoras

#### Mejoras de Vehículo

| Tipo | Efecto |
|------|--------|
| Salud Máxima | +20 HP |
| Velocidad | +20 px/s |

#### Mejoras de Armas

| Tipo | Efecto | Aplicable a |
|------|--------|-------------|
| Daño | +20% | Todas |
| Cadencia | +15% | Excepto CircularSaw |
| Área de Impacto | +30% | GrenadeLauncher, CircularSaw, Flamethrower |
| Multi-disparo | +1 proyectil | Pistol, LMG, GrenadeLauncher, Shotgun |

#### Nueva Arma
- Disponible si tiene menos de 3 armas
- Muestra armas no obtenidas

### UpgradeMenu.java
**Ubicación**: `com.atropellalo.game.ui.UpgradeMenu`

Menú visual de selección de mejoras.

### UpgradeIconGenerator.java
**Ubicación**: `com.atropellalo.game.sprite.UpgradeIconGenerator`

Genera iconos estándar 48x48 píxeles para cada tipo de mejora.

**Iconos disponibles**:

| Tipo | Icono | Descripción |
|------|-------|-------------|
| VEHICLE_HEALTH | Cruz médica roja | Mejora de salud del vehículo |
| VEHICLE_SPEED | Rayo amarillo | Mejora de velocidad |
| NEW_WEAPON | Estrella dorada | Nueva arma disponible |
| WEAPON_DAMAGE | Espada con impacto | Mejora de daño |
| WEAPON_FIRE_RATE | Balas en secuencia | Mejora de cadencia |
| WEAPON_IMPACT_AREA | Círculos concéntricos | Mejora de área |
| WEAPON_PROJECTILE_COUNT | Balas en abanico | Más proyectiles |

**Características**:
- 3 tarjetas de opciones
- Navegación con A/D o ←/→
- Selección con Enter, Espacio o 1/2/3
- Animación de selección
- Juego pausado mientras está abierto

---

## Mapa Urbano

### Diseño Actual: Estacionamiento
El mapa consiste en un gran estacionamiento rodeado por cuatro avenidas principales, sin obstáculos colisionables.

**Características**:
- **Área central**: Gran estacionamiento de asfalto (2320x1200 px)
- **Avenidas**: 4 calles perimetrales de 120px de ancho cada una
- **Sin colisiones**: Todo el espacio es transitable
- **Dimensiones totales**: 2560x1440 píxeles

**Elementos visuales**:
- Líneas amarillas de estacionamiento (80x120 px por espacio)
- Líneas blancas punteadas en las avenidas
- Veredas grises en los bordes
- Textura de asfalto con manchas de desgaste

**Paleta de colores**:
- Asfalto estacionamiento: RGB(45, 45, 45)
- Asfalto avenidas: RGB(35, 35, 35)
- Líneas viales: RGB(255, 255, 255)
- Líneas estacionamiento: RGB(200, 200, 50)
- Veredas: RGB(160, 160, 160)

### MapGenerator.java
**Ubicación**: `com.atropellalo.game.util.MapGenerator`

Genera la imagen del mapa proceduralmente.

**Métodos principales**:
- `drawParkingLot()`: Dibuja área central de estacionamiento
- `drawAvenues()`: Dibuja 4 avenidas perimetrales con líneas
- `drawParkingLines()`: Dibuja líneas de demarcación de espacios
- `addAsphaltTexture()`: Agrega manchas para textura realista

### CityMap.java (Deshabilitado)
**Ubicación**: `com.atropellalo.game.map.CityMap`

Mapa de ciudad generado proceduralmente.

**Configuración**:

| Parámetro | Valor | Descripción |
|-----------|-------|-------------|
| STREET_WIDTH | 120 px | Ancho calles principales |
| ALLEY_WIDTH | 80 px | Ancho callejones |
| MIN_BLOCK_SIZE | 250 px | Tamaño mínimo manzana |
| MAX_BLOCK_SIZE | 500 px | Tamaño máximo manzana |
| BLOCK_SKIP_CHANCE | 55% | Probabilidad de omitir bloque |
| Spawn Zone | 350x350 px | Área segura en centro |

**Semilla**: 12345L (fija para desarrollo)

### Building.java
**Ubicación**: `com.atropellalo.game.map.Building`

Edificios individuales con colisión.

**Tipos de Edificios**:

| Tipo | Color | Descripción |
|------|-------|-------------|
| RESIDENTIAL | Marrón | Edificio bajo con techo a dos aguas |
| COMMERCIAL | Azul acero | Edificio medio con AC |
| OFFICE | Gris plata | Edificio alto con múltiples AC |
| WAREHOUSE | Siena | Almacén ancho con puertas de carga |
| SKYSCRAPER | Azul | Muy alto con antena y helipuerto |

### CityBlock.java
**Ubicación**: `com.atropellalo.game.map.CityBlock`

Manzana que contiene múltiples edificios.

**Layouts**:
- Grid: 2-3 columnas x 2-3 filas
- L-Shape: Edificio principal + extensión
- Mixed: Edificio grande + pequeños alrededor
- Single: Un edificio grande

### Colisiones

**Resolución de Colisiones** (sliding):
1. Intentar movimiento completo (X + Y)
2. Si colisiona, intentar solo X
3. Si colisiona, intentar solo Y
4. Si todo falla, mantener posición anterior

---

## Sistema de Loot

### LootManager.java
**Ubicación**: `com.atropellalo.game.loot.LootManager`

Gestiona spawn y recolección de items.

### Tipos de Loot

#### Fuel (Combustible)

| Propiedad | Valor |
|-----------|-------|
| Restaura | 25 combustible |
| Tamaño | 24 px |
| Spawn inicial | 2 |
| Máximo | 6 |
| Intervalo spawn | 4-8 segundos |

**Visual**: Bidón naranja con gota blanca.

#### Scrap (Chatarra)

| Propiedad | Valor |
|-----------|-------|
| Restaura | 15 HP |
| Tamaño | 22 px |
| Spawn inicial | 1 |
| Máximo | 4 |
| Intervalo spawn | 5-10 segundos |

**Visual**: Pieza metálica gris con cruz verde.

#### XPOrb (Orbe de Experiencia)

| Propiedad | Valor |
|-----------|-------|
| Tamaño | 12 px |
| Distancia atracción | 100 px |
| Velocidad atracción | 220 px/s |
| Pickup distance | 20 px |

**Visual**: Esfera con gradiente verde/amarillo.
**Comportamiento**: Atracción magnética hacia el jugador.

---

## HUD y UI

### GameHUD.java
**Ubicación**: `com.atropellalo.game.ui.GameHUD`

HUD del jugador.

**Elementos**:

| Elemento | Posición | Colores |
|----------|----------|---------|
| Barra Salud | Superior izquierda | Verde/Naranja/Rojo según % |
| Barra Combustible | Bajo salud | Naranja/Rojo según % |
| Barra XP | Bajo combustible | Azul con indicador de nivel |
| Oleada/Kills | Superior derecha | Blanco |
| Game Over | Centro | Rojo con botón reinicio |

**Pantalla Game Over**:
- Overlay oscuro semi-transparente
- Texto "GAME OVER" en rojo
- Botón parpadeante "Presiona R para reiniciar"

---

## Configuración (GameConfig.java)

### Jugador
```java
PLAYER_MAX_HEALTH = 100.0f
PLAYER_MAX_FUEL = 100.0f
FUEL_CONSUMPTION_RATE = 3.0f
PLAYER_SPEED = 200.0f
PLAYER_SIZE = 32
```

### Oleadas
```java
WAVE_INTERVAL = 5.0f
WAVE_BASE_ENEMIES = 10
WAVE_ENEMY_INCREMENT = 5
MAX_ENEMIES_ON_MAP = 100
ENEMY_SPAWN_INTERVAL = 0.3f
```

### Jefes
```java
BRUISER_BOSS_WAVE = 5
INFECTOR_BOSS_WAVE = 10
```

### XP y Niveles
```java
XP_BASE_TO_LEVEL_UP = 100
XP_LEVEL_SCALING = 1.2f
LEVEL_UP_FUEL_BONUS = 10.0f
NO_FUEL_SPEED_PENALTY = 0.7f  // Penalización de velocidad sin combustible (70%)
```

### Mejoras
```java
UPGRADE_HEALTH_AMOUNT = 20.0f
UPGRADE_SPEED_AMOUNT = 20.0f
UPGRADE_WEAPON_DAMAGE_FACTOR = 1.2f
UPGRADE_WEAPON_FIRE_RATE_FACTOR = 0.85f
MAX_WEAPONS = 3
```

---

## Cómo Ejecutar

### Compilar
```powershell
mvn clean compile
```

### Ejecutar
```powershell
mvn exec:java "-Dexec.mainClass=com.atropellalo.Game"
```

### Empaquetar JAR
```powershell
mvn clean package
java -jar target/atropellalo-game-1.0-SNAPSHOT.jar
```

---

## Historial de Fases

| Fase | Descripción | Estado |
|------|-------------|--------|
| 1 | Ventana básica y mapa | ✅ |
| 2 | Jugador y movimiento WASD | ✅ |
| 3 | Sistema de loot (combustible/chatarra) | ✅ |
| 4 | Enemigos y oleadas | ✅ |
| 5 | Sistema de armas (pistola) | ✅ |
| 6 | XP, niveles y mejoras | ✅ |
| 7 | Nuevas armas (6 tipos) | ✅ |
| 8 | Jefes (Aplastador, Infectador) | ✅ |
| 9 | Mapa urbano con colisiones | ✅ |
| 10 | Menos obstáculos, pathfinding, reinicio | ✅ |
| 10.1 | Pathfinding mejorado, fix reinicio | ✅ |
| 11 | Rifle de Francotirador (Sniper Railgun) | ✅ |
| 12 | Mejoras visuales: explosión zombie, iconos upgrades, limpieza SlashWhip | ✅ |
| 13 | Mecánica de combustible mejorada, sprites únicos por arma | ✅ |
| 14 | Oleadas continuas, multi-objetivo, ángulo lanzallamas, eliminación CircularSaw | ✅ |

---

## Fase 13 - Ajustes de Combustible y Sprites de Armas

### Cambios Implementados

#### 1. Nueva Mecánica de Combustible
- **Antes**: El jugador se detenía completamente al quedarse sin combustible
- **Ahora**: El jugador puede moverse con velocidad reducida (configurable)
- **Configuración**: `NO_FUEL_SPEED_PENALTY = 0.7f` en GameConfig (70% de reducción)
- **Comportamiento**: 
  - Con combustible: velocidad normal (200 px/s)
  - Sin combustible: velocidad reducida (60 px/s con penalty de 0.7)

#### 2. Sprites Únicos para Cada Arma
Se crearon sprites detallados para cada tipo de arma en el menú de mejoras:

| Arma | Características del Sprite |
|------|---------------------------|
| **Pistola** | Diseño clásico con empuñadura de madera, gatillo y brillo metálico |
| **Ametralladora Ligera** | Cañón largo con ventilación, cargador lateral, balas visibles |
| **Lanzagranadas** | Tubo grande, granada visible, correa táctica |
| **Sierras Circulares** | Sierra metálica con dientes, efecto de giro |
| **Lanzallamas** | Tanque de combustible rojo, llamas animadas saliendo |
| **Escopeta** | Cañones dobles, culata de madera con vetas |
| **Rifle de Francotirador** | Cañón largo, mira telescópica con lente azul, efecto railgun |

### Archivos Modificados
- `GameConfig.java`: Nueva constante `NO_FUEL_SPEED_PENALTY`
- `Player.java`: Lógica de movimiento con penalización por falta de combustible
- `UpgradeIconGenerator.java`: Sprites detallados para cada tipo de arma

---

## Fase 14 - Oleadas Continuas, Multi-Objetivo y Mejoras de Lanzallamas

### Cambios Implementados

#### 1. Sistema de Oleadas Refactorizado
- **Antes**: Había un tiempo de espera entre oleadas
- **Ahora**: Las oleadas son continuas sin pausa entre ellas
- **Spawn en Bordes**: Los enemigos ahora aparecen desde los bordes del mapa, no cerca del jugador
- **Método**: `getEdgeSpawnPosition()` calcula posiciones aleatorias en los 4 bordes del mundo

#### 2. Nueva Mejora: Multi-Objetivo
- **Antes**: "Multi-disparo" disparaba proyectiles en abanico
- **Ahora**: "Multi-objetivo" permite disparar a múltiples enemigos simultáneamente
- **Restricciones**:
  - Máximo 2 upgrades (dispara hasta 3 objetivos)
  - Solo disponible para: Pistol, LightMachineGun, GrenadeLauncher, SniperRailgun
  - **Excluidas**: Shotgun (ya dispara múltiples proyectiles) y Flamethrower (cono de área)
- **Implementación**: `findMultipleClosestEnemies()` en clase base `Weapon`

#### 3. Nueva Mejora: Ángulo de Cono (Lanzallamas)
- **Descripción**: Aumenta el ángulo del cono de fuego del lanzallamas
- **Incremento**: +15° por upgrade
- **Máximo**: 180° (medio círculo)
- **Configuración**:
  - `UPGRADE_FLAMETHROWER_CONE_ANGLE = 15.0f`
  - `FLAMETHROWER_MAX_CONE_ANGLE = 180.0f`

#### 4. Eliminación de Sierras Circulares
- **Razón**: Arma removida del juego
- **Archivos eliminados**: `CircularSaw.java`
- **Constantes removidas**: Todas las `SAW_*` de `GameConfig`
- **Enums actualizados**: `WeaponType.CIRCULAR_SAW` eliminado

### Nuevas Constantes en GameConfig
```java
// Multi-objetivo
UPGRADE_MULTI_TARGET_MAX = 2           // Máximo 2 upgrades (3 objetivos)

// Ángulo de cono lanzallamas
UPGRADE_FLAMETHROWER_CONE_ANGLE = 15.0f  // Grados por upgrade
FLAMETHROWER_MAX_CONE_ANGLE = 180.0f     // Máximo 180°
```

### Tipos de Mejora Actualizados
| Tipo Anterior | Tipo Nuevo | Descripción |
|---------------|------------|-------------|
| `WEAPON_PROJECTILE_COUNT` | `WEAPON_MULTI_TARGET` | Dispara a más enemigos |
| N/A | `WEAPON_CONE_ANGLE` | Aumenta ángulo del lanzallamas |

### Armas Actuales (6 tipos)
| Arma | Multi-Objetivo | Ángulo Cono |
|------|----------------|-------------|
| Pistol | ✅ | ❌ |
| LightMachineGun | ✅ | ❌ |
| GrenadeLauncher | ✅ | ❌ |
| SniperRailgun | ✅ | ❌ |
| Shotgun | ❌ | ❌ |
| Flamethrower | ❌ | ✅ |

### Archivos Modificados
- `EnemyManager.java`: Spawn en bordes del mapa, oleadas continuas
- `Weapon.java`: Sistema multi-objetivo, campos targetCount, coneAngle
- `Flamethrower.java`: Campo coneAngle con getter
- `Pistol.java`, `LightMachineGun.java`, `GrenadeLauncher.java`, `SniperRailgun.java`: Multi-target
- `WeaponType.java`: Eliminado CIRCULAR_SAW
- `WeaponManager.java`: Eliminado CircularSaw
- `GameConfig.java`: Nuevas constantes, eliminadas SAW_*
- `UpgradeType.java`: WEAPON_MULTI_TARGET, WEAPON_CONE_ANGLE
- `UpgradeManager.java`: Lógica de nuevas mejoras
- `UpgradeIconGenerator.java`: Iconos para multi-target y cone-angle

---

## Fase 14.1 - Mejora del Daño en Área del Lanzallamas

### Problema Identificado
El lanzallamas no estaba aplicando daño correctamente porque:
1. `FLAMETHROWER_TICK_RATE = 0f` causaba que el daño se aplicara incorrectamente
2. El daño se multiplicaba por `fireDelay` (que era 0), resultando en daño nulo
3. Los valores eran demasiado bajos para ser efectivos

### Cambios Implementados

#### Ajustes en GameConfig
| Parámetro | Antes | Después | Efecto |
|-----------|-------|---------|--------|
| `FLAMETHROWER_DAMAGE` | 10.0f | 15.0f | +50% daño base |
| `FLAMETHROWER_RANGE` | 100.0f | 120.0f | +20% alcance |
| `FLAMETHROWER_CONE_ANGLE` | 45.0f | 60.0f | +33% ángulo |
| `FLAMETHROWER_TICK_RATE` | 0f | 0.1f | Daño cada 0.1 segundos |

#### Corrección de Lógica de Daño
- **Antes**: `enemy.takeDamage(damage * damageMultiplier * fireDelay)` - multiplicaba por 0
- **Ahora**: `enemy.takeDamage(damage * damageMultiplier)` - aplica daño completo
- Daño por segundo efectivo: 15 × 10 ticks = **150 DPS** a todos los enemigos en el cono
- Reducción de daño por distancia: solo 20% menos en el borde del cono

### Archivos Modificados
- `GameConfig.java`: Ajuste de valores del lanzallamas
- `Flamethrower.java`: Corrección de cálculo de daño

---

**Fecha de Creación**: 29/11/2025  
**Última Actualización**: 30/11/2025 - Fase 14.1  
**Versión**: 1.0-SNAPSHOT
