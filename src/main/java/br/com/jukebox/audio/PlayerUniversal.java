package br.com.jukebox.audio;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.File;
import java.io.FileInputStream;

public class PlayerUniversal {

    private AdvancedPlayer player;
    private Thread thread;
    private File currentFile;
    private boolean isPaused = false;
    private int pausedFrame = 0;

    public void tocar(File arquivo) {
        parar(); // Garante que outra música não esteja tocando
        currentFile = arquivo;
        isPaused = false;

        thread = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(currentFile);
                player = new AdvancedPlayer(fis);
                player.play(pausedFrame, Integer.MAX_VALUE); // Começa do frame pausado (ou 0)
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void parar() {
        if (player != null) {
            player.close();
        }
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        pausedFrame = 0;
    }

    public void pausar() {
        if (player != null) {
            // Essa simulação não é perfeita: JLayer não expõe frame atual.
            // Aqui a gente apenas fecha o player e marca como "pausado".
            isPaused = true;
            parar(); // Fecha o player
            // Opcionalmente, poderíamos estimar o frame com base no tempo
            System.out.println("⏸ Pausado (reprodução interrompida, posição não precisa ser exata)");
        }
    }

    public void retomar() {
        if (isPaused && currentFile != null) {
            tocar(currentFile);
        }
    }
}
