package tracer.ray;

import java.util.concurrent.ThreadLocalRandom;

import tracer.math.Vector3;

/**
 * Represents an origin and direction.
 */
public class Ray
{

    public Vector3 origin;
    public Vector3 direction;

    public Ray(Vector3 origin, Vector3 direction)
    {
        this.origin = origin;
        this.direction = direction;
    }

    public void reflect(Vector3 normal, double roughness)
    {
        Vector3 specularDir = getSpecularReflection(normal);
        Vector3 diffuseDir = getDiffuseReflection(normal);
        this.direction = specularDir.lerp(diffuseDir, roughness).normalize();
    }

    public void diffuselyReflect(Vector3 normal)
    {
        this.direction = getDiffuseReflection(normal);
    }

    public void specularlyReflect(Vector3 normal)
    {
        this.direction = getSpecularReflection(normal);
    }

    private Vector3 getDiffuseReflection(Vector3 normal)
    {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        double x = rand.nextDouble() * 2 - 1;
        double y = rand.nextDouble() * 2 - 1;
        double z = rand.nextDouble() * 2 - 1;
        Vector3 randomDirection = new Vector3(x, y, z).normalize();
        if (normal.dot(randomDirection) >= 0) return randomDirection;
        else return randomDirection.negative();
    }

    private Vector3 getSpecularReflection(Vector3 normal)
    {
        return this.direction.subtract(normal.multiply(this.direction.dot(normal) * 2d));
    }

}
