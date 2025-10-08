package tracer.reader.property;

public class InvalidPropertyValueException extends InvalidPropertyException
{
    public InvalidPropertyValueException(Property property, PropertyValue.Type expected)
    {
        super("Property \'" + property.getName() + "\' is not of type \'" + expected + "\' (actual: \'" + property.getValue().type + "\')");
    }
}
