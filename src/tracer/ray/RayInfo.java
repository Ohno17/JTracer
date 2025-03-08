package tracer.ray;

import tracer.mesh.Material;
import tracer.vector.Vector3;
/**
 * Represents data collected when a ray hits a surface.
 */
public class RayInfo
{
    
    public boolean hit;

    public Vector3 normal;
    public Vector3 point;
    public double distance;

    public Material material;

    public RayInfo(boolean hit)
    {
        this.hit = hit;
        this.distance = Double.POSITIVE_INFINITY;
    }

    public RayInfo(double distance, Vector3 point, Vector3 normal)
    {
        this.hit = true;
        this.distance = distance;
        this.point = point;
        this.normal = normal;
    }

}
