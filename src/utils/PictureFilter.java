package utils;

import models.Palette;
import models.Picture;
import models.Pixel;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Tse Qin on 21/12/2016.
 */
public class PictureFilter {

    public static BufferedImage gridDifferenceFilter(Picture picture) {
        int width = picture.getWidth();
        int height = picture.getHeight();

        Pixel[][] original = picture.getPixels();
        Pixel[][] result = new Pixel[height][width];

        // Initialize palette
        Palette palette = new Palette();
        Pixel currentColor = null;

        // Iterate through pixel matrix
        for (int i = 0; i < height; i += 4) {
            for (int j = 0; j < width; j += 4) {

                // Obtain average of 4 inner colors
                ArrayList<Pixel> colors = new ArrayList<>();
                Pixel averageColor;
                for (int x = Math.min(i + 1, height); x < Math.min(i + 3, height); x++) {
                    for (int y = Math.min(j + 1, width); y < Math.min(j + 3, width); y++) {
                        colors.add(original[x][y]);
                    }
                }
                averageColor = colors.size() == 0 ? original[i][j] : Pixel.average(colors);

                // Obtain current color
                if (currentColor == null) {
                    palette.add(averageColor);
                    currentColor = averageColor;
                }
                if (!Pixel.colorDifference(currentColor, averageColor).isSimilar()) {
                    if (palette.exists(averageColor)) {
                        currentColor = palette.getColor();
                    } else {
                        palette.add(averageColor);
                        currentColor = averageColor;
                    }
                }

                // Paint the 4x4 grid
                for (int x = i; x < Math.min(height, i + 4); x++) {
                    for (int y = j; y < Math.min(width, j + 4); y++)
                        result[x][y] = currentColor;
                }
            }
        }

        return (new Picture(result)).getImage();
    }

    /**
     * Determines the color of the current pixel based on the color history of the traversed pixels, and the 3
     * northern neighbors of the current pixel.
     *
     * @param picture Picture to be transformed
     * @return transformed picture
     */
    public static BufferedImage linearDifferenceFilter2(Picture picture) {
        int width = picture.getWidth();
        int height = picture.getHeight();

        Pixel[][] original = picture.getPixels();
        Pixel[][] result = new Pixel[height][width];

        // Initialize palette
        Pixel currentColor = original[0][0];
        Palette palette = new Palette();
        palette.add(currentColor);

        // Iterate through pixel matrix
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!Pixel.colorDifference(currentColor, original[i][j]).isSimilar()) {
                    // Check 3 neighbors above current pixel
                    boolean found = false;
                    for (int x = Math.max(0, j - 1); i != 0 && x < Math.min(width, j + 2); x++) {
                        if (Pixel.colorDifference(original[i][j], result[i - 1][x]).isSimilar()) {
                            currentColor = result[i - 1][x];
                            found = true;
                            break;
                        }
                    }
                    // Check palette
                    if (!found) {
                        if (palette.exists(original[i][j])) {
                            currentColor = palette.getColor();
                        } else {
                            palette.add(original[i][j]);
                            currentColor = original[i][j];
                        }
                    }
                }
                result[i][j] = currentColor;
            }
        }

        return (new Picture(result)).getImage();
    }

    /**
     * Determines the color of the current pixel based on the color history of the traversed pixels.
     *
     * @param picture Picture to be transformed
     * @return transformed picture
     */
    public static BufferedImage lienarDifferenceFilter(Picture picture) {
        int width = picture.getWidth();
        int height = picture.getHeight();

        Pixel[][] original = picture.getPixels();
        Pixel[][] result = new Pixel[height][width];

        // Initialize palette
        Pixel currentColor = original[0][0];
        Palette palette = new Palette();
        palette.add(currentColor);

        // Iterate through pixel matrix
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (Pixel.colorDifference(currentColor, original[i][j]).greaterThan(PerceptionScale.LEVEL_3)) {
                    // Check palette
                    if (palette.exists(original[i][j])) {
                        currentColor = palette.getColor();
                    } else {
                        palette.add(original[i][j]);
                        currentColor = original[i][j];
                    }
                }
                result[i][j] = currentColor;
            }
        }

        return (new Picture(result)).getImage();
    }

    /**
     * Determine the color of the neighbor pixels in a 4x4 grid based on the color of the top left pixel.
     *
     * @param picture Picture to be transformed
     * @return transformed picture
     */
    public static BufferedImage gridSpaceFilter(Picture picture) {
        int width = picture.getWidth();
        int height = picture.getHeight();

        Pixel[][] original = picture.getPixels();
        Pixel[][] result = new Pixel[height][width];

        // Iterate through pixel matrix
        for (int i = 0; i < height; i += 4) {
            for (int j = 0; j < width; j += 4) {

                // Iterate through 4x4 grid
                for (int x = i; x < Math.min(height, i + 4); x++) {
                    for (int y = j; y < Math.min(width, j + 4); y++) {
                        result[x][y] = original[i][j];
                    }
                }
            }
        }

        return (new Picture(result)).getImage();
    }

    /**
     * Determine the color of the middle pixel in a 5x5 grid by assigning weights to the neighbors.
     *
     * @param picture        Picture to be transformed
     * @param midWeight      weight of middle pixel
     * @param neighborWeight weight of neighbor pixels
     * @return transformed image
     */
    public static BufferedImage gridWeightFilter(Picture picture, double midWeight, double neighborWeight) {
        midWeight = (midWeight == 0 ? 0.1 : midWeight);
        neighborWeight = (neighborWeight == 0 ? 0.0375 : neighborWeight);

        int width = picture.getWidth();
        int height = picture.getHeight();

        Pixel[][] original = picture.getPixels();
        Pixel[][] result = new Pixel[height][width];

        // Iterate through pixel matrix
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Pixel pixel = new Pixel(0, 0, 0, 0);
                double totalWeight = 0;

                // Iterate through 5x5 grid
                for (int x = Math.max(0, i - 2); x < Math.min(height, i + 3); x++) {
                    for (int y = Math.max(0, j - 2); y < Math.min(width, j + 3); y++) {
                        if (x == i && y == j) {
                            pixel.add(original[x][y], midWeight);
                            totalWeight += midWeight;
                            continue;
                        }
                        pixel.add(original[x][y], neighborWeight);
                        totalWeight += neighborWeight;
                    }
                }

                // Assign remaining weight to middle pixel.
                if (totalWeight < 1) {
                    pixel.add(original[i][j], 1 - totalWeight);
                }

                pixel.computeArgb();
                result[i][j] = pixel;
            }
        }

        return (new Picture(result)).getImage();
    }
}
