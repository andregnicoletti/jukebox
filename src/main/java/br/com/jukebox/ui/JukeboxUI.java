package br.com.jukebox.ui;

import br.com.jukebox.audio.Mp3StreamPlayer;
import br.com.jukebox.service.MusicService;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.Map;

public class JukeboxUI extends JFrame {

    private final DefaultListModel<File> listModel = new DefaultListModel<>();
    private final DefaultListModel<File> playlistModel = new DefaultListModel<>();
    private final JList<File> musicList = new JList<>(listModel);
    private final JList<File> playlistList = new JList<>(playlistModel);
    private final Mp3StreamPlayer player = new Mp3StreamPlayer();
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel tempoLabel = new JLabel("00:00 / 00:00");
    private Timer progressTimer;
    private long tempoEstimado = 180;
    private long tempoDecorrido = 0;
    private final JSlider volumeSlider = new JSlider(0, 100, 80);
    private final JLabel volumeLabel = new JLabel("80%");
    private final JLabel volumeIcon = new JLabel("🔊");
    private final JButton muteButton = new JButton("Mute");
    private boolean muted = false;
    private int volumeAntesDoMute = 80;

    private final JPanel albumPanel = new JPanel(new GridLayout(2, 5, 10, 10));
    private List<File> albuns = new ArrayList<>();
    private int paginaAtual = 0;
    private int albumSelecionadoIndex = 0;
    private int faixaAtual = 0;

    public JukeboxUI() {
        super("Jukebox");

        URL iconURL = getClass().getClassLoader().getResource("icon.ico");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        }

        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        albumPanel.setPreferredSize(new Dimension(800, 160));
        albumPanel.setBorder(BorderFactory.createTitledBorder("ÁLBUNS"));
        add(albumPanel, BorderLayout.NORTH);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() != KeyEvent.KEY_PRESSED || albuns.isEmpty()) return false;

            int totalAlbuns = albuns.size();
            int totalPaginas = (int) Math.ceil(totalAlbuns / 10.0);
            int linha = albumSelecionadoIndex / 5;
            int coluna = albumSelecionadoIndex % 5;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (coluna > 0) albumSelecionadoIndex--;
                    else if (paginaAtual > 0) {
                        paginaAtual--;
                        albumSelecionadoIndex = 9;
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (coluna < 4 && albumSelecionadoIndex + 1 < Math.min(10, totalAlbuns - paginaAtual * 10))
                        albumSelecionadoIndex++;
                    else if (paginaAtual < totalPaginas - 1) {
                        paginaAtual++;
                        albumSelecionadoIndex = 0;
                    }
                }
                case KeyEvent.VK_UP -> {
                    if (linha == 1) albumSelecionadoIndex -= 5;
                }
                case KeyEvent.VK_DOWN -> {
                    if (linha == 0 && albumSelecionadoIndex + 5 < Math.min(10, totalAlbuns - paginaAtual * 10))
                        albumSelecionadoIndex += 5;
                }
                case KeyEvent.VK_ENTER -> {
                    int realIndex = paginaAtual * 10 + albumSelecionadoIndex;
                    if (realIndex < albuns.size()) carregarMusicas(albuns.get(realIndex));
                }
                default -> {
                    return false;
                }
            }

            atualizarAlbuns();
            return true;
        });


        JPanel listsPanel = new JPanel(new GridLayout(1, 3));

        JScrollPane scrollMusicas = new JScrollPane(musicList);
        scrollMusicas.setBorder(BorderFactory.createTitledBorder("LISTA DE MÚSICAS"));
        JScrollPane scrollPlaylist = new JScrollPane(playlistList);
        scrollPlaylist.setBorder(BorderFactory.createTitledBorder("LISTA DE REPRODUÇÃO"));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JButton btnAdicionar = new JButton("ADICIONAR →");
        JButton btnRemover = new JButton("← REMOVER");
        JButton btnProximo = new JButton("PRÓXIMO");
        JButton btnPausar = new JButton("PAUSAR");
        JButton btnPlay = new JButton("PLAY");

        btnAdicionar.addActionListener(e -> {
            File selected = musicList.getSelectedValue();
            if (selected != null && !playlistModel.contains(selected)) {
                playlistModel.addElement(selected);
            }
        });

        btnRemover.addActionListener(e -> {
            File selected = playlistList.getSelectedValue();
            if (selected != null) {
                playlistModel.removeElement(selected);
            }
        });

        btnPlay.addActionListener(e -> {
            if (!playlistModel.isEmpty()) {
                faixaAtual = playlistList.getSelectedIndex();
                if (faixaAtual == -1) faixaAtual = 0;
                if (faixaAtual < playlistModel.size()) {
                    File musica = playlistModel.get(faixaAtual);
                    player.tocar(musica);
                    player.setVolume(volumeSlider.getValue() / 100f);

                    tempoDecorrido = 0;
                    tempoEstimado = estimarDuracaoEmSegundos(musica);
                    progressBar.setMaximum((int) tempoEstimado);
                    progressBar.setValue(0);
                    tempoLabel.setText("00:00 / " + formatarTempo(tempoEstimado));

                    if (progressTimer != null) progressTimer.stop();

                    progressTimer = new Timer(1000, ev -> {
                        tempoDecorrido++;
                        if (tempoDecorrido <= tempoEstimado) {
                            progressBar.setValue((int) tempoDecorrido);
                            tempoLabel.setText(formatarTempo(tempoDecorrido) + " / " + formatarTempo(tempoEstimado));
                            progressBar.setString(formatarTempo(tempoDecorrido) + " / " + formatarTempo(tempoEstimado));
                        } else {
                            progressTimer.stop();
                        }
                    });
                    progressTimer.start();

                }
            }
        });

        btnPausar.addActionListener(e -> player.pausar());

        btnProximo.addActionListener(e -> {
            if (!playlistModel.isEmpty()) {
                faixaAtual++;
                if (faixaAtual >= playlistModel.size()) faixaAtual = 0;
                playlistList.setSelectedIndex(faixaAtual);
                File musica = playlistModel.get(faixaAtual);
                player.tocar(musica);
                player.setVolume(volumeSlider.getValue() / 100f);
            }
        });

        buttonsPanel.add(btnAdicionar);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(btnRemover);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(btnProximo);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(btnPausar);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(btnPlay);

        listsPanel.add(scrollMusicas);
        listsPanel.add(buttonsPanel);
        listsPanel.add(scrollPlaylist);

        add(listsPanel, BorderLayout.CENTER);

        JPanel controlsPanel = new JPanel();

        progressBar.setMinimum(0);
        progressBar.setMaximum((int) tempoEstimado);
        progressBar.setValue(0);
        progressBar.setEnabled(false);
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setStringPainted(true);

        controlsPanel.add(progressBar);
        controlsPanel.add(tempoLabel);

        volumeSlider.setPreferredSize(new Dimension(100, 20));
        volumeSlider.setToolTipText("Volume");
        volumeSlider.addChangeListener(e -> {
            int volume = volumeSlider.getValue();
            float volumeFloat = volume / 100f;
            player.setVolume(volumeFloat);
            volumeLabel.setText(volume + "%");

            if (volume == 0) volumeIcon.setText("🔇");
            else if (volume < 50) volumeIcon.setText("🔉");
            else volumeIcon.setText("🔊");
        });

        muteButton.addActionListener(e -> {
            if (!muted) {
                volumeAntesDoMute = volumeSlider.getValue();
                volumeSlider.setValue(0);
                muted = true;
                muteButton.setText("Unmute");
            } else {
                volumeSlider.setValue(volumeAntesDoMute);
                muted = false;
                muteButton.setText("Mute");
            }
        });

        controlsPanel.add(volumeIcon);
        controlsPanel.add(volumeSlider);
        controlsPanel.add(volumeLabel);
        controlsPanel.add(muteButton);

        add(controlsPanel, BorderLayout.SOUTH);

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
            carregarAlbuns(pasta);
        }
    }

    private void carregarAlbuns(File pasta) {
        File[] pastas = pasta.listFiles(File::isDirectory);
        if (pastas != null) {
            albuns = Arrays.asList(pastas);
            paginaAtual = 0;
            atualizarAlbuns();
        }
    }

    private void atualizarAlbuns() {
        albumPanel.removeAll();
        int inicio = paginaAtual * 10;
        int fim = Math.min(inicio + 10, albuns.size());

        for (int i = inicio; i < fim; i++) {
            File album = albuns.get(i);
            JButton botao = new JButton(album.getName());
            if (i - inicio == albumSelecionadoIndex) botao.setBackground(Color.YELLOW);
            botao.addActionListener(e -> carregarMusicas(album));
            albumPanel.add(botao);
        }

        for (int i = fim; i < inicio + 10; i++) {
            albumPanel.add(new JLabel());
        }

        albumPanel.revalidate();
        albumPanel.repaint();
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
                return microseconds / 1_000_000;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Não foi possível estimar duração: " + e.getMessage());
        }
        return 180;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JukeboxUI ui = new JukeboxUI();
            ui.setVisible(true);
        });
    }
}
