package tracer.reader.dsl;

import java.util.List;

public class DSL
{

    public static boolean hadError = false;
    public static boolean hadRuntimeError = false;
    
    private final String source;

    public DSL(String source)
    {
        this.source = source;
    }

    public List<DSLBlock> compile()
    {
        Tokenizer tokenizer = new Tokenizer(source);
        List<Token> tokens = tokenizer.scanTokens();

        Parser parser = new Parser(tokens);
        List<Statement> statements = parser.parse();

        if (hadError) return null;

        Processor processor = new Processor();
        List<DSLBlock> blocks = processor.process(statements);

        if (hadRuntimeError) return null;

        return blocks;
    }

    static void error(int line, String message)
    {
        report(line, "", message);
    }

    static void error(Token token, String message)
    {
        if (token.type == Token.Type.EOF)
        {
            report(token.line, " at end", message);
        }
        else
        {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    public static void runtimeError(Processor.ProcessorException exception) {
        System.err.println(exception.getMessage() + "\n[line " + exception.token.line + "]");
        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message)
    {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

}
