package com.kevinsa.jjkfight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class MenuScreen implements Screen {

    private Stage stage;
    private Texture backgroundTexture;
    private Texture playTexture;
    private Texture settingsTexture;
    private Texture exitTexture;
    // Nuevas texturas para el botón de sonido
    private Texture soundOnTexture;
    private Texture soundOffTexture;

    private Image backgroundImage;
    private Image playButton;
    private Image settingsButton;
    private Image exitButton;
    // Botón de sonido
    private Image soundButton;

    private JJKFight game; // Referencia a la clase principal

    // Música del menú
    private Music menuMusic;
    // Variable para saber si el sonido está activo
    private boolean soundEnabled = true;

    public MenuScreen(JJKFight game) {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Cargar las texturas (asegúrate de que estas imágenes estén en assets/menu/)
        backgroundTexture = new Texture(Gdx.files.internal("menu/menu_fondo.png"));
        playTexture       = new Texture(Gdx.files.internal("menu/start_menu.png"));
        settingsTexture   = new Texture(Gdx.files.internal("menu/settings_menu.png"));
        exitTexture       = new Texture(Gdx.files.internal("menu/exit_menu.png"));
        // Cargar las texturas para el botón de sonido
        soundOnTexture    = new Texture(Gdx.files.internal("menu/on.png"));
        soundOffTexture   = new Texture(Gdx.files.internal("menu/off.png"));

        // Crear los actores de imagen
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        playButton = new Image(playTexture);
        settingsButton = new Image(settingsTexture);
        exitButton = new Image(exitTexture);
        // Iniciar el botón de sonido con la imagen "on"
        soundButton = new Image(soundOnTexture);

        // Cargar la música del menú (colócala en assets/menu/ o en otra carpeta y ajusta la ruta)
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menu/menu.mp3"));
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.3f);
        menuMusic.play();

        // Agregar listeners a los botones
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cambiar a la pantalla principal de juego
                game.setScreen(new GameScreen(game));
                // Opcional: detener la música del menú al iniciar el juego
                menuMusic.stop();
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Pantalla de configuraciones (no implementada)");
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Listener para el botón de sonido
        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                soundEnabled = !soundEnabled;
                if (soundEnabled) {
                    soundButton.setDrawable(new Image(soundOnTexture).getDrawable());
                    menuMusic.play();
                } else {
                    soundButton.setDrawable(new Image(soundOffTexture).getDrawable());
                    menuMusic.pause();
                }
            }
        });

        // Organizar los elementos en una Table
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Agregar botones con un padding
        table.add(playButton).pad(20).row();
        table.add(settingsButton).pad(20).row();
        table.add(exitButton).pad(20).row();
        table.add(soundButton).width(100).pad(20); // Agrega el botón de sonido

        // Agregar los actores al Stage; el fondo primero, luego la Table
        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    @Override
    public void show() {
        // Se llama cuando esta pantalla es mostrada
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        playTexture.dispose();
        settingsTexture.dispose();
        exitTexture.dispose();
        soundOnTexture.dispose();
        soundOffTexture.dispose();
        menuMusic.dispose();
    }
}
