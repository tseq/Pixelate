package models;

import utils.ColorMath;
import utils.PerceptionScale;

import java.util.List;

/**
 * Pixel class. Enables calculation of RGB values.
 * TODO: Consider using an extension of Java's Color class instead.
 */
public class Pixel {
    public static final int MAX_ALPHA = -16777216;

    private static final int GRADIENT_STEPS = 4;

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

    public Pixel(double[] rgb) {
        this.red = (int) rgb[0];
        this.green = (int) rgb[1];
        this.blue = (int) rgb[2];
        computeArgb();
    }

    /**
     * Constructor.
     *
     * @param rgb rgb value of color
     */
    public Pixel(int rgb) {
        setRGB(rgb);
    }

    /**
     * Increase saturation of the current pixel.
     *
     * @param increase the increase (in times, eg. 1.5x, 2.0x) in saturation
     */
    public void increaseSaturation(float increase) {
        argb = ColorMath.changeSaturation(new int[] {getRed(), getGreen(), getBlue()}, increase);
        computeColors();
    }

    /**
     * Increase contrast of current pixel.
     *
     * @param increase the increase in contrast (in val, from -128 to 128)
     */
    public void increaseContrast(int increase) {
        if (increase > 128)
            increase = 128;
        if (increase < -128)
            increase = -128;
        argb = ColorMath.changeContrast(new int[]{getRed(), getGreen(), getBlue()}, increase);
        computeColors();
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
     * Compute and update the colors of the pixel.
     */
    public void computeColors() {
        alpha = getAlpha();
        red = getRed();
        green = getGreen();
        blue = getBlue();
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
     * @param p pixel to be compared with
     * @return value of the difference
     */
    public double colorDifferenceVal(Pixel p) {
        return colorDifferenceVal(this, p);
    }

    /**
     * Compute the difference between the current pixel and another pixel in value, where the formula used is
     * specified by mode.
     *
     * @param p pixel to be compared with
     * @param mode the color distance formula to use
     * @return value of the difference
     */
    public double colorDifferenceVal(Pixel p, ColorMath.Mode mode) {
        return colorDifferenceVal(this, p, mode);
    }

    /**
     * Compute luminance of this pixel.
     *
     * @return luminosity of the pixel
     */
    public double luminance() { return ColorMath.luminance(getRGBComponents()); }

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
     * Get the RGB components for this Pixel.
     *
     * @return the RGB components in the exact order in an array
     */
    public int[] getRGBComponents() {
        return new int[] {getRed(), getGreen(), getBlue()};
    }

    /**
     * Set the RGB value for this pixel.
     *
     * @param rgb RGB value
     */
    public void setRGB(int rgb) {
        argb = rgb;
        computeColors();
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

    /**
     * Compute color difference between 2 pixels in value, where the formula used is specified by mode.
     *
     * @param p1 first pixel
     * @param p2 second pixel
     * @param mode the color distance formula to use
     * @return value of the difference between the two pixels.
     */
    public static double colorDifferenceVal(Pixel p1, Pixel p2, ColorMath.Mode mode) {
        return ColorMath.colorDifferenceVal(new double[]{p1.getRed(), p1.getGreen(), p1.getBlue()},
                new double[]{p2.getRed(), p2.getGreen(), p2.getBlue()}, mode);
    }

    /**
     * Compute the generateGradient for 2 pixels.
     *
     * @param start start color
     * @param end end color
     * @return array of Pixels representing the generateGradient
     */
    public static Pixel[] generateGradient(Pixel start, Pixel end) {
        int[] gradient =  ColorMath.generateGradient(start.getRGBComponents(), end.getRGBComponents(), GRADIENT_STEPS);
        Pixel[] result = new Pixel[GRADIENT_STEPS];
        for (int i = 0; i < gradient.length; i++)
            result[i] = new Pixel(gradient[i]);
        return result;
    }

    /**
     * Convert RGB array to Pixel array.
     *
     * @param rgb RGB array
     * @return Pixel array that represents the RGB array
     */
    public static Pixel[] toPixels(int[] rgb) {
        Pixel pixels[] = new Pixel[rgb.length];
        for (int i = 0; i < rgb.length; i++)
            pixels[i] = new Pixel(rgb[i]);
        return pixels;
    }
}
