package models.palette;

import models.Pixel;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class for Pixel for the use of Apache Commons Math KMeans method.
 */
public class PixelClusterable implements Clusterable {
    private Pixel pixel;
    private double[] point;

    /**
     * Constructor to create a clusterable Pixel.
     *
     * @param pixel Pixel object to be wrapped around
     */
    public PixelClusterable(Pixel pixel) {
        this.pixel = pixel;
        this.point = new double[] {pixel.getRed(), pixel.getGreen(), pixel.getBlue()};
    }

    /**
     * Getter for Pixel instance variable.
     *
     * @return the Pixel object that this Wrapper object is wrapped around.
     */
    public Pixel getPixel() {
        return pixel;
    }

    /**
     * Getter for the point of this clusterable object, AKA the vector coordinates.
     *
     * @return RGB value of this Pixel
     */
    @Override
    public double[] getPoint() {
        return point;
    }

    /**
     * Create a PixelClusterable list from a matrix of Pixel objects.
     *
     * @param pixels Pixel matrix to be converted into PixelClusterable list
     * @return PixelClusterable list of the specified Pixel matrix
     */
    public static List<PixelClusterable> toClusterable(Pixel[][] pixels) {
        ArrayList<PixelClusterable> clusterable = new ArrayList<>();
        for (int i = 0; i < pixels.length; i++)
            for (int j = 0; j < pixels[i].length; j++)
                clusterable.add(new PixelClusterable(pixels[i][j]));
        return clusterable;
    }

    /**
     * Create a PixelClusterable list from an array of Pixel objects.
     *
     * @param pixels Pixel array to be converted into PixelClusterable list
     * @return PixelClusterable list of the specified Pixel array
     */
    public static List<PixelClusterable> toClusterable(Pixel[] pixels) {
        ArrayList<PixelClusterable> clusterable = new ArrayList<>();
            for (Pixel pixel : pixels)
                clusterable.add(new PixelClusterable(pixel));
        return clusterable;
    }

    /**
     * Convert the centroids into a Pixel array from the centroidClusters.
     *
     * @param centroidClusters centroidClusters to extract centroids from
     * @return Pixel array representation of the centroids.
     */
    public static Pixel[] toPixel(List<CentroidCluster<PixelClusterable>> centroidClusters) {
        List<Pixel> pixels = new ArrayList<>();
        for (CentroidCluster<PixelClusterable> c : centroidClusters)
            pixels.add(new Pixel(c.getCenter().getPoint()));
        return pixels.toArray(new Pixel[pixels.size()]);
    }
}
