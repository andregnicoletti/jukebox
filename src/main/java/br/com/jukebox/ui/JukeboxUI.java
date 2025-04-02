package br.com.jukebox.ui;

import br.com.jukebox.audio.PlayerUniversal;
import br.com.jukebox.service.MusicService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class JukeboxUI extends JFrame {

    private final DefaultListModel<File> listModel = new DefaultListModel<>();
    private final JList<File> musicList = new JList<>(listModel);
    private final JLabel musicLabel = new JLabel("Nenhuma música selecionada");
    private final JLabel albumCover = new JLabel();
    private final PlayerUniversal player = new PlayerUniversal();
    private File currentMusic;

    public JukeboxUI() {
        super("Jukebox");

        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);

        // Painel esquerdo: Lista de músicas
        JScrollPane scrollPane = new JScrollPane(musicList);
        musicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        musicList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                currentMusic = musicList.getSelectedValue();
                if (currentMusic != null) {
                    musicLabel.setText("🎵 " + currentMusic.getName());
                }
            }
        });
        add(scrollPane, BorderLayout.WEST);

        // Painel central: capa + nome da música
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        albumCover.setHorizontalAlignment(JLabel.CENTER);
        albumCover.setPreferredSize(new Dimension(200, 200));
        albumCover.setIcon(new ImageIcon(new ImageIcon("default-cover.png").getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
        centerPanel.add(albumCover, BorderLayout.CENTER);
        musicLabel.setHorizontalAlignment(JLabel.CENTER);
        centerPanel.add(musicLabel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // Painel inferior: botões
        JPanel controlsPanel = new JPanel();
        JButton playButton = new JButton("▶ Play");
        JButton pauseButton = new JButton("⏸ Pause");
        JButton stopButton = new JButton("⏹ Stop");

        playButton.addActionListener((ActionEvent e) -> {
            if (currentMusic != null) {
                player.tocar(currentMusic);
            }
        });

        pauseButton.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Pause ainda não implementado.");
        });

        stopButton.addActionListener((ActionEvent e) -> {
            player.parar();
        });

        controlsPanel.add(playButton);
        controlsPanel.add(pauseButton);
        controlsPanel.add(stopButton);
        add(controlsPanel, BorderLayout.SOUTH);

        // Menu topo: selecionar pasta
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Arquivo");
        JMenuItem selecionarPasta = new JMenuItem("Selecionar Pasta de Músicas");
        selecionarPasta.addActionListener(e -> selecionarPasta());
        menu.add(selecionarPasta);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void selecionarPasta() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int resultado = chooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File pasta = chooser.getSelectedFile();
            carregarMusicas(pasta);
        }
    }

    private void carregarMusicas(File pasta) {
        listModel.clear();
        MusicService musicService = new MusicService();
        List<File> musicas = musicService.listarMusicas(pasta.getAbsolutePath());
        for (File musica : musicas) {
            listModel.addElement(musica);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JukeboxUI ui = new JukeboxUI();
            ui.setVisible(true);
        });
    }
}
