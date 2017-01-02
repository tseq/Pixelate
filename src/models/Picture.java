package models;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

/**
 * Picture class. Holds the matrix of all the pixels and enables translation between pixel matrix and
 * BufferedImage.
 */
public class Picture {
    private Pixel pixels[][];
    private String imgSrc;

    /**
     * Constructor. Accept image path as argument.
     *
     * @param imgSrc path of image to be parsed
     */
    public Picture(String imgSrc) {
        this.imgSrc = imgSrc;
        parseImg(imgSrc);
    }

    /**
     * Constructor. Accept Pixel[][] matrix as argument.
     *
     * @param pixels pixel matrix to be parsed
     */
    public Picture(Pixel[][] pixels) {
        this.pixels = pixels;
    }

    /**
     * Constructor. Accept BufferedImage as argument.
     *
     * @param image BufferedImage to be parsed.
     */
    public Picture(BufferedImage image) {
        pixels = convertToPixelMatrix(image);
    }

    /**
     * Downsample an image. Uses the imgscalr lib from:
     * https://github.com/rkalla/imgscalr
     *
     * @return downsampled image
     */
    public Pixel[][] downsample() {
        BufferedImage image = getImage();
        int width = image.getWidth();
        int height = image.getHeight();
        double ratio;

        if (width >= height) {
            ratio = 50.0 / width;
        } else {
            ratio = 50.0 / height;
        }

        BufferedImage scaledImage = Scalr.resize(image, (int) (width * ratio), (int) (height * ratio));
        return convertToPixelMatrix(scaledImage);
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
            pixels = convertToPixelMatrix(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Algorithm to convert image to RGB matrix.
     * Source: http://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
     *
     * @return Pixel matrix with RGB value
     */
    private Pixel[][] convertToPixelMatrix(BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        DataBuffer buffer = image.getRaster().getDataBuffer();

        if (buffer.getDataType() == DataBuffer.TYPE_BYTE) {
            final byte[] data = ((DataBufferByte) buffer).getData();
            boolean hasAlphaChannel = image.getAlphaRaster() != null;
            return getPixelMatrix(hasAlphaChannel, data, width, height);
        } else {
            final int[] data = ((DataBufferInt) buffer).getData();
            return getPixelMatrix(data, width, height);
        }
    }

    /**
     * Helper method to convert image to RGB matrix given data is of type byte.
     *
     * @param hasAlpha  true if image has alpha channel, false otherwise
     * @param data      the byte array of the image
     * @param imgWidth  the width of the image
     * @param imgHeight the height of the image
     * @return the Pixel matrix of the image
     */
    private Pixel[][] getPixelMatrix(boolean hasAlpha, byte[] data, int imgWidth, int imgHeight) {
        Pixel result[][] = new Pixel[imgHeight][imgWidth];
        final int pixelLength = hasAlpha ? 4 : 3;

        for (int pixelNum = 0, row = 0, col = 0; pixelNum < data.length; pixelNum += pixelLength) {
            if (hasAlpha) {
                result[row][col] = new Pixel(data[pixelNum], data[pixelNum + 3], data[pixelNum + 2], data[pixelNum + 1]);
            } else {
                result[row][col] = new Pixel(Pixel.MAX_ALPHA, data[pixelNum + 2], data[pixelNum + 1], data[pixelNum]);
            }
            col++;
            if (col == imgWidth) {
                col = 0;
                row++;
            }
        }
        return result;
    }

    /**
     * Helper method to convert image to RGB matrix given data is of type int.
     *
     * @param data      the int array of the image
     * @param imgWidth  the width of the image
     * @param imgHeight the height of the image
     * @return the Pixel matrix of the image
     */
    private Pixel[][] getPixelMatrix(int[] data, int imgWidth, int imgHeight) {
        Pixel result[][] = new Pixel[imgHeight][imgWidth];

        for (int pixelNum = 0, row = 0, col = 0; pixelNum < data.length; pixelNum++) {
            result[row][col] = new Pixel(data[pixelNum]);
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
        for (int i = 0; i < pixels.length; i++)
            for (int j = 0; j < pixels[0].length; j++)
                image.setRGB(j, i, pixels[i][j].getRGB());
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
        return pixels;
    }
}
