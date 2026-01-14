package tracer.reader.dsl;

import tracer.reader.dsl.Expression.*;

/**
 * Class to visualize a scene description's AST
 */
class ASTPrinter implements Expression.Visitor<String>
{
    public String print(Expression expression)
    {
        return expression.accept(this);
    }

    @Override
    public String visitLiteralExpression(Literal expression)
    {
        return expression.value.toString();
    }

    @Override
    public String visitUnaryExpression(Unary expression)
    {
        return parenthesize(expression.operator.lexeme, expression.right);
    }

    @Override
    public String visitBinaryExpression(Binary expression)
    {
        return parenthesize(expression.operator.lexeme, expression.left, expression.right);
    }

    @Override
    public String visitGroupingExpression(Grouping expression)
    {
        return parenthesize("group", expression.expression);
    }

    @Override
    public String visitListExpression(List list)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("[");
        for (Expression expression : list.values)
        {
            builder.append(" ");
            builder.append(expression.accept(this));
        }
        builder.append("]");

        return builder.toString();
    }

    private String parenthesize(String name, Expression... expressions)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expression expression : expressions)
        {
            builder.append(" ");
            builder.append(expression.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

}
