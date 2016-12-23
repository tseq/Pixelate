package utils;

/**
 * Enum to keep track of color perception levels.
 * Source: http://zschuessler.github.io/DeltaE/learn/
 */
public enum PerceptionScale {
    LEVEL_1(0, 0.99), // Not perceptible by human eyes.
    LEVEL_2(1, 1.99), // Perceptible through close observation.
    LEVEL_3(2, 10), // Perceptible at a glance.
    LEVEL_4(11, 49), // Colors are more similar than opposite.
    LEVEL_5(50, 100); // Colors are exact opposite

    private final double low;
    private final double high;

    PerceptionScale(double low, double high) {
        this.low = low;
        this.high = high;
    }

    public boolean lessThan(PerceptionScale p) {
        return high < p.low;
    }

    public boolean greaterThan(PerceptionScale p) {
        return low > p.high;
    }

    public boolean isSimilar() {
        return lessThan(LEVEL_4);
    }
}
