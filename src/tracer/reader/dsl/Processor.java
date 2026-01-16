package tracer.reader.dsl;

import java.util.ArrayList;
import java.util.List;

import tracer.math.BaseVector3;
import tracer.math.Vector3;
import tracer.reader.ReaderDebugInfo;
import tracer.reader.property.Property;
import tracer.reader.property.PropertyValue;

public class Processor implements Expression.Visitor<Object>, Statement.Visitor<Void>
{
    private static ArrayList<Property> currentBlockProperties = new ArrayList<>();
    private ArrayList<DSLBlock> blocks = new ArrayList<>();

    public static class ProcessorException extends RuntimeException
    {
        final Token token;

        ProcessorException(Token token, String message)
        {
            super(message);
            this.token = token;
        }
    }

    public List<DSLBlock> process(List<Statement> statements)
    {
        currentBlockProperties = new ArrayList<>();

        try
        {
            for (Statement statement : statements)
            {
                statement.accept(this);
            }
        }
        catch(ProcessorException exception)
        {
            DSL.processingError(exception);
        }

        return blocks;
    }

    @Override
    public Object visitLiteralExpression(Expression.Literal expression)
    {
        return expression.value;
    }

    @Override
    public Object visitUnaryExpression(Expression.Unary expression)
    {
        Object right = expression.right.accept(this);
        checkNumberOperand(expression.operator, right);

        switch (expression.operator.type)
        {
            case MINUS:
                return -(double)right;
            default:
                return null;
        }
    }

    @Override
    public Object visitBinaryExpression(Expression.Binary expression)
    {
        Object left = expression.left.accept(this);
        Object right = expression.right.accept(this);

        switch (expression.operator.type)
        {
            case PLUS:
                if (left instanceof Double && right instanceof Double)
                    return (double)left + (double)right;

                if (right instanceof String && left instanceof String)
                    return (String)left + (String)right;

                if (right instanceof BaseVector3 && left instanceof BaseVector3)
                    return ((BaseVector3<?>)left).add((BaseVector3<?>)right);
                if (right instanceof BaseVector3 && left instanceof Double)
                    return ((BaseVector3<?>)right).add((Double)left);
                if (right instanceof BaseVector3 && left instanceof BaseVector3)
                    return ((BaseVector3<?>)left).add((Double)right);

                throw new ProcessorException(expression.operator, "Operands must be two numbers/vectors, or two strings.");
            case MINUS:
                if (left instanceof Double && right instanceof Double)
                    return (double)left - (double)right;

                if (right instanceof BaseVector3 && left instanceof BaseVector3)
                    return ((BaseVector3<?>)left).subtract((BaseVector3<?>)right);
                if (right instanceof BaseVector3 && left instanceof Double)
                    return ((BaseVector3<?>)right).subtract((Double)left);
                if (right instanceof Double && left instanceof BaseVector3)
                    throw new ProcessorException(expression.operator, "A vector cannot be subtracted from a number.");

                throw new ProcessorException(expression.operator, "Operands must be two numbers/vectors.");
            case SLASH:
                if (left instanceof Double && right instanceof Double)
                    return (double)left / (double)right;

                if (right instanceof BaseVector3 && left instanceof BaseVector3)
                    return ((BaseVector3<?>)left).divide((BaseVector3<?>)right);
                if (right instanceof BaseVector3 && left instanceof Double)
                    return ((BaseVector3<?>)right).divide((Double)left);
                if (right instanceof Double && left instanceof BaseVector3)
                    throw new ProcessorException(expression.operator, "A number cannot be divided by a vector.");

                throw new ProcessorException(expression.operator, "Operands must be two numbers/vectors.");
            case STAR:
                if (left instanceof Double && right instanceof Double)
                    return (double)left * (double)right;

                if (right instanceof BaseVector3 && left instanceof BaseVector3)
                    return ((BaseVector3<?>)left).multiply((BaseVector3<?>)right);
                if (right instanceof BaseVector3 && left instanceof Double)
                    return ((BaseVector3<?>)right).multiply((Double)left);
                if (right instanceof Double && left instanceof BaseVector3)
                    return ((BaseVector3<?>)left).multiply((Double)right);

                throw new ProcessorException(expression.operator, "Operands must be two numbers/vectors.");
            default:
                return null;
        }
    }

    @Override
    public Object visitGroupingExpression(Expression.Grouping expression)
    {
        return expression.expression.accept(this);
    }

    @Override
    public Object visitListExpression(Expression.List list)
    {
        if (list.values.size() != 3)
            throw new ProcessorException(list.listToken, "Vectors must be of length 3.");
        
        try
        {
            double x = (double) list.values.get(0).accept(this);
            double y = (double) list.values.get(1).accept(this);
            double z = (double) list.values.get(2).accept(this);

            return new Vector3(x, y, z);
        }
        catch (ClassCastException exception)
        {
            throw new ProcessorException(list.listToken, "Vector values must be numbers.");
        }
    }

    @Override
    public Void visitBlockDefinitionStatement(Statement.BlockDefinition statement)
    {
        for (Statement.PropertyDefinition property : statement.properties)
        {
            property.accept(this);
        }

        DSLBlock block;
        if (statement.name == null) block = new DSLBlock(statement.type.lexeme, currentBlockProperties);
        else block = new DSLBlock(statement.type.lexeme, statement.name.lexeme, currentBlockProperties);
        block.debugInfo = new ReaderDebugInfo(statement.type.line);

        blocks.add(block);
        currentBlockProperties = new ArrayList<>();
        return null;
    }

    @Override
    public Void visitPropertyDefinitionStatement(Statement.PropertyDefinition property)
    {
        Object value = property.value.accept(this);

        PropertyValue.Type type = null;
        if (value instanceof BaseVector3) type = PropertyValue.Type.VECTOR3;
        else if (value instanceof String) type = PropertyValue.Type.STRING;
        else type = PropertyValue.Type.NUMBER;

        Property domainProperty = new Property(property.name.lexeme, type, value);
        domainProperty.debugInfo = new ReaderDebugInfo(property.name.line);
        currentBlockProperties.add(domainProperty);
        return null;
    }

    private void checkNumberOperand(Token operator, Object operand)
    {
        if (operand instanceof Double)
            return;
        throw new ProcessorException(operator, "Operand must be a number.");
    }
    
}
