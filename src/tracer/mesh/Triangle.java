package tracer.mesh;

import java.util.List;

import tracer.ray.Ray;
import tracer.ray.RayInfo;
import tracer.reader.property.InvalidPropertyException;
import tracer.reader.property.InvalidPropertyNameException;
import tracer.reader.property.Property;
import tracer.reader.property.PropertyValue;
import tracer.vector.Vector3;
/**
 * Represents a sphere that can be intersected with a ray.
 */
public class Triangle extends Mesh
{

    private static final double ELIPSON = Math.ulp(1d);

    private Vector3 a;
    private Vector3 b;
    private Vector3 c;

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

        // Calculate hit-dst and barycentric coordinates
        double dst = vertRayOffset.dot(triFaceVector) * invDet;
        double u = edgeAC.dot(rayOffsetPerp) * invDet;
        double v = -edgeAB.dot(rayOffsetPerp) * invDet;
        double w = 1 - u - v;

        // Initialize hit info
        RayInfo hitInfo = new RayInfo();
        boolean cullBackface = false;
        boolean keep = cullBackface ? determinant >= ELIPSON : Math.abs(determinant) >= ELIPSON;

        hitInfo.hit = keep && dst > 0 && u >= 0 && v >= 0 && w >= 0;
        hitInfo.normal = triFaceVector.normalize();
        hitInfo.point = ray.origin.add(ray.direction.multiply(dst));
        hitInfo.distance = dst;
        hitInfo.u = u;
        hitInfo.v = v;
        
        return hitInfo;
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
                    throw new InvalidPropertyNameException(this, property.getName());
            }
        }
    }

}

