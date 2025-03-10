package com.kevinsa.jjkfight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SettingsScreen implements Screen {

    private Stage stage;
    private JJKFight game;
    private Music menuMusic;

    private Texture backgroundTexture;
    private Texture returnTexture;

    private Texture[] volumeTextures;

    private Image backgroundImage;
    private Image volumeImage;
    private Image returnButton;

    private final int[] volumes = {0, 18, 34, 66, 82, 98};
    private int currentIndex;

    public SettingsScreen(JJKFight game, Music menuMusic, int currentVolumeIndex) {
        this.game = game;
        this.menuMusic = menuMusic;
        this.currentIndex = currentVolumeIndex;

        stage = new Stage(new FitViewport(1600,900));
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("menu/vol/fondo.png"));
        returnTexture = new Texture(Gdx.files.internal("menu/vol/return.png"));

        volumeTextures = new Texture[6];
        volumeTextures[0] = new Texture(Gdx.files.internal("menu/vol/vol_0.png"));
        volumeTextures[1] = new Texture(Gdx.files.internal("menu/vol/vol_18.png"));
        volumeTextures[2] = new Texture(Gdx.files.internal("menu/vol/vol_34.png"));
        volumeTextures[3] = new Texture(Gdx.files.internal("menu/vol/vol_66.png"));
        volumeTextures[4] = new Texture(Gdx.files.internal("menu/vol/vol_82.png"));
        volumeTextures[5] = new Texture(Gdx.files.internal("menu/vol/vol_98.png"));

        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        volumeImage = new Image(volumeTextures[currentIndex]);
        returnButton = new Image(returnTexture);

        // Al pulsar el botón "return" se actualiza el volumen y se vuelve al menú
        returnButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                float newVolume = volumes[currentIndex] / 100f;
                menuMusic.setVolume(newVolume);
                game.setScreen(new MenuScreen(game, menuMusic, currentIndex));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(volumeImage).pad(10).row();
        table.add(returnButton).pad(10).row();

        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Manejar teclas izquierda y derecha para cambiar la imagen y el volumen
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if(currentIndex < volumes.length - 1) {
                currentIndex++;
                volumeImage.setDrawable(new TextureRegionDrawable(volumeTextures[currentIndex]));
                menuMusic.setVolume(volumes[currentIndex] / 100f);
            }
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if(currentIndex > 0) {
                currentIndex--;
                volumeImage.setDrawable(new TextureRegionDrawable(volumeTextures[currentIndex]));
                menuMusic.setVolume(volumes[currentIndex] / 100f);
            }
        }

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
        returnTexture.dispose();
        for(Texture t : volumeTextures) {
            t.dispose();
        }
    }
}
