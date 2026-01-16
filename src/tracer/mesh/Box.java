package tracer.mesh;

import java.util.List;

import tracer.math.Vector3;
import tracer.ray.Ray;
import tracer.ray.RayInfo;
import tracer.reader.property.InvalidPropertyException;
import tracer.reader.property.InvalidPropertyNameException;
import tracer.reader.property.Property;
import tracer.reader.property.PropertyValue;
/**
 * Represents a axis-aligned box that can be intersected with a ray.
 */
public class Box extends Mesh
{

    private Vector3 min;
    private Vector3 max;

    public Box()
    {

    }

    public Box(Vector3 min, Vector3 max)
    {
        this.min = min;
        this.max = max;
    }

    @Override
    public RayInfo intersect(Ray ray)
    {
        double[] tNear = {(min.x - ray.origin.x) / ray.direction.x, (min.y - ray.origin.y) / ray.direction.y, (min.z - ray.origin.z) / ray.direction.z};
        double[] tFar =  {(max.x - ray.origin.x) / ray.direction.x, (max.y - ray.origin.y) / ray.direction.y, (max.z - ray.origin.z) / ray.direction.z};

        double t0 = Double.NEGATIVE_INFINITY;
        double t1 = Double.POSITIVE_INFINITY;
        int hitAxis = 0;

        for (int i = 0; i < 3; i++)
        {
            if (tNear[i] > tFar[i])
            {
                double temp = tNear[i];
                tNear[i] = tFar[i];
                tFar[i] = temp;
            }
            if (tNear[i] > t0)
            {
                t0 = tNear[i];
                hitAxis = i;
            }
            if (tFar[i] < t1)
                t1 = tFar[i];
        }

        if (t0 > t1 || t1 < 0) return new RayInfo();

        // Determine if we hit from inside (backface)
        boolean backface = t0 < 0;
        double tHit = backface ? t1 : t0;

        if (backface)
            for (int i = 0; i < 3; i++)
                if (t1 == tFar[i] || t1 == tNear[i]) hitAxis = i;

        Vector3 hitPoint = ray.origin.add(ray.direction.multiply(tHit));

        double[] normalDir = new double[3];
        double rayDirAtAxis = (hitAxis == 0) ? ray.direction.x : (hitAxis == 1) ? ray.direction.y : ray.direction.z;
        normalDir[hitAxis] = rayDirAtAxis > 0 ? -1 : 1;

        Vector3 normal = new Vector3(normalDir[0], normalDir[1], normalDir[2]);

        // UV
        double u = 0, v = 0;
        if (hitAxis == 0) // X
        {
            u = (hitPoint.z - min.z) / (max.z - min.z);
            v = (hitPoint.y - min.y) / (max.y - min.y);
        }
        else if (hitAxis == 1) // Y
        {
            u = (hitPoint.x - min.x) / (max.x - min.x);
            v = (hitPoint.z - min.z) / (max.z - min.z);
        }
        else // Z
        { 
            u = (hitPoint.x - min.x) / (max.x - min.x);
            v = (hitPoint.y - min.y) / (max.y - min.y);
        }

        return new RayInfo(backface, tHit, hitPoint, normal, u, v, this.getMaterial());
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
                    throw new InvalidPropertyNameException(this, property);
            }
        }
    }
    
}
