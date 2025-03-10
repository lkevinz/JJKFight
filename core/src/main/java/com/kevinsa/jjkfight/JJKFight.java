package com.kevinsa.jjkfight;

import com.badlogic.gdx.Game;

public class JJKFight extends Game {
    // Volumen por defecto (66%)
    private float volume = 0.2f;

    @Override
    public void create() {
        // Al iniciar, se muestra la pantalla de menú
        setScreen(new MenuScreen(this));
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
}
