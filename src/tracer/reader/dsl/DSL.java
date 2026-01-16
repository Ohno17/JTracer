package tracer.reader.dsl;

import java.util.List;

import tracer.Logger;

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
            Logger.errorPrintln("Failed with " + syntaxErrors + " syntax errors.");
            return null;
        }

        Processor processor = new Processor();
        List<DSLBlock> blocks = processor.process(statements);

        if (processingError)
        {
            Logger.errorPrintln("Failed while processing.");
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
        Logger.errorPrintln("[line " + exception.token.line + "] Error: " + exception.getMessage());
        processingError = true;
    }

    private static void syntaxError(int line, String where, String message)
    {
        Logger.errorPrintln("[line " + line + "] Error" + where + ": " + message);
        syntaxErrors++;
    }

}
