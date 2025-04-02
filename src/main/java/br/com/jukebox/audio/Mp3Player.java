package br.com.jukebox.audio;

import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.File;

public class Mp3Player {
    private Player player;

    public void tocar(File arquivo) {
        try {
            FileInputStream fis = new FileInputStream(arquivo);
            player = new Player(fis);
            player.play(); // este método é BLOQUEANTE até o fim da música
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
