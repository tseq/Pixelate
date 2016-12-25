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
    private static final String RED_APPLE = FOLDER + "red_apple.jpg";
    private static final String GREEN_RED_APPLE = FOLDER + "green_red_apple.jpg";
    private static final String BLURRY_CAT = FOLDER + "blurry_cat.jpg";
    private static final String TINY_CAT = FOLDER + "tiny_cat.jpg";
    private static final String JAP_CLASSROOM = FOLDER + "jap_classroom.jpg";
    private static final String PIZZA_SLICE = FOLDER + "pizza_slice.JPEG";
    private static final String SMALL_PIZZA = FOLDER + "small_pizza.png";
    private static final String SQUARE_PIZZA = FOLDER + "square_pizza.jpg";
    private static final String THIN_TREE = FOLDER + "thin_tree.jpg";

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
                return PictureFilter.linearDifferenceFilter(picture);
            case 3:
                return PictureFilter.linearDifferenceFilter2(picture);
            case 4:
                return PictureFilter.linearDifferenceFilter3(picture);
            case 5:
                return PictureFilter.linearDifferenceFilter4(picture);
            case 6:
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
        Pixelator pixelator = new Pixelator(RED_APPLE);

        BufferedImage originalImage = pixelator.getImage();
        pixelator.filter(-1);
        BufferedImage saturatedImage = pixelator.getImage();
        //pixelator.filter(-2);
        //BufferedImage contrastedImage = pixelator.getImage();
        BufferedImage blurImage = pixelator.filter(0);
        BufferedImage mosaicImage = pixelator.filter(1);
        BufferedImage linearDiffImage = pixelator.filter(2);
        BufferedImage linearDiff2Image = pixelator.filter(3);
        BufferedImage linearDiff3Image = pixelator.filter(4);
        BufferedImage linearDiff4Image = pixelator.filter(5);
        BufferedImage gridDiffImage = pixelator.filter(6);

        JPanel container = new JPanel(new GridLayout(2, 3));

        JLabel originalIcon = new JLabel(new ImageIcon(originalImage));
        setText(originalIcon, "Original Image");
        JLabel blurIcon = new JLabel(new ImageIcon(blurImage));
        setText(blurIcon, "Grid Weight Filter");
        JLabel mosaicIcon = new JLabel(new ImageIcon(mosaicImage));
        setText(mosaicIcon, "Grid Space Filter");
        JLabel linearDiffIcon = new JLabel(new ImageIcon(linearDiffImage));
        setText(linearDiffIcon, "<html>Linear Difference<br>Filter</html>");
        JLabel linearDiff2Icon = new JLabel(new ImageIcon(linearDiff2Image));
        setText(linearDiff2Icon, "<html>Linear Difference<br>Filter 2</html>");
        JLabel linearDiff3Icon = new JLabel(new ImageIcon(linearDiff3Image));
        setText(linearDiff3Icon, "<html>Linear Difference<br>Filter 3</html>");
        JLabel linearDiff4Icon = new JLabel(new ImageIcon(linearDiff4Image));
        setText(linearDiff4Icon, "<html>Linear Difference<br>Filter 4</html>");
        JLabel gridDiffIcon = new JLabel(new ImageIcon(gridDiffImage));
        setText(gridDiffIcon, "<html>Grid-Difference<br>Filter</html>");

        container.add(originalIcon);
        container.add(new JLabel(new ImageIcon(saturatedImage)));
//        container.add(new JLabel(new ImageIcon(contrastedImage)));
        container.add(blurIcon);
        container.add(mosaicIcon);
        container.add(linearDiffIcon);
        container.add(linearDiff2Icon);
        container.add(linearDiff3Icon);
        container.add(linearDiff4Icon);
        container.add(gridDiffIcon);

        JFrame frame = new JFrame("Pixelator");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(container);
        frame.pack();
        frame.setVisible(true);
    }

    public static void save(BufferedImage image) {
        try {
            File outputfile = new File("processed_pics/temp.jpg");
            ImageIO.write(image, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
