package br.com.jukebox.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
    private Clip clip;

    public void tocar(File arquivo) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivo);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void parar() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}
