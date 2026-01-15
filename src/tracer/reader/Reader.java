package tracer.reader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
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
public class Reader
{
    public static final String ERROR_COLOR = "\u001B[31m";
    public static final String RESET_COLOR = "\u001B[0m";

    private static Scene scene;
    private static HashMap<String, Material> materialMap;

    private static int applyErrors = 0;

    private static void reportApplyError(DSLBlock block, String message)
    {
        System.err.print(ERROR_COLOR);
        if (block.debugInfo == null)
            System.err.println("[error] Error: " + message);
        else if (block.name == null)
            System.err.println("[line " + block.debugInfo.line + "] Error: " + message);
        else if (!message.contains("[line"))
            System.err.println("[within block at line " + block.debugInfo.line + "] Error in " + block.type + " \'" + block.name + "\': " + message);
        else
            System.err.println(message + " (Error in " + block.type + " \'" + block.name + "\')");
        System.err.print(RESET_COLOR);
        applyErrors++;
    }

    /**
     * Processes a scene file and inserts the collected elements into the given scene object.
     * @param scene the scene into which the elements of the file will be inserted
     * @param path path to the scene file
     * @return true on success, and false on failure
     * @throws IOException
     */
    public static boolean readInto(Scene scene, Path path) throws IOException
    {
        System.out.println();
        applyErrors = 0;

        Reader.scene = scene;

        String source = Files.readString(path, Charset.forName("UTF-8"));

        DSL dsl = new DSL(source);
        List<DSLBlock> blocks = dsl.compile();
        if (blocks == null) return false; // compilation failed with it's own error messages

        materialMap = createMaterialMap(blocks);
        if (!handleSceneBlock(blocks)) return false;

        for (DSLBlock block : blocks)
        {
            if (!processBlock(block))
            {
                System.err.println(ERROR_COLOR + "Failed with " + applyErrors + " errors while applying properties." + RESET_COLOR);
                return false;
            }
        }
        
        if (applyErrors > 0) System.err.println(ERROR_COLOR + "Success with " + applyErrors + " errors while applying properties." + RESET_COLOR);
        else System.out.println("Scene loaded successfully.");
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
