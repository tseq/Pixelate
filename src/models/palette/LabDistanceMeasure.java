package models.palette;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import utils.ColorMath;

/**
 * Created by Tse Qin on 1/1/2017.
 */
public class LabDistanceMeasure implements DistanceMeasure {
    public static final int VECTOR_DIMENSION = 3;

    @Override
    public double compute(double[] doubles, double[] doubles1) throws DimensionMismatchException {
        if (doubles.length != doubles1.length)
            throw new DimensionMismatchException(doubles.length, VECTOR_DIMENSION);
        return ColorMath.colorDifferenceVal(doubles, doubles1);
    }
}
