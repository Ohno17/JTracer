package tracer.mesh;

import javax.imageio.ImageIO;

import tracer.Logger;
import tracer.math.Color;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture
{

    private int[] data;
    private int width;
    private int height;

    private double scale = 1.0;

    public Texture()
    {
        this.data = null;
    }

    public Texture(String path)
    {
        setImagePath(path);
    }

    public boolean exists()
    {
        return data != null;
    }

    public void setImagePath(String path)
    {
        try
        {
            Logger.outputPrintln("textures", "Loading \'" + path + "\'");
            BufferedImage image = ImageIO.read(new File(path));
            this.width = image.getWidth();
            this.height = image.getHeight();

            data = new int[width * height];
            image.getRGB(0, 0, width, height, data, 0, width);
        }
        catch (IOException e)
        {
            Logger.errorPrintln("textures", "Could not load \'" + path + "\': " + e.getMessage());
            data = null;
        }
    }

    public void setScale(double scale)
    {
        this.scale = scale;
    }

    public Color sample(double u, double v)
    {
        double uWrapped = (u * scale) - Math.floor(u * scale);
        double vWrapped = (v * scale) - Math.floor(v * scale);

        int x = (int) (uWrapped * (width - 1));
        int y = (int) (vWrapped * (height - 1));

        int rgb = data[(y * width) + x];

        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        return new Color(r / 255d, g / 255d, b / 255d);
    }
}