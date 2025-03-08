package tracer;

import tracer.ray.Ray;
import tracer.vector.Matrix3x3;
import tracer.vector.Vector3;
/**
 * Represents a camera from which rays originate.
 */
public class Camera
{

    private Vector3 position;
    private Vector3 forward;

    private double fieldOfViewX;
    private double fieldOfViewY;

    public Camera()
    {
        this.position = new Vector3(0, 0, 0);
        this.forward = new Vector3(0, 0, 1);
        this.setFieldOfView(80, 1d / 1d);
    }

    public Camera(double fieldOfViewDegrees, double aspectRatio)
    {
        this.position = new Vector3(0, 0, 0);
        this.forward = new Vector3(0, 0, 1);
        setFieldOfView(fieldOfViewDegrees, aspectRatio);
    }

    public void setFieldOfView(double fieldOfViewDegrees, double aspectRatio)
    {
        this.fieldOfViewX = fieldOfViewDegrees * (Math.PI / 180d);
        this.fieldOfViewY = fieldOfViewDegrees * (Math.PI / 180d) * aspectRatio;
    }

    public void setPosition(Vector3 position)
    {
        this.position = position;
    }

    public void movePosition(Vector3 movement)
    {
        this.position = this.position.add(movement);
    }

    public void setForward(Vector3 forward)
    {
        this.forward = forward.normalize();
    }

    public void rotateForward(Vector3 rotation)
    {
        this.forward = this.forward.add(rotation).normalize();
    }

    public Matrix3x3 getRotationMatrix()
    {
        Vector3 right = Scene.UP.cross(forward).normalize();
        Vector3 up = forward.cross(right).normalize();
        return new Matrix3x3(right, up, forward, Matrix3x3.Orientation.COLUMNS);
    }

    public void pointTowards(Vector3 point)
    {
        this.forward = point.subtract(position).normalize();
        System.out.println(getRotationMatrix());
    }

    public Ray getRay(double x, double y)
    {
        double pX = (x * 2) - 1, pY = (-y * 2) + 1;
        Vector3 pixelDirection = new Vector3(pX * Math.tan(fieldOfViewX / 2), pY * Math.tan(fieldOfViewY / 2), 1).normalize();
        return new Ray(position, pixelDirection.multiply(getRotationMatrix()));
    }
    
}
