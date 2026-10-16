package tracer.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import tracer.mesh.Box;
import tracer.mesh.Material;
import tracer.mesh.Mesh;
import tracer.mesh.Plane;
import tracer.mesh.Sphere;
import tracer.mesh.Triangle;
import tracer.Logger;
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
public class Reader
{

    private static Scene scene;
    private static HashMap<String, Material> materialMap;

    private static int applyErrors = 0;

    private static void reportApplyError(DSLBlock block, String message)
    {
        if (block.debugInfo == null)
            Logger.errorPrintln("[error] Error: " + message);
        else if (block.name == null)
            Logger.errorPrintln("[line " + block.debugInfo.line() + "] Error: " + message);
        else if (!message.contains("[line"))
            Logger.errorPrintln("[block at line " + block.debugInfo.line() + "] Error in " + block.type + " \'" + block.name + "\': " + message);
        else
            Logger.errorPrintln(message + " (Error in " + block.type + " \'" + block.name + "\')");
        applyErrors++;
    }

    public static String readToString(InputStream inputStream) throws IOException
    {
        if (inputStream == null) return null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
        {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    /**
     * Processes a scene file and inserts the collected elements into the given scene object.
     * @param scene the scene into which the elements of the file will be inserted
     * @param path path to the scene file
     * @return true on success, and false on failure
     * @throws IOException
     */
    public static boolean readInto(Scene scene, String path) throws IOException
    {
        Logger.outputPrintln("");
        Logger.outputPrintln("reader", "Reading \'" + path + "\'");
        applyErrors = 0;

        Reader.scene = scene;

        InputStream stream = Reader.class.getResourceAsStream(path.replace('\\', '/'));
        if (stream == null) 
        {
            Logger.errorPrintln("reader", "Failed to read scene file.");
            return false;
        }
        String source = readToString(stream);

        DSL dsl = new DSL(source);
        List<DSLBlock> blocks = dsl.compile();
        if (blocks == null) return false; // compilation failed with it's own error messages

        materialMap = createMaterialMap(blocks);
        if (!handleSceneBlock(blocks)) return false;

        for (DSLBlock block : blocks)
        {
            if (!processBlock(block))
            {
                Logger.errorPrintln("reader", "Failed with " + applyErrors + " errors while applying properties.");
                return false;
            }
        }
        
        if (applyErrors > 0) Logger.errorPrintln("reader", "Success with " + applyErrors + " errors while applying properties.");
        else Logger.outputPrintln("reader", "Scene loaded successfully.");
        return true;
    }

    private static boolean processBlock(DSLBlock block)
    {
        if (block.type.equals("Material") || block.type.equals("Scene")) return true;

        Mesh mesh;
        switch (block.type)
        {
            case "Sphere" -> mesh = new Sphere();
            case "Plane" -> mesh = new Plane();
            case "Box" -> mesh = new Box();
            case "Triangle" -> mesh = new Triangle();
            default -> {
                reportApplyError(block, "Unrecongnized type \'" + block.type + "\'");
                return false;
            }
        }

        Material material = processMaterialProperty(block);
        if (material == null)
        {
            reportApplyError(block, "Mesh must have a material (type \'" + block.type + "\')");
            return false;
        }

        try
        {
            mesh.apply(block.properties);
        }
        catch (InvalidPropertyException exception)
        {
            reportApplyError(block, exception.getMessage());
            return false;
        }

        mesh.setMaterial(material);

        scene.add(mesh);
        return true;
    }

    private static HashMap<String, Material> createMaterialMap(List<DSLBlock> blocks)
    {
        HashMap<String, Material> materialMap = new HashMap<>();

        // Add materials to material map
        for (DSLBlock block : blocks)
        {
            if (!block.type.equals("Material")) continue;

            Material material = new Material();
            try
            {
                material.apply(block.properties);
            }
            catch (InvalidPropertyException exception)
            {
                reportApplyError(block, exception.getMessage());
            }
            materialMap.put(block.name, material);
        }

        return materialMap;
    }

    private static boolean handleSceneBlock(List<DSLBlock> blocks)
    {
        for (DSLBlock block : blocks)
        {
            if (!block.type.equals("Scene")) continue;

            Material material = processMaterialProperty(block);
            if (material != null)
                scene.setMaterial(material);

            try
            {
                scene.apply(block.properties);
            }
            catch (InvalidPropertyException exception)
            {
                reportApplyError(block, exception.getMessage());
            }
        }
        return true;
    }

    // changes the given list by removing the material property if it exists
    private static Material processMaterialProperty(DSLBlock originalBlock)
    {
        Material material = null;
        for (int i = 0; i < originalBlock.properties.size(); i++)
        {
            Property property = originalBlock.properties.get(i);

            if (!property.getName().equals("material")) continue;

            String materialKey;
            try
            {
                materialKey = property.getValue(PropertyValue.Type.STRING);
            }
            catch (InvalidPropertyValueException exception)
            {
                reportApplyError(originalBlock, exception.getMessage());
                return null;
            }

            material = materialMap.get(materialKey);
            if (material == null)
            {
                reportApplyError(originalBlock, "No material named \'" + materialKey + "\'");
                return null;
            }

            originalBlock.properties.remove(property);
            i--;
            break;
        }
        return material;
    }
    
}
