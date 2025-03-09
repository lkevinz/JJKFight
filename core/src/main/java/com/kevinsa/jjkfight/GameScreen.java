package com.kevinsa.jjkfight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class GameScreen implements Screen {
    private JJKFight game; // Referencia a la clase principal (JJKFight extiende Game)
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture fondo;

    // Animación idle: array de 3 frames (por ejemplo, en "static")
    private TextureRegion[] idleFrames;
    private static final int NUM_IDLE_FRAMES = 3;

    // Frames para volar, caer y dash
    private TextureRegion flyFrame;   // usado cuando se presiona W (volar)
    private TextureRegion fallFrame;  // usado cuando se presiona S o se cae
    private TextureRegion dashFrame;  // usado cuando se mueve lateralmente o se vuela en diagonal

    // Escala del personaje
    private static final float SCALE = 0.75f;

    // Posición del personaje (esquina superior izquierda)
    private float personajeX, personajeY;

    // Tamaño original y escalado
    private float personajeOriginalAncho, personajeOriginalAlto;
    private float personajeAncho, personajeAlto;

    // Velocidad horizontal
    private float velocidad = 600;

    // Variables para movimiento vertical y gravedad
    private float verticalVelocity = 0;
    private static final float GRAVITY = -600;

    // Grosor de las paredes
    private static final float WALL_THICKNESS = 50;

    // Rectángulo de colisión (único)
    private Rectangle personajeRect;

    // Ajustes de colisión interna (porcentajes)
    private static final float COLLISION_WIDTH_PERCENT = 0.20f;
    private static final float COLLISION_HEIGHT_PERCENT = 0.68f;

    // Animación idle
    private float idleTime = 0;
    private static final float FRAME_DURATION = 0.5f;

    // Dimensiones fijas del juego
    private static final int GAME_WIDTH = 1600;
    private static final int GAME_HEIGHT = 900;

    // Variable para flip (si el personaje mira a la derecha)
    private boolean facingRight = true;

    public GameScreen(JJKFight game) {
        this.game = game;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Carga del fondo (por ejemplo, "fondoShibuya.png" en assets)
        fondo = new Texture(Gdx.files.internal("fondoShibuya.png"));

        // Cargar los 3 frames de idle (por ejemplo, en assets/static)
        idleFrames = new TextureRegion[NUM_IDLE_FRAMES];
        for (int i = 0; i < NUM_IDLE_FRAMES; i++) {
            idleFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("static/gojo(" + (i + 1) + ").png"))
            );
        }

        // Cargar el frame de volar (fly) y caer (fall) (por ejemplo, en assets/fly)
        flyFrame = new TextureRegion(new Texture(Gdx.files.internal("fly/gojo(1).png")));
        fallFrame = new TextureRegion(new Texture(Gdx.files.internal("fly/gojo(2).png")));

        // Cargar el frame de dash (por ejemplo, en assets/dash)
        dashFrame = new TextureRegion(new Texture(Gdx.files.internal("dash/gojo(1).png")));

        // Usar las dimensiones del primer frame idle para calcular el tamaño escalado
        personajeOriginalAncho = idleFrames[0].getTexture().getWidth();
        personajeOriginalAlto  = idleFrames[0].getTexture().getHeight();
        personajeAncho = personajeOriginalAncho * SCALE;
        personajeAlto  = personajeOriginalAlto * SCALE;

        // Posición inicial (centrado en X y en el "suelo" definido por WALL_THICKNESS)
        personajeX = (GAME_WIDTH - personajeAncho) / 2f;
        personajeY = WALL_THICKNESS;

        // Crear el rectángulo de colisión
        personajeRect = new Rectangle(personajeX, personajeY, personajeAncho, personajeAlto);
    }

    @Override
    public void render(float delta) {
        // Actualizamos la lógica de movimiento y colisiones
        // Variables de control para determinar el estado del personaje
        boolean movingSide = false; // si se mueve lateralmente
        boolean flying = false;     // si se presiona W
        boolean sideFlying = false; // si se mueve diagonalmente (W + A/D)

        // Movimiento horizontal
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            personajeX -= velocidad * delta;
            facingRight = false;
            movingSide = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            personajeX += velocidad * delta;
            facingRight = true;
            movingSide = true;
        }

        // Movimiento vertical
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            personajeY += velocidad * delta;
            verticalVelocity = 0;
            flying = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            float nuevaY = personajeY - velocidad * delta;
            if (nuevaY < WALL_THICKNESS) {
                personajeY = WALL_THICKNESS;
                verticalVelocity = 0;
            } else {
                personajeY = nuevaY;
            }
        } else {
            verticalVelocity += GRAVITY * delta;
            personajeY += verticalVelocity * delta;
        }

        // Si se vuela y se mueve lateralmente, se considera sideFlying
        if (flying && movingSide) {
            sideFlying = true;
        }

        // Actualización del rectángulo de colisión (usamos el mismo cálculo)
        float collisionWidth = personajeAncho * COLLISION_WIDTH_PERCENT;
        float collisionHeight = personajeAlto * COLLISION_HEIGHT_PERCENT;
        float offsetX = (personajeAncho - collisionWidth) / 2f;
        float offsetY = (personajeAlto - collisionHeight) / 2f;
        personajeRect.set(
            personajeX + offsetX,
            personajeY + offsetY,
            collisionWidth,
            collisionHeight
        );

        // Definir paredes fijas en 1600x900
        Rectangle leftWall = new Rectangle(0, 0, 2, GAME_HEIGHT);
        Rectangle rightWall = new Rectangle(GAME_WIDTH - 2, 0, 2, GAME_HEIGHT);
        Rectangle topWall = new Rectangle(0, GAME_HEIGHT - 2, GAME_WIDTH, 2);
        Rectangle bottomWall = new Rectangle(0, 0, GAME_WIDTH, WALL_THICKNESS);

        // Comprobación de colisiones
        if (personajeRect.overlaps(leftWall)) {
            personajeX = leftWall.x + leftWall.width - offsetX;
        }
        if (personajeRect.overlaps(rightWall)) {
            personajeX = rightWall.x - personajeAncho + offsetX;
        }
        if (personajeRect.overlaps(topWall)) {
            personajeY = topWall.y - personajeAlto + offsetY;
            verticalVelocity = 0;
        }
        if (personajeRect.overlaps(bottomWall)) {
            personajeY = bottomWall.y + bottomWall.height - offsetY;
            verticalVelocity = 0;
        }

        // Seleccionar el frame actual según el estado:
        TextureRegion currentFrame;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            // Si se vuela en diagonal (W+A o W+D) se usa dashFrame
            currentFrame = (sideFlying) ? dashFrame : flyFrame;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)
            || (!Gdx.input.isKeyPressed(Input.Keys.W) && verticalVelocity < 0)) {
            currentFrame = fallFrame;
        } else if (movingSide) {
            // Si solo se mueve lateralmente
            currentFrame = dashFrame;
        } else {
            // Caso idle: animación con 3 frames
            boolean idle = !(Gdx.input.isKeyPressed(Input.Keys.A)
                || Gdx.input.isKeyPressed(Input.Keys.D)
                || Gdx.input.isKeyPressed(Input.Keys.W)
                || Gdx.input.isKeyPressed(Input.Keys.S));
            if (idle) {
                idleTime += delta;
            } else {
                idleTime = 0;
            }
            int frameIndex = (int)(idleTime / FRAME_DURATION) % NUM_IDLE_FRAMES;
            currentFrame = idleFrames[frameIndex];
        }

        // Renderizado
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(fondo, 0, 0, GAME_WIDTH, GAME_HEIGHT);
        // Dibujar el personaje con flip horizontal si es necesario
        if (facingRight) {
            batch.draw(currentFrame, personajeX, personajeY, personajeAncho, personajeAlto);
        } else {
            batch.draw(currentFrame, personajeX + personajeAncho, personajeY, -personajeAncho, personajeAlto);
        }
        batch.end();

        // Dibujo de debug: paredes y rectángulo de colisión
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(leftWall.x, leftWall.y, leftWall.width, leftWall.height);
        shapeRenderer.rect(rightWall.x, rightWall.y, rightWall.width, rightWall.height);
        shapeRenderer.rect(topWall.x, topWall.y, topWall.width, topWall.height);
        shapeRenderer.rect(bottomWall.x, bottomWall.y, bottomWall.width, bottomWall.height);
        shapeRenderer.rect(personajeRect.x, personajeRect.y, personajeRect.width, personajeRect.height);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        // Ignoramos cambios de tamaño
    }

    @Override
    public void show() { }
    @Override
    public void hide() { }
    @Override
    public void pause() { }
    @Override
    public void resume() { }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        fondo.dispose();
        for (TextureRegion region : idleFrames) {
            region.getTexture().dispose();
        }
        flyFrame.getTexture().dispose();
        fallFrame.getTexture().dispose();
        dashFrame.getTexture().dispose();
    }
}
