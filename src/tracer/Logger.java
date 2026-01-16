package tracer;

public class Logger
{

    public static final String ERROR_COLOR = "\u001B[31m";
    public static final String RESET_COLOR = "\u001B[0m";

    public static void errorPrintln(String message)
    {
        System.err.println(ERROR_COLOR + message + RESET_COLOR);
    }

    public static void errorPrintln(String system, String message)
    {
        System.err.println(ERROR_COLOR + "[" + system + "]" + message + RESET_COLOR);
    }

    public static void outputPrintln(String message)
    {
        System.out.println(message);
    }

    public static void outputPrintln(String system, String message)
    {
        System.out.println("[" + system + "]" + message);
    }
    
}
