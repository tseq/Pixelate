package models;

import java.util.ArrayList;

/**
 * Created by Tse Qin on 22/12/2016.
 */
public class Palette {
    private ArrayList<Pixel> palette;
    private Pixel tempColor;

    public Palette() {
        palette = new ArrayList<>();
        tempColor = null;
    }

    public void add(Pixel color) {
        palette.add(color);
    }

    public boolean exists(Pixel color) {
        for (Pixel p : palette) {
            if (p.colorDifference(color).isSimilar()) {
                tempColor = p;
                return true;
            }
        }
        return false;
    }

    public Pixel getColor() {
        return tempColor;
    }
}
