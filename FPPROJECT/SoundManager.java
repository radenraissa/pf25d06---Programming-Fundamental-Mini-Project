package FPPROJECT;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundManager {

    public static void playSound(String soundFilePath) {
        if (soundFilePath == null || soundFilePath.trim().isEmpty()) {
            return;
        }

        try {
            URL url = SoundManager.class.getClassLoader().getResource(soundFilePath);
            if (url == null) {
                System.err.println("Tidak dapat menemukan file suara: " + soundFilePath);
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Gagal memutar suara: " + soundFilePath);
            e.printStackTrace();
        }
    }
}