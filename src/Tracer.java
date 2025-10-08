import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JPanel;

import tracer.Scene;
import tracer.vector.Color;
import tracer.reader.SceneReader;
/**
 * Holds a ray tracer to display in a swing context.
 * @author ajaysri5715
 */
public class Tracer extends JPanel
{
    
    private BufferedImage image;
    private Scene scene;

    private int raysPerPixel = 10;

    public Tracer(Dimension size, String path)
    {
        setPreferredSize(size);

        image = new BufferedImage((int) size.getWidth(), (int) size.getHeight(), BufferedImage.TYPE_INT_RGB);
        scene = new Scene(10);
        
        try
        {
            SceneReader.readInto(scene, Path.of(path));
        }
        catch (IOException exception)
        {
            System.err.println("Scene file not found");
        }
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

    public void reloadScene(String path)
    {
        Scene newScene = new Scene(10);
        boolean sceneFileGood = false;
        try
        {
            sceneFileGood = SceneReader.readInto(newScene, Path.of(path));
        }
        catch (IOException exception)
        {
            System.err.println("Scene file not found");
            return;
        }
        if (sceneFileGood) this.scene = newScene;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        g2.drawImage(image, null, 0, 0);
    }

}
