package br.com.jukebox.ui;

import br.com.jukebox.audio.Mp3StreamPlayer;
import br.com.jukebox.service.MusicService;
import br.com.jukebox.util.AlbumArtExtractor;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class JukeboxUI extends JFrame {

    private final DefaultListModel<File> listModel = new DefaultListModel<>();
    private final JList<File> musicList = new JList<>(listModel);
    private final JLabel musicLabel = new JLabel("Nenhuma música selecionada");
    private final JLabel albumCover = new JLabel();
    private final Mp3StreamPlayer player = new Mp3StreamPlayer();
    private File currentMusic;
    private final JSlider progressBar = new JSlider();
    private final JLabel tempoLabel = new JLabel("00:00 / 00:00");
    private Timer progressTimer;
    private long tempoEstimado = 180; // padrão de 3 min, depois ajustamos
    private long tempoDecorrido = 0;
    private final JSlider volumeSlider = new JSlider(0, 100, 80); // 80% de volume inicial


    public JukeboxUI() {
        super("Jukebox");

        // Aqui vai o código do ícone:
        URL iconURL = getClass().getClassLoader().getResource("icon.ico");
        if (iconURL != null) {
            System.out.println("✅ Ícone encontrado: " + iconURL);
            Image image = new ImageIcon(iconURL).getImage();
            setIconImage(image);
        } else {
            System.out.println("❌ Ícone não encontrado no classpath!");
        }


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
                    ImageIcon capa = AlbumArtExtractor.extrairCapa(currentMusic, 200, 200);
                    albumCover.setIcon(capa != null ? capa :
                            new ImageIcon(getClass().getResource("/default-cover.png")));

                    tempoEstimado = estimarDuracaoEmSegundos(currentMusic);
                    tempoLabel.setText("00:00 / " + formatarTempo(tempoEstimado));
                    progressBar.setValue(0);
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

            tempoDecorrido = 0;
            progressBar.setValue(0);
            progressBar.setMaximum((int) tempoEstimado); // ou use 200, 300, etc
            progressBar.setEnabled(true);
            tempoLabel.setText("00:00 / " + formatarTempo(tempoEstimado));

            if (progressTimer != null) progressTimer.stop();

            progressTimer = new Timer(1000, ev -> {
                tempoDecorrido++;
                if (tempoDecorrido <= tempoEstimado) {
                    progressBar.setValue((int) tempoDecorrido);
                    tempoLabel.setText(formatarTempo(tempoDecorrido) + " / " + formatarTempo(tempoEstimado));
                } else {
                    progressTimer.stop();
                }
            });
            progressTimer.start();
        });

        pauseButton.addActionListener((ActionEvent e) -> {
            player.pausar();
        });

        stopButton.addActionListener((ActionEvent e) -> {
            player.parar();

            if (progressTimer != null) progressTimer.stop();
            tempoDecorrido = 0;
            progressBar.setValue(0);
            tempoLabel.setText("00:00 / " + formatarTempo(tempoEstimado));
        });

        controlsPanel.add(playButton);
        controlsPanel.add(pauseButton);
        controlsPanel.add(stopButton);
        add(controlsPanel, BorderLayout.SOUTH);

        // Barra de progresso
        progressBar.setMinimum(0);
        progressBar.setMaximum((int) tempoEstimado);
        progressBar.setValue(0);
        progressBar.setEnabled(false);
        progressBar.setPreferredSize(new Dimension(200, 20));

        controlsPanel.add(progressBar);
        controlsPanel.add(tempoLabel);

        // Volume Slider
        volumeSlider.setPreferredSize(new Dimension(100, 20));
        volumeSlider.setToolTipText("Volume");
        volumeSlider.addChangeListener(e -> {
            int volume = volumeSlider.getValue();
            float volumeFloat = volume / 100f;
            player.setVolume(volumeFloat); // envia para o Mp3StreamPlayer
        });

        controlsPanel.add(new JLabel("🔊"));
        controlsPanel.add(volumeSlider);


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

    private String formatarTempo(long segundos) {
        long min = segundos / 60;
        long sec = segundos % 60;
        return String.format("%02d:%02d", min, sec);
    }

    private long estimarDuracaoEmSegundos(File arquivo) {
        try {
            AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(arquivo);
            Map<?, ?> propriedades = baseFileFormat.properties();
            Long microseconds = (Long) propriedades.get("duration");
            if (microseconds != null) {
                return microseconds / 1_000_000; // microsegundos para segundos
            }
        } catch (Exception e) {
            System.out.println("⚠️ Não foi possível estimar duração: " + e.getMessage());
        }
        return 180; // padrão (3 min) se não conseguir detectar
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JukeboxUI ui = new JukeboxUI();
            ui.setVisible(true);
        });
    }
}
