import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JDialog;

/**
 * Simulate realistic light reflection and refraction in digital environments.
 */
public class Main
{

    private static final Dimension RENDER_WINDOW_SIZE = new Dimension(600, 600);
    private static final Dimension CONTROL_WINDOW_SIZE = new Dimension(350, 500);

    private static String currentSceneFile = "/demo/plane_sky.txt";

    public static void main(String[] args) throws Exception
    {
        JFrame mainFrame = new JFrame("JTracer - Render Window");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(RENDER_WINDOW_SIZE);

        // Initalize Tracer
        Tracer tracer = new Tracer(RENDER_WINDOW_SIZE, currentSceneFile);
        mainFrame.add(tracer);
        
        mainFrame.setVisible(true);

        JDialog controlFrame = new JDialog(mainFrame, "JTracer - Control Panel");
        controlFrame.setSize(CONTROL_WINDOW_SIZE);

        JButton renderButton = new JButton("Render");
        renderButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                tracer.render();
            }
        });
        controlFrame.add(renderButton);

        JButton reloadButton = new JButton("Reload Scene File");
        reloadButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                tracer.reloadScene(currentSceneFile);
            }
        });
        controlFrame.add(reloadButton);

        JComboBox<String> comboBox = new JComboBox<>(new String[] {
            "/demo/ball_scene.txt",
            "/demo/gtest.txt",
            "/demo/gtest2.txt",
            "/demo/gtest3.txt",
            "/demo/mirror_room.txt",
            "/demo/plane_sky.txt",
            "/demo/texture_walls.txt",
            "/demo/triangles_indiv.txt"
        });
        comboBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String selectedItem = (String) comboBox.getSelectedItem();
                currentSceneFile = selectedItem;
                tracer.reloadScene(currentSceneFile);
            }
        });
        controlFrame.add(comboBox);

        controlFrame.setLayout(new FlowLayout());
        controlFrame.pack();

        controlFrame.setVisible(true);
        tracer.render();
    }

}
