package tracer.reader.dsl;

import java.util.List;

import tracer.reader.ReaderDebugInfo;
import tracer.reader.property.Property;

public class DSLBlock
{

    public final String type;
    public final String name;
    public final List<Property> properties;

    public ReaderDebugInfo debugInfo;

    public DSLBlock(String type, List<Property> properties)
    {
        this.type = type;
        this.name = null;
        this.properties = properties;
    }

    public DSLBlock(String type, String name, List<Property> properties)
    {
        this.type = type;
        this.name = name;
        this.properties = properties;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(type + " " + name + " {");
        for (Property property : properties)
        {
            builder.append(property + ", ");
        }
        builder.append("}");

        return builder.toString();
    }
    
}
