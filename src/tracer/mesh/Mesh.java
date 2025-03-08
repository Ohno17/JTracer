package tracer.mesh;

import tracer.ray.Ray;
import tracer.ray.RayInfo;
/**
 * Represents a 3D mesh that can be intersected with a ray.
 */
public abstract class Mesh
{

    private Material material;

    public void setMaterial(Material material)
    {
        this.material = material;
    }

    public Material getMaterial()
    {
        return material;
    }

    public abstract RayInfo intersect(Ray ray);
    
}
