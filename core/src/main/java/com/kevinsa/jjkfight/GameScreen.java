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
    private JJKFight game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture fondo;

    // -------------------- GOJO --------------------
    private TextureRegion[] gojoIdleFrames;
    private static final int NUM_GOJO_IDLE_FRAMES = 3;
    private TextureRegion gojoFlyFrame;
    private TextureRegion gojoFallFrame;
    private TextureRegion gojoDashFrame;
    private float gojoX, gojoY;
    private float gojoOriginalWidth, gojoOriginalHeight;
    private float gojoWidth, gojoHeight;
    private float gojoVerticalVelocity = 0;
    private float gojoIdleTime = 0;
    private boolean gojoFlip = false;
    private Rectangle gojoRect;

    // -------------------- SUKUNA --------------------
    private TextureRegion[] sukunaIdleFrames;
    private static final int NUM_SU_KUNA_IDLE_FRAMES = 3;
    private TextureRegion sukunaFlyFrame;
    private TextureRegion sukunaFallFrame;
    private TextureRegion sukunaDashFrame;
    private float sukunaX, sukunaY;
    private float sukunaOriginalWidth, sukunaOriginalHeight;
    private float sukunaWidth, sukunaHeight;
    private float sukunaVerticalVelocity = 0;
    private float sukunaIdleTime = 0;
    private boolean sukunaFlip = true;
    private Rectangle sukunaRect;

    // -------------------- VIDA --------------------
    // Se espera tener 18 imágenes para cada personaje (de 0 a 17)
    private Texture[] gojoLifeTextures;
    private Texture[] sukunaLifeTextures;
    // Índices de vida (0 es el estado inicial, y se incrementa al recibir golpes)
    private int gojoLifeIndex = 0;
    private int sukunaLifeIndex = 0;

    // -------------------- Comunes --------------------
    private static final float SCALE = 0.75f;
    private static final float SPEED = 600;
    // Sin gravedad (se mantiene en 0)
    private static final float GRAVITY = 0;
    // Aquí se establece el grosor de las paredes en 0 para evitar restricciones
    private static final float WALL_THICKNESS = 0;
    private static final float FRAME_DURATION = 0.5f;
    private static final int GAME_WIDTH = 1600;
    private static final int GAME_HEIGHT = 900;
    // Porcentajes para el rectángulo de colisión
    private static final float COLLISION_WIDTH_PERCENT = 0.20f;
    private static final float COLLISION_HEIGHT_PERCENT = 0.68f;

    public GameScreen(JJKFight game) {
        this.game = game;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Cargar fondo
        fondo = new Texture(Gdx.files.internal("fondoShibuya.png"));

        // ---------------- Cargar imágenes de vida ----------------
        // Se asume que están en la carpeta "vida/"
        gojoLifeTextures = new Texture[18];
        sukunaLifeTextures = new Texture[18];
        for (int i = 0; i < 18; i++) {
            gojoLifeTextures[i] = new Texture(Gdx.files.internal("vida/gojo(" + i + ").png"));
            sukunaLifeTextures[i] = new Texture(Gdx.files.internal("vida/sukuna(" + i + ").png"));
        }

        // ---------------- Cargar imágenes de Gojo ----------------
        gojoIdleFrames = new TextureRegion[NUM_GOJO_IDLE_FRAMES];
        for (int i = 0; i < NUM_GOJO_IDLE_FRAMES; i++) {
            gojoIdleFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("static/gojo(" + (i + 1) + ").png"))
            );
        }
        gojoFlyFrame = new TextureRegion(new Texture(Gdx.files.internal("fly/gojo(1).png")));
        gojoFallFrame = new TextureRegion(new Texture(Gdx.files.internal("fly/gojo(2).png")));
        gojoDashFrame = new TextureRegion(new Texture(Gdx.files.internal("dash/gojo(1).png")));

        gojoOriginalWidth = gojoIdleFrames[0].getTexture().getWidth();
        gojoOriginalHeight = gojoIdleFrames[0].getTexture().getHeight();
        gojoWidth = gojoOriginalWidth * SCALE;
        gojoHeight = gojoOriginalHeight * SCALE;

        // Posición inicial de Gojo
        gojoX = 50;
        gojoY = 0;
        gojoRect = new Rectangle(gojoX, gojoY, gojoWidth, gojoHeight);

        // ---------------- Cargar imágenes de Sukuna ----------------
        sukunaIdleFrames = new TextureRegion[NUM_SU_KUNA_IDLE_FRAMES];
        for (int i = 0; i < NUM_SU_KUNA_IDLE_FRAMES; i++) {
            sukunaIdleFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("static/sukuna(" + (i + 1) + ").png"))
            );
        }
        sukunaFlyFrame = new TextureRegion(new Texture(Gdx.files.internal("fly/sukuna(1).png")));
        sukunaFallFrame = new TextureRegion(new Texture(Gdx.files.internal("fly/sukuna(2).png")));
        sukunaDashFrame = new TextureRegion(new Texture(Gdx.files.internal("dash/sukuna(1).png")));

        sukunaOriginalWidth = sukunaIdleFrames[0].getTexture().getWidth();
        sukunaOriginalHeight = sukunaIdleFrames[0].getTexture().getHeight();
        sukunaWidth = sukunaOriginalWidth * SCALE;
        sukunaHeight = sukunaOriginalHeight * SCALE;

        // Posición inicial de Sukuna
        sukunaX = GAME_WIDTH - sukunaWidth - 50;
        sukunaY = 0;
        sukunaRect = new Rectangle(sukunaX, sukunaY, sukunaWidth, sukunaHeight);
    }

    @Override
    public void resize(int width, int height) {
        // Ignoramos cambios de tamaño para mantener dimensiones fijas
    }

    @Override
    public void render(float delta) {
        // ---------------- Actualizar controles de Gojo ----------------
        boolean gojoMovingSide = false;
        boolean gojoFlying = false;
        boolean gojoSideFlying = false;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            gojoX -= SPEED * delta;
            gojoMovingSide = true;
            gojoFlip = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            gojoX += SPEED * delta;
            gojoMovingSide = true;
            gojoFlip = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            gojoY += SPEED * delta;
            gojoVerticalVelocity = 0;
            gojoFlying = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            float nuevaY = gojoY - SPEED * delta;
            if (nuevaY < WALL_THICKNESS) {
                gojoY = WALL_THICKNESS;
                gojoVerticalVelocity = 0;
            } else {
                gojoY = nuevaY;
            }
        } else {
            gojoVerticalVelocity = 0;
        }
        if (gojoFlying && gojoMovingSide) {
            gojoSideFlying = true;
        }
        gojoRect.set(gojoX, gojoY, gojoWidth, gojoHeight);

        // ---------------- Actualizar controles de Sukuna ----------------
        boolean sukunaMovingSide = false;
        boolean sukunaFlying = false;
        boolean sukunaSideFlying = false;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            sukunaX -= SPEED * delta;
            sukunaMovingSide = true;
            sukunaFlip = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            sukunaX += SPEED * delta;
            sukunaMovingSide = true;
            sukunaFlip = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            sukunaY += SPEED * delta;
            sukunaVerticalVelocity = 0;
            sukunaFlying = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            float nuevaY = sukunaY - SPEED * delta;
            if (nuevaY < WALL_THICKNESS) {
                sukunaY = WALL_THICKNESS;
                sukunaVerticalVelocity = 0;
            } else {
                sukunaY = nuevaY;
            }
        } else {
            sukunaVerticalVelocity = 0;
        }
        if (sukunaFlying && sukunaMovingSide) {
            sukunaSideFlying = true;
        }
        sukunaRect.set(sukunaX, sukunaY, sukunaWidth, sukunaHeight);

        // ---------------- Calcular rectángulos de colisión para cada personaje ----------------
        float gojoCollisionWidth = gojoWidth * COLLISION_WIDTH_PERCENT;
        float gojoCollisionHeight = gojoHeight * COLLISION_HEIGHT_PERCENT;
        float gojoOffsetX = (gojoWidth - gojoCollisionWidth) / 2f;
        float gojoOffsetY = (gojoHeight - gojoCollisionHeight) / 2f;
        Rectangle activeGojoRect = new Rectangle(gojoX + gojoOffsetX, gojoY + gojoOffsetY, gojoCollisionWidth, gojoCollisionHeight);

        float sukunaCollisionWidth = sukunaWidth * COLLISION_WIDTH_PERCENT;
        float sukunaCollisionHeight = sukunaHeight * COLLISION_HEIGHT_PERCENT;
        float sukunaOffsetX = (sukunaWidth - sukunaCollisionWidth) / 2f;
        float sukunaOffsetY = (sukunaHeight - sukunaCollisionHeight) / 2f;
        Rectangle activeSukunaRect = new Rectangle(sukunaX + sukunaOffsetX, sukunaY + sukunaOffsetY, sukunaCollisionWidth, sukunaCollisionHeight);

        // ---------------- Definir paredes fijas ----------------
        Rectangle leftWall = new Rectangle(0, 0, 2, GAME_HEIGHT);
        Rectangle rightWall = new Rectangle(GAME_WIDTH - 2, 0, 2, GAME_HEIGHT);
        // Ejemplo: pared superior e inferior (ajusta según necesites)
        Rectangle topWall = new Rectangle(0, 740, GAME_WIDTH, 100);
        Rectangle bottomWall = new Rectangle(0, 0, GAME_WIDTH, WALL_THICKNESS);

        // ---------------- Comprobación de colisiones contra paredes para Gojo ----------------
        if (activeGojoRect.overlaps(leftWall)) {
            gojoX = leftWall.x + leftWall.width - gojoOffsetX;
        }
        if (activeGojoRect.overlaps(rightWall)) {
            gojoX = rightWall.x - gojoWidth + gojoOffsetX;
        }
        if (activeGojoRect.overlaps(topWall)) {
            gojoY = topWall.y - gojoHeight + gojoOffsetY;
            gojoVerticalVelocity = 0;
        }
        if (activeGojoRect.overlaps(bottomWall)) {
            gojoY = bottomWall.y + bottomWall.height - gojoOffsetY;
            gojoVerticalVelocity = 0;
        }
        gojoRect.set(gojoX, gojoY, gojoWidth, gojoHeight);

        // ---------------- Comprobación de colisiones contra paredes para Sukuna ----------------
        if (activeSukunaRect.overlaps(leftWall)) {
            sukunaX = leftWall.x + leftWall.width - sukunaOffsetX;
        }
        if (activeSukunaRect.overlaps(rightWall)) {
            sukunaX = rightWall.x - sukunaWidth + sukunaOffsetX;
        }
        if (activeSukunaRect.overlaps(topWall)) {
            sukunaY = topWall.y - sukunaHeight + sukunaOffsetY;
            sukunaVerticalVelocity = 0;
        }
        if (activeSukunaRect.overlaps(bottomWall)) {
            sukunaY = bottomWall.y + bottomWall.height - sukunaOffsetY;
            sukunaVerticalVelocity = 0;
        }
        sukunaRect.set(sukunaX, sukunaY, sukunaWidth, sukunaHeight);

        // ---------------- Definir rectángulos de colisión "post" sin evitar solapamiento ----------------
        Rectangle activeGojoRectPost = new Rectangle(gojoX + gojoOffsetX, gojoY + gojoOffsetY, gojoCollisionWidth, gojoCollisionHeight);
        Rectangle activeSukunaRectPost = new Rectangle(sukunaX + sukunaOffsetX, sukunaY + sukunaOffsetY, sukunaCollisionWidth, sukunaCollisionHeight);

        // ---------------- Detectar ataques ----------------
        // Gojo ataca con la tecla C (si se presiona y los campos de colisión se solapan, baja la vida de Sukuna)
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            if (activeGojoRectPost.overlaps(activeSukunaRectPost)) {
                if (sukunaLifeIndex < 17) {
                    sukunaLifeIndex++;
                }
            }
        }
        // Sukuna ataca con la tecla L
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            if (activeSukunaRectPost.overlaps(activeGojoRectPost)) {
                if (gojoLifeIndex < 17) {
                    gojoLifeIndex++;
                }
            }
        }

        // ---------------- Seleccionar el frame actual para cada personaje ----------------
        TextureRegion gojoFrame;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            gojoFrame = (gojoSideFlying) ? gojoDashFrame : gojoFlyFrame;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)
            || (!Gdx.input.isKeyPressed(Input.Keys.W) && gojoVerticalVelocity < 0)) {
            gojoFrame = gojoFallFrame;
        } else if (gojoMovingSide) {
            gojoFrame = gojoDashFrame;
        } else {
            boolean idle = !(Gdx.input.isKeyPressed(Input.Keys.A)
                || Gdx.input.isKeyPressed(Input.Keys.D)
                || Gdx.input.isKeyPressed(Input.Keys.W)
                || Gdx.input.isKeyPressed(Input.Keys.S));
            if (idle) {
                gojoIdleTime += delta;
            } else {
                gojoIdleTime = 0;
            }
            int frameIndex = (int)(gojoIdleTime / FRAME_DURATION) % NUM_GOJO_IDLE_FRAMES;
            gojoFrame = gojoIdleFrames[frameIndex];
        }

        TextureRegion sukunaFrame;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            sukunaFrame = (sukunaSideFlying) ? sukunaDashFrame : sukunaFlyFrame;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)
            || (!Gdx.input.isKeyPressed(Input.Keys.UP) && sukunaVerticalVelocity < 0)) {
            sukunaFrame = sukunaFallFrame;
        } else if (sukunaMovingSide) {
            sukunaFrame = sukunaDashFrame;
        } else {
            boolean idle = !(Gdx.input.isKeyPressed(Input.Keys.LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                || Gdx.input.isKeyPressed(Input.Keys.UP)
                || Gdx.input.isKeyPressed(Input.Keys.DOWN));
            if (idle) {
                sukunaIdleTime += delta;
            } else {
                sukunaIdleTime = 0;
            }
            int frameIndex = (int)(sukunaIdleTime / FRAME_DURATION) % NUM_SU_KUNA_IDLE_FRAMES;
            sukunaFrame = sukunaIdleFrames[frameIndex];
        }

        // ---------------- Renderizado ----------------
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(fondo, 0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Dibujar Gojo:
        if (gojoFlip) {
            batch.draw(gojoFrame, gojoX + gojoWidth, gojoY, -gojoWidth, gojoHeight);
        } else {
            batch.draw(gojoFrame, gojoX, gojoY, gojoWidth, gojoHeight);
        }
        // Dibujar Sukuna:
        if (sukunaFlip) {
            batch.draw(sukunaFrame, sukunaX + sukunaWidth, sukunaY, -sukunaWidth, sukunaHeight);
        } else {
            batch.draw(sukunaFrame, sukunaX, sukunaY, sukunaWidth, sukunaHeight);
        }

        // Dibujar las barras de vida
        Texture currentGojoLife = gojoLifeTextures[gojoLifeIndex];
        Texture currentSukunaLife = sukunaLifeTextures[sukunaLifeIndex];
        // Posiciones para las barras de vida (ajusta según necesites)
        float gojoLifeX = 0;
        float gojoLifeY = 0;
        float sukunaLifeX = 0;
        float sukunaLifeY = 0;
        batch.draw(currentGojoLife, gojoLifeX, gojoLifeY);
        batch.draw(currentSukunaLife, sukunaLifeX, sukunaLifeY);

        batch.end();

        // ---------------- Renderizado de Debug ----------------
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(leftWall.x, leftWall.y, leftWall.width, leftWall.height);
        shapeRenderer.rect(rightWall.x, rightWall.y, rightWall.width, rightWall.height);
        shapeRenderer.rect(topWall.x, topWall.y, topWall.width, topWall.height);
        shapeRenderer.rect(bottomWall.x, bottomWall.y, bottomWall.width, bottomWall.height);
        shapeRenderer.rect(activeGojoRectPost.x, activeGojoRectPost.y, activeGojoRectPost.width, activeGojoRectPost.height);
        shapeRenderer.rect(activeSukunaRectPost.x, activeSukunaRectPost.y, activeSukunaRectPost.width, activeSukunaRectPost.height);
        shapeRenderer.end();
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
        for (TextureRegion region : gojoIdleFrames) {
            region.getTexture().dispose();
        }
        gojoFlyFrame.getTexture().dispose();
        gojoFallFrame.getTexture().dispose();
        gojoDashFrame.getTexture().dispose();

        for (TextureRegion region : sukunaIdleFrames) {
            region.getTexture().dispose();
        }
        sukunaFlyFrame.getTexture().dispose();
        sukunaFallFrame.getTexture().dispose();
        sukunaDashFrame.getTexture().dispose();
        // Disponer las texturas de vida
        for (Texture t : gojoLifeTextures) {
            t.dispose();
        }
        for (Texture t : sukunaLifeTextures) {
            t.dispose();
        }
    }
}
