package tracer.mesh;

import tracer.ray.Ray;
import tracer.ray.RayInfo;
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

            if (t >= 0) return new RayInfo(
                t,
                ray.origin.add(ray.direction.multiply(t)),
                ray.origin.add(ray.direction.multiply(t)).subtract(center).normalize()
            );
        }
        return new RayInfo(false);
    }

}
