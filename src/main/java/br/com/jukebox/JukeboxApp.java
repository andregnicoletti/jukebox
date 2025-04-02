package br.com.jukebox;

import br.com.jukebox.audio.AudioPlayer;
import br.com.jukebox.audio.Mp3Player;
import br.com.jukebox.audio.PlayerUniversal;
import br.com.jukebox.service.MusicService;

import javax.swing.*;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class JukeboxApp {

    private static final String musicPath = System.getProperty("user.home") + "/Músicas";

    public static void main(String[] args) {

//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Jukebox");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(400, 300);
//            frame.setLocationRelativeTo(null);
//            frame.setVisible(true);
//        });

        MusicService musicService = new MusicService();
        List<File> musicas = musicService.listarMusicas(musicPath);

        if (musicas.isEmpty()) {
            System.out.println("Nenhuma música encontrada.");
            return;
        }

        System.out.println("Músicas encontradas:");
        for (int i = 0; i < musicas.size(); i++) {
            System.out.println(i + " - " + musicas.get(i).getName());
        }

        System.out.print("Escolha o número da música para tocar: ");
        Scanner scanner = new Scanner(System.in);
        int escolha = scanner.nextInt();

        if (escolha >= 0 && escolha < musicas.size()) {
            File musicaEscolhida = musicas.get(escolha);
            System.out.println("Tocando: " + musicaEscolhida.getName());
            PlayerUniversal player = new PlayerUniversal();
            player.tocar(musicaEscolhida);
        } else {
            System.out.println("Escolha inválida.");
        }
    }

}
