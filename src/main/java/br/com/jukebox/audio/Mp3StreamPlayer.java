package br.com.jukebox.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Mp3StreamPlayer {

    private SourceDataLine line;
    private Thread playThread;
    private volatile boolean paused = false;
    private volatile boolean stopped = false;

    public void tocar(File mp3) {
        parar(); // Para qualquer reprodução anterior

        playThread = new Thread(() -> {
            try (AudioInputStream in = AudioSystem.getAudioInputStream(mp3)) {
                AudioFormat baseFormat = in.getFormat();
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(),
                        false
                );
                try (AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in)) {
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
                    line = (SourceDataLine) AudioSystem.getLine(info);
                    line.open(decodedFormat);
                    line.start();

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while (!stopped && (bytesRead = din.read(buffer, 0, buffer.length)) != -1) {
                        if (paused) {
                            synchronized (this) {
                                wait(); // espera até continuar
                            }
                        }
                        line.write(buffer, 0, bytesRead);
                    }

                    line.drain();
                    line.stop();
                    line.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stopped = false;
        paused = false;
        playThread.start();
    }

    public void pausar() {
        paused = true;
    }

    public synchronized void continuar() {
        if (paused) {
            paused = false;
            notifyAll();
        }
    }

    public void parar() {
        stopped = true;
        if (playThread != null && playThread.isAlive()) {
            playThread.interrupt();
        }
        if (line != null && line.isOpen()) {
            line.stop();
            line.close();
        }
    }

    public void setVolume(float volumePercent) {
        if (line != null && line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volume = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            float min = volume.getMinimum();
            float max = volume.getMaximum();
            float value = min + (max - min) * volumePercent; // volumePercent de 0.0 a 1.0
            volume.setValue(value);
        }
    }
}
