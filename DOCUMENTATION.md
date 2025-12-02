# Documentación Completa - Atropellalo Game

## Información General

- **Nombre del Proyecto**: Atropellalo
- **Tipo**: Juego estilo Survivor (inspirado en Vampire Survivors)
- **Lenguaje**: Java 21
- **Framework UI**: Swing
- **Build Tool**: Maven
- **Versión**: 1.0-SNAPSHOT
- **Estado**: Fase 17 Completada

---

## Tabla de Contenidos

1. [Pautas de Desarrollo](#1-pautas-de-desarrollo)
2. [Estructura del Proyecto](#2-estructura-del-proyecto)
3. [Arquitectura del Sistema](#3-arquitectura-del-sistema)
4. [Sistema de Juego](#4-sistema-de-juego)
5. [Sistema de Menús](#5-sistema-de-menús)
6. [Sistema de Enemigos](#6-sistema-de-enemigos)
7. [Sistema de Armas](#7-sistema-de-armas)
8. [Sistema de Mejoras](#8-sistema-de-mejoras)
9. [Sistema de Loot](#9-sistema-de-loot)
10. [Sistema de Efectos Visuales](#10-sistema-de-efectos-visuales)
11. [Sistema de Sonido](#11-sistema-de-sonido)
12. [Sistema de Depuración](#12-sistema-de-depuración)
13. [Mapa del Juego](#13-mapa-del-juego)
14. [Configuración](#14-configuración)
15. [Ejecución](#15-ejecución)
16. [Historial de Desarrollo](#16-historial-de-desarrollo)

---

## 1. Pautas de Desarrollo

### Principios Fundamentales

#### 1.1 Mejores Prácticas de Programación
- Convenciones de nombres Java estándar (camelCase para métodos/variables, PascalCase para clases)
- Encapsulamiento adecuado de propiedades (campos privados con getters/setters)
- Manejo estructurado de excepciones con logging
- Uso de constantes centralizadas en `GameConfig`
- Documentación JavaDoc completa

#### 1.2 Bajo Acoplamiento y Alta Cohesión
- **Game**: Punto de entrada único
- **GameWindow**: Gestión de ventana y navegación
- **GamePanel**: Game loop y coordinación de sistemas
- **Managers**: Cada uno gestiona un dominio específico
- Comunicación mediante interfaces y callbacks

#### 1.3 KISS (Keep It Simple, Stupid)
- Implementación directa sin complejidad innecesaria
- Uso de Swing estándar sin frameworks adicionales
- Carga simple de recursos desde classpath
- Lógica clara y comprensible

#### 1.4 DRY (Don't Repeat Yourself)
- Constantes centralizadas en `GameConfig`
- Clases base abstractas (`Enemy`, `Weapon`, `Loot`)
- Métodos reutilizables
- Logging centralizado

#### 1.5 Definition of Done (DoD)
- Solo se implementa lo solicitado
- Código funcional y probado
- Documentación actualizada
- Sin características no requeridas

#### 1.6 Nivel Profesional
- Código documentado con JavaDoc
- Manejo apropiado de errores
- Estructura Maven estándar
- Patrones de diseño aplicados correctamente

---

## 2. Estructura del Proyecto

```
atropellalo/
├── pom.xml
├── README.md
├── DOCUMENTATION_COMPLETE.md (este archivo)
└── src/
    └── main/
        ├── java/com/atropellalo/
        │   ├── Game.java
        │   └── game/
        │       ├── camera/
        │       │   └── Camera.java
        │       ├── config/
        │       │   ├── GameConfig.java
        │       │   └── DebugConfig.java
        │       ├── debug/
        │       │   ├── DebugManager.java
        │       │   ├── DebugConsole.java
        │       │   ├── DebugOverlay.java
        │       │   └── PerformanceMonitor.java
        │       ├── effect/
        │       │   ├── VisualEffect.java
        │       │   ├── ExplosionEffect.java
        │       │   ├── FlamethrowerEffect.java
        │       │   └── VisualEffectManager.java
        │       ├── enemy/
        │       │   ├── Enemy.java
        │       │   ├── EnemyType.java
        │       │   ├── FastZombie.java
        │       │   ├── SlowZombie.java
        │       │   ├── ExplosiveZombie.java
        │       │   ├── BufferZombie.java
        │       │   ├── SpitterZombie.java
        │       │   ├── BroodCarrier.java
        │       │   ├── BruiserBoss.java
        │       │   ├── InfectorBoss.java
        │       │   └── EnemyManager.java
        │       ├── entity/
        │       │   └── Player.java
        │       ├── input/
        │       │   └── InputHandler.java
        │       ├── loot/
        │       │   ├── Loot.java
        │       │   ├── LootType.java
        │       │   ├── Fuel.java
        │       │   ├── Scrap.java
        │       │   ├── XPOrb.java
        │       │   └── LootManager.java
        │       ├── sound/
        │       │   └── SoundManager.java
        │       ├── sprite/
        │       │   ├── Animation.java
        │       │   ├── AnimationState.java
        │       │   ├── BossSpriteGenerator.java
        │       │   ├── TruckSpriteGenerator.java
        │       │   ├── UpgradeIconGenerator.java
        │       │   └── ZombieSpriteGenerator.java
        │       ├── ui/
        │       │   ├── GameWindow.java
        │       │   ├── GamePanel.java
        │       │   ├── GameHUD.java
        │       │   ├── MainMenu.java
        │       │   ├── InstructionsPanel.java
        │       │   ├── OptionsPanel.java
        │       │   ├── PauseMenu.java
        │       │   └── UpgradeMenu.java
        │       ├── upgrade/
        │       │   ├── UpgradeType.java
        │       │   ├── UpgradeOption.java
        │       │   └── UpgradeManager.java
        │       └── weapon/
        │           ├── Weapon.java
        │           ├── WeaponType.java
        │           ├── Pistol.java
        │           ├── LightMachineGun.java
        │           ├── Shotgun.java
        │           ├── GrenadeLauncher.java
        │           ├── Flamethrower.java
        │           ├── SniperRailgun.java
        │           ├── Projectile.java
        │           ├── PenetratingProjectile.java
        │           ├── GrenadeProjectile.java
        │           └── WeaponManager.java
        └── resources/
            ├── effects/
            ├── images/
            └── sounds/
                ├── pistol.mp3
                ├── machinegun.mp3
                ├── shotgun.mp3
                ├── grenade-launcher.mp3
                ├── fireflammer.mp3
                └── sniperrifle.mp3
```

---

## 3. Arquitectura del Sistema

### 3.1 Patrones de Diseño

| Patrón | Implementación |
|--------|----------------|
| **Template Method** | `Enemy`, `Weapon`, `Loot` |
| **Observer/Callback** | `LevelUpCallback`, `RestartCallback`, `PauseMenuCallback` |
| **Manager** | `EnemyManager`, `WeaponManager`, `LootManager`, `UpgradeManager`, `VisualEffectManager`, `SoundManager`, `DebugManager` |
| **Factory** | `WeaponManager.createWeapon()`, `EnemyManager.createEnemy()` |
| **Singleton** | `VisualEffectManager`, `SoundManager`, `DebugManager` |
| **Game Loop** | `GamePanel.run()` con delta time |
| **Strategy** | Comportamientos de armas y enemigos |
| **Command** | Sistema de comandos en `DebugConsole` |

### 3.2 Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────┐
│                          Game (Main)                             │
│                              │                                   │
│                        GameWindow                                │
│                     (CardLayout Manager)                         │
│                              │                                   │
│  ┌───────────┬───────────────┼───────────────┬──────────────┐  │
│  │           │               │               │              │  │
│  ▼           ▼               ▼               ▼              ▼  │
│ MainMenu  Instructions  OptionsPanel    GamePanel    (otros)  │
│                                              │                  │
│  ┌───────────────────────────────────────────┘                  │
│  │                                                               │
│  │  ┌─────────────────────────────────────────────────────┐    │
│  │  │               Game Loop (60 FPS)                     │    │
│  │  │  update(deltaTime) → render() → sleep()             │    │
│  │  └─────────────────────────────────────────────────────┘    │
│  │                           │                                  │
│  │  ┌────────────┬───────────┼────────────┬────────────┐       │
│  │  │            │           │            │            │       │
│  │  ▼            ▼           ▼            ▼            ▼       │
│  │ Player    Camera   InputHandler   GameHUD     PauseMenu    │
│  │  │                                                           │
│  │  ├─── Managers ────────────────────────────────────────     │
│  │  │    ├── EnemyManager                                      │
│  │  │    ├── WeaponManager                                     │
│  │  │    ├── LootManager                                       │
│  │  │    ├── UpgradeManager                                    │
│  │  │    ├── VisualEffectManager                               │
│  │  │    ├── SoundManager                                      │
│  │  │    └── DebugManager                                      │
│  │  │                                                           │
│  │  └─── UpgradeMenu (pausable)                                │
│  └───────────────────────────────────────────────────────────  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 4. Sistema de Juego

### 4.1 Game Loop

**Ubicación**: `GamePanel.java`

**Características**:
- 60 FPS target
- Delta time para movimiento independiente del framerate
- Estados: jugando, pausado, game over

**Flujo**:
```
1. Calcular deltaTime
2. Si no está pausado:
   a. Procesar input
   b. Actualizar jugador
   c. Actualizar enemigos
   d. Actualizar armas/proyectiles
   e. Actualizar loot
   f. Verificar colisiones
   g. Actualizar cámara
   h. Actualizar efectos visuales
   i. Actualizar sistema de debug
3. Renderizar todo
4. Sleep para mantener FPS
```

### 4.2 Player (Jugador)

**Stats Principales**:

| Stat | Valor Inicial |
|------|---------------|
| Salud Máxima | 100 HP |
| Combustible Máximo | 100 |
| Velocidad | 200 px/s |
| Tamaño | 32 px |
| Consumo Combustible | 3/s |

**Sistema de Niveles**:
- XP base nivel 2: 100
- Escalado: `XP = 100 * 1.2^(nivel-1)`
- Bonus por nivel: +10 combustible

**Mecánica de Combustible**:
- Con combustible: velocidad normal
- Sin combustible: velocidad reducida (30% - configurable)
- El jugador nunca se detiene completamente

### 4.3 Cámara

**Configuración**:
- Viewport: 1280x720
- Sigue al jugador
- Limitada a bordes del mundo
- Smooth following

---

## 5. Sistema de Menús

### 5.1 MainMenu (Menú Principal)

**Opciones**:
- **Jugar**: Inicia el juego (lazy loading del GamePanel)
- **Instrucciones**: Muestra controles
- **Opciones**: Configuración (placeholder)

**Diseño**:
- Título del juego en grande
- 3 botones con hover effect
- Navegación con mouse

### 5.2 PauseMenu (Menú de Pausa)

**Activación**: Tecla ESC durante el juego

**Características**:
- Overlay oscuro semi-transparente
- Panel central (600x650 px)
- Área scrolleable de estadísticas
- 2 botones: Reanudar y Salir

**Estadísticas Mostradas**:

#### Jugador:
- Nivel actual
- XP (actual / necesaria)
- Salud (actual / máxima)
- Combustible (actual / máximo)
- Velocidad

#### Armas (por cada arma equipada):
- Nombre
- Nivel
- Daño
- Cadencia (disparos/s)
- Rango
- Velocidad proyectil

#### Globales:
- Disparos realizados
- Disparos acertados
- Precisión (%)
- Proyectiles activos

**Controles**:
- `ESC`: Abrir/cerrar
- `↑/↓` o `W/S`: Scroll
- `←/→` o `A/D`: Navegar botones
- `Enter/Espacio`: Confirmar

### 5.3 UpgradeMenu (Menú de Mejoras)

**Activación**: Automática al subir de nivel

**Características**:
- 3 opciones aleatorias
- Sprites únicos por tipo de mejora
- Navegación con teclado
- Pausa el juego automáticamente

---

## 6. Sistema de Enemigos

### 6.1 EnemyManager

**Sistema de Oleadas**:
- Oleadas continuas sin pausa
- Spawn en bordes del mapa
- Incremento progresivo de dificultad

**Configuración**:

| Parámetro | Valor |
|-----------|-------|
| Intervalo oleadas | 5s |
| Enemigos base | 10 |
| Incremento/oleada | +5 |
| Máximo simultáneo | 100 |
| Spawn interval | 0.3s |

**Escalado por Oleada**:

| Factor | Incremento |
|--------|------------|
| Salud | +5% |
| Velocidad | +2% |
| Daño | +3% |
| XP | +5% |

### 6.2 Tipos de Enemigos

#### FastZombie
- Velocidad: 150 px/s
- Salud: 30 HP
- Daño: 8 HP
- XP: 10
- Spawn: 50%

#### SlowZombie
- Velocidad: 50 px/s
- Salud: 100 HP
- Daño: 15 HP
- XP: 25
- Spawn: 30%

#### ExplosiveZombie
- Velocidad: 80 px/s
- Salud: 40 HP
- Daño contacto: 10 HP
- Daño explosión: 25 HP
- Radio: 80 px
- XP: 20
- Spawn: 20%
- **Especial**: Explota al morir

### 6.3 Jefes

#### BruiserBoss (Oleada 5)
- Salud: 800 HP
- Velocidad: 60 px/s
- Daño: 35 HP
- XP: 550

**Habilidades**:
- **Terremoto**: Onda de choque (150 px, 30 daño, CD: 4s)
- **Carga**: Embestida rápida (400 px/s, 40 daño, CD: 6s)

#### InfectorBoss (Oleada 10)
- Salud: 650 HP
- Velocidad: 80 px/s
- Daño: 25 HP
- XP: 750

**Habilidades**:
- **Nube Tóxica**: Aura (100 px, 10 daño/s, pasiva)
- **Bomba Química**: Poza tóxica (70 px, 8 daño/s, 6s, CD: 3s)
- **Explosión Final**: Al morir (100 px, 50 daño)

### 6.4 Pathfinding

**Sistema de Evasión de Obstáculos**:
1. Detecta bloqueo (movimiento < 10% velocidad)
2. Activa modo evasión (0.4s)
3. Prueba 16 ángulos para encontrar camino
4. Si falla, usa perpendicular al objetivo
5. Teleport de emergencia si bloqueado > 3s

---

## 7. Sistema de Armas

### 7.1 Armas Disponibles (6 tipos)

#### Pistol (Inicial)
- Daño: 15 HP
- Rango: 250 px
- Cadencia: 0.5s
- Multi-target: ✅

#### LightMachineGun
- Daño: 3 HP
- Rango: 200 px
- Cadencia: 0.15s
- Multi-target: ✅

#### Shotgun
- Daño: 25 HP
- Rango: 150 px
- Cadencia: 1.0s
- Proyectiles: 5 (dispersión 54°)
- Multi-target: ❌

#### GrenadeLauncher
- Daño: 50 HP
- Rango: 200 px
- Cadencia: 2.0s
- Radio explosión: 60 px
- Multi-target: ✅

#### Flamethrower
- Daño: 15 HP/s
- Rango: 120 px
- Ángulo cono: 60°
- Tick rate: 0.1s
- Mejorable: Ángulo de cono ✅
- Multi-target: ❌

#### SniperRailgun
- Daño: 120 HP
- Rango: 500 px
- Cadencia: 2.5s
- Penetración: 3 enemigos
- Velocidad: 800 px/s
- Multi-target: ✅

### 7.2 Sistema Multi-Objetivo

**Disponible para**: Pistol, LMG, GrenadeLauncher, SniperRailgun

**Características**:
- Máximo 2 upgrades (hasta 3 objetivos)
- Dispara simultáneamente a múltiples enemigos
- Prioriza enemigos cercanos

---

## 8. Sistema de Mejoras

### 8.1 Tipos de Mejoras

#### Vehículo
- **Salud Máxima**: +20 HP
- **Velocidad**: +20 px/s

#### Armas Generales
- **Daño**: +20%
- **Cadencia**: +15% (más rápido)
- **Área de Impacto**: +30% (Granada, Lanzallamas)
- **Multi-Objetivo**: +1 objetivo (máx 2 upgrades)

#### Armas Específicas
- **Ángulo de Cono** (Lanzallamas): +15° (máx 180°)

#### Nueva Arma
- Aparece si tienes < 3 armas
- Muestra armas no obtenidas

### 8.2 Sistema de Selección

- 3 opciones aleatorias al subir nivel
- Sprites únicos por tipo
- Navegación: A/D, ←/→, 1/2/3
- Confirmar: Enter, Espacio

---

## 9. Sistema de Loot

### 9.1 Tipos de Loot

#### Fuel (Combustible)
- Restaura: 25
- Tamaño: 24 px
- Spawn inicial: 2
- Máximo: 6
- Intervalo: 4-8s

#### Scrap (Chatarra)
- Restaura: 15 HP
- Tamaño: 22 px
- Spawn inicial: 1
- Máximo: 4
- Intervalo: 5-10s

#### XPOrb (Experiencia)
- Tamaño: 12 px
- Atracción: 100 px
- Velocidad atracción: 220 px/s
- Drop: Al matar enemigos

---

## 10. Sistema de Efectos Visuales

### 10.1 VisualEffectManager

**Patrón**: Singleton

**Responsabilidades**:
- Crear efectos temporales (explosiones)
- Gestionar efectos persistentes (lanzallamas)
- Actualizar y renderizar todos los efectos

### 10.2 ExplosionEffect

**Características**:
- 3 ondas de choque expansivas
- Núcleo de fuego con gradiente
- 12 partículas de escombros con física
- Duración: 0.5s

**Uso**: Zombies explosivos

### 10.3 FlamethrowerEffect

**Características**:
- Cono base con 5 capas de gradiente
- 60 partículas de fuego/segundo
- Brasas/chispas con parpadeo
- Ondas de calor animadas

**Colores**:
1. Blanco-amarillo (núcleo)
2. Amarillo brillante
3. Naranja
4. Rojo-naranja
5. Rojo oscuro (exterior)

---

## 11. Sistema de Sonido

### 11.1 SoundManager

**Patrón**: Singleton

**Características**:
- Soporte MP3 (vía mp3spi)
- Reproducción única y en loop
- Control de volumen maestro y por categoría
- Conversión automática PCM

**Configuración**:
- `SOUND_ENABLED`: true/false
- `SOUND_MASTER_VOLUME`: 0.0-1.0
- `SOUND_WEAPON_VOLUME`: 0.0-1.0
- `SOUND_WEAPON_LOOP_ENABLED`: true/false

### 11.2 Sonidos de Armas

| Arma | Archivo | Tipo |
|------|---------|------|
| Pistol | pistol.mp3 | Único |
| LMG | machinegun.mp3 | Único |
| Shotgun | shotgun.mp3 | Único |
| Granada | grenade-launcher.mp3 | Único |
| Lanzallamas | fireflammer.mp3 | Loop |
| Sniper | sniperrifle.mp3 | Único |

---

## 12. Sistema de Depuración

### 12.1 DebugManager

**Patrón**: Singleton

**Activación**: F3 (overlay), F1 (consola)

**Componentes**:
- `PerformanceMonitor`: Métricas de rendimiento
- `DebugOverlay`: Información visual en pantalla
- `DebugConsole`: Consola de comandos interactiva

### 12.2 Métricas Monitoreadas

- **FPS**: Frames por segundo
- **Frame Time**: Promedio, min, max
- **Memoria**: Uso, total, máximo
- **Entidades**: Enemigos, proyectiles, loot

### 12.3 Comandos de Debug

#### Visualización:
- `fps`, `memory`, `entities`, `player`
- `collision`, `grid`
- `all`, `none`

#### Juego:
- `addxp <cantidad>`: Agregar XP
- `heal`: Restaurar salud
- `levelup`: Subir nivel
- `addfuel [cantidad]`: Agregar combustible
- `stopspawn` / `startspawn`: Control de spawn
- `clearwave`: Eliminar enemigos
- `gc`: Garbage collection

#### Utilidad:
- `help`: Lista comandos
- `clear`: Limpiar consola

**Controles**:
- `↑/↓`: Navegar historial
- `Enter`: Ejecutar
- `Esc`: Cerrar consola

---

## 13. Mapa del Juego

### 13.1 Diseño Actual: Estacionamiento

**Características**:
- Área central: Estacionamiento (2320x1200 px)
- Avenidas: 4 calles perimetrales (120 px ancho)
- Sin colisiones: Todo es transitable
- Dimensiones totales: 2560x1440 px

**Elementos visuales**:
- Líneas de estacionamiento amarillas
- Líneas viales blancas punteadas
- Veredas grises
- Textura de asfalto con desgaste

**Paleta de colores**:
- Asfalto estacionamiento: RGB(45, 45, 45)
- Asfalto avenidas: RGB(35, 35, 35)
- Líneas viales: RGB(255, 255, 255)
- Líneas estacionamiento: RGB(200, 200, 50)
- Veredas: RGB(160, 160, 160)

---

## 14. Configuración

### 14.1 GameConfig.java

Todas las constantes del juego centralizadas:

**Jugador**:
```java
PLAYER_MAX_HEALTH = 100.0f
PLAYER_MAX_FUEL = 100.0f
FUEL_CONSUMPTION_RATE = 3.0f
PLAYER_SPEED = 200.0f
NO_FUEL_SPEED_PENALTY = 0.7f  // 70% reducción
```

**Oleadas**:
```java
WAVE_INTERVAL = 5.0f
WAVE_BASE_ENEMIES = 10
WAVE_ENEMY_INCREMENT = 5
MAX_ENEMIES_ON_MAP = 100
```

**Mejoras**:
```java
UPGRADE_HEALTH_AMOUNT = 20.0f
UPGRADE_SPEED_AMOUNT = 20.0f
UPGRADE_WEAPON_DAMAGE_FACTOR = 1.2f  // +20%
UPGRADE_WEAPON_FIRE_RATE_FACTOR = 0.85f  // +15%
UPGRADE_MULTI_TARGET_MAX = 2
UPGRADE_FLAMETHROWER_CONE_ANGLE = 15.0f
```

**Sonido**:
```java
SOUND_ENABLED = true
SOUND_MASTER_VOLUME = 0.8f
SOUND_WEAPON_VOLUME = 0.7f
```

**Efectos Visuales**:
```java
VFX_EXPLOSION_DURATION = 0.5f
VFX_EXPLOSION_PARTICLE_COUNT = 12
VFX_FLAME_PARTICLES_PER_SECOND = 60f
```

### 14.2 DebugConfig.java

Configuración de herramientas de debug:

```java
showFPS = true
showEntityCount = true
showPlayerStats = true
showMemoryUsage = true
showCollisionBoxes = false
enableConsole = false
```

---

## 15. Ejecución

### 15.1 Compilación

```powershell
mvn clean compile
```

### 15.2 Ejecución Directa

```powershell
mvn exec:java "-Dexec.mainClass=com.atropellalo.Game"
```

### 15.3 Empaquetado JAR

```powershell
mvn clean package
java -jar target/atropellalo-game-1.0-SNAPSHOT.jar
```

### 15.4 Requisitos

- **Java**: 21 o superior
- **Maven**: 3.6+
- **Memoria**: Mínimo 512 MB RAM
- **Pantalla**: Mínimo 1280x720

---

## 16. Historial de Desarrollo

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
| 10 | Pathfinding mejorado, reinicio | ✅ |
| 11 | Rifle de Francotirador | ✅ |
| 12 | Explosión zombie, iconos upgrades | ✅ |
| 13 | Combustible mejorado, sprites armas | ✅ |
| 14 | Oleadas continuas, multi-objetivo | ✅ |
| 15 | Sistema de sonidos | ✅ |
| 16 | Sistema de efectos visuales | ✅ |
| 17 | Menú principal y navegación | ✅ |

### 16.1 Características Destacadas por Fase

**Fase 13**: Nueva mecánica de combustible
- El jugador se mueve con velocidad reducida sin combustible (no se detiene)
- Sprites únicos para cada arma en el menú de mejoras

**Fase 14**: Sistema de combate mejorado
- Spawn de enemigos en bordes del mapa
- Sistema multi-objetivo para armas
- Mejora de ángulo de cono para lanzallamas
- Eliminación de Sierras Circulares

**Fase 15**: Inmersión auditiva
- Soporte completo para sonidos MP3
- Sonidos únicos por arma
- Sistema de loop para armas continuas

**Fase 16**: Feedback visual profesional
- Explosiones animadas procedurales
- Efectos de fuego realistas
- Sistema centralizado de VFX

**Fase 17**: Experiencia de usuario completa
- Menú principal profesional
- Panel de instrucciones
- Menú de pausa con estadísticas detalladas
- Sistema de navegación con CardLayout

---

## Apéndices

### A. Dependencias Maven

```xml
<dependencies>
    <!-- MP3 Audio Support -->
    <dependency>
        <groupId>com.googlecode.soundlibs</groupId>
        <artifactId>mp3spi</artifactId>
        <version>1.9.5.4</version>
    </dependency>
    <dependency>
        <groupId>com.googlecode.soundlibs</groupId>
        <artifactId>jlayer</artifactId>
        <version>1.0.1.4</version>
    </dependency>
    <dependency>
        <groupId>com.googlecode.soundlibs</groupId>
        <artifactId>tritonus-share</artifactId>
        <version>0.3.7.4</version>
    </dependency>
</dependencies>
```

### B. Controles del Juego

| Acción | Teclas |
|--------|--------|
| Movimiento | W, A, S, D |
| Abrir/Cerrar Pausa | ESC |
| Menú Mejoras | 1, 2, 3 o Enter |
| Debug Overlay | F3 |
| Debug Console | F1 |
| Reiniciar (Game Over) | R |

### C. Roadmap Futuro

**Posibles Mejoras**:
- [ ] Más tipos de enemigos
- [ ] Nuevos jefes
- [ ] Sistema de power-ups temporales
- [ ] Guardado de partidas
- [ ] Leaderboards
- [ ] Modo difícil/fácil
- [ ] Personalización de vehículo
- [ ] Más mapas
- [ ] Música de fondo
- [ ] Achievements

---

**Fecha de Creación**: 29 de noviembre de 2025  
**Última Actualización**: 2 de diciembre de 2025  
**Versión del Documento**: 1.0  
**Autor**: Equipo de Desarrollo Atropellalo
