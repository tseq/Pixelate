package utils;

import models.Picture;
import models.Pixel;

import java.awt.image.BufferedImage;

/**
 * Created by Tse Qin on 1/1/2017.
 */
public class PictureDraw {
    /**
     * Naive algorithm to detect and draw the edge for this picture.
     *
     * @param picture Picture to be transformed
     * @return transformed picture
     */
    public static BufferedImage detectEdge(Picture picture) {
        int width = picture.getWidth();
        int height = picture.getHeight();

        Pixel[][] original = picture.getPixels();
        Pixel[][] result = new Pixel[height][width];

        // Iterate through pixel matrix horizontally
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width - 1; j++) {
                if (original[i][j].getRGB() != original[i][j + 1].getRGB())
                    result[i][j] = new Pixel(Pixel.MAX_ALPHA, 0, 0, 0);
                else
                    result[i][j] = original[i][j];
            }
        }

        // Iterate through pixel matrix vertically
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height - 1; i++) {
                if (original[i][j].getRGB() != original[i + 1][j].getRGB())
                    result[i][j] = new Pixel(Pixel.MAX_ALPHA, 0, 0, 0);
                else
                    result[i][j] = result[i][j] == null? original[i][j] : result[i][j];
            }
        }

        result[height - 1][width - 1] = original[height - 1][width - 1];

        return (new Picture(result)).getImage();
    }
}
