package tracer.mesh;

import javax.imageio.ImageIO;

import tracer.vector.Color;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture {
    private BufferedImage image;
    private int width;
    private int height;

    public Texture(String path) {
        try {
            this.image = ImageIO.read(new File(path));
            this.width = image.getWidth();
            this.height = image.getHeight();
        } catch (IOException e) {
            System.err.println("Could not load texture: " + e.getMessage());
            this.image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
    }

    public Color sample(double u, double v) {
        // Clamp u and v to [0, 1] to prevent out-of-bounds errors
        u = Math.max(0, Math.min(1, u));
        v = Math.max(0, Math.min(1, v));

        // Map [0, 1] to pixel coordinates [0, width-1]
        int x = (int) (u * (width - 1));
        int y = (int) (v * (height - 1));

        int rgb = image.getRGB(x, y);

        // Extract color components (assuming standard ARGB format)
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        return new Color(r, g, b);
    }
}