package br.com.jukebox.audio;

import javazoom.jl.player.Player;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PlayerUniversal {

    private Clip clip;           // Para .wav
    private Player mp3Player;    // Para .mp3
    private Thread thread;       // Para tocar MP3 sem travar o app

    public void tocar(File arquivo) {
        String nome = arquivo.getName().toLowerCase();

        if (nome.endsWith(".mp3")) {
            tocarMp3(arquivo);
        } else if (nome.endsWith(".wav")) {
            tocarWav(arquivo);
        } else {
            System.out.println("Formato não suportado: " + nome);
        }
    }

    private void tocarMp3(File arquivo) {
        thread = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(arquivo);
                mp3Player = new Player(fis);
                mp3Player.play();
            } catch (Exception e) {
                System.out.println("Erro ao tocar MP3: " + e.getMessage());
            }
        });
        thread.start();
    }

    private void tocarWav(File arquivo) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivo);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Erro ao tocar WAV: " + e.getMessage());
        }
    }

    public void parar() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
        if (mp3Player != null) {
            mp3Player.close();
        }
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }
}
