package com.kevinsa.jjkfight.lwjgl3;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.kevinsa.jjkfight.JJKFight;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        // Título de la ventana
        config.setTitle("JJKFight");

        // Dimensiones de la ventana
        config.setWindowedMode(1600, 900);

        // Evita que se pueda redimensionar
        config.setResizable(false);

        // Asigna los íconos en distintos tamaños
        config.setWindowIcon(
            Files.FileType.Internal,
            "icon/icon16.png",
            "icon/icon32.png",
            "icon/icon128.png"
        );

        // Inicia la aplicación con JJKFight
        new Lwjgl3Application(new JJKFight(), config);
    }
}
