import models.Picture;
import utils.PictureFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Main class.
 */
public class Pixelator {
    private Picture picture;

    public Pixelator(String imgSrc) {
        picture = new Picture(imgSrc);
    }

    private BufferedImage filter(int filterChoice) {
        switch(filterChoice) {
            case 0:
                return PictureFilter.gridWeightFilter(picture, 0, 0);
            case 1:
                return PictureFilter.gridSpaceFilter(picture);
            case 2:
                return PictureFilter.lienarDifferenceFilter(picture);
            case 3:
                return PictureFilter.linearDifferenceFilter2(picture);
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
        Pixelator pixelator = new Pixelator("unprocessed_pics/red_apple.jpg");

        BufferedImage originalImage = pixelator.getImage();
        BufferedImage blurImage = pixelator.filter(0);
        BufferedImage mosaicImage = pixelator.filter(1);
        BufferedImage linearDiffImage = pixelator.filter(2);
        BufferedImage linearDiff2Image = pixelator.filter(3);

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

        container.add(originalIcon);
        container.add(blurIcon);
        container.add(mosaicIcon);
        container.add(linearDiffIcon);
        container.add(linearDiff2Icon);

        JFrame frame = new JFrame("Pixelator");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(container);
        frame.pack();
        frame.setVisible(true);
    }

    public static void storeSomeMethod() {
        //File outputfile = new File("processed_pics/temp.jpg");
        //ImageIO.write(someImage, "jpg", outputfile);
    }
}
