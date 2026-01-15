package tracer.reader.property;

public class InvalidPropertyValueException extends InvalidPropertyException
{
    public InvalidPropertyValueException(Property property, PropertyValue.Type expected)
    {
        super(
            "Property \'" + property.getName() + "\' should be of type \'" + expected + "\' (actual: \'" + property.getValue().type + "\')",
            property.debugInfo != null ? property.debugInfo.line() : InvalidPropertyException.NO_DEBUG_INFO
        );
    }
}
