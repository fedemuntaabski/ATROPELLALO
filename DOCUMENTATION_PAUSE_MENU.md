# Documentación - Menú de Pausa

## Resumen
Implementación de un menú de pausa activado con la tecla **ESC** que muestra estadísticas del jugador y armas, con opciones para reanudar o salir del juego.

---

## Archivos Creados

### 1. `PauseMenu.java`
**Ubicación**: `src/main/java/com/atropellalo/game/ui/PauseMenu.java`

**Descripción**: Clase responsable de la interfaz del menú de pausa.

**Características**:
- **Overlay oscuro semi-transparente** sobre el juego
- **Panel central** con diseño profesional (600x650 px)
- **Área de estadísticas scrolleable** (450px de altura)
- **Dos botones**: "Reanudar" y "Salir"
- **Navegación por teclado**: Flechas/WASD para scroll y selección, Enter/Espacio para confirmar

**Estadísticas mostradas**:

#### Jugador:
- Nivel
- Experiencia (actual / necesaria)
- Salud (actual / máxima)
- Combustible (actual / máximo)
- Velocidad

#### Armas (por cada arma equipada):
- Nombre del arma
- Daño
- Cadencia de fuego (disparos/segundo)
- Velocidad de proyectil (si aplica)
- Rango
- Nivel del arma

#### Estadísticas Globales:
- Disparos realizados
- Disparos acertados
- Precisión (%)
- Proyectiles activos

**Principios aplicados**:
- **Alta cohesión**: Toda la lógica del menú de pausa en una única clase
- **Bajo acoplamiento**: Interface `PauseMenuCallback` para comunicación con `GamePanel`
- **KISS**: Interfaz simple y directa
- **DRY**: Métodos reutilizables para renderizado de botones y estadísticas

---

## Archivos Modificados

### 1. `Weapon.java`
**Cambios realizados**:
- ✅ Agregado campo `level` para trackear el nivel del arma
- ✅ Inicialización de `level = 1` en constructores
- ✅ Método `upgradeLevel()` para incrementar nivel
- ✅ Método `getName()` que retorna el nombre del arma
- ✅ Método `getProjectileSpeed()` (retorna 0 por defecto, override en subclases si aplica)
- ✅ Método `getFireRate()` que retorna el delay entre disparos
- ✅ Getter `getLevel()` para obtener el nivel del arma

**Justificación**: Necesarios para mostrar información detallada de las armas en el menú de pausa.

---

### 2. `GamePanel.java`
**Cambios realizados**:
- ✅ Agregado campo `pausedByMenu` para distinguir pausa de menú vs pausa por nivel
- ✅ Instancia de `PauseMenu` creada en `initializeGame()`
- ✅ Configuración de callback del menú con métodos `resumeGame()` y `quitToDesktop()`
- ✅ Método `showPauseMenu()` para mostrar el menú (verifica que no esté muerto o ya pausado)
- ✅ Método `resumeGame()` para reanudar desde el menú
- ✅ Método `quitToDesktop()` para salir del juego
- ✅ Actualización en `update()` para considerar `pausedByMenu`
- ✅ Renderizado del menú en `paintComponent()`
- ✅ Manejo de ESC en `keyPressed()` con prioridad al menú de pausa

**Lógica de pausa**:
```java
// No actualiza el juego si:
if (!player.isAlive() || paused || pausedByMenu) {
    return;
}
```

**Prioridad de teclas**:
1. Menú de pausa (si está visible)
2. ESC para abrir menú de pausa (si no está pausado por nivel o muerto)
3. Menú de mejoras
4. HUD de reinicio (cuando está muerto)

**Principios aplicados**:
- **Separación de responsabilidades**: El menú de pausa es una clase aparte
- **Bajo acoplamiento**: Comunicación mediante callbacks
- **DRY**: Reutilización de la variable `paused` existente

---

## Controles del Menú de Pausa

| Tecla | Acción |
|-------|--------|
| **ESC** | Abrir/Cerrar menú de pausa |
| **←/→** o **A/D** | Navegar entre botones |
| **↑/↓** o **W/S** | Scroll en estadísticas |
| **Enter** o **Espacio** | Confirmar selección |

---

## Flujo de Uso

1. **Durante el juego**, presionar **ESC**
2. El juego se pausa automáticamente
3. Se muestra el menú con estadísticas
4. El jugador puede:
   - Hacer scroll para ver todas las estadísticas
   - Seleccionar "Reanudar" para continuar
   - Seleccionar "Salir" para cerrar el juego
5. Al reanudar, el juego continúa exactamente donde se pausó

---

## Diseño Visual

### Colores:
- **Overlay**: Negro semi-transparente (alpha 180)
- **Panel principal**: Gris oscuro (30, 30, 40)
- **Área de stats**: Gris muy oscuro (20, 20, 30)
- **Botones**: Azul acero (70, 130, 180)
- **Botones hover**: Azul más claro (100, 160, 210)
- **Texto**: Blanco
- **Valores de stats**: Verde claro (100, 255, 100)

### Fuentes:
- **Título**: Arial Bold 36px
- **Secciones**: Arial Bold 18px
- **Stats**: Arial Plain 14px
- **Botones**: Arial Bold 18px

---

## Validaciones

✅ No se puede pausar cuando el jugador está muerto  
✅ No se puede pausar cuando el menú de mejoras está abierto  
✅ El scroll está limitado al contenido disponible  
✅ Los indicadores de scroll (↑/↓) solo aparecen cuando hay contenido oculto  
✅ Al cerrar el menú, el juego retoma exactamente donde quedó  

---

## Mejores Prácticas Aplicadas

1. **Responsabilidad Única**: `PauseMenu` solo maneja la interfaz de pausa
2. **Encapsulamiento**: Toda la lógica de renderizado y estado dentro de `PauseMenu`
3. **Callback Pattern**: Comunicación desacoplada con `GamePanel`
4. **Inmutabilidad**: Las listas de armas se copian antes de retornar
5. **Validación de estado**: Verifica condiciones antes de pausar
6. **Código limpio**: Nombres descriptivos, métodos pequeños y enfocados
7. **Comentarios Javadoc**: Documentación completa de métodos públicos

---

## Testing Manual Recomendado

- [ ] Pausar con ESC durante juego normal
- [ ] Scroll con W/S y flechas arriba/abajo
- [ ] Navegación de botones con A/D y flechas izquierda/derecha
- [ ] Reanudar con botón "Reanudar"
- [ ] Reanudar presionando ESC nuevamente
- [ ] Salir con botón "Salir"
- [ ] Verificar que las estadísticas se actualizan correctamente
- [ ] Intentar pausar cuando está muerto (no debería funcionar)
- [ ] Intentar pausar durante menú de mejoras (no debería funcionar)

---

## Notas de Implementación

- **Scroll suave**: Incrementos de 30 píxeles por pulsación
- **Diseño responsive**: Centrado dinámico basado en dimensiones de pantalla
- **Anti-aliasing**: Renderizado suavizado para mejor apariencia
- **Clipping**: Se usa `setClip()` para el área scrolleable, evitando renders fuera del área
- **Estado persistente**: El menú mantiene su estado de scroll entre aperturas

---

**Fecha de implementación**: 30 de noviembre de 2025  
**Desarrollado por**: GitHub Copilot  
**Nivel de calidad**: Profesional
