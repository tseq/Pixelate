import models.Picture;
import utils.PictureFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Main class.
 */
public class Pixelator {
    private static final String FOLDER = "unprocessed_pics/";
    private static final String TEMP = "processed_pics/temp.jpg";
    private static final String RED_APPLE = FOLDER + "red_apple.jpg";
    private static final String GREEN_RED_APPLE = FOLDER + "green_red_apple.jpg";
    private static final String BLURRY_CAT = FOLDER + "blurry_cat.jpg";
    private static final String TINY_CAT = FOLDER + "tiny_cat.jpg";
    private static final String JAP_CLASSROOM = FOLDER + "jap_classroom.jpg";
    private static final String PIZZA_SLICE = FOLDER + "pizza_slice.JPEG";
    private static final String SMALL_PIZZA = FOLDER + "small_pizza.png";
    private static final String SQUARE_PIZZA = FOLDER + "square_pizza.jpg";
    private static final String THIN_TREE = FOLDER + "thin_tree.jpg";
    private static final String LENA = FOLDER + "lena.jpg";
    private static final String CHINA = FOLDER + "china.png";
    private static final String CHINA2 = FOLDER + "china2.png";
    private static final String CHINA3 = FOLDER + "china3.png";

    private Picture picture;

    public Pixelator(String imgSrc) {
        picture = new Picture(imgSrc);
    }

    private BufferedImage filter(int filterChoice) {
        switch(filterChoice) {
            case -2:
                PictureFilter.increaseContrast(picture);
                return null;
            case -1:
                PictureFilter.increaseSaturation(picture);
                return null;
            case 0:
                return PictureFilter.gridWeightFilter(picture, 0, 0);
            case 1:
                return PictureFilter.gridSpaceFilter(picture);
            case 2:
                return PictureFilter.gridSpaceFilter2(picture);
            case 3:
                return PictureFilter.gridSpaceFilter3(picture, 64);
            case 4:
                return PictureFilter.linearDifferenceFilter(picture);
            case 5:
                return PictureFilter.linearDifferenceFilter2(picture);
            case 6:
                return PictureFilter.linearDifferenceFilter3(picture);
            case 7:
                return PictureFilter.linearDifferenceFilter4(picture);
            case 8:
                return PictureFilter.gridDifferenceFilter(picture);
            default:
                return null;
        }
    }

    public BufferedImage getImage() {
        return picture.getImage();
    }

    public static void setText(JLabel label, String text) {
        label.setIconTextGap(-125);
        label.setText(text);
    }

    public static void main(String[] args) throws IOException {
        Pixelator pixelator = new Pixelator(LENA);

        BufferedImage originalImage = pixelator.getImage();
        pixelator.filter(-1);
        BufferedImage saturatedImage = pixelator.getImage();

        long startTime = System.currentTimeMillis();
        BufferedImage colorImage64 = pixelator.filter(3);
        long endTime = System.currentTimeMillis();
        System.out.println((endTime - startTime) + " milliseconds");

        JPanel container = new JPanel(new GridLayout(2, 2));

        JLabel originalLabel = new JLabel(new ImageIcon(originalImage));
        setText(originalLabel, "Original Image");
        JLabel saturatedLabel = new JLabel(new ImageIcon(saturatedImage));
        setText(saturatedLabel, "Saturated Image");
        JLabel color64Label = new JLabel(new ImageIcon(colorImage64));
        setText(color64Label, "64 Colors Image");

        container.add(originalLabel);
        container.add(saturatedLabel);
        container.add(color64Label);

        show(container);
    }

    public static void show(JPanel container) {
        JFrame frame = new JFrame("Pixelator");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(container);
        frame.pack();
        frame.setVisible(true);
    }

    public static void save(BufferedImage image) {
        try {
            File outputfile = new File(TEMP);
            ImageIO.write(image, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
