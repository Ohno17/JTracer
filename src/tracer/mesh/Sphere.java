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
public class Sphere extends Mesh
{

    private Vector3 center;
    private double radius;

    public Sphere(Vector3 center, double radius)
    {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public RayInfo intersect(Ray ray)
    {
        // Ray: x + dx, y + dy, z + dz
        // Sphere: x^2 + y^2 + z^2 = r^2

        Vector3 transformedRayOrigin = ray.origin.subtract(center);
        
        double a = ray.direction.dot(ray.direction);
        double b = 2d * transformedRayOrigin.dot(ray.direction);
        double c = transformedRayOrigin.dot(transformedRayOrigin) - (radius * radius);

        double discriminant = (b * b) - (4d * a * c);

        if (discriminant >= 0)
        {
            double t = ((-b) - Math.sqrt(discriminant)) / (2d * a);
            Vector3 hitPoint = ray.origin.add(ray.direction.multiply(t));
            Vector3 normal = hitPoint.subtract(center).normalize();
            double u = 0.5 + (Math.atan2(normal.x, normal.z) / (2 * Math.PI));
            double v = 0.5 - (Math.asin(normal.y) / Math.PI);

            if (t >= 0) return new RayInfo(
                t,
                hitPoint,
                normal,
                u,
                v
            );
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
                case "center":
                    center = property.getValue(PropertyValue.Type.VECTOR3);
                    break;
                case "radius":
                    radius = property.getValue(PropertyValue.Type.NUMBER);
                    break;
                default:
                    throw new InvalidPropertyNameException(this, property.getName());
            }
        }
    }

}
