package models;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Tse Qin on 21/12/2016.
 */
public class Picture {
    private Pixel pixels[][];
    private String imgSrc;
    private boolean hasAlphaChannel;

    public Picture(String imgSrc) {
        this.imgSrc = imgSrc;
        parseImg(imgSrc);
    }

    public Picture(Pixel[][] pixels) {
        this.pixels = pixels;
    }

    // HELPER METHODS

    /**
     * Parse the imgSrc into a Pixel matrix.
     *
     * @param imgSrc image path
     */
    private void parseImg(String imgSrc) {
        try {
            BufferedImage image = ImageIO.read(new File(imgSrc));
            pixels = convertToRGBMatrix(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Algorithm to convert image to RGB matrix.
     * Source: http://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
     *
     * @param image BufferedImage to be converted into RGB matrix
     * @return Pixel matrix with RGB value
     */
    private Pixel[][] convertToRGBMatrix(BufferedImage image) {
        final byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        hasAlphaChannel = image.getAlphaRaster() != null;

        return getPixelMatrix(image, hasAlphaChannel, data, width, height);
    }

    /**
     * Helper method to convert image to RGB matrix.
     *
     * @param hasAlpha  true if image has alpha channel, false otherwise
     * @param data      the byte array of the image
     * @param imgWidth  the width of the image
     * @param imgHeight the height of the image
     * @return the Pixel matrix of the image
     */
    private Pixel[][] getPixelMatrix(BufferedImage image, boolean hasAlpha, byte[] data, int imgWidth, int imgHeight) {
        Pixel result[][] = new Pixel[imgHeight][imgWidth];
        final int pixelLength = hasAlpha ? 4 : 3;

        for (int pixelNum = 0, row = 0, col = 0; pixelNum < data.length; pixelNum += pixelLength) {
            if (hasAlpha) {
                result[row][col] = new Pixel(data[pixelNum], data[pixelNum + 3], data[pixelNum + 2], data[pixelNum + 1]);
            } else {
                result[row][col] = new Pixel(-16777216, data[pixelNum + 2], data[pixelNum + 1], data[pixelNum]);
            }
            col++;
            if (col == imgWidth) {
                col = 0;
                row++;
            }
        }
        return result;
    }

    // GETTERS & SETTERS

    /**
     * Getter method for imgSrc.
     *
     * @return imgSrc
     */
    public String getImgSrc() {
        return imgSrc;
    }

    /**
     * Generate the buffered image of the picture.
     *
     * @return buffered image of the picture
     */
    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                image.setRGB(j, i, pixels[i][j].getRGB());
            }
        }
        return image;
    }

    /**
     * Get the width of the picture.
     *
     * @return width of picture
     */
    public int getWidth() {
        return pixels == null ? 0 : pixels[0].length;
    }

    /**
     * Get the height of the picture
     *
     * @return height of picture.
     */
    public int getHeight() {
        return pixels == null ? 0 : pixels.length;
    }

    /**
     * Get a copy of the Pixel matrix
     *
     * @return copy of Pixel matrix.
     */
    public Pixel[][] getPixels() {
        Pixel[][] copy = new Pixel[getHeight()][];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = Arrays.copyOf(pixels[i], pixels[i].length);
        }
        return copy;
    }
}
