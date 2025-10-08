package tracer.reader.dsl;

public class Token
{

    public static enum Type
    {
        IDENTIFIER, NUMBER,
        
        CURLY_OPEN, CURLY_CLOSE,
        PAREN_OPEN, PAREN_CLOSE,
        SQUARE_OPEN, SQUARE_CLOSE,

        PLUS, MINUS, STAR, SLASH,
        EQUALS, COMMA, SEMICOLON,

        EOF
    }

    public final Type type;
    public final String lexeme;
    public final Object literal;
    public final int line;

    public Token(Type type, String lexeme, Object literal, int line)
    {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString()
    {
        return String.format("%s: %s (%s)", type, lexeme, literal);
    }
    
}
