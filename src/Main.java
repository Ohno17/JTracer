import java.awt.Dimension;
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

    private static final Dimension RENDER_WINDOW_SIZE = new Dimension(600, 600);
    private static final Dimension CONTROL_WINDOW_SIZE = new Dimension(350, 500);

    public static void main(String[] args) throws Exception
    {
        JFrame mainFrame = new JFrame("JTracer - Render Window");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(RENDER_WINDOW_SIZE);

        // Initalize Tracer
        Tracer tracer = new Tracer(RENDER_WINDOW_SIZE);
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

        controlFrame.setVisible(true);
    }

}
