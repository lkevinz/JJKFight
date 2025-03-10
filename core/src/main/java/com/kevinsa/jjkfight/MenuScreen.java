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
    private JJKFight game;
    private Music menuMusic;
    private int currentVolumeIndex;
    // Arreglo de volúmenes (valores del 0 al 100)
    private final int[] volumes = {0, 18, 34, 66, 82, 98};

    // Texturas
    private Texture backgroundTexture;
    private Texture playTexture;
    private Texture settingsTexture;
    private Texture exitTexture;
    private Texture soundOnTexture;
    private Texture soundOffTexture;

    private Image backgroundImage;
    private Image playButton;
    private Image settingsButton;
    private Image exitButton;
    private Image soundButton;

    // Constructor por defecto: se crea la música y se asigna un índice de volumen por defecto (ej.: 3 → 66)
    public MenuScreen(JJKFight game) {
        this(game, Gdx.audio.newMusic(Gdx.files.internal("menu/menu.mp3")), 3);
    }

    // Constructor completo que permite pasar la música y el índice de volumen
    public MenuScreen(JJKFight game, Music menuMusic, int currentVolumeIndex) {
        this.game = game;
        this.menuMusic = menuMusic;
        this.currentVolumeIndex = currentVolumeIndex;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Cargar texturas
        backgroundTexture = new Texture(Gdx.files.internal("menu/menu_fondo.png"));
        playTexture       = new Texture(Gdx.files.internal("menu/start_menu.png"));
        settingsTexture   = new Texture(Gdx.files.internal("menu/settings_menu.png"));
        exitTexture       = new Texture(Gdx.files.internal("menu/exit_menu.png"));
        soundOnTexture    = new Texture(Gdx.files.internal("menu/on.png"));
        soundOffTexture   = new Texture(Gdx.files.internal("menu/off.png"));

        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        playButton = new Image(playTexture);
        settingsButton = new Image(settingsTexture);
        exitButton = new Image(exitTexture);
        soundButton = new Image(soundOnTexture);

        // Iniciar la música con el volumen definido (convertido a 0..1)
        menuMusic.setLooping(true);
        menuMusic.setVolume(volumes[currentVolumeIndex] / 100f);
        menuMusic.play();

        // Listener para botón Play
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
                menuMusic.stop();
            }
        });

        // Listener para botón Settings (abre la pantalla de configuraciones)
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game, menuMusic, currentVolumeIndex));
            }
        });

        // Listener para botón Exit
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Listener para botón de sonido (alternar entre on/off)
        // Listener para botón de sonido (alternar entre on/off)
        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (menuMusic.isPlaying()){
                    menuMusic.pause();
                    soundButton.setDrawable(new Image(soundOffTexture).getDrawable());
                    game.setVolume(0f); // Desactiva el sonido en el juego
                } else {
                    menuMusic.play();
                    soundButton.setDrawable(new Image(soundOnTexture).getDrawable());
                    game.setVolume(volumes[currentVolumeIndex] / 100f); // Restaura el volumen seleccionado
                }
            }
        });


        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(playButton).pad(20).row();
        table.add(settingsButton).pad(20).row();
        table.add(exitButton).pad(20).row();
        table.add(soundButton).width(100).pad(20);

        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        // Actualiza el volumen en cada renderizado, por si se ha modificado en SettingsScreen
        menuMusic.setVolume(volumes[currentVolumeIndex] / 100f);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

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
