package tracer.mesh;

import tracer.ray.Ray;
import tracer.ray.RayInfo;
import tracer.vector.Vector3;
/**
 * Represents a rectangular prism that can be intersected with a ray.
 */
public class Box extends Mesh
{

    private Vector3 min;
    private Vector3 max;

    public Box(Vector3 min, Vector3 max)
    {
        this.min = min;
        this.max = max;
    }

    @Override
    public RayInfo intersect(Ray ray)
    {
        return new RayInfo(false);
    }
    
}
