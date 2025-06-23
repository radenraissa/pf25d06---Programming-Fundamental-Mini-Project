package FPPROJECT;

/**
 * This enum is used by:
 * 1. Player: takes value of CROSS or NOUGHT
 * 2. Cell content: takes value of CROSS, NOUGHT, or NO_SEED.
 *
 * Ideally, we should define two enums with inheritance, which is,
 *  however, not supported.
 */

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
/**
 * This enum is used by:
 * 1. Player: takes value of CROSS or NOUGHT
 * 2. Cell content: takes value of CROSS, NOUGHT, or NO_SEED.
 *
 * We also attach a display image icon (text or image) for the items.
 *   and define the related variable/constructor/getter.
 * To draw the image:
 *   g.drawImage(content.getImage(), x, y, width, height, null);
 *
 * Ideally, we should define two enums with inheritance, which is,
 *  however, not supported.
 */
public enum Seed {   // to save as "Seed.java"
    CROSS("X", "FPPROJECT/images/x/xIcon.png"),
    NOUGHT("O", "FPPROJECT/images/o/oIcon.png"),
    NO_SEED(" ", null);

    // Private variables
    private String displayName;
    private Image img = null;
    private final String defaultImageFilename;
    private String soundFilename;

    // Constructor (must be private)
    private Seed(String name, String imageFilename) {
        this.displayName = name;
        this.defaultImageFilename = imageFilename; // Simpan path default
        loadImage(imageFilename); // Muat gambar default saat inisialisasi
        this.soundFilename = null;
    }
    // Method untuk meload gambar
    private void loadImage(String filename) {
        if (filename != null) {
            URL imgURL = getClass().getClassLoader().getResource(filename);
            if (imgURL != null) {
                this.img = new ImageIcon(imgURL).getImage();
            } else {
                System.err.println("Tidak dapat menemukan file gambar: " + filename);
                this.img = null;
            }
        } else {
            this.img = null;
        }
    }

    // Modifikasi method untuk mengatur gambar dan suara
    public void setImageAndSound(String imageFilename, String soundFilename) {
        loadImage(imageFilename);
        this.soundFilename = soundFilename;
    }

    // method-nya dimodifikasi buat ngereset suara & gambar
    public void resetToDefault() {
        loadImage(this.defaultImageFilename);
        this.soundFilename = null; // Reset suara juga
    }
    // Public getters
    public String getDisplayName() {
        return displayName;
    }
    public Image getImage() {
        return img;
    }
    public String getSoundFilename() {
        return soundFilename;
    }
}
