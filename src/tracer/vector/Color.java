package tracer.vector;

/**
 * Represents an RGB color.
 */
public class Color extends BaseVector3<Color>
{

    public Color(int r, int g, int b)
    {
        super(r / 255d, g / 255d, b / 255d);
    }

    public Color(double r, double g, double b)
    {
        super(r, g, b);
    }

    private static double clamp(double value) {
        return Math.max(0, Math.min(value, 1));
    }

    public int toBytes()
    {
        return ((int) (clamp(x) * 255) << 16) |
            ((int) (clamp(y) * 255) << 8) |
            ((int) (clamp(z) * 255));
    }

    @Override
    protected Color createNew(double x, double y, double z) {
        return new Color(x, y, z);
    }

    @Override
    public String toString()
    {
        return "R: " + x + " G: " + y + " B: " + z;
    }

}
