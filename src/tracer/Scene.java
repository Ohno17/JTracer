package tracer;

import java.util.ArrayList;

import tracer.mesh.Material;
import tracer.mesh.Mesh;
import tracer.ray.Ray;
import tracer.ray.RayInfo;
import tracer.vector.Color;
import tracer.vector.Vector3;
/**
 * Represents a scene filled with meshes.
 */
public class Scene
{

    public static final Vector3 UP = new Vector3(0, 1, 0);

    private final int bounces;

    private Camera camera;
    private ArrayList<Mesh> meshes;

    public Scene(int bounces)
    {
        this.bounces = bounces;

        this.camera = new Camera();
        this.meshes = new ArrayList<Mesh>();
    }

    public Scene(int bounces, double fieldOfView, double aspectRatio)
    {
        this.bounces = bounces;

        this.camera = new Camera(fieldOfView, aspectRatio);
        this.meshes = new ArrayList<Mesh>();
    }

    public Camera getCamera()
    {
        return camera;
    }

    public void add(Mesh mesh)
    {
        meshes.add(mesh);
    }

    private RayInfo intersect(Ray ray)
    {
        RayInfo closestInfo = new RayInfo(false);

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
        double factor = (-ray.direction.y + 1) / 2d;
        return new Color(180, 180, 255).multiply(1.0 - factor).add(new Color(0, 0, 0).multiply(factor));
    }

    public Color getColor(double x, double y)
    {
        return getColor(camera.getRay(x, y));
    }

    public Color getColor(Ray ray)
    {
        Color rayLightColor = new Color(0, 0, 0);
        Color rayColor = new Color(255, 255, 255);

        for (int b = 0; b < bounces; b++)
        {
            RayInfo info = intersect(ray);
            if (info.hit)
            {
                ray.origin = info.point;
                ray.reflect(info.normal, info.material.getRoughness());

                Material material = info.material;
                Color emission = material.getEmissionColor().multiply(material.getEmissionStrength());
                rayLightColor = rayLightColor.add(emission.multiply(rayColor));
                rayColor = rayColor.multiply(material.getColor());
            } else {
                rayLightColor = rayLightColor.add(rayColor.multiply(getBackgroundColor(ray)));
                break;
            }
        }
        
        return rayLightColor;
    }

}
