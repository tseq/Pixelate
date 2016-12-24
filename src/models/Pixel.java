package models;

import utils.ColorMath;
import utils.PerceptionScale;

import java.util.List;

/**
 * Pixel class. Enables calculation of RGB values.
 */
public class Pixel {
    private int argb;
    private double red, green, blue, alpha;

    public Pixel(int alpha, int red, int green, int blue) {
        this.alpha = alpha;
        this.red = red;
        this.green = green;
        this.blue = blue;
        computeArgb();
    }

    public void add(Pixel pixel, double weight) {
        alpha += pixel.getAlpha() * weight;
        red += pixel.getRed() * weight;
        green += pixel.getGreen() * weight;
        blue += pixel.getBlue() * weight;
    }

    public void computeArgb() {
        int a = ((int) alpha) & 0xFF;
        int r = ((int) red) & 0xFF;
        int g = ((int) green) & 0xFF;
        int b = ((int) blue) & 0xFF;

        argb = (a << 24) | (r << 16) | (g << 8) | b;
    }

    public PerceptionScale colorDifference(Pixel p) {
        return colorDifference(this, p);
    }

    public int getRGB() {
        return (argb << 8) >> 8;
    }

    /**
     * Get the alpha value of the pixel.
     *
     * @return alpha value
     */
    public int getAlpha() {
        return (argb >> 24) & 0xFF;
    }

    /**
     * Get the red value of the pixel.
     *
     * @return red value
     */
    public int getRed() {
        return (argb >> 16) & 0xFF;
    }

    /**
     * Get the green value of the pixel.
     *
     * @return green value
     */
    public int getGreen() {
        return (argb >> 8) & 0xFF;
    }

    /**
     * Get the blue value of the pixel.
     *
     * @return blue value
     */
    public int getBlue() {
        return argb & 0xFF;
    }

    /**
     * Get the argb value of the pixel.
     *
     * @return argb value
     */
    public int getARGB() {
        return argb;
    }

    public static Pixel copy(Pixel pixel) {
        return new Pixel(pixel.getAlpha(), pixel.getRed(), pixel.getGreen(), pixel.getBlue());
    }

    public static Pixel average(Pixel pixels[]) {
        int total = pixels.length;
        double weight = 1.0 / total;
        Pixel newPixel = new Pixel(0, 0, 0, 0);

        for (Pixel p : pixels) {
            newPixel.add(p, weight);
        }

        newPixel.computeArgb();
        return newPixel;
    }

    public static Pixel average(List<Pixel> pixels) {
        return average(pixels.toArray(new Pixel[pixels.size()]));
    }

    public static PerceptionScale colorDifference(Pixel p1, Pixel p2) {
        return ColorMath.colorDifference(new double[]{p1.getRed(), p1.getGreen(), p1.getBlue()},
                new double[]{p2.getRed(), p2.getGreen(), p2.getBlue()});
    }
}
