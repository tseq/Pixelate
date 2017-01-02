package models.palette;

import models.Pixel;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tse Qin on 1/1/2017.
 */
public class PixelClusterable implements Clusterable {
    private Pixel pixel;
    private double[] point;

    public PixelClusterable(Pixel pixel) {
        this.pixel = pixel;
        this.point = new double[] {pixel.getRed(), pixel.getGreen(), pixel.getBlue()};
    }

    public Pixel getPixel() {
        return pixel;
    }

    @Override
    public double[] getPoint() {
        return point;
    }

    public static List<PixelClusterable> toClusterable(Pixel[][] pixels) {
        ArrayList<PixelClusterable> clusterable = new ArrayList<>();
        for (int i = 0; i < pixels.length; i++)
            for (int j = 0; j < pixels[i].length; j++)
                clusterable.add(new PixelClusterable(pixels[i][j]));
        return clusterable;
    }

    public static Pixel[] toPixel(List<CentroidCluster<PixelClusterable>> centroidClusters) {
        List<Pixel> pixels = new ArrayList<>();
        for (CentroidCluster<PixelClusterable> c : centroidClusters)
            pixels.add(new Pixel(c.getCenter().getPoint()));
        return pixels.toArray(new Pixel[pixels.size()]);
    }
}
