package br.com.jukebox.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicService {

    public List<File> listarMusicas(String caminhoDaPasta) {
        File pasta = new File(caminhoDaPasta);
        List<File> musicas = new ArrayList<>();

        if (pasta.exists() && pasta.isDirectory()) {
            for (File arquivo : pasta.listFiles()) {
                if (arquivo.isFile() && (arquivo.getName().endsWith(".mp3") || arquivo.getName().endsWith(".wav"))) {
                    musicas.add(arquivo);
                }
            }
        }

        return musicas;
    }
}
