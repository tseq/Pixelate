package utils;

import java.awt.*;

/**
 * ColorMath class. Contains static methods to perform calculations for colors.
 * TODO: Migrate from using colorDifferenceScale --> colorDifferenceVal. Use valueToScale().
 * TODO: Change rgb arrays to int instead of double
 */
public class ColorMath {
    // Reference white values for XYZ to Lab conversion.
    private final static double REF_X = 95.047, REF_Y = 100.000, REF_Z = 108.883;

    public enum ColorSpace {
        RGB, HSB
    }

    public static int changeContrast(int[] rgb, int contrast) {
        double factor = (259.0 * (contrast + 255)) / (255.0 * (259 - contrast));
        int newRed = (int) truncate((factor * rgb[0] - 128) + 128, ColorSpace.RGB);
        int newGreen = (int) truncate((factor * rgb[1] - 128) + 128, ColorSpace.RGB);
        int newBlue = (int) truncate((factor * rgb[2]  - 128) + 128, ColorSpace.RGB);
        return new Color(newRed, newGreen, newBlue).getRGB();
    }

    /**
     * Change saturation of the rgb color.
     *
     * @param rgb color to change
     * @param change change made to color
     * @return rgb value of color
     */
    public static int changeSaturation(int[] rgb, float change) {
        float[] hsb = Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], null);
        return Color.HSBtoRGB(hsb[0], (float) truncate(hsb[1] * change, ColorSpace.HSB), hsb[2]);
    }

    /**
     * Truncate color space components.
     *
     * @param component value of component
     * @param colorSpace color space of component
     * @return truncated value
     */
    public static double truncate(double component, ColorSpace colorSpace) {
        int max, min = 0;
        switch(colorSpace) {
            case RGB:
                max = 255;
                break;
            case HSB:
                max = 1;
                break;
            default:
                max = 0;
        }

        if (component > max)
            return max;
        if (component < min)
            return min;
        return component;
    }

    /**
     * Compute color differences in terms of perception scale.
     *
     * @param rgb1 first color
     * @param rgb2 second color
     * @return difference translated to scale
     */
    public static PerceptionScale colorDifferenceScale(double[] rgb1, double[] rgb2) {
        double diff = cie76(rgb1, rgb2);
        if (diff < 1) {
            return PerceptionScale.LEVEL_1;
        } else if (diff < 2) {
            return PerceptionScale.LEVEL_2;
        } else if (diff < 11) {
            return PerceptionScale.LEVEL_3;
        } else if (diff < 59) {
            return PerceptionScale.LEVEL_4;
        } else {
            return PerceptionScale.LEVEL_5;
        }
    }

    /**
     * Convert difference in value to scale.
     *
     * @param diff difference in value
     * @return difference translated to scale
     */
    public static PerceptionScale valueToScale(double diff) {
        if (diff < 1) {
            return PerceptionScale.LEVEL_1;
        } else if (diff < 2) {
            return PerceptionScale.LEVEL_2;
        } else if (diff < 11) {
            return PerceptionScale.LEVEL_3;
        } else if (diff < 59) {
            return PerceptionScale.LEVEL_4;
        } else {
            return PerceptionScale.LEVEL_5;
        }
    }

    /**
     * Compute color differences in terms of double.
     *
     * @param rgb1 first color
     * @param rgb2 second color
     * @return difference between first and second color
     */
    public static double colorDifferenceVal(double[] rgb1, double[] rgb2) {
        return cie76(rgb1, rgb2);
    }

    /**
     * CIE76 Color difference formula implementation.
     *
     * @param rgb1 first color
     * @param rgb2 second color
     * @return difference between rgb1 and rgb2, delta Eab
     */
    public static double cie76(double[] rgb1, double[] rgb2) {
        double[] lab1 = rgbToLab(rgb1), lab2 = rgbToLab(rgb2);

        double distL = Math.pow(lab2[0] - lab1[0], 2),
                distA = Math.pow(lab2[1] - lab1[1], 2),
                distB = Math.pow(lab2[2] - lab1[2], 2);

        return Math.sqrt(distL + distA + distB);
    }

    /**
     * CIE94 Color difference formula implemenetation.
     * Reference: https://en.wikipedia.org/wiki/Color_difference
     *
     * @param rgb1 first color
     * @param rgb2 second color
     * @return diffence between rgb1 and rgb2, delta E94
     */
    public static double cie94(double[] rgb1, double[] rgb2) {
        double[] lab1 = rgbToLab(rgb1), lab2 = rgbToLab(rgb2);

        double l1 = lab1[0], l2 = lab2[0],
                a1 = lab1[1], a2 = lab2[1],
                b1 = lab1[2], b2 = lab2[2];

        double deltaL = l1 - l2,
                deltaA = a1 - a2,
                deltaB = b1 - b2;

        double c1 = Math.sqrt(Math.pow(a1, 2) + Math.pow(b1, 2)),
                c2 = Math.sqrt(Math.pow(a2, 2) + Math.pow(b2, 2)),
                deltaC = c1 - c2,
                deltaH = Math.sqrt(Math.pow(deltaA, 2) + Math.pow(deltaB, 2) - Math.pow(deltaC, 2));

        double sl = 1,
                sc = 1 + (0.045 * c1),
                sh = 1 + (0.015 * c1);

        return Math.sqrt(Math.pow(deltaL / sl, 2) + Math.pow(deltaC / sc, 2) + Math.pow(deltaH / sh, 2));
    }

    /**
     * CIEDE2000 Color difference formula implementation. Assume kl = kc = kh = 1.
     * Reference: http://www.ece.rochester.edu/~gsharma/ciede2000/ciede2000noteCRNA.pdf
     *
     * @param rgb1 first color
     * @param rgb2 second color
     * @return difference between rgb1 and rgb2, delta E
     */
    public static double ciede2000(double[] rgb1, double[] rgb2) {
        double[] lab1 = rgbToLab(rgb1), lab2 = rgbToLab(rgb2);

        double[][] chPrime = cPrime_hPrime(lab1, lab2);
        double[] cPrime = chPrime[0],
                hPrime = chPrime[1];

        double[] lch = deltaLCH(new double[]{lab1[0], lab2[0]}, cPrime, hPrime);

        double lMean = (lab1[0] + lab2[0]) / 2,
                cMean = (cPrime[1] + cPrime[0]) / 2;

        double hMean;
        if (cPrime[0] * cPrime[1] == 0)
            hMean = hPrime[0] + hPrime[1];
        else if (Math.abs(hPrime[0] - hPrime[1]) <= 180)
            hMean = (hPrime[0] + hPrime[1]) / 2;
        else if (Math.abs(hPrime[0] - hPrime[1]) > 180)
            hMean = (hPrime[0] + hPrime[1] + 360) / 2;
        else
            hMean = (hPrime[0] + hPrime[1] - 360) / 2;

        double t = 1 - (0.17 * Math.cos(hMean - 30)) + (0.24 * Math.cos(2 * hMean) +
                (0.32 * Math.cos(3 * hMean + 6) - (0.20 * Math.cos(4 * hMean - 63)))),
                deltaTheta = 30 * Math.exp(-1 * Math.pow((hMean - 275) / 25, 2)),
                rc = 2 * Math.sqrt(Math.pow(cMean, 7) / (Math.pow(cMean, 7) + Math.pow(25, 7))),
                sl = 1 + ((0.015 * Math.pow(lMean - 50, 2)) / Math.sqrt(20 + Math.pow(lMean - 50, 2))),
                sc = 1 + (0.045 * cMean),
                sh = 1 + (0.015 * cMean * t),
                rr = -1 * Math.sin(2 * deltaTheta) * rc;
        return Math.sqrt(Math.pow(lch[0] / sl, 2) + Math.pow(lch[1] / sc, 2) + Math.pow(lch[2] / sh, 2) + (rr *
                (lch[1] / sc) * (lch[2] / sh)));
    }

    /**
     * Convert RGB to XYZ color space.
     * Reference: http://www.easyrgb.com/index.php?X=MATH&H=07#text7
     *
     * @param r red value
     * @param g green value
     * @param b blue value
     * @return array of {x, y, z} values
     */
    public static double[] rgbToXyz(double r, double g, double b) {
        double varR = transformRgb(r),
                varG = transformRgb(g),
                varB = transformRgb(b);

        double x = varR * 0.4124 + varG * 0.3576 + varB * 0.1805,
                y = varR * 0.2126 + varG * 0.7152 + varB * 0.0722,
                z = varR * 0.0193 + varG * 0.1192 + varB * 0.9505;

        return new double[]{x, y, z};
    }

    /**
     * Convert XYZ to Lab color space.
     * Reference: http://www.easyrgb.com/index.php?X=MATH&H=07#text7
     *
     * @param x x value
     * @param y y value
     * @param z z value
     * @return array of {l, a, b} values
     */
    public static double[] xyzToLab(double x, double y, double z) {
        double varX = transformXyz(x, REF_X),
                varY = transformXyz(y, REF_Y),
                varZ = transformXyz(z, REF_Z);

        double l = (116 * varY) - 16,
                a = 500 * (varX - varY),
                b = 200 * (varY - varZ);

        return new double[]{l, a, b};
    }

    /**
     * Convert RGB to Lab.
     *
     * @param r red value
     * @param g green value
     * @param b blue value
     * @return array of {l, a, b} values
     */
    public static double[] rgbToLab(double r, double g, double b) {
        return xyzToLab(rgbToXyz(r, g, b));
    }

    /**
     * Convert RGB to Lab. Wraps around rgbToLab(r, g, b).
     *
     * @param rgb double array with values {r, g, b} in the exact order
     * @return array of {l, a, b} values
     */
    public static double[] rgbToLab(double[] rgb) {
        return rgbToLab(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Convert RGB to XYZ. Wraps around rgbToXyz(r, g, b).
     *
     * @param rgb double array with values {r, g, b} in the exact order
     * @return array of {x, y, z} values
     */
    public static double[] rgbToXyz(double[] rgb) {
        return rgbToXyz(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Convert XYZ to Lab. Wraps around xyzToLab(x, y, z).
     *
     * @param xyz double array with values {x, y, z} in the exact order
     * @return array of {l, a, b} values
     */
    public static double[] xyzToLab(double[] xyz) {
        return xyzToLab(xyz[0], xyz[1], xyz[2]);
    }

    /**
     * Helper method to transform RGB to XYZ.
     *
     * @param color rgb color value
     * @return transformed value
     */
    private static double transformRgb(double color) {
        double result = color / 255;
        if (result > 0.04045)
            result = Math.pow((result + 0.055) / 1.055, 2.4);
        else
            result /= 12.92;
        return result * 100;
    }

    /**
     * Helper method to transform XYZ to Lab.
     *
     * @param color xyz color value
     * @param ref   reference white
     * @return transformed value
     */
    private static double transformXyz(double color, double ref) {
        double result = color / ref;
        if (result > 0.008856) {
            result = Math.pow(result, 1.0 / 3.0);
        } else {
            result = (7.787 * result) + (16.0 / 116.0);
        }
        return result;
    }

    /**
     * Helper method to compute delta L, delta C, and delta H for CIEDE2000.
     *
     * @param lArr   array {L1, L2}
     * @param cPrime array {C'1, C'2}
     * @param hPrime array {h'1, h'2}
     * @return {deltaL, deltaC, deltaH}
     */
    private static double[] deltaLCH(double[] lArr, double[] cPrime, double[] hPrime) {
        double deltaL = lArr[1] - lArr[0],
                deltaC = cPrime[1] - cPrime[0];

        double deltaH;
        if (cPrime[0] * cPrime[1] == 0)
            deltaH = 0;
        else if (Math.abs(hPrime[1] - hPrime[0]) <= 180)
            deltaH = hPrime[1] - hPrime[0];
        else if (hPrime[1] - hPrime[0] > 180)
            deltaH = hPrime[1] - hPrime[0] - 360;
        else
            deltaH = hPrime[1] - hPrime[0] + 360;
        deltaH = 2 * (Math.sqrt(cPrime[0] * cPrime[1])) * Math.sin(deltaH / 2);

        return new double[]{deltaL, deltaC, deltaH};
    }

    /**
     * Helper method to compute cPrime and hPrime for CIEDE2000.
     *
     * @param lab1 array {l1, a1, b1}
     * @param lab2 array {l2, a2, b2}
     * @return array { {cPrime1, cPrime2}, {hPrime1, hPrime2} }
     */
    private static double[][] cPrime_hPrime(double[] lab1, double[] lab2) {
        double a1 = lab1[1], a2 = lab2[1],
                b1 = lab1[2], b2 = lab2[2];

        double c1 = Math.sqrt(Math.pow(a1, 2) + Math.pow(b1, 2)),
                c2 = Math.sqrt(Math.pow(a2, 2) + Math.pow(b2, 2));

        double cMean = (c1 + c2) / 2,
                cMeanPow7 = Math.pow(cMean, 7),
                twoFivePow7 = Math.pow(25, 7);

        double g = 0.5 * (1 - Math.sqrt(cMeanPow7 / (cMeanPow7 + twoFivePow7)));

        double aPrime1 = (1 + g) * a1,
                aPrime2 = (1 + g) * a2;

        double cPrime1 = Math.sqrt(Math.pow(aPrime1, 2) + Math.pow(b1, 2)),
                cPrime2 = Math.sqrt(Math.pow(aPrime2, 2) + Math.pow(b2, 2));

        double hPrime1 = (b1 == aPrime1) ? 0 : Math.toDegrees(Math.atan2(b1, aPrime1)),
                hPrime2 = (b2 == aPrime2) ? 0 : Math.toDegrees(Math.atan2(b2, aPrime2));

        return new double[][]{{cPrime1, cPrime2}, {hPrime1, hPrime2}};
    }
}
