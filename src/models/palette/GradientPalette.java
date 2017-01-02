package models.palette;

import models.Pixel;
import utils.ColorMath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gradient palette. Creates the palette using color interpolation for each hue.
 */
public class GradientPalette {
    private static final int STEPS = 16;

    private static Pixel hues[];

    private HashMap<Pixel, ColorRamp> palette;
    private Pixel tempColor;

    public enum Hue {
        RED(0xff0000), ORANGE(0xff7f00), YELLOW(0xffff00), CHARTREUSE_GREEN(0x7fff00), GREEN(0x00ff00),
        SPRING_GREEN(0x00ff7f), CYAN(0x00ffff), AZURE(0x007fff), BLUE(0x0000ff), VIOLET(0x7f00ff), MAGENTA(0xff00ff),
        ROSE(0xff007f);

        private final int rgb;

        /**
         * Constructor. Sets the rgb value of the Hue enum.
         *
         * @param rgb rgb value
         */
        Hue(int rgb) {
            this.rgb = rgb;
        }

        /**
         * Return RGB value that this hue represents.
         *
         * @return RGB value
         */
        public int getRGB() {
            return rgb;
        }
    }

    // Static initializer block to initialize hues array.
    static {
        Hue allHues[] = Hue.values();
        hues = new Pixel[allHues.length];
        for (int i = 0; i < allHues.length; i++) {
            hues[i] = new Pixel(allHues[i].getRGB());
        }
    }

    /**
     * Constructor. Initializes instant variables.
     */
    public GradientPalette() {
        tempColor = null;
        initMap();
    }

    /**
     * Initialize the map that stores all the colors.
     */
    private void initMap() {
        palette = new HashMap<>();
        for (Pixel hue : hues) {
            palette.put(hue, new ColorRamp());
        }
    }

    /**
     * Add a color to the palette.
     *
     * @param color color to be added
     */
    public void add(Pixel color) {
        Pixel nearestHue = nearest(color, hues);
        palette.get(nearestHue).add(color);
    }

    /**
     * Check if a similar color exists. If such color exists, save it in tempColor. This method should be used with
     * getColor() if it returns true.
     *
     * @param color color to be compared with
     * @return true if the color exists, false otherwise.
     */
    public boolean exists(Pixel color) {
        Pixel nearestHue = nearest(color, hues);
        tempColor = nearestColor(color, palette.get(nearestHue).getRamp());
        return tempColor != null;
    }

    /**
     * Condense the palette by reducing each ramp to 4 colors.
     */
    public void condense() {
        for (Map.Entry<Pixel, ColorRamp> entry : palette.entrySet())
            entry.getValue().condense();
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
     * Get size of the palette (number of colors stored).
     *
     * @return size of the palette.
     */
    public int getSize() {
        return palette.size();
    }

    /**
     * Get the nearest hue of the color.
     *
     * @param currentColor color benchmark
     * @param colors       hues array
     * @return Pixel of the nearest hue
     */
    private static Pixel nearest(Pixel currentColor, Pixel colors[]) {
        double min = currentColor.colorDifferenceVal(colors[0]);
        Pixel nearest = colors[0];

        for (int i = 1; i < colors.length; i++) {
            double diff = currentColor.colorDifferenceVal(colors[i]);
            if (diff < min) {
                min = diff;
                nearest = colors[i];
            }
        }

        return nearest;
    }

    /**
     * Obtain the nearest color in an array of colors.
     *
     * @param currentColor color benchmark
     * @param colors       array of colors to be compared with the benchmark
     * @return the nearest color if it exists, null if the nearest color is not similar to the current color
     */
    public static Pixel nearestColor(Pixel currentColor, Pixel colors[]) {
        if (colors.length == 0)
            return null;

        double min = currentColor.colorDifferenceVal(colors[0]);
        Pixel nearest = colors[0];

        for (int i = 1; i < colors.length; i++) {
            double diff = currentColor.colorDifferenceVal(colors[i]);
            if (diff < min) {
                min = diff;
                nearest = colors[i];
            }
        }

        if (ColorMath.valueToScale(min).isSimilar())
            return nearest;
        return null;
    }

    /**
     * Obtain the nearest color in an array of colors.
     *
     * @param currentColor color benchmark
     * @param colors       array list of colors to be compared with the benchmark
     * @return the nearest color if it exists, null if the nearest color is not similar to the current color
     */
    public static Pixel nearestColor(Pixel currentColor, List<Pixel> colors) {
        return nearestColor(currentColor, colors.toArray(new Pixel[colors.size()]));
    }

    private class ColorRamp {
        private ArrayList<Pixel> ramp;

        /**
         * Constructor.
         */
        ColorRamp() {
            ramp = new ArrayList<>();
        }

        /**
         * Add a Pixel(color) to the ramp.
         *
         * @param pixel Pixel to be added to ramp
         */
        void add(Pixel pixel) {
            ramp.add(pixel);
        }

        /**
         * Get ramp.
         *
         * @return ramp
         */
        List<Pixel> getRamp() {
            return ramp;
        }

        /**
         * Condense the ramp by reducing the number of colors to 4.
         */
        void condense() {
            if (ramp.size() == 0)
                return;

            Pixel[] gradient = Pixel.toPixels(ColorMath.generateGradient(getLightestColor().getRGBComponents(),
                    getDarkestColor().getRGBComponents(), STEPS));

            for (Pixel p : ramp) {
                Pixel nearest = nearest(p, gradient);
                p.setRGB(nearest.getRGB());
            }
        }

        /**
         * Get the lightest color in the ramp. The lightest color is the one with the highest luminance.
         *
         * @return lightest color
         */
        private Pixel getLightestColor() {
            double max = ColorMath.luminance(ramp.get(0).getRGBComponents());
            Pixel lightest = ramp.get(0);

            for (int i = 1; i < ramp.size(); i++) {
                Pixel current = ramp.get(i);
                double luminance = ColorMath.luminance(current.getRGBComponents());
                if (luminance > max) {
                    max = luminance;
                    lightest = current;
                }
            }

            return lightest;
        }

        /**
         * Get the darkest color in the ramp. The darkest color is the one with the lowest luminance.
         *
         * @return darkest color
         */
        private Pixel getDarkestColor() {
            double min = ColorMath.luminance(ramp.get(0).getRGBComponents());
            Pixel darkest = ramp.get(0);

            for (int i = 1; i < ramp.size(); i++) {
                Pixel current = ramp.get(i);
                double luminance = ColorMath.luminance(current.getRGBComponents());
                if (luminance < min) {
                    min = luminance;
                    darkest = current;
                }
            }

            return darkest;
        }
    }
}
