package tracer.reader.property;

public class InvalidPropertyNameException extends InvalidPropertyException
{
    public InvalidPropertyNameException(PropertyConsumer consumer, String property)
    {
        super("Invalid property \'" + property + "\' for consumer \'" + consumer.getClass().getSimpleName() + "\'");
    }
}
