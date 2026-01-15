package tracer.reader.dsl;

import java.util.List;

import tracer.reader.Reader;

public class DSL
{

    public static int syntaxErrors = 0;
    public static boolean processingError = false;
    
    private final String source;

    public DSL(String source)
    {
        this.source = source;
    }

    public List<DSLBlock> compile()
    {
        syntaxErrors = 0;
        processingError = false;

        Tokenizer tokenizer = new Tokenizer(source);
        List<Token> tokens = tokenizer.scanTokens();

        Parser parser = new Parser(tokens);
        List<Statement> statements = parser.parse();

        if (syntaxErrors > 0)
        {
            System.err.println(Reader.ERROR_COLOR + "Failed with " + syntaxErrors + " syntax errors." + Reader.RESET_COLOR);
            return null;
        }

        Processor processor = new Processor();
        List<DSLBlock> blocks = processor.process(statements);

        if (processingError)
        {
            System.err.println(Reader.ERROR_COLOR + "Failed while processing." + Reader.RESET_COLOR);
            return null;
        }

        return blocks;
    }

    static void syntaxError(int line, String message)
    {
        syntaxError(line, "", message);
    }

    static void syntaxError(Token token, String message)
    {
        if (token.type == Token.Type.EOF)
        {
            syntaxError(token.line, " at end", message);
        }
        else
        {
            syntaxError(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    public static void processingError(Processor.ProcessorException exception) {
        System.err.println(Reader.ERROR_COLOR + "[line " + exception.token.line + "] Error: " + exception.getMessage() + Reader.RESET_COLOR);
        processingError = true;
    }

    private static void syntaxError(int line, String where, String message)
    {
        System.err.println(Reader.ERROR_COLOR + "[line " + line + "] Error" + where + ": " + message + Reader.RESET_COLOR);
        syntaxErrors++;
    }

}
