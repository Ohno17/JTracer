package tracer.reader.dsl;

import java.util.List;

abstract class Statement
{
    interface Visitor<R>
    {
        R visitBlockStatement(Block statement);

        R visitPropertyStatement(Property property);
    }

    abstract <R> R accept(Visitor<R> visitor);

    static class Block extends Statement
    {
        final Token type;
        final Token name;
        final List<Property> properties;

        Block(Token type, Token name, List<Property> properties)
        {
            this.type = type;
            this.name = name;
            this.properties = properties;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitBlockStatement(this);
        }
    }

    static class Property extends Statement
    {
        final Token name;
        final Expression value;

        Property(Token name, Expression value)
        {
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitPropertyStatement(this);
        }
    }
}
