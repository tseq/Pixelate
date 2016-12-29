package colorRamp;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Tse Qin on 27/12/2016.
 */
public class ColorRampView extends JFrame {
    private static final int RAMP_LABEL_WIDTH = 100;

    private static final String COLOR_RAMP = "Color Ramp";
    private static final String HUE = "Hue";
    private static final String SATURATION = "Saturation";
    private static final String BRIGHTNESS = "Brightness";

    private JPanel ramp;
    private JSlider hueSlider, saturationSlider, brightnessSlider;

    public ColorRampView(String title) {
        super(title);
        initFrameConfigs();
        initLayout();
        addComponents();
    }

    private void initFrameConfigs() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);
    }

    private void initLayout() {
        setLayout(new GridLayout(1, 1));
    }

    private void addComponents() {
        addRamp();
        addSliders();
    }

    private void addRamp() {
        ramp = getDefaultRamp();
        add(ramp);
    }

    public void display() {
        setVisible(true);
        pack();
    }

    private void addSliders() {

    }


    private JLabel getRampLabel(Color color) {
        JLabel label = new JLabel(Gradient.getHexValue(color));
        label.setBackground(color);
        label.setForeground(getContrast(color));
        label.setOpaque(true);
        label.setPreferredSize(new Dimension(RAMP_LABEL_WIDTH, RAMP_LABEL_WIDTH));
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JPanel getDefaultRamp() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder(COLOR_RAMP));
        Gradient gradient = new Gradient();
        for (int colorVal : gradient.getGradientArray(4)) {
            Color color = new Color(colorVal);
            panel.add(getRampLabel(color));
        }
        return panel;
    }

    /**
     * Compute the contrasting color. Algorithm obtained from:
     * Reference: http://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color
     *
     * @param color color for which contrast is computed
     * @return contrast
     */
    private Color getContrast(Color color) {
        int rgbVal = 0;
        // Computing the perceptive luminance - human eye favors green color.
        double luminance = 1 - (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        // Compare luminance.
        if (luminance < 0.5)
            rgbVal = 0; // Bright colors - black font
        else
            rgbVal = 255; // Dark colors - white font
        return new Color(rgbVal, rgbVal, rgbVal);
    }
}
