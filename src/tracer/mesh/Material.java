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
    
    private Color color;

    private double roughness;

    private Color emissionColor;
    private double emissionStrength;

    public Material(Color color)
    {
        this.color = color;
        this.roughness = 0.5;
        this.emissionColor = new Color(0, 0, 0);
        this.emissionStrength = 0;
    }

    private Material(Builder builder)
    {
        this.color = builder.color;
        this.roughness = builder.roughness;
        this.emissionColor = builder.emissionColor;
        this.emissionStrength = builder.emissionStrength;
    }

    public Color getColor()
    {
        return color;
    }

    public double getRoughness()
    {
        return roughness;
    }

    public Color getEmissionColor()
    {
        return emissionColor;
    }

    public double getEmissionStrength()
    {
        return emissionStrength;
    }

    /**
     * Allows creation of a material with optional aspects
     */
    public static class Builder
    {

        public Color color;

        public double roughness = 0.5;

        public Color emissionColor = new Color(0, 0, 0);
        public double emissionStrength = 0;

        public Builder(Color color)
        {
            this.color = color;
        }

        public Builder setRoughness(double roughness)
        {
            this.roughness = roughness;
            return this;
        }

        public Builder setEmission(Color emissionColor, double emissionStrength)
        {
            this.emissionColor = emissionColor;
            this.emissionStrength = emissionStrength;
            return this;
        }

        public Material build()
        {
            return new Material(this);
        }

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
                    throw new InvalidPropertyNameException(this, property.getName());
            }
        }
    }

}
