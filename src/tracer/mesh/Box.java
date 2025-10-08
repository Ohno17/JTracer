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
        // TODO: Actually get intersection formula for boxes
        return new RayInfo(false);
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
