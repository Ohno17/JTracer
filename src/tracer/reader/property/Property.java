package tracer.reader.property;

import tracer.reader.ReaderDebugInfo;

public class Property
{
    private final String name;
    private final PropertyValue value;

    public ReaderDebugInfo debugInfo;

    public Property(String name, PropertyValue.Type type, Object value)
    {
        this.name = name;
        this.value = new PropertyValue(type, value);
    }

    public String getName()
    {
        return name;
    }

    public PropertyValue getValue()
    {
        return value;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(PropertyValue.Type type) throws InvalidPropertyValueException
    {
        if (value.type != type)
        {
            throw new InvalidPropertyValueException(this, type);
        }
        return (T) value.value;
    }

    @Override
    public String toString()
    {
        return name + "=" + value;
    }

}
