package br.com.jukebox.util;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

public class AlbumArtExtractor {

    public static ImageIcon extrairCapa(File mp3, int largura, int altura) {
        try {
            AudioFile audioFile = AudioFileIO.read(mp3);
            Tag tag = audioFile.getTag();
            if (tag != null) {
                Artwork artwork = tag.getFirstArtwork();
                if (artwork != null) {
                    byte[] imagemBytes = artwork.getBinaryData();
                    BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(imagemBytes));
                    Image redimensionada = imagem.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
                    return new ImageIcon(redimensionada);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao extrair capa: " + e.getMessage());
        }

        // Se não tiver capa, retorna null (você pode usar imagem padrão depois)
        return null;
    }
}
