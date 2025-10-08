import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * Simulate realistic light reflection and refraction in digital environments.
 * @author ajaysri5715
 */
public class Main
{

    private static final String SCENE_FILE_PATH = "scene.txt";

    private static final Dimension RENDER_WINDOW_SIZE = new Dimension(600, 600);
    private static final Dimension CONTROL_WINDOW_SIZE = new Dimension(350, 500);

    public static void main(String[] args) throws Exception
    {
        JFrame mainFrame = new JFrame("JTracer - Render Window");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(RENDER_WINDOW_SIZE);

        // Initalize Tracer
        Tracer tracer = new Tracer(RENDER_WINDOW_SIZE, SCENE_FILE_PATH);
        mainFrame.add(tracer);
        
        mainFrame.setVisible(true);

        JFrame controlFrame = new JFrame("JTracer - Control Panel");
        controlFrame.setSize(CONTROL_WINDOW_SIZE);

        JButton renderButton = new JButton("Render");
        renderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                tracer.render();
            }
        });
        controlFrame.add(renderButton);

        JButton reloadButton = new JButton("Reload Scene File");
        reloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                tracer.reloadScene(SCENE_FILE_PATH);
            }
        });
        controlFrame.add(reloadButton);

        controlFrame.setLayout(new FlowLayout());
        controlFrame.pack();

        controlFrame.setVisible(true);
    }

}
