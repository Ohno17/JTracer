package tracer.reader.property;

import tracer.vector.Vector3;

public class PropertyValue
{

    public static enum Type
    {
        NUMBER,
        STRING,
        VECTOR3
    }

    public Type type;
    public Object value;

    public PropertyValue(Type type, Object value)
    {
        this.type = type;
        this.value = value;
    }

    public PropertyValue(String value)
    {
        try
        {
            this.value = Double.parseDouble(value);
            this.type = Type.NUMBER;
        }
        catch (NumberFormatException notNumberException)
        {
            if (value.startsWith("[") && value.endsWith("]"))
            {
                String[] parts = value.substring(1, value.length() - 1).split(",");
                if (parts.length == 3)
                {
                    try
                    {
                        double x = Double.parseDouble(parts[0].trim());
                        double y = Double.parseDouble(parts[1].trim());
                        double z = Double.parseDouble(parts[2].trim());
                        this.value = new Vector3(x, y, z);
                        this.type = Type.VECTOR3;
                    }
                    catch (NumberFormatException notVectorException)
                    {
                        this.value = value;
                        this.type = Type.STRING;
                    }
                }
                else
                {
                    this.value = value;
                    this.type = Type.STRING;
                }
            }
            else
            {
                this.value = value;
                this.type = Type.STRING;
            }
        }
    }

    @Override
    public String toString()
    {
        return value.toString();
    }
    
}
