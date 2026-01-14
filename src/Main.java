import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

/**
 * Simulate realistic light reflection and refraction in digital environments.
 */
public class Main
{

    private static final Dimension RENDER_WINDOW_SIZE = new Dimension(600, 600);
    private static final Dimension CONTROL_WINDOW_SIZE = new Dimension(350, 500);

    private static String currentSceneFile = "demo/plane_sky.txt";

    public static void main(String[] args) throws Exception
    {
        JFrame mainFrame = new JFrame("JTracer - Render Window");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(RENDER_WINDOW_SIZE);

        // Initalize Tracer
        Tracer tracer = new Tracer(RENDER_WINDOW_SIZE, currentSceneFile);
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
                tracer.reloadScene(currentSceneFile);
            }
        });
        controlFrame.add(reloadButton);

        String[] choices;
        List<String> choicesList;
        try (Stream<Path> walk = Files.walk(Paths.get("demo"))) {
            choicesList = walk
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".txt"))
                .map(Path::toString)
                .collect(Collectors.toList());
            choices = new String[choicesList.size()];
            choicesList.toArray(choices);
        }

        JComboBox<String> comboBox = new JComboBox<>(choices);
        comboBox.addActionListener(e -> {
            String selectedItem = (String) comboBox.getSelectedItem();
            currentSceneFile = selectedItem;
            tracer.reloadScene(currentSceneFile);
        });
        controlFrame.add(comboBox);

        controlFrame.setLayout(new FlowLayout());
        controlFrame.pack();

        controlFrame.setVisible(true);
        tracer.render();
    }

}
