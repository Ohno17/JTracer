package tracer.vector;

/**
 * Represents a vector in 3D space.
 */
public class Vector3 extends BaseVector3<Vector3>
{

    public Vector3(double s)
    {
        super(s, s, s);
    }
    
    public Vector3(double x, double y, double z)
    {
        super(x, y, z);
    }

    @Override
    protected Vector3 createNew(double x, double y, double z) {
        return new Vector3(x, y, z);
    }

    @Override
    public String toString()
    {
        return "X: " + x + ", Y: " + y + ", Z: " + z;
    }

}
