package tracer.ray;

import tracer.vector.Vector3;

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
        Vector3 combinedReflectionDirection = getDiffuseReflection(normal).multiply(roughness).add(getSpecularReflection(normal).multiply(1 - roughness));
        this.direction = normal.add(combinedReflectionDirection).normalize();
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
        double x = Math.random() * 2 - 1;
        double y = Math.random() * 2 - 1;
        double z = Math.random() * 2 - 1;
        Vector3 randomDirection = new Vector3(x, y, z).normalize();
        if (normal.dot(randomDirection) >= 0) return randomDirection;
        else return randomDirection.negative();
    }

    private Vector3 getSpecularReflection(Vector3 normal)
    {
        return this.direction.subtract(normal.multiply(this.direction.dot(normal) * 2d));
    }

}
