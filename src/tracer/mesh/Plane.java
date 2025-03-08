package tracer.mesh;

import tracer.ray.Ray;
import tracer.ray.RayInfo;
import tracer.vector.Vector3;
/**
 * Represents a infinite plane that can be intersected with a ray.
*/
public class Plane extends Mesh
{

    private static final double ELIPSON = Math.ulp(1d);

    private Vector3 normal;
    private Vector3 center;

    public Plane(Vector3 normal, Vector3 center)
    {
        this.normal = normal;
        this.center = center;
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
            if (t >= ELIPSON) return new RayInfo(
                t,
                ray.origin.add(ray.direction.multiply(t)),
                normal.dot(ray.direction) < 0 ? normal : normal.negative()
            );
        }

        return new RayInfo(false);
    }
    
}
