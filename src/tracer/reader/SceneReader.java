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

    /**
     * Processes a scene file and inserts the collected elements into the given scene object.
     * @param scene the scene into which the elements of the file will be inserted
     * @param path path to the scene file
     * @return true on success, and false on failure
     * @throws IOException
     */
    public static boolean readInto(Scene scene, Path path) throws IOException
    {
        String source = Files.readString(path, Charset.forName("UTF-8"));

        DSL dsl = new DSL(source);
        List<DSLBlock> blocks = dsl.compile();

        HashMap<String, Material> materialMap;

        try
        {
            materialMap = createMaterialMap(blocks);
        }
        catch (InvalidPropertyException exception)
        {
            System.err.println(exception.getMessage());
            return false;
        }

        for (DSLBlock block : blocks)
        {
            if (block.type.equals("Material")) continue;

            Mesh consumer;
            switch (block.type)
            {
                case "Sphere" -> consumer = new Sphere(null, 0);
                case "Plane" -> consumer = new Plane(null, null);
                case "Box" -> consumer = new Box(null, null);
                default -> {
                    System.err.println("Unrecongnized type \'" + block.type + "\'");
                    return false;
                }
            }

            String materialKey = null;
            ArrayList<Property> properties = new ArrayList<>(block.properties);
            for (int i = 0; i < properties.size(); i++)
            {
                if (properties.get(i).getName().equals("material"))
                {
                    try
                    {
                        materialKey = properties.get(i).getValue(PropertyValue.Type.STRING);
                    }
                    catch (InvalidPropertyValueException exception)
                    {
                        System.err.println(exception.getMessage());
                        return false;
                    }

                    properties.remove(properties.get(i));
                    i--;
                }
            }

            try
            {
                consumer.apply(properties);
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
                consumer.setMaterial(material);
            }

            scene.add(consumer);
        }

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

}
