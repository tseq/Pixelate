package utils;

/**
 * ColorMath class. Contains static methods to perform calculations for colors.
 * TODO: Migrate from using colorDifferenceScale --> colorDifferenceVal. Use valueToScale().
 */
public class ColorMath {
    // Reference white values for XYZ to Lab conversion.
    private final static double REF_X = 95.047;
    private final static double REF_Y = 100.000;
    private final static double REF_Z = 108.883;

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
     * @return difference between rgb1 and rgb2, delta E
     */
    public static double cie76(double[] rgb1, double[] rgb2) {
        double[] lab1 = rgbToLab(rgb1);
        double[] lab2 = rgbToLab(rgb2);

        double distL = Math.pow(lab2[0] - lab1[0], 2);
        double distA = Math.pow(lab2[1] - lab1[1], 2);
        double distB = Math.pow(lab2[2] - lab1[2], 2);

        return Math.sqrt(distL + distA + distB);
    }

    /**
     * CIEDE2000 Color difference formula implementation.
     *
     * @param rgb1 first color
     * @param rgb2 second color
     * @return difference between rgb1 and rgb2, delta E
     */
    public static double ciede2000(double[] rgb1, double[] rgb2) {
        double[] lab1 = rgbToLab(rgb1);
        double[] lab2 = rgbToLab(rgb2);

        double l1 = lab1[0];
        double l2 = lab2[0];
        double a1 = lab1[1];
        double a2 = lab2[1];
        double b1 = lab1[2];
        double b2 = lab2[2];

        double c1 = Math.sqrt(Math.pow(a1, 2) + Math.pow(b1, 2));
        double c2 = Math.sqrt(Math.pow(a2, 2) + Math.pow(b2, 2));

        double cMean = (c1 + c2) / 2;
        double cMeanPow7 = Math.pow(cMean, 7);
        double twoFivePow7 = Math.pow(25, 7);

        double g = 0.5 * (1 - Math.sqrt(cMeanPow7 / (cMeanPow7 + twoFivePow7)));

        double aPrime1 = (1 + g) * a1;
        double aPrime2 = (1 + g) * a2;

        double cPrime1 = Math.sqrt(Math.pow(aPrime1, 2) + Math.pow(b1, 2));
        double cPrime2 = Math.sqrt(Math.pow(aPrime2, 2) + Math.pow(b2, 2));

        double hPrime1 = (b1 == aPrime1) ? 0 : Math.atan2(b1, aPrime1);
        double hPrime2 = (b2 == aPrime2) ? 0 : Math.atan2(b2, aPrime2);

        return -1; // TODO: To be continued.
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
        double varR = transformRgb(r);
        double varG = transformRgb(g);
        double varB = transformRgb(b);

        double x = varR * 0.4124 + varG * 0.3576 + varB * 0.1805;
        double y = varR * 0.2126 + varG * 0.7152 + varB * 0.0722;
        double z = varR * 0.0193 + varG * 0.1192 + varB * 0.9505;

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
        double varX = transformXyz(x, REF_X);
        double varY = transformXyz(y, REF_Y);
        double varZ = transformXyz(z, REF_Z);

        double l = (116 * varY) - 16;
        double a = 500 * (varX - varY);
        double b = 200 * (varY - varZ);

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

}
