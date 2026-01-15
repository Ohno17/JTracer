package tracer.mesh;

import java.util.List;

import tracer.vector.Color;
import tracer.vector.Vector3;
import tracer.reader.property.InvalidPropertyException;
import tracer.reader.property.InvalidPropertyNameException;
import tracer.reader.property.Property;
import tracer.reader.property.PropertyConsumer;
import tracer.reader.property.PropertyValue;

/**
 * Represents the characteristics of the material of a mesh.
 */
public class Material implements PropertyConsumer
{
    
    public Color color;
    public Texture texture;

    public double roughness;

    public Color emissionColor;
    public double emissionStrength;

    public Material()
    {
        this.color = new Color(1);
        this.texture = new Texture();
        this.roughness = 0.5;
        this.emissionColor = new Color(0);
        this.emissionStrength = 0;
    }

    public Color sample(double u, double v)
    {
        if (texture.exists())
            return texture.sample(u, v).multiply(color);
        return color;
    }

    @Override
    public void apply(List<Property> properties) throws InvalidPropertyException
    {
        for (Property property : properties)
        {
            switch (property.getName())
            {
                case "color":
                    color = ((Vector3) property.getValue(PropertyValue.Type.VECTOR3)).toColor();
                    break;
                case "texture":
                    texture.setImagePath(property.getValue(PropertyValue.Type.STRING));
                    break;
                case "textureScale":
                    texture.setScale(property.getValue(PropertyValue.Type.NUMBER));
                    break;
                case "roughness":
                    roughness = property.getValue(PropertyValue.Type.NUMBER);
                    break;
                case "emissionColor":
                    emissionColor = ((Vector3) property.getValue(PropertyValue.Type.VECTOR3)).toColor();
                    break;
                case "emissionStrength":
                    emissionStrength = property.getValue(PropertyValue.Type.NUMBER);
                    break;
                default:
                    throw new InvalidPropertyNameException(this, property);
            }
        }
    }

}
