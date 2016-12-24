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

    /**
     * Constructor.
     *
     * @param alpha alpha value
     * @param red   red value
     * @param green green value
     * @param blue  blue value
     */
    public Pixel(int alpha, int red, int green, int blue) {
        this.alpha = alpha;
        this.red = red;
        this.green = green;
        this.blue = blue;
        computeArgb();
    }

    /**
     * Add the values of a pixel to the current pixel.
     *
     * @param pixel  pixel to be added
     * @param weight weight of that pixel
     */
    public void add(Pixel pixel, double weight) {
        alpha += pixel.getAlpha() * weight;
        red += pixel.getRed() * weight;
        green += pixel.getGreen() * weight;
        blue += pixel.getBlue() * weight;
    }

    /**
     * Compute the argb value of a pixel.
     */
    public void computeArgb() {
        int a = ((int) alpha) & 0xFF;
        int r = ((int) red) & 0xFF;
        int g = ((int) green) & 0xFF;
        int b = ((int) blue) & 0xFF;

        argb = (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Compute the difference between the current pixel and another pixel in scale.
     *
     * @param p pixel to be compared with
     * @return PerceptionScale of the difference
     */
    public PerceptionScale colorDifferenceScale(Pixel p) {
        return colorDifferenceScale(this, p);
    }

    /**
     * Compute the difference between the current pixel and another pixel in value.
     *
     * @param p pixel to be copared with
     * @return value of the difference
     */
    public double colorDifferenceVal(Pixel p) {
        return colorDifferenceVal(this, p);
    }

    /**
     * Compute rgb value of the pixel.
     *
     * @return rgb value
     */
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

    /**
     * Compute the average values (color) of an array of pixels.
     *
     * @param pixels array of pixels to be averaged
     * @return averaged pixel
     */
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

    /**
     * Compute the average values (color) of an array of pixels. Wraps around average(Pixel pixels[]) method.
     *
     * @param pixels array of pixels to be averaged
     * @return averaged pixel
     */
    public static Pixel average(List<Pixel> pixels) {
        return average(pixels.toArray(new Pixel[pixels.size()]));
    }

    /**
     * Compute color difference between 2 pixels in scale.
     *
     * @param p1 first pixel
     * @param p2 second pixel
     * @return PerceptionScale of the difference between the two pixels.
     */
    public static PerceptionScale colorDifferenceScale(Pixel p1, Pixel p2) {
        return ColorMath.colorDifferenceScale(new double[]{p1.getRed(), p1.getGreen(), p1.getBlue()},
                new double[]{p2.getRed(), p2.getGreen(), p2.getBlue()});
    }

    /**
     * Compute color difference between 2 pixels in value.
     *
     * @param p1 first pixel
     * @param p2 second pixel
     * @return value of the difference between the two pixels.
     */
    public static double colorDifferenceVal(Pixel p1, Pixel p2) {
        return ColorMath.colorDifferenceVal(new double[]{p1.getRed(), p1.getGreen(), p1.getBlue()},
                new double[]{p2.getRed(), p2.getGreen(), p2.getBlue()});
    }
}
