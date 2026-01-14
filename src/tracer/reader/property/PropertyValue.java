package tracer.reader.property;

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

    @Override
    public String toString()
    {
        return value.toString();
    }
    
}
