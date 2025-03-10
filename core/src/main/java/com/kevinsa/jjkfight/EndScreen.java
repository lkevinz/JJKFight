package com.kevinsa.jjkfight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class EndScreen implements Screen {
    private JJKFight game;
    private Stage stage;
    private SpriteBatch batch;
    private Texture winTexture;
    private Texture homeTexture;
    private Image homeButton;

    /**
     * @param game El objeto principal.
     * @param gojoLifeIndex Índice de vida de Gojo.
     * @param sukunaLifeIndex Índice de vida de Sukuna.
     */
    public EndScreen(JJKFight game, int gojoLifeIndex, int sukunaLifeIndex) {
        this.game = game;
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Determinar el ganador:
        // Si gojoLifeIndex == 17, Gojo está muerto, por lo tanto gana Sukuna.
        // Si sukunaLifeIndex == 17, Sukuna está muerto, por lo tanto gana Gojo.
        if (gojoLifeIndex == 17) {
            winTexture = new Texture(Gdx.files.internal("sukuna_win.png"));
        } else if (sukunaLifeIndex == 17) {
            winTexture = new Texture(Gdx.files.internal("gojo_win.png"));
        } else {
            // Por defecto, si ninguno está muerto, se usa un fondo genérico.
            winTexture = new Texture(Gdx.files.internal("default_win.png"));
        }

        // Cargar la imagen home.png que funcionará como botón para volver al menú
        homeTexture = new Texture(Gdx.files.internal("home.png"));
        homeButton = new Image(homeTexture);
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Al hacer clic, se cambia a la pantalla de menú.
                game.setScreen(new MenuScreen(game));
            }
        });

        // Organizar el botón en la esquina inferior derecha
        Table table = new Table();
        table.setFillParent(true);
        table.bottom().right().pad(20);
        table.add(homeButton);
        stage.addActor(table);
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar el fondo ganador a pantalla completa
        batch.begin();
        batch.draw(winTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

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
        batch.dispose();
        stage.dispose();
        winTexture.dispose();
        homeTexture.dispose();
    }
}
