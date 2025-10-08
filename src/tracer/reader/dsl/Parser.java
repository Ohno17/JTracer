package tracer.reader.dsl;

import static tracer.reader.dsl.Token.Type.*;

import java.util.ArrayList;
import java.util.List;

import tracer.reader.dsl.Token.Type;

/**
 * Parses a series of tokens into an AST
 * 
 * expression -> term -> factor ->
 */
public class Parser
{

    private static class ParseException extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens)
    {
        this.tokens = tokens;
    }

    public List<Statement> parse()
    {
        List<Statement> statements = new ArrayList<>();
        while (!atEnd())
        {
            statements.add(statement());
        }

        return statements;
    }

    private Statement statement()
    {
        return blockStatement();
    }

    private Statement blockStatement()
    {
        try
        {
            Token type = consume(IDENTIFIER, "Expect block type");

            Token name = null;
            if (check(IDENTIFIER))
            {
                consume(IDENTIFIER, "Expect block type");
                name = previous();
            }

            consume(CURLY_OPEN, "Expect '{' before block body");

            ArrayList<Statement.Property> properties = new ArrayList<>();
            while (!check(CURLY_CLOSE) && !atEnd())
            {
                properties.add(property());
            }

            consume(CURLY_CLOSE, "Expect '}' after block body.");

            return new Statement.Block(type, name, properties);
        }
        catch (ParseException exception)
        {
            synchronize();
            return null;
        }
    }

    private Statement.Property property()
    {
        Token name = consume(IDENTIFIER, "Expect parameter name");
        consume(EQUALS, "Expect '=' in property");
        Expression value = expression();

        consume(SEMICOLON, "Expect ';' after property");
        return new Statement.Property(name, value);
    }

    private Expression expression()
    {
        return term();
    }

    private Expression term()
    {
        Expression expression = factor();
        
        while (match(MINUS, PLUS))
        {
            Token operator = previous();
            Expression right = factor();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression factor()
    {
        Expression expression = unary();

        while (match(SLASH, STAR))
        {
            Token operator = previous();
            Expression right = unary();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression unary()
    {
        if (match(MINUS))
        {
            Token operator = previous();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }

        return primary();
    }

    private Expression primary()
    {
        if (match(NUMBER))
        {
            return new Expression.Literal(previous().literal);
        }

        if (match(IDENTIFIER))
        {
            return new Expression.Literal(previous().lexeme);
        }

        if (match(PAREN_OPEN))
        {
            Expression expression = expression();
            consume(PAREN_CLOSE, "Expect ')' after expression");
            return new Expression.Grouping(expression);
        }

        if (match(SQUARE_OPEN))
        {
            ArrayList<Expression> values = new ArrayList<>();
            values.add(expression());
            while (!check(SQUARE_CLOSE) && !atEnd())
            {
                consume(COMMA, "Expect ',' to seperate vector values");
                values.add(expression());
            }

            Token listToken = consume(SQUARE_CLOSE, "Expect ']' after vector");

            return new Expression.List(values, listToken);
        }

        throw error(peek(), "Expect expression.");
    }

    private boolean match(Type... types)
    {
        for (Type type : types)
        {
            if (check(type))
            {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(Type type)
    {
        if (atEnd()) return false;
        return peek().type == type;
    }

    private Token consume(Type type, String message)
    {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private Token advance()
    {
        if (!atEnd()) current++;
        return previous();
    }

    private boolean atEnd()
    {
        return peek().type == Type.EOF;
    }

    private Token peek()
    {
        return tokens.get(current);
    }

    private Token previous() 
    {
        return tokens.get(current - 1);
    }

    private ParseException error(Token token, String message)
    {
        DSL.error(token, message);
        return new ParseException();
    }

    private void synchronize() {
        advance();

        while (!atEnd()) {
            if (previous().type == SEMICOLON) return;
            advance();
        }
    }

}
