package tracer.reader.dsl;

import java.util.ArrayList;
import java.util.List;

import tracer.reader.ReaderDebugInfo;
import tracer.reader.property.Property;
import tracer.reader.property.PropertyValue;
import tracer.vector.BaseVector3;
import tracer.vector.Vector3;

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
        checkNumberOperands(expression.operator, left, right);

        switch (expression.operator.type)
        {
            case PLUS:
                return (double)left + (double)right;
            case MINUS:
                return (double)left - (double)right;
            case SLASH:
                return (double)left / (double)right;
            case STAR:
                return (double)left * (double)right;
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

    private void checkNumberOperands(Token operator, Object left, Object right)
    {
        if (left instanceof Double && right instanceof Double)
            return;
        throw new ProcessorException(operator, "Operands must be numbers.");
    }
    
}
