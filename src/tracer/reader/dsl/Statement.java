package tracer.reader.dsl;

import java.util.List;

abstract class Statement
{
    interface Visitor<R>
    {
        R visitBlockDefinitionStatement(BlockDefinition statement);

        R visitPropertyDefinitionStatement(PropertyDefinition property);
    }

    abstract <R> R accept(Visitor<R> visitor);

    static class BlockDefinition extends Statement
    {
        final Token type;
        final Token name;
        final List<PropertyDefinition> properties;

        BlockDefinition(Token type, Token name, List<PropertyDefinition> properties)
        {
            this.type = type;
            this.name = name;
            this.properties = properties;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitBlockDefinitionStatement(this);
        }
    }

    static class PropertyDefinition extends Statement
    {
        final Token name;
        final Expression value;

        PropertyDefinition(Token name, Expression value)
        {
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitPropertyDefinitionStatement(this);
        }
    }
}
