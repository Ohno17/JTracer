package tracer.reader.dsl;

abstract class Expression {
    interface Visitor<R>
    {
        R visitLiteralExpression(Literal expression);
        R visitUnaryExpression(Unary expression);
        R visitBinaryExpression(Binary expression);
        R visitGroupingExpression(Grouping expression);
        R visitListExpression(List list);
    }

    abstract <R> R accept(Visitor<R> visitor);

    static class Literal extends Expression
    {
        final Object value;

        Literal(Object value)
        {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitLiteralExpression(this);
        }
    }

    static class Unary extends Expression
    {
        Unary(Token operator, Expression right)
        {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitUnaryExpression(this);
        }

        final Token operator;
        final Expression right;
    }

    static class Binary extends Expression
    {
        Binary(Expression left, Token operator, Expression right)
        {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitBinaryExpression(this);
        }

        final Expression left;
        final Token operator;
        final Expression right;
    }

    static class Grouping extends Expression
    {
        final Expression expression;

        Grouping(Expression expression)
        {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitGroupingExpression(this);
        }
    }

    static class List extends Expression
    {
        final java.util.List<Expression> values;
        final Token listToken;

        List(java.util.List<Expression> values, Token listToken)
        {
            this.values = values;
            this.listToken = listToken;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitListExpression(this);
        }
    }

}