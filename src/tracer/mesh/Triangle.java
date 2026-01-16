package tracer.mesh;

import java.util.List;

import tracer.math.Vector3;
import tracer.ray.Ray;
import tracer.ray.RayInfo;
import tracer.reader.property.InvalidPropertyException;
import tracer.reader.property.InvalidPropertyNameException;
import tracer.reader.property.Property;
import tracer.reader.property.PropertyValue;
/**
 * Represents a sphere that can be intersected with a ray.
 */
public class Triangle extends Mesh
{

    private static final double ELIPSON = 1e-8;

    private Vector3 a;
    private Vector3 b;
    private Vector3 c;

    public Triangle()
    {

    }

    public Triangle(Vector3 a, Vector3 b, Vector3 c)
    {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public RayInfo intersect(Ray ray)
    {
        Vector3 edgeAB = this.b.subtract(this.a);
        Vector3 edgeAC = this.c.subtract(this.a);
        Vector3 triFaceVector = edgeAB.cross(edgeAC);
        Vector3 vertRayOffset = ray.origin.subtract(this.a);
        Vector3 rayOffsetPerp = vertRayOffset.cross(ray.direction);
        double determinant = -ray.direction.dot(triFaceVector);
        double invDet = 1 / determinant;

        double dst = vertRayOffset.dot(triFaceVector) * invDet;
        double u = edgeAC.dot(rayOffsetPerp) * invDet;
        double v = -edgeAB.dot(rayOffsetPerp) * invDet;
        double w = 1 - u - v;

        boolean degenerate = Math.abs(determinant) >= ELIPSON;

        if (degenerate && dst > 0 && u >= 0 && v >= 0 && w >= 0)
            return new RayInfo(
                determinant < 0,
                dst,
                ray.origin.add(ray.direction.multiply(dst)),
                triFaceVector.normalize(),
                u,
                v,
                this.getMaterial()
            );
        return new RayInfo();
    }

    @Override
    public void apply(List<Property> properties) throws InvalidPropertyException
    {
        for (Property property : properties)
        {
            switch (property.getName())
            {
                case "a":
                    a = property.getValue(PropertyValue.Type.VECTOR3);
                    break;
                case "b":
                    b = property.getValue(PropertyValue.Type.VECTOR3);
                    break;
                case "c":
                    c = property.getValue(PropertyValue.Type.VECTOR3);
                    break;
                default:
                    throw new InvalidPropertyNameException(this, property);
            }
        }
    }

}

