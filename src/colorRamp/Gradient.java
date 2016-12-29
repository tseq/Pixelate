package colorRamp;

import utils.ColorMath;

import java.awt.Color;

/**
 * Gradient class. Create a Gradient object using the start and end colors, and get the array of colors that form a
 * generateGradient through interpolation.
 */
public class Gradient {
    public static final int DEFAULT_START_COLOR = 0xFF0000, DEFAULT_END_COLOR = 0x0000FF, DEFAULT_SHADES = 8;

    Color startColor, endColor;

    public Gradient(Color startColor, Color endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public Gradient(int startRGB, int endRGB) {
        startColor = new Color(startRGB);
        endColor = new Color(endRGB);
    }

    public Gradient() {
        startColor = new Color(DEFAULT_START_COLOR);
        endColor = new Color(DEFAULT_END_COLOR);
    }

    public int[] getGradientArray() {
        return getGradientArray(DEFAULT_SHADES);
    }

    public int[] getGradientArray(int steps) {
        float[] startHSB = Color.RGBtoHSB(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), null);
        float[] endHSB = Color.RGBtoHSB(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), null);

        int colors[] = new int[steps];
        for (int i = 0; i < steps; i++) {
            float h = linearInterpolate(startHSB[0], endHSB[0], steps - 1, i);
            float s = linearInterpolate(startHSB[1], endHSB[1], steps - 1, i);
            float b = linearInterpolate(startHSB[2], endHSB[2], steps - 1, i);

            colors[i] = Color.HSBtoRGB(h, s, b);
        }

        return colors;
    }

    private float linearInterpolate(float start, float end, int steps, int step) {
        float p = (float) step / steps;
        return (float) ColorMath.truncate((end * p) + (start * (1 - p)), ColorMath.ColorSpace.HSB);
    }

    public void setStartColor(Color startColor) {
        this.startColor = startColor;
    }

    public void setEndColor(Color endColor) {
        this.endColor = endColor;
    }

    public Color getStartColor() {
        return startColor;
    }

    public Color getEndColor() {
        return endColor;
    }

    public static String getHexValue(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
