# Atropellalo - Survivor Game

Juego estilo Vampire Survivors desarrollado en Java usando Swing.

## Requisitos

- Java 11 o superior
- Maven 3.6+

## Inicio Rápido

```powershell
# Compilar y ejecutar
mvn clean compile
mvn exec:java "-Dexec.mainClass=com.atropellalo.Game"
```

## Generar Ejecutable

```powershell
mvn clean package
java -jar target/atropellalo-game-1.0-SNAPSHOT.jar
```

## Estado Actual

✅ Ventana del juego (1280x720)  
✅ Mapa de fondo más grande que la ventana (2560x1440)  
✅ Jugador controlable (cuadrado rojo - prototipo)  
✅ Movimiento con WASD  
✅ Cámara que sigue al jugador  
✅ Game loop a 60 FPS  
✅ Renderizado con calidad (antialiasing)

### Controles
- **W**: Mover arriba
- **A**: Mover izquierda  
- **S**: Mover abajo
- **D**: Mover derecha

## Documentación Completa

Ver [DOCUMENTATION.md](DOCUMENTATION.md) para detalles técnicos, arquitectura y próximos pasos.

## Estructura del Proyecto

```
src/main/
├── java/com/atropellalo/
│   ├── Game.java              # Punto de entrada
│   └── game/
│       ├── camera/
│       │   └── Camera.java            # Sistema de cámara
│       ├── entity/
│       │   └── Player.java            # Jugador
│       ├── input/
│       │   └── InputHandler.java      # Controles WASD
│       ├── ui/
│       │   ├── GameWindow.java        # Ventana principal
│       │   └── GamePanel.java         # Game loop y renderizado
│       └── util/
│           └── MapGenerator.java      # Generador de mapa
└── resources/images/
    └── map.jpg                # Mapa de 2560x1440
```

## Licencia

Este proyecto se desarrolla con fines educativos.
