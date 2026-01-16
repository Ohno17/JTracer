package tracer.ray;

import tracer.math.Vector3;
import tracer.mesh.Material;
/**
 * Represents data collected when a ray hits a surface.
 */
public record RayInfo(
    boolean hit,
    Vector3 normal,
    Vector3 point,
    double distance,
    Material material,
    double u,
    double v
)
{

    public RayInfo()
    {
        this(false, null, null, Double.POSITIVE_INFINITY, null, 0.0, 0.0);
    }

    public RayInfo(double distance, Vector3 point, Vector3 normal, double u, double v, Material material)
    {
        this(true, normal, point, distance, material, u, v);
    }

}
