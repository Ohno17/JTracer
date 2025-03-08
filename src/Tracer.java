import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import tracer.Scene;
import tracer.mesh.Material;
import tracer.mesh.Plane;
import tracer.mesh.Sphere;
import tracer.vector.Color;
import tracer.vector.Vector3;
/**
 * Holds a ray tracer to display in a swing context.
 * @author ajaysri5715
 */
public class Tracer extends JPanel
{
    
    private BufferedImage image;
    private Scene scene;

    private int raysPerPixel = 10;

    public Tracer(Dimension size)
    {
        setPreferredSize(size);

        image = new BufferedImage((int) size.getWidth(), (int) size.getHeight(), BufferedImage.TYPE_INT_RGB);
        scene = new Scene(10);

        // Create walls
        Plane myp5 = new Plane(new Vector3(0, 1, 0).normalize(), new Vector3(0, -1, 0));
        myp5.setMaterial(new Material(new Color(200, 100, 50)));
        scene.add(myp5);

        // Create sphere
        Sphere mys2 = new Sphere(new Vector3(0, 1, 5), 2);
        mys2.setMaterial(new Material.Builder(new Color(255, 255, 255))
            .setEmission(new Color(255, 255, 255), 1)
            .build());
        scene.add(mys2);

        Sphere mys3 = new Sphere(new Vector3(1, -0.5, 2), 0.5);
        mys3.setMaterial(new Material(new Color(200, 50, 10)));
        scene.add(mys3);

        Sphere mys1 = new Sphere(new Vector3(-1, -0.5, 3), 0.5);
        mys1.setMaterial(new Material.Builder(new Color(20, 200, 80))
            .setRoughness(0)
            .build());
        scene.add(mys1);
    }

    public void render()
    {
        for (int x = 0; x < image.getWidth(); x++)
        {
            for (int y = 0; y < image.getHeight(); y++)
            {
                double pX = (double) x / image.getWidth();
                double pY = (double) y / image.getHeight();

                Color pixelColor = new Color(0, 0, 0);

                for (int r = 0; r < raysPerPixel; r++)
                {
                    pixelColor = pixelColor.add(scene.getColor(pX, pY));
                }
                
                pixelColor = pixelColor.divide(raysPerPixel);
                image.setRGB(x, y, pixelColor.toBytes());
            }
        }

        repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        g2.drawImage(image, null, 0, 0);
    }

}
