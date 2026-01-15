import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JPanel;

import tracer.Scene;
import tracer.vector.Color;
import tracer.reader.Reader;
/**
 * Holds a ray tracer to display in a swing context.
 */
public class Tracer extends JPanel
{
    
    private BufferedImage image;
    private Scene scene;

    private int raysPerPixel = 64;
    private int tileSize = 32;

    public Tracer(Dimension size, String path)
    {
        setPreferredSize(size);

        image = new BufferedImage((int) size.getWidth(), (int) size.getHeight(), BufferedImage.TYPE_INT_RGB);
        scene = new Scene();
        
        try
        {
            Reader.readInto(scene, Path.of(path));
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

        List<int[]> tiles = new ArrayList<>();
        for (int tileX = 0; tileX < width; tileX += tileSize)
        {
            for (int tileY = 0; tileY < height; tileY += tileSize)
            {
                tiles.add(new int[]{tileX, tileY});
            }
        }
        tiles.sort(Comparator.comparingLong(coord -> mortonCode(coord[0], coord[1])));

        for (int[] tile : tiles)
        {
            final int startX = tile[0];
            final int startY = tile[1];
            final int endX = Math.min(startX + tileSize, width);
            final int endY = Math.min(startY + tileSize, height);

            executor.submit(() ->
            {
                try 
                {
                    ThreadLocalRandom rand = ThreadLocalRandom.current();

                    for (int y = startY; y < endY; y++)
                    {
                        for (int x = startX; x < endX; x++)
                        {

                            Color pixelColor = new Color(0, 0, 0);
                            for (int r = 0; r < raysPerPixel; r++)
                            {
                                double pX = (x + 0.4 * (rand.nextDouble() - 0.5)) / width;
                                double pY = (y + 0.4 * (rand.nextDouble() - 0.5)) / height;
                                pixelColor = pixelColor.add(scene.getColor(pX, pY));
                            }
                            image.setRGB(x, y, pixelColor.divide(raysPerPixel).toBytes());
                        }
                    }
                    
                    repaint(startX - 1, startY - 1, (endX - startX) + 2, (endY - startY) + 2);
                }
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
    }

    private long mortonCode(int x, int y) {
        long code = 0;
        for (int i = 0; i < 16; i++) {
            code |= (long) (x & (1 << i)) << i | (long) (y & (1 << i)) << (i + 1);
        }
        return code;
    }

    public void reloadScene(String path)
    {
        Scene newScene = new Scene();
        boolean sceneFileGood = false;
        try
        {
            sceneFileGood = Reader.readInto(newScene, Path.of(path));
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

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        g2.drawImage(image, null, 0, 0);
    }

}
