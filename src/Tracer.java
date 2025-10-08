import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

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

    private int raysPerPixel = 20;
    private int bounces = 20;
    private int tileSize = 32;

    public Tracer(Dimension size, String path)
    {
        setPreferredSize(size);

        image = new BufferedImage((int) size.getWidth(), (int) size.getHeight(), BufferedImage.TYPE_INT_RGB);
        scene = new Scene(bounces);
        
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
        int width = image.getWidth();
        int height = image.getHeight();

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        for (int tileX = 0; tileX < width; tileX += tileSize)
        {
            for (int tileY = 0; tileY < height; tileY += tileSize)
            {
                final int startX = tileX;
                final int startY = tileY;
                final int endX = Math.min(startX + tileSize, width);
                final int endY = Math.min(startY + tileSize, height);

                executor.submit(() -> {
                    ThreadLocalRandom rand = ThreadLocalRandom.current();

                    for (int y = startY; y < endY; y++)
                    {
                        for (int x = startX; x < endX; x++)
                        {
                            Color pixelColor = new Color(0, 0, 0);

                            for (int r = 0; r < raysPerPixel; r++)
                            {
                                double changedX = x + 0.4 * (rand.nextDouble() - 0.5);
                                double changedY = y + 0.4 * (rand.nextDouble() - 0.5);
                                double pX = changedX / width;
                                double pY = changedY / height;

                                pixelColor = pixelColor.add(scene.getColor(pX, pY));
                            }

                            pixelColor = pixelColor.divide(raysPerPixel);
                            image.setRGB(x, y, pixelColor.toBytes());
                        }
                    }
                });
            }
        }

        executor.shutdown();
        try
        {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        repaint();
    }

    public void reloadScene(String path)
    {
        Scene newScene = new Scene(bounces);
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
