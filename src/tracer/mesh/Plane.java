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
 * Represents a infinite plane that can be intersected with a ray.
*/
public class Plane extends Mesh
{

    private static final double ELIPSON = 1e-8;

    private Vector3 normal;
    private Vector3 center;

    private Vector3 helper;
    private Vector3 uAxis;
    private Vector3 vAxis;

    public Plane()
    {

    }

    public Plane(Vector3 normal, Vector3 center)
    {
        this.normal = normal;
        this.center = center;

        helper = (Math.abs(normal.y) > 0.9) ? new Vector3(1, 0, 0) : new Vector3(0, 1, 0);
        uAxis = normal.cross(helper).normalize();
        vAxis = normal.cross(uAxis).normalize();
    }

    @Override
    public RayInfo intersect(Ray ray)
    {
        // Ray: x + t(dx), y + t(dy), z + t(dz)
        // Plane: ax + by + cz = 0

        double denominator = normal.dot(ray.direction);
        if (Math.abs(denominator) > 0)
        {
            Vector3 transformedRayOrigin = center.subtract(ray.origin);
            double t = normal.dot(transformedRayOrigin) / denominator;
            if (t >= ELIPSON)
            {
                Vector3 hitPoint = ray.origin.add(ray.direction.multiply(t));
                Vector3 finalNormal = denominator < 0 ? normal : normal.negative();
                Vector3 transformedCenter = hitPoint.subtract(center);

                return new RayInfo(
                    t,
                    hitPoint,
                    finalNormal,
                    uAxis.dot(transformedCenter),
                    vAxis.dot(transformedCenter)
                );
            }
        }

        return new RayInfo();
    }

    @Override
    public void apply(List<Property> properties) throws InvalidPropertyException
    {
        for (Property property : properties)
        {
            switch (property.getName())
            {
                case "normal":
                    normal = ((Vector3) property.getValue(PropertyValue.Type.VECTOR3)).normalize();
                    break;
                case "center":
                    center = property.getValue(PropertyValue.Type.VECTOR3);
                    break;
                default:
                    throw new InvalidPropertyNameException(this, property);
            }
        }

        helper = (Math.abs(normal.y) > 0.9) ? new Vector3(1, 0, 0) : new Vector3(0, 1, 0);
        uAxis = helper.cross(normal).normalize();
        vAxis = normal.cross(uAxis).normalize();
    }
    
}
