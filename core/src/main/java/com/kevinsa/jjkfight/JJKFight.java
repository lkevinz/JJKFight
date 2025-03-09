package com.kevinsa.jjkfight;

import com.badlogic.gdx.Game;

public class JJKFight extends Game {
    @Override
    public void create() {
        // Al iniciar, se muestra la pantalla de menú
        setScreen(new MenuScreen(this));
    }
}
