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
 * Represents a axis-aligned box that can be intersected with a ray.
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
        double tMin = (min.x - ray.origin.x) / ray.direction.x;
        double tMax = (max.x - ray.origin.x) / ray.direction.x;

        if (tMin > tMax) {
            double temp = tMin;
            tMin = tMax;
            tMax = temp;
        }

        double tyMin = (min.y - ray.origin.y) / ray.direction.y;
        double tyMax = (max.y - ray.origin.y) / ray.direction.y;

        if (tyMin > tyMax) {
            double temp = tyMin;
            tyMin = tyMax;
            tyMax = temp;
        }

        if ((tMin > tyMax) || (tyMin > tMax))
            return new RayInfo(false);

        if (tyMin > tMin)
            tMin = tyMin;
        if (tyMax < tMax)
            tMax = tyMax;

        double tzMin = (min.z - ray.origin.z) / ray.direction.z;
        double tzMax = (max.z - ray.origin.z) / ray.direction.z;

        if (tzMin > tzMax) {
            double temp = tzMin;
            tzMin = tzMax;
            tzMax = temp;
        }

        if ((tMin > tzMax) || (tzMin > tMax))
            return new RayInfo(false);

        if (tzMin > tMin)
            tMin = tzMin;
        if (tzMax < tMax)
            tMax = tzMax;

        // Ignore intersections behind the ray origin
        if (tMax < 0)
            return new RayInfo(false);

        double tHit = tMin >= 0 ? tMin : tMax;

        Vector3 hitPoint = ray.origin.add(ray.direction.multiply(tHit));

        // Calculate normal
        Vector3 normal = null;
        final double epsilon = 1e-5;

        if (Math.abs(hitPoint.x - min.x) < epsilon) normal = new Vector3(-1, 0, 0);
        else if (Math.abs(hitPoint.x - max.x) < epsilon) normal = new Vector3(1, 0, 0);
        else if (Math.abs(hitPoint.y - min.y) < epsilon) normal = new Vector3(0, -1, 0);
        else if (Math.abs(hitPoint.y - max.y) < epsilon) normal = new Vector3(0, 1, 0);
        else if (Math.abs(hitPoint.z - min.z) < epsilon) normal = new Vector3(0, 0, -1);
        else if (Math.abs(hitPoint.z - max.z) < epsilon) normal = new Vector3(0, 0, 1);
        else normal = new Vector3(0, 0, 0); // Should not happen

        return new RayInfo(tHit, hitPoint, normal);
    }

    @Override
    public void apply(List<Property> properties) throws InvalidPropertyException
    {
        for (Property property : properties)
        {
            switch (property.getName())
            {
                case "min":
                    min = property.getValue(PropertyValue.Type.VECTOR3);
                    break;
                case "max":
                    max = property.getValue(PropertyValue.Type.VECTOR3);
                    break;
                default:
                    throw new InvalidPropertyNameException(this, property.getName());
            }
        }
    }
    
}
