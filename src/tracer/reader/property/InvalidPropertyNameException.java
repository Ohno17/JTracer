package tracer.reader.property;

public class InvalidPropertyNameException extends InvalidPropertyException
{
    public InvalidPropertyNameException(PropertyConsumer consumer, Property property)
    {
        super(
            "Invalid property \'" + property.getName() + "\' for consumer \'" + consumer.getClass().getSimpleName() + "\'",
            property.debugInfo != null ? property.debugInfo.line() : InvalidPropertyException.NO_DEBUG_INFO
        );
    }
}
