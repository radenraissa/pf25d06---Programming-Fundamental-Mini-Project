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

    // private final String displayName; // kami pakai getImage() ehingga icon berupa gambar
    private Image img = null;
    private final String defaultImageFilename;
    private String soundFilename;

    Seed(String name, String imageFilename) {
        // this.displayName = name;
        this.defaultImageFilename = imageFilename; // Simpan path default
        loadImage(imageFilename); // Muat gambar default saat inisialisasi
        this.soundFilename = null;
    }
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

    public void setImageAndSound(String imageFilename, String soundFilename) {
        loadImage(imageFilename);
        this.soundFilename = soundFilename;
    }

    public void resetToDefault() {
        loadImage(this.defaultImageFilename);
        this.soundFilename = null; // Reset suara juga
    }
    // Public getters
//    public String getDisplayName() {
//        return displayName;
//    }  // karena kami pakai getImage() tanpa melakukan penggambaran icon

    public Image getImage() {
        return img;
    }
    public String getSoundFilename() {
        return soundFilename;
    }
}
