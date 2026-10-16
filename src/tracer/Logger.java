package tracer;

public class Logger
{

    public static final String ERROR_COLOR = "\u001B[31m";
    public static final String RESET_COLOR = "\u001B[0m";

    private static final boolean LOGS_ENABLED = false;

    public static void errorPrintln(String message)
    {
        if (!LOGS_ENABLED) return;
        System.err.println(ERROR_COLOR + message + RESET_COLOR);
    }

    public static void errorPrintln(String system, String message)
    {
        if (!LOGS_ENABLED) return;
        System.err.println(ERROR_COLOR + "[" + system + "] " + message + RESET_COLOR);
    }

    public static void outputPrintln(String message)
    {
        if (!LOGS_ENABLED) return;
        System.out.println(message);
    }

    public static void outputPrintln(String system, String message)
    {
        if (!LOGS_ENABLED) return;
        System.out.println("[" + system + "] " + message);
    }
    
}
