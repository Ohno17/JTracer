package tracer;

import java.util.ArrayList;
import java.util.List;

import tracer.mesh.Material;
import tracer.mesh.Mesh;
import tracer.ray.Ray;
import tracer.ray.RayInfo;
import tracer.reader.property.InvalidPropertyException;
import tracer.reader.property.InvalidPropertyNameException;
import tracer.reader.property.Property;
import tracer.reader.property.PropertyConsumer;
import tracer.reader.property.PropertyValue;
import tracer.vector.Color;
import tracer.vector.Vector3;
/**
 * Represents a scene that can be filled with meshes.
 */
public class Scene implements PropertyConsumer
{

    public static final Vector3 UP = new Vector3(0, 1, 0);

    private int bounces;

    private Camera camera;
    private ArrayList<Mesh> meshes;
    private Material backgroundMaterial;

    public Scene()
    {
        this.bounces = 20;

        this.camera = new Camera(80, 1d / 1d);
        this.meshes = new ArrayList<Mesh>();
    }

    public Scene(int bounces, double fieldOfView, double aspectRatio, Material backgroundMaterial)
    {
        this.bounces = bounces;
        this.camera = new Camera(fieldOfView, aspectRatio);
        this.meshes = new ArrayList<Mesh>();
        this.backgroundMaterial = backgroundMaterial;
    }

    public Camera getCamera()
    {
        return camera;
    }

    public void add(Mesh mesh)
    {
        meshes.add(mesh);
    }

    public void setMaterial(Material backgroundMaterial)
    {
        this.backgroundMaterial = backgroundMaterial;
    }

    private RayInfo intersect(Ray ray)
    {
        RayInfo closestInfo = new RayInfo();

        for (Mesh mesh : meshes)
        {
            RayInfo info = mesh.intersect(ray);
            if (info.hit && info.distance < closestInfo.distance)
            {
                closestInfo = info;
                closestInfo.material = mesh.getMaterial();
            }
        }

        return closestInfo;
    }

    private Color getBackgroundColor(Ray ray) 
    {
        if (backgroundMaterial == null)
        {
            double factor = (-ray.direction.y + 1) / 2d;
            return new Color(0.6, 0.6, 1).lerp(new Color(0), factor);
        }

        Vector3 dir = ray.direction.normalize();

        double theta = Math.atan2(dir.z, dir.x);
        double phi = Math.asin(dir.y);

        double u = (theta + Math.PI) / (2.0 * Math.PI);
        double v = (phi + (Math.PI / 2.0)) / Math.PI;

        return backgroundMaterial.sample(u, 1.0 - v);
    }

    public Color getColor(double x, double y)
    {
        return getColor(camera.getRay(x, y));
    }

    public Color getColor(Ray ray)
    {
        Color rayLightColor = new Color(0, 0, 0);
        Color rayColor = new Color(1, 1, 1);

        for (int b = 0; b < bounces; b++)
        {
            RayInfo info = intersect(ray);
            if (info.hit)
            {
                ray.origin = info.point;
                ray.reflect(info.normal, info.material.roughness);
                
                Material material = info.material;
                Color surfaceColor = material.sample(info.u, info.v); 
                Color emission = material.emissionColor.multiply(material.emissionStrength);
                rayLightColor = rayLightColor.add(emission.multiply(rayColor));
                rayColor = rayColor.multiply(surfaceColor);
            } else {
                rayLightColor = rayLightColor.add(getBackgroundColor(ray).multiply(rayColor));
                break;
            }
        }
        
        return rayLightColor;
    }

    @Override
    public void apply(List<Property> properties) throws InvalidPropertyException
    {
        for (Property property : properties)
        {
            switch (property.getName())
            {
                case "cameraPosition":
                    camera.setPosition(property.getValue(PropertyValue.Type.VECTOR3));
                    break;
                case "cameraLookAtPosition":
                    camera.pointTowards(property.getValue(PropertyValue.Type.VECTOR3));
                    break;
                case "bounces":
                    double dblBounces = property.getValue(PropertyValue.Type.NUMBER);
                    try
                    {
                        bounces = (int) dblBounces;
                    }
                    catch (ClassCastException e)
                    {
                        System.err.println("The amount of bounces must be an integer. Received: " + dblBounces);
                    }
                    break;
                default:
                    throw new InvalidPropertyNameException(this, property.getName());
            }
        }
    }

}
