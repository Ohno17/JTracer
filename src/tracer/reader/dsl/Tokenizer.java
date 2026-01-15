package tracer.reader.dsl;

import static tracer.reader.dsl.Token.Type;
import static tracer.reader.dsl.Token.Type.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Scans a scene description source file and processes it into tokens
 * @implNote https://craftinginterpreters.com/scanning.html
 */
public class Tokenizer
{

    private final String source;
    private ArrayList<Token> tokens = new ArrayList<Token>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Tokenizer(String source)
    {
        this.source = source;
    }

    List<Token> scanTokens()
    {
        while (!atEnd())
        {
            start = current;
            scanToken();
        }

        tokens.add(new Token(Type.EOF, "", null, line));
        return tokens;
    }

    private void scanToken()
    {
        char c = advance();
        switch (c)
        {
            case '(' -> addToken(Type.PAREN_OPEN);
            case ')' -> addToken(Type.PAREN_CLOSE);
            case '{' -> addToken(Type.CURLY_OPEN);
            case '}' -> addToken(Type.CURLY_CLOSE);
            case '[' -> addToken(Type.SQUARE_OPEN);
            case ']' -> addToken(Type.SQUARE_CLOSE);
            case '+' -> addToken(Type.PLUS);
            case '-' -> addToken(Type.MINUS);
            case '*' -> addToken(Type.STAR);
            case '/' -> addToken(Type.SLASH);
            case ',' -> addToken(Type.COMMA);
            case '=' -> addToken(Type.EQUALS);
            case ';' -> addToken(Type.SEMICOLON);
            case '#' ->
            {
                while (peek() != '\n' && !atEnd())
                    advance();
            }
            case ' ', '\r', '\t' -> {}
            case '\n' -> line++;
            case '\"' -> string();
            default ->
            {
                if (Character.isDigit(c)) number();
                else if (Character.isAlphabetic(c)) identifier();
                else DSL.syntaxError(line, "Unexpected character.");
            }
        }
    }

    private void string() {
        while (peek() != '\"' && !atEnd())
        {
            if (peek() == '\n') line++;
            advance();
        }

        if (atEnd())
        {
            DSL.syntaxError(line, "Unterminated string.");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(Type.STRING, value);
    }

    private void identifier()
    {
        while (Character.isAlphabetic(peek()) || Character.isDigit(peek()))
            advance();
        
        addToken(IDENTIFIER);
    }

    private void number()
    {
        while (Character.isDigit(peek()))
            advance();

        if (peek() == '.' && Character.isDigit(peekNext()))
        {
            advance();

            while (Character.isDigit(peek()))
                advance();
        }

        addToken(Type.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peek()
    {
        if (atEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext()
    {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean atEnd() {
        return current >= source.length();
    }

    private char advance()
    {
        return source.charAt(current++);
    }

    private void addToken(Type type)
    {
        addToken(type, null);
    }

    private void addToken(Type type, Object literal)
    {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
    
}
