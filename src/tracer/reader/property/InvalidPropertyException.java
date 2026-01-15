package tracer.reader.property;

public abstract class InvalidPropertyException extends Exception
{
    public static final int NO_DEBUG_INFO = -1;

    // send line as NO_DEBUG_INFO if no debug info is available
    public InvalidPropertyException(String message, int line)
    {
        super("[" + (line == NO_DEBUG_INFO ? "error" : "line " + line) + "] " + message);
    }
}
