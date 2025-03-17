package com.kevinsa.jjkfight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

public class GameScreen implements Screen {
    private JJKFight game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture fondo;

    // -------------------- Efectos de sonido --------------------
    private Sound readySound;
    private Sound punchSound;

    // -------------------- Música de fondo --------------------
    private Music gameMusic;

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

    // Animación de ataque de Gojo (5 fotogramas)
    private TextureRegion[] gojoAttackFrames;
    private boolean gojoAttacking = false;
    private float gojoAttackTime = 0f;

    // Animación de power de Gojo (9 fotogramas)
    private TextureRegion[] gojoPowerFrames;
    private static final int NUM_GOJO_POWER_FRAMES = 9;
    private static final float POWER_FRAME_DURATION = 0.05f;
    private boolean gojoPowerActive = false;
    private float gojoPowerTime = 0f;

    // -------------------- SUKUNA --------------------
    private TextureRegion[] sukunaIdleFrames;
    private static final int NUM_SUKUNA_IDLE_FRAMES = 3;
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

    // Animación de ataque de Sukuna (5 fotogramas)
    private TextureRegion[] sukunaAttackFrames;
    private boolean sukunaAttacking = false;
    private float sukunaAttackTime = 0f;

    // -------------------- VIDA --------------------
    private Texture[] gojoLifeTextures;
    private Texture[] sukunaLifeTextures;
    private int gojoLifeIndex = 0;
    private int sukunaLifeIndex = 0;

    // -------------------- NUEVAS VARIABLES PARA MUERTE --------------------
    private Texture gojoDeadTexture;
    private Texture sukunaDeadTexture;
    private static final float DEAD_GRAVITY = -600f;

    // -------------------- Comunes --------------------
    private static final float SCALE = 0.75f;
    private static final float SPEED = 600;
    private static final float GRAVITY = 0;
    private static final float WALL_THICKNESS = 0;
    private static final float FRAME_DURATION = 0.5f;
    private static final int GAME_WIDTH = 1600;
    private static final int GAME_HEIGHT = 900;
    private static final float ATTACK_FRAME_DURATION = 0.07f;
    private static final float COLLISION_WIDTH_PERCENT = 0.30f;
    private static final float COLLISION_HEIGHT_PERCENT = 0.68f;
    private float endScreenDelayTimer = 0f;

    // ------------- NUEVAS VARIABLES PARA EL PROYECTIL -------------
    private boolean projectileActive = false;
    private boolean projectileExploding = false;
    private float projectileX, projectileY;
    private float projectileSpeed = 800; // velocidad del proyectil
    private Circle projectileCircle;
    // Variable para poder redimensionar el área de colisión a tu manera:
    private float projectileCollisionScale = 0.20f; // Ajusta este valor según necesites
    // Variables para almacenar el ancho y alto “virtual” del proyectil
    private float projectileWidth, projectileHeight;
    // Variables para almacenar la dirección fija del proyectil
    private float projectileDirX, projectileDirY;
    private Texture projectileTexture; // imagen rojo(1).png
    private TextureRegion[] explosionFrames; // imágenes rojo(2).png, rojo(3).png, rojo(4).png
    private static final int NUM_EXPLOSION_FRAMES = 3;
    private static final float EXPLOSION_FRAME_DURATION = 0.1f;
    private float explosionTime = 0f;

    public GameScreen(JJKFight game) {
        this.game = game;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Cargar fondo
        fondo = new Texture(Gdx.files.internal("fondoShibuya.png"));

        // ---------------- Cargar sonidos ----------------
        readySound = Gdx.audio.newSound(Gdx.files.internal("fight/ready.mp3"));
        punchSound = Gdx.audio.newSound(Gdx.files.internal("fight/punch.mp3"));

        // ---------------- Cargar música de fondo ----------------
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("game.mp3"));
        gameMusic.setLooping(true);
        gameMusic.setVolume(game.getVolume());
        gameMusic.play();

        // ---------------- Cargar imágenes de vida ----------------
        gojoLifeTextures = new Texture[18];
        sukunaLifeTextures = new Texture[18];
        for (int i = 0; i < 18; i++) {
            gojoLifeTextures[i] = new Texture(Gdx.files.internal("vida/gojo(" + i + ").png"));
            sukunaLifeTextures[i] = new Texture(Gdx.files.internal("vida/sukuna(" + i + ").png"));
        }

        // ---------------- Cargar imágenes de Gojo (Idle, Fly, Fall, Dash) ----------------
        gojoIdleFrames = new TextureRegion[NUM_GOJO_IDLE_FRAMES];
        for (int i = 0; i < NUM_GOJO_IDLE_FRAMES; i++) {
            gojoIdleFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("static/gojo(" + (i + 1) + ").png"))
            );
        }
        gojoFlyFrame = new TextureRegion(new Texture(Gdx.files.internal("fly/gojo(1).png")));
        gojoFallFrame = new TextureRegion(new Texture(Gdx.files.internal("fly/gojo(2).png")));
        gojoDashFrame = new TextureRegion(new Texture(Gdx.files.internal("dash/gojo(1).png")));

        // ---------------- Cargar imágenes de ataque de Gojo (5 fotogramas) ----------------
        gojoAttackFrames = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            gojoAttackFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("fight/gojo(" + (i + 1) + ").png"))
            );
        }

        // ---------------- Cargar imágenes de power de Gojo (9 fotogramas) ----------------
        gojoPowerFrames = new TextureRegion[NUM_GOJO_POWER_FRAMES];
        for (int i = 0; i < NUM_GOJO_POWER_FRAMES; i++) {
            gojoPowerFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("power/gojo(" + (i + 1) + ").png"))
            );
        }

        // Cargar imagen de muerte de Gojo
        gojoDeadTexture = new Texture(Gdx.files.internal("dead/gojo(1).png"));

        // Cargar recursos para el proyectil y explosión
        projectileTexture = new Texture(Gdx.files.internal("power/rojo(1).png"));
        explosionFrames = new TextureRegion[NUM_EXPLOSION_FRAMES];
        for (int i = 0; i < NUM_EXPLOSION_FRAMES; i++) {
            explosionFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("power/rojo(" + (i + 2) + ").png"))
            );
        }

        // Dimensiones de Gojo
        gojoOriginalWidth = gojoIdleFrames[0].getTexture().getWidth();
        gojoOriginalHeight = gojoIdleFrames[0].getTexture().getHeight();
        gojoWidth = gojoOriginalWidth * SCALE;
        gojoHeight = gojoOriginalHeight * SCALE;
        gojoX = 50;
        gojoY = 0;
        gojoRect = new Rectangle(gojoX, gojoY, gojoWidth, gojoHeight);

        // ---------------- Cargar imágenes de Sukuna (Idle, Fly, Fall, Dash) ----------------
        sukunaIdleFrames = new TextureRegion[NUM_SUKUNA_IDLE_FRAMES];
        for (int i = 0; i < NUM_SUKUNA_IDLE_FRAMES; i++) {
            sukunaIdleFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("static/sukuna(" + (i + 1) + ").png"))
            );
        }
        sukunaFlyFrame = new TextureRegion(new Texture(Gdx.files.internal("fly/sukuna(1).png")));
        sukunaFallFrame = new TextureRegion(new Texture(Gdx.files.internal("fly/sukuna(2).png")));
        sukunaDashFrame = new TextureRegion(new Texture(Gdx.files.internal("dash/sukuna(1).png")));

        // ---------------- Cargar imágenes de ataque de Sukuna (5 fotogramas) ----------------
        sukunaAttackFrames = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            sukunaAttackFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("fight/sukuna(" + (i + 1) + ").png"))
            );
        }

        // Cargar imagen de muerte de Sukuna
        sukunaDeadTexture = new Texture(Gdx.files.internal("dead/sukuna(1).png"));

        // Dimensiones de Sukuna
        sukunaOriginalWidth = sukunaIdleFrames[0].getTexture().getWidth();
        sukunaOriginalHeight = sukunaIdleFrames[0].getTexture().getHeight();
        sukunaWidth = sukunaOriginalWidth * SCALE;
        sukunaHeight = sukunaOriginalHeight * SCALE;
        sukunaX = GAME_WIDTH - sukunaWidth - 50;
        sukunaY = 0;
        sukunaRect = new Rectangle(sukunaX, sukunaY, sukunaWidth, sukunaHeight);
    }

    @Override
    public void resize(int width, int height) {
        // Ignorar cambios de tamaño para mantener dimensiones fijas
    }

    @Override
    public void show() {
        readySound.play();
    }

    @Override
    public void render(float delta) {
        gameMusic.setVolume(game.getVolume());

        if (gojoLifeIndex == 17 || sukunaLifeIndex == 17) {
            endScreenDelayTimer += delta;
            if (endScreenDelayTimer >= 5f) {
                gameMusic.stop();
                game.setScreen(new EndScreen(game, gojoLifeIndex, sukunaLifeIndex));
                return;
            }
        }

        // ---------------- Actualizar controles de Gojo ----------------
        boolean gojoMovingSide = false;
        boolean gojoFlying = false;
        boolean gojoSideFlying = false;

        if (gojoLifeIndex != 17) {
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
        } else {
            gojoVerticalVelocity += DEAD_GRAVITY * delta;
            gojoY += gojoVerticalVelocity * delta;
            if (gojoY < 0) {
                gojoY = 0;
                gojoVerticalVelocity = 0;
            }
        }
        gojoRect.set(gojoX, gojoY, gojoWidth, gojoHeight);

        // ---------------- Actualizar controles de Sukuna ----------------
        boolean sukunaMovingSide = false;
        boolean sukunaFlying = false;
        boolean sukunaSideFlying = false;

        if (sukunaLifeIndex != 17) {
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
        } else {
            sukunaVerticalVelocity += DEAD_GRAVITY * delta;
            sukunaY += sukunaVerticalVelocity * delta;
            if (sukunaY < 0) {
                sukunaY = 0;
                sukunaVerticalVelocity = 0;
            }
        }
        sukunaRect.set(sukunaX, sukunaY, sukunaWidth, sukunaHeight);

        // ---------------- Calcular rectángulos de colisión ----------------
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

        // ---------------- Paredes ----------------
        Rectangle leftWall = new Rectangle(0, 0, 2, GAME_HEIGHT);
        Rectangle rightWall = new Rectangle(GAME_WIDTH - 2, 0, 2, GAME_HEIGHT);
        Rectangle topWall = new Rectangle(0, 740, GAME_WIDTH, 100);
        Rectangle bottomWall = new Rectangle(0, 0, GAME_WIDTH, WALL_THICKNESS);

        // ---------------- Comprobación de colisiones contra paredes (Gojo) ----------------
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

        // ---------------- Comprobación de colisiones contra paredes (Sukuna) ----------------
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

        Rectangle activeGojoRectPost = new Rectangle(gojoX + gojoOffsetX, gojoY + gojoOffsetY, gojoCollisionWidth, gojoCollisionHeight);
        Rectangle activeSukunaRectPost = new Rectangle(sukunaX + sukunaOffsetX, sukunaY + sukunaOffsetY, sukunaCollisionWidth, sukunaCollisionHeight);

        // ---------------- Lógica de ataque ----------------
        if (Gdx.input.isKeyJustPressed(Input.Keys.V) && !gojoPowerActive) {
            gojoPowerActive = true;
            gojoPowerTime = 0f;
            if (activeGojoRectPost.overlaps(activeSukunaRectPost)) {
                if (sukunaLifeIndex < 17) {
                    sukunaLifeIndex++;
                }
                punchSound.play();
            }
        }
        if (gojoPowerActive) {
            gojoPowerTime += delta;
            if (gojoPowerTime / POWER_FRAME_DURATION >= NUM_GOJO_POWER_FRAMES) {
                gojoPowerActive = false;
                // Lanzar el proyectil con dirección fija hacia Sukuna
                if (!projectileActive) {
                    projectileActive = true;
                    projectileExploding = false;
                    projectileWidth = projectileTexture.getWidth() * SCALE;
                    projectileHeight = projectileTexture.getHeight() * SCALE;
                    if (gojoFlip) {
                        projectileX = gojoX - projectileWidth * 0.5f;
                    } else {
                        projectileX = gojoX + gojoWidth - projectileWidth * 0.5f;
                    }
                    projectileY = gojoY + gojoHeight / 2 - projectileHeight / 2;
                    // Calcular la dirección inicial hacia el centro de Sukuna (una sola vez)
                    float targetX = sukunaRect.x + sukunaRect.width / 2;
                    float targetY = sukunaRect.y + sukunaRect.height / 2;
                    float projCenterX = projectileX + projectileWidth / 2;
                    float projCenterY = projectileY + projectileHeight / 2;
                    float diffX = targetX - projCenterX;
                    float diffY = targetY - projCenterY;
                    float distance = (float) Math.sqrt(diffX * diffX + diffY * diffY);
                    if (distance != 0) {
                        projectileDirX = diffX / distance;
                        projectileDirY = diffY / distance;
                    } else {
                        projectileDirX = 0;
                        projectileDirY = 0;
                    }
                    projectileCircle = new Circle(projectileX + projectileWidth / 2, projectileY + projectileHeight / 2, (projectileWidth / 2) * projectileCollisionScale);
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C) && !gojoAttacking) {
            gojoAttacking = true;
            gojoAttackTime = 0f;
            if (activeGojoRectPost.overlaps(activeSukunaRectPost)) {
                if (sukunaLifeIndex < 17) {
                    sukunaLifeIndex++;
                }
                punchSound.play();
            }
        }
        if (gojoAttacking) {
            gojoAttackTime += delta * 2;
            if (gojoAttackTime / ATTACK_FRAME_DURATION >= 5) {
                gojoAttacking = false;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.L) && !sukunaAttacking) {
            sukunaAttacking = true;
            sukunaAttackTime = 0f;
            if (activeSukunaRectPost.overlaps(activeGojoRectPost)) {
                if (gojoLifeIndex < 17) {
                    gojoLifeIndex++;
                }
                punchSound.play();
            }
        }
        if (sukunaAttacking) {
            sukunaAttackTime += delta * 2;
            if (sukunaAttackTime / ATTACK_FRAME_DURATION >= 5) {
                sukunaAttacking = false;
            }
        }

        // ------------- Actualización del proyectil -------------
        if (projectileActive && !projectileExploding) {
            // Mover el proyectil usando la dirección fija calculada al inicio
            projectileX += projectileDirX * projectileSpeed * delta;
            projectileY += projectileDirY * projectileSpeed * delta;
            projectileCircle.set(projectileX + projectileWidth / 2, projectileY + projectileHeight / 2, (projectileWidth / 2) * projectileCollisionScale);
            if (projectileCircle.x - projectileCircle.radius < 0 || projectileCircle.x + projectileCircle.radius > GAME_WIDTH ||
                projectileCircle.y - projectileCircle.radius < 0 || projectileCircle.y + projectileCircle.radius > GAME_HEIGHT ||
                Intersector.overlaps(projectileCircle, leftWall) || Intersector.overlaps(projectileCircle, rightWall) ||
                Intersector.overlaps(projectileCircle, topWall) || Intersector.overlaps(projectileCircle, bottomWall) ||
                Intersector.overlaps(projectileCircle, activeSukunaRect)) {
                if (Intersector.overlaps(projectileCircle, activeSukunaRect)) {
                    if (sukunaLifeIndex < 17) {
                        sukunaLifeIndex += 3;
                        if (sukunaLifeIndex > 17) sukunaLifeIndex = 17;
                    }
                }
                projectileExploding = true;
                explosionTime = 0f;
            }
        }
        if (projectileExploding) {
            explosionTime += delta;
            if (explosionTime / EXPLOSION_FRAME_DURATION >= NUM_EXPLOSION_FRAMES) {
                projectileActive = false;
                projectileExploding = false;
            }
        }

        // ---------------- Seleccionar frame para cada personaje ----------------
        TextureRegion gojoFrame;
        if (gojoLifeIndex == 17) {
            gojoFrame = new TextureRegion(gojoDeadTexture);
        } else if (gojoPowerActive) {
            int frameIndex = (int)(gojoPowerTime / POWER_FRAME_DURATION);
            if (frameIndex >= NUM_GOJO_POWER_FRAMES) frameIndex = NUM_GOJO_POWER_FRAMES - 1;
            gojoFrame = gojoPowerFrames[frameIndex];
        } else if (gojoAttacking) {
            int frameIndex = (int)(gojoAttackTime / ATTACK_FRAME_DURATION);
            if (frameIndex >= 5) frameIndex = 4;
            gojoFrame = gojoAttackFrames[frameIndex];
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                gojoFrame = (gojoSideFlying) ? gojoDashFrame : gojoFlyFrame;
            } else if (Gdx.input.isKeyPressed(Input.Keys.S) || (!Gdx.input.isKeyPressed(Input.Keys.W) && gojoVerticalVelocity < 0)) {
                gojoFrame = gojoFallFrame;
            } else if (gojoMovingSide) {
                gojoFrame = gojoDashFrame;
            } else {
                boolean idle = !(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D) ||
                    Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.S));
                if (idle) {
                    gojoIdleTime += delta;
                } else {
                    gojoIdleTime = 0;
                }
                int frameIndex = (int)(gojoIdleTime / FRAME_DURATION) % NUM_GOJO_IDLE_FRAMES;
                gojoFrame = gojoIdleFrames[frameIndex];
            }
        }

        TextureRegion sukunaFrame;
        if (sukunaLifeIndex == 17) {
            sukunaFrame = new TextureRegion(sukunaDeadTexture);
        } else if (sukunaAttacking) {
            int frameIndex = (int)(sukunaAttackTime / ATTACK_FRAME_DURATION);
            if (frameIndex >= 5) frameIndex = 4;
            sukunaFrame = sukunaAttackFrames[frameIndex];
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                sukunaFrame = (sukunaSideFlying) ? sukunaDashFrame : sukunaFlyFrame;
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || (!Gdx.input.isKeyPressed(Input.Keys.UP) && sukunaVerticalVelocity < 0)) {
                sukunaFrame = sukunaFallFrame;
            } else if (sukunaMovingSide) {
                sukunaFrame = sukunaDashFrame;
            } else {
                boolean idle = !(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
                    Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.DOWN));
                if (idle) {
                    sukunaIdleTime += delta;
                } else {
                    sukunaIdleTime = 0;
                }
                int frameIndex = (int)(sukunaIdleTime / FRAME_DURATION) % NUM_SUKUNA_IDLE_FRAMES;
                sukunaFrame = sukunaIdleFrames[frameIndex];
            }
        }

        // ---------------- Renderizado ----------------
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(fondo, 0, 0, GAME_WIDTH, GAME_HEIGHT);
        if (gojoFlip) {
            batch.draw(gojoFrame, gojoX + gojoWidth, gojoY, -gojoWidth, gojoHeight);
        } else {
            batch.draw(gojoFrame, gojoX, gojoY, gojoWidth, gojoHeight);
        }
        if (sukunaFlip) {
            batch.draw(sukunaFrame, sukunaX + sukunaWidth, sukunaY, -sukunaWidth, sukunaHeight);
        } else {
            batch.draw(sukunaFrame, sukunaX, sukunaY, sukunaWidth, sukunaHeight);
        }
        Texture currentGojoLife = gojoLifeTextures[gojoLifeIndex];
        Texture currentSukunaLife = sukunaLifeTextures[sukunaLifeIndex];
        float gojoLifeX = 0;
        float gojoLifeY = 0;
        float sukunaLifeX = 0;
        float sukunaLifeY = 0;
        batch.draw(currentGojoLife, gojoLifeX, gojoLifeY);
        batch.draw(currentSukunaLife, sukunaLifeX, sukunaLifeY);

        // Dibujar el proyectil: si está activo y no explotando se muestra la imagen "rojo(1).png"
        if (projectileActive) {
            if (!projectileExploding) {
                batch.draw(projectileTexture, projectileX, projectileY, projectileWidth, projectileHeight);
            } else {
                int explosionFrameIndex = (int)(explosionTime / EXPLOSION_FRAME_DURATION);
                if (explosionFrameIndex >= NUM_EXPLOSION_FRAMES) explosionFrameIndex = NUM_EXPLOSION_FRAMES - 1;
                batch.draw(explosionFrames[explosionFrameIndex], projectileX, projectileY, projectileWidth, projectileHeight);
            }
        }
        batch.end();

        // (El renderizado de colisiones en debug se ha comentado)
    }

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
        readySound.dispose();
        punchSound.dispose();
        gameMusic.dispose();
        for (TextureRegion region : gojoIdleFrames) {
            region.getTexture().dispose();
        }
        gojoFlyFrame.getTexture().dispose();
        gojoFallFrame.getTexture().dispose();
        gojoDashFrame.getTexture().dispose();
        for (TextureRegion region : gojoAttackFrames) {
            region.getTexture().dispose();
        }
        for (TextureRegion region : gojoPowerFrames) {
            region.getTexture().dispose();
        }
        projectileTexture.dispose();
        for (TextureRegion region : explosionFrames) {
            region.getTexture().dispose();
        }
        for (TextureRegion region : sukunaIdleFrames) {
            region.getTexture().dispose();
        }
        sukunaFlyFrame.getTexture().dispose();
        sukunaFallFrame.getTexture().dispose();
        sukunaDashFrame.getTexture().dispose();
        for (TextureRegion region : sukunaAttackFrames) {
            region.getTexture().dispose();
        }
        for (Texture t : gojoLifeTextures) {
            t.dispose();
        }
        for (Texture t : sukunaLifeTextures) {
            t.dispose();
        }
        gojoDeadTexture.dispose();
        sukunaDeadTexture.dispose();
    }
}
