package tracer.reader.property;

public abstract class InvalidPropertyException extends Exception
{
    public InvalidPropertyException(String message)
    {
        super(message);
    }
}
