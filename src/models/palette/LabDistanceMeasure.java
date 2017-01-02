package models.palette;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import utils.ColorMath;

/**
 * DistanceMeasure class for use of Apache Commons Math K-Means.
 */
public class LabDistanceMeasure implements DistanceMeasure {
    public static final int VECTOR_DIMENSION = 3;

    private ColorMath.Mode mode;

    /**
     * Constructor with default mode.
     */
    public LabDistanceMeasure() {
        mode = ColorMath.Mode.CIE76;
    }

    /**
     * Constructor with specified mode.
     *
     * @param mode the formula to use to calculate the distance between two colors.
     */
    public LabDistanceMeasure(ColorMath.Mode mode) {
        this.mode = mode;
    }

    /**
     * Overridden method to compute the distance between two vectors.
     *
     * @param doubles first vector
     * @param doubles1 second vector
     * @return distance between doubles and doubles1
     * @throws DimensionMismatchException error thrown if the dimension of the vector is not the same.
     */
    @Override
    public double compute(double[] doubles, double[] doubles1) throws DimensionMismatchException {
        if (doubles.length != doubles1.length)
            throw new DimensionMismatchException(doubles.length, VECTOR_DIMENSION);
        return ColorMath.colorDifferenceVal(doubles, doubles1, mode);
    }
}
