package tracer.mesh;

import tracer.vector.Color;

/**
 * Represents the characteristics of the material of a mesh.
 */
public class Material
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

}
