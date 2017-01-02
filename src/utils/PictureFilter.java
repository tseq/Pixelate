package utils;

import models.palette.KMeansPalette;
import models.palette.Palette;
import models.Picture;
import models.Pixel;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * PictureFilter class. Contains implementations of all filters. TODO: Code cleaning
 */
public class PictureFilter {

    /**
     * Combines the pros of grid algorithm (averaging, mosaic) and color difference algorithm (palette) together.
     *
     * @param picture Picture to be transformed
     * @return transformed picture
     */
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
                if (!Pixel.colorDifferenceScale(currentColor, averageColor).isSimilar()) {
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
     * TODO: CLEAN THIS METHOD UP!
     * Determines the color of the current pixel based on the color history of the traversed pixels, and its
     * neighbors (depending on which case) by traversing the matrix in an out-in spiral motion.
     *
     * @param picture Picture to be transformed
     * @return
     */
    public static BufferedImage linearDifferenceFilter4(Picture picture) {
        int width = picture.getWidth();
        int height = picture.getHeight();

        Pixel[][] original = picture.getPixels();
        Pixel[][] result = new Pixel[height][width];

        // Initialize palette
        Pixel currentColor = original[0][0];
        Palette palette = new Palette();
        palette.add(currentColor);

        // Iterate through pixel matrix in spiral motion
        int firstCol = 0, lastCol = width - 1;
        int firstRow = 0, lastRow = height - 1;

        while (firstCol <= lastCol && firstRow <= lastRow) {
            // Case 1: Right - Neighbors: 3 Northern, 1 West (current)
            for (int i = firstCol; i <= lastCol; i++) {
                ArrayList<Pixel> colors = new ArrayList<>();
                // Check neighbors
                for (int x = Math.max(0, i - 1); firstRow != 0 && x < Math.min(width, i + 2); x++) {
                    colors.add(result[firstRow - 1][x]);
                }
                colors.add(currentColor);
                currentColor = Palette.nearestColor(original[firstRow][i], colors);
                // Check palette
                if (currentColor == null) {
                    if (palette.exists(original[firstRow][i])) {
                        currentColor = palette.getColor();
                    } else {
                        palette.add(original[firstRow][i]);
                        currentColor = original[firstRow][i];
                    }
                }
                result[firstRow][i] = currentColor;
            }
            firstRow++;

            // Case 2: Down - Neighbors: 3 Eastern, 1 North
            for (int i = firstRow; i <= lastRow; i++) {
                ArrayList<Pixel> colors = new ArrayList<>();
                // Check neighbors
                for (int x = Math.max(0, i - 1); lastCol != width - 1 && x < Math.min(height, i + 2); x++) {
                    colors.add(result[x][lastCol + 1]);
                }
                colors.add(currentColor);
                currentColor = Palette.nearestColor(original[i][lastCol], colors);
                // Check palette
                if (currentColor == null) {
                    if (palette.exists(original[i][lastCol])) {
                        currentColor = palette.getColor();
                    } else {
                        palette.add(original[i][lastCol]);
                        currentColor = original[i][lastCol];
                    }
                }
                result[i][lastCol] = currentColor;
            }
            lastCol--;

            // Case 3: Left - Neighbors: 3 Southern, 1 East
            for (int i = lastCol; i >= firstCol; i--) {
                ArrayList<Pixel> colors = new ArrayList<>();
                // Check neighbors
                for (int x = Math.max(0, i - 1); lastRow != height - 1 && x < Math.min(width, i + 2); x++) {
                    colors.add(result[lastRow + 1][x]);
                }
                colors.add(currentColor);
                currentColor = Palette.nearestColor(original[lastRow][i], colors);
                // Check palette
                if (currentColor == null) {
                    if (palette.exists(original[lastRow][i])) {
                        currentColor = palette.getColor();
                    } else {
                        palette.add(original[lastRow][i]);
                        currentColor = original[lastRow][i];
                    }
                }
                result[lastRow][i] = currentColor;
            }
            lastRow--;

            // Case 4: Up - Neighbors: 3 Western, 1 South
            for (int i = lastRow; i >= firstRow; i--) {
                ArrayList<Pixel> colors = new ArrayList<>();
                // Check neighbors
                for (int x = Math.max(0, i - 1); firstCol != 0 && x < Math.min(height, i + 2); x++) {
                    colors.add(result[x][firstCol - 1]);
                }
                colors.add(currentColor);
                currentColor = Palette.nearestColor(original[i][firstCol], colors);
                // Check palette
                if (currentColor == null) {
                    if (palette.exists(original[i][firstCol])) {
                        currentColor = palette.getColor();
                    } else {
                        palette.add(original[i][firstCol]);
                        currentColor = original[i][firstCol];
                    }
                }
                result[i][firstCol] = currentColor;
            }
            firstCol++;

        }

        return (new Picture(result)).getImage();
    }

    /**
     * Determines the color of the current pixel based on the color history of the traversed pixels, and the 3
     * northern neighbors of the current pixel using nearest color method.
     *
     * @param picture Picture to be transformed
     * @return transformed picture
     */
    public static BufferedImage linearDifferenceFilter3(Picture picture) {
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
                if (!Pixel.colorDifferenceScale(currentColor, original[i][j]).isSimilar()) {
                    // Check 3 neighbors above current pixel
                    ArrayList<Pixel> colors = new ArrayList<>();
                    colors.add(currentColor);
                    for (int x = Math.max(0, j - 1); i != 0 && x < Math.min(width, j + 2); x++) {
                        colors.add(result[i - 1][x]);
                    }
                    currentColor = Palette.nearestColor(original[i][j], colors);

                    // Check palette
                    if (currentColor == null) {
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
                if (!Pixel.colorDifferenceScale(currentColor, original[i][j]).isSimilar()) {
                    // Check 3 neighbors above current pixel
                    boolean found = false;
                    for (int x = Math.max(0, j - 1); i != 0 && x < Math.min(width, j + 2); x++) {
                        if (Pixel.colorDifferenceScale(original[i][j], result[i - 1][x]).isSimilar()) {
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
    public static BufferedImage linearDifferenceFilter(Picture picture) {
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
                if (!Pixel.colorDifferenceScale(currentColor, original[i][j]).isSimilar()) {
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
     * Determine the color of the neighbor pixels in a 4x4 grid based on the color of the pixel at (1, 1).
     *
     * @param picture Picture to be transformed
     * @return transformed picture
     */
    public static BufferedImage gridSpaceFilter2(Picture picture) {
        int width = picture.getWidth();
        int height = picture.getHeight();

        Pixel[][] original = picture.getPixels();
        Pixel[][] result = new Pixel[height][width];

        // Iterate through pixel matrix
        for (int i = 0; i < height; i += 4) {
            for (int j = 0; j < width; j += 4) {

                ArrayList<Pixel> colors = new ArrayList<>();
                Pixel currentColor;
                for (int x = Math.min(i + 1, height); x < Math.min(i + 3, height); x++) {
                    for (int y = Math.min(j + 1, width); y < Math.min(j + 3, width); y++) {
                        colors.add(original[x][y]);
                        break;
                    }
                }
                currentColor = colors.size() == 0 ? original[i][j] : colors.get(0);

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
     * Determine the color of the neighbor pixels in a 4x4 grid based on the color of the top left pixel.
     *
     * @param picture Picture to be transformed
     * @return transformed picture
     */
    public static BufferedImage gridSpaceFilter3(Picture picture, int numColors) {
        int width = picture.getWidth();
        int height = picture.getHeight();

        Pixel[][] original = picture.getPixels();
        Pixel[][] downsample = picture.downsample();
        KMeansPalette palette = new KMeansPalette(downsample, numColors);

        Pixel[][] result = new Pixel[height][width];
        // Iterate through pixel matrix
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Pixel nearestColor = palette.nearestColor(original[i][j]);
                result[i][j] = nearestColor;
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

    /**
     * Increase saturation of picture.
     *
     * @param picture Picture to be transformed
     */
    public static void increaseSaturation(Picture picture) {
        int width = picture.getWidth();
        int height = picture.getHeight();

        Pixel[][] pixels = picture.getPixels();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixels[i][j].increaseSaturation(1.5f);
            }
        }
    }

    /**
     * Increase contrast of picture.
     *
     * @param picture Picture to transform
     */
    public static void increaseContrast(Picture picture) {
        int width = picture.getWidth();
        int height = picture.getHeight();

        Pixel[][] pixels = picture.getPixels();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixels[i][j].increaseContrast(20);
            }
        }
    }
}
