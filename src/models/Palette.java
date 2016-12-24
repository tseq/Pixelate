package models;

import utils.ColorMath;

import java.util.ArrayList;
import java.util.List;

/**
 * Palette class. Mimics a real life palette by holding various colors (Pixel objects).
 */
public class Palette {
    private ArrayList<Pixel> palette;
    private Pixel tempColor;

    /**
     * Constructor. Initializes instant variables.
     */
    public Palette() {
        palette = new ArrayList<>();
        tempColor = null;
    }

    /**
     * Add a color to the palette.
     *
     * @param color color to be added
     */
    public void add(Pixel color) {
        palette.add(color);
    }

    /**
     * Check if a similar color exists. If such color exists, save it in tempColor. This method should be used with
     * getColor() if it returns true.
     *
     * @param color color to be compared with
     * @return true if the color exists, false otherwise.
     */
    public boolean exists(Pixel color) {
        for (Pixel p : palette) {
            if (p.colorDifferenceScale(color).isSimilar()) {
                tempColor = p;
                return true;
            }
        }
        return false;
    }

    /**
     * Get the tempColor stored as a result of exists(Pixel color) method. tempColor is updated if the
     * exists(Pixel color) method returns true.
     *
     * @return tempColor saved as a byproduct of exists(Pixel color)
     */
    public Pixel getColor() {
        return tempColor;
    }

    /**
     * Obtain the nearest color in an array of colors.
     * @param currentColor color benchmark
     * @param colors array of colors to be compared with the benchmark
     * @return the nearest color if it exists, null if the nearest color is not similar to the current color
     */
    public static Pixel nearestColor(Pixel currentColor, Pixel colors[]) {
        double min = currentColor.colorDifferenceVal(colors[0]);
        Pixel nearest = null;

        for (int i = 1; i < colors.length; i++) {
            double diff = currentColor.colorDifferenceVal(colors[i]);
            if (diff < min) {
                min = diff;
                nearest = colors[i];
            }
        }

        if (nearest == null || !ColorMath.valueToScale(min).isSimilar())
            return null;

        return nearest;
    }

    public static Pixel nearestColor(Pixel currentColor, List<Pixel> colors) {
        return nearestColor(currentColor, colors.toArray(new Pixel[colors.size()]));
    }
}
