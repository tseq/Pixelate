package models.palette;

import models.Pixel;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import java.util.List;

/**
 * KMeans palette. Creates the palette using KMeans color quantization.
 */
public class KMeansPalette {
    private Pixel[][] image;
    private Pixel[] palette;
    private int paletteSize;

    /**
     * Constructor. Initializes instant variables.
     */
    public KMeansPalette(Pixel[][] image, int paletteSize) {
        this.image = image;
        this.paletteSize = paletteSize;
        initPalette();
    }

    /**
     * Initialize the palette through K-means color quantization.
     */
    private void initPalette() {
        KMeansPlusPlusClusterer<PixelClusterable> clusterer = new KMeansPlusPlusClusterer<>(paletteSize, 80, new
                LabDistanceMeasure());
        System.out.println("clustering...");
        System.out.println(PixelClusterable.toClusterable(image).size());
        List<CentroidCluster<PixelClusterable>> centroidClusters = clusterer.cluster(PixelClusterable.toClusterable
                (image));
        System.out.println("clustering complete");
        palette = PixelClusterable.toPixel(centroidClusters);
    }

    /**
     * Get size of the palette (number of colors stored).
     *
     * @return size of the palette.
     */
    public int getSize() {
        return paletteSize;
    }

    /**
     * Obtain the nearest color in this palette. Chains to static nearestColor().
     *
     * @param currentColor color benchmark
     * @return the nearest color
     */
    public Pixel nearestColor(Pixel currentColor) {
        return nearestColor(currentColor, palette);
    }

    /**
     * Obtain the nearest color in an array of colors.
     *
     * @param currentColor color benchmark
     * @param colors array of colors to be compared with the benchmark
     * @return the nearest color
     */
    public static Pixel nearestColor(Pixel currentColor, Pixel colors[]) {
        double min = currentColor.colorDifferenceVal(colors[0]);
        Pixel nearest = colors[0];

        for (int i = 1; i < colors.length; i++) {
            double diff = currentColor.colorDifferenceVal(colors[i]);
            if (diff < min) {
                min = diff;
                nearest = colors[i];
            }
        }
        return nearest;
    }

    /**
     * Obtain the nearest color in an array of colors. Chains to static method nearestColor().
     *
     * @param currentColor color benchmark
     * @param colors array of colors to be compared with the benchmark
     * @return the nearest color
     */
    public static Pixel nearestColor(Pixel currentColor, List<Pixel> colors) {
        return nearestColor(currentColor, colors.toArray(new Pixel[colors.size()]));
    }
}
