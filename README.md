# JJKFight

JJKFight es un juego 2D de lucha inspirado en la épica batalla entre **Gojo Satoru** y **Ryomen Sukuna** del manga *Jujutsu Kaisen*. Este proyecto se creó utilizando [libGDX](https://libgdx.com/) y fue generado inicialmente con [gdx-liftoff](https://github.com/libgdx/gdx-liftoff). El juego pone énfasis en el dinamismo de los combates, utilizando animaciones, colisiones precisas y efectos especiales (como proyectiles y explosiones) para recrear el intenso enfrentamiento entre estos dos poderosos personajes.

---

## Plataformas

Este proyecto sigue la estructura de un proyecto libGDX generado con gdx-liftoff y se compone de los siguientes módulos:

- **core:** Contiene la lógica principal del juego compartida entre todas las plataformas.
- **lwjgl3:** Plataforma de escritorio utilizando LWJGL3 (anteriormente llamado "desktop").
- *(Android e iOS pueden integrarse con las configuraciones adecuadas, aunque en este ejemplo se centra en la versión de escritorio.)*

---

## Funcionalidad del Juego

### Inspiración y Concepto

El juego está inspirado en la lucha entre **Gojo Satoru** y **Ryomen Sukuna**. Cada personaje tiene sus propias animaciones de movimiento, ataques y estados (incluyendo animaciones de poder y muerte). La mecánica especial de Gojo consiste en un ataque "power" que, al finalizar su animación, lanza un proyectil (con imagen `rojo(1).png`) dirigido a Sukuna. Al impactar, el proyectil explota (reproduciendo la secuencia de explosión con `rojo(2).png`, `rojo(3).png` y `rojo(4).png`) y le quita 3 puntos de vida a Sukuna.

### Mecánicas y Dinámica de Juego

- **Animaciones de Movimiento:**  
  Ambos personajes cuentan con animaciones para estar en reposo (idle), volar, caer y desplazarse (dash).  
  - Los fotogramas de idle se cargan desde la carpeta `static/`.
  - Las animaciones de vuelo, caída y dash provienen de las carpetas `fly/`, `fall/` y `dash/` respectivamente.

- **Ataques:**  
  - **Ataque Normal:**  
    Cada personaje posee una animación de ataque de 5 fotogramas.  
  - **Ataque Power de Gojo (Tecla V):**  
    Al presionar **V**, se activa una animación de power de 9 fotogramas. Una vez terminada, se lanza un proyectil que se mueve hacia Sukuna.
  - **Ataque de Sukuna (Tecla L):**  
    Se inicia la animación de ataque de Sukuna, causando daño a Gojo si se detecta colisión.

- **Proyectil y Explosión:**  
  - El proyectil se lanza tras la animación de power y se posiciona muy cerca de Gojo, dirigiéndose hacia el centro de Sukuna.
  - Si el proyectil choca con cualquier pared o con el área de Sukuna, se activa una animación de explosión (3 fotogramas) y, si impacta a Sukuna, se descuentan 3 puntos de vida.

- **Colisiones:**  
  Se utilizan rectángulos para detectar colisiones:
  - Los rectángulos de colisión de Gojo y Sukuna se calculan en base a un porcentaje de sus dimensiones.
  - Se muestran en modo debug (en color verde) todas las colisiones, incluyendo la del proyectil.

---

## Controles

### Durante el Combate (GameScreen)

- **Gojo Satoru (Jugador 1):**
  - **A:** Moverse a la izquierda.
  - **D:** Moverse a la derecha.
  - **W:** Moverse hacia arriba (vuelo).
  - **S:** Moverse hacia abajo (caída).
  - **V:** Iniciar la animación de power y lanzar el proyectil dirigido a Sukuna.

- **Ryomen Sukuna (Enemigo):**
  - **Flechas (Izquierda, Derecha, Arriba, Abajo):** Moverse en la dirección correspondiente.
  - **L:** Iniciar la animación de ataque.

### Menú y Configuración

- **Menú Principal (MenuScreen):**
  - **Play:** Inicia la partida (cambia a GameScreen).
  - **Settings:** Abre la pantalla de configuraciones.
  - **Exit:** Cierra el juego.
  - **Botón de Sonido:** Alterna entre sonido activado y desactivado.

- **Pantalla de Configuración (SettingsScreen):**
  - **Flecha Izquierda/Derecha:** Ajusta el nivel de volumen (con valores de 0 a 100).
  - **Botón de Retorno:** Vuelve al menú principal y aplica el volumen seleccionado.

---

## Pantallas del Juego

- **Lwjgl3Launcher.java:**  
  Configura la ventana del juego (título, dimensiones, íconos) y lanza la aplicación utilizando LWJGL3.

- **JJKFight.java:**  
  Clase principal que extiende de `Game` y, al iniciar, muestra la pantalla de menú.

- **MenuScreen.java:**  
  Pantalla de inicio que ofrece opciones para jugar, ajustar configuraciones, salir y alternar el sonido.

- **SettingsScreen.java:**  
  Permite al jugador ajustar el volumen del juego mediante botones y teclas (izquierda y derecha).

- **GameScreen.java:**  
  Pantalla principal de combate donde se desarrollan:
  - Movimiento y animaciones de Gojo y Sukuna.
  - Gestión de ataques (normal y power).
  - Lógica de lanzamiento y movimiento del proyectil dirigido a Sukuna.
  - Detección de colisiones, actualización de vidas y efectos de explosión.
  - Visualización de rectángulos de colisión en modo debug.

- **EndScreen.java:**  
  Pantalla final que muestra el ganador de la pelea (determinado por el índice de vida alcanzado) y ofrece un botón para volver al menú.

---

## Estructura y Ejecución

### Estructura del Proyecto

El proyecto está organizado en módulos siguiendo la plantilla de gdx-liftoff:

- **core:** Lógica compartida del juego.
- **lwjgl3:** Plataforma de escritorio para ejecutar el juego.
- *(Adicionalmente, el proyecto puede configurarse para Android y iOS.)*

### Compilación y Ejecución

El proyecto utiliza [Gradle](https://gradle.org/) para la gestión de dependencias. Algunos comandos útiles son:

- `./gradlew clean` – Elimina carpetas de compilación.
- `./gradlew lwjgl3:run` – Ejecuta la aplicación en modo escritorio.

Para iniciar el juego, ejecuta el archivo **Lwjgl3Launcher.java**. Esto abrirá una ventana de 1600 x 900 píxeles, sin posibilidad de redimensionar, y mostrará la pantalla de menú.

---

## Créditos y Referencias

- **Inspiración:**  
  El juego se inspira en la épica pelea entre **Gojo Satoru** y **Ryomen Sukuna** del manga *Jujutsu Kaisen*.
  
- **Tecnología:**  
  Utiliza [libGDX](https://libgdx.com/), un framework de desarrollo de juegos en Java.

- **Plantilla:**  
  El proyecto se generó inicialmente con [gdx-liftoff](https://github.com/libgdx/gdx-liftoff), lo que permite una estructura modular para múltiples plataformas.

- **Recursos:**  
  Las imágenes, sonidos y demás recursos están organizados en carpetas específicas (por ejemplo, `static/`, `fly/`, `fight/`, `power/`, `dead/`, `menu/`, etc.).

---

Este README.md ofrece una descripción detallada del juego JJKFight, sus mecánicas, controles y la estructura de sus pantallas. Puedes utilizarlo como base para documentar y explicar el funcionamiento del proyecto a futuros colaboradores o para uso personal.
