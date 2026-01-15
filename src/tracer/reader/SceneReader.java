package tracer.reader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tracer.mesh.Box;
import tracer.mesh.Material;
import tracer.mesh.Mesh;
import tracer.mesh.Plane;
import tracer.mesh.Sphere;
import tracer.Scene;
import tracer.reader.dsl.DSL;
import tracer.reader.dsl.DSLBlock;
import tracer.reader.property.InvalidPropertyException;
import tracer.reader.property.InvalidPropertyValueException;
import tracer.reader.property.Property;
import tracer.reader.property.PropertyValue;
/**
 * Reads a file and stores its scene data into the given scene object.
 */
public class SceneReader
{
    private static Scene scene;
    private static HashMap<String, Material> materialMap;

    /**
     * Processes a scene file and inserts the collected elements into the given scene object.
     * @param scene the scene into which the elements of the file will be inserted
     * @param path path to the scene file
     * @return true on success, and false on failure
     * @throws IOException
     */
    public static boolean readInto(Scene scene, Path path) throws IOException
    {
        SceneReader.scene = scene;

        String source = Files.readString(path, Charset.forName("UTF-8"));

        DSL dsl = new DSL(source);
        List<DSLBlock> blocks = dsl.compile();
        if (blocks == null) return false; // compilation failed

        try
        {
            materialMap = createMaterialMap(blocks);
            handleSceneBlock(blocks);
        }
        catch (InvalidPropertyException exception)
        {
            System.err.println(exception.getMessage());
            return false;
        }

        for (DSLBlock block : blocks)
        {
            if (!processBlock(block)) return false;
        }

        return true;
    }

    private static boolean processBlock(DSLBlock block)
    {
        if (block.type.equals("Material") || block.type.equals("Scene")) return true;

        Mesh mesh;
        switch (block.type)
        {
            case "Sphere" -> mesh = new Sphere(null, 0);
            case "Plane" -> mesh = new Plane(null, null);
            case "Box" -> mesh = new Box(null, null);
            default -> {
                System.err.println("Unrecongnized type \'" + block.type + "\'");
                return false;
            }
        }

        // Special properties
        String materialKey = null;
        ArrayList<Property> properties = new ArrayList<Property>(block.properties);
        for (int i = 0; i < properties.size(); i++)
        {
            Property property = properties.get(i);
            if (property.getName().equals("material"))
            {
                try
                {
                    materialKey = property.getValue(PropertyValue.Type.STRING);
                }
                catch (InvalidPropertyValueException exception)
                {
                    System.err.println(exception.getMessage());
                    return false;
                }

                properties.remove(property);
                i--;
            }
        }

        try
        {
            mesh.apply(properties);
        }
        catch (InvalidPropertyException exception)
        {
            System.err.println(exception.getMessage());
            return false;
        }

        if (materialKey != null)
        {
            Material material = materialMap.get(materialKey);
            if (material == null)
            {
                System.err.println("No material named \'" + materialKey + "\'");
                return false;
            }
            mesh.setMaterial(material);
        }

        scene.add(mesh);
        return true;
    }

    private static HashMap<String, Material> createMaterialMap(List<DSLBlock> blocks) throws InvalidPropertyException
    {
        HashMap<String, Material> materialMap = new HashMap<>();

        // Add materials to material map
        for (DSLBlock block : blocks)
        {
            if (!block.type.equals("Material")) continue;

            Material material = new Material(null);
            material.apply(block.properties);
            materialMap.put(block.name, material);
        }

        return materialMap;
    }

    private static boolean handleSceneBlock(List<DSLBlock> blocks)
    {
        for (DSLBlock block : blocks)
        {
            if (!block.type.equals("Scene")) continue;
            try
            {
                scene.apply(block.properties);
            }
            catch (InvalidPropertyException exception)
            {
                System.err.println(exception.getMessage());
                return false;
            }
        }
        return true;
    }
    
}
