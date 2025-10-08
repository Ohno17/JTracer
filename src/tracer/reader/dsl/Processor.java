package tracer.reader.dsl;

import java.util.ArrayList;
import java.util.List;

import tracer.reader.dsl.Expression.*;
import tracer.reader.dsl.Statement.*;
import tracer.reader.property.PropertyValue;
import tracer.vector.BaseVector3;
import tracer.vector.Vector3;

public class Processor implements Expression.Visitor<Object>, Statement.Visitor<Void>
{

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
            DSL.runtimeError(exception);
        }

        return blocks;
    }

    @Override
    public Object visitLiteralExpression(Literal expression)
    {
        return expression.value;
    }

    @Override
    public Object visitUnaryExpression(Unary expression)
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
    public Object visitBinaryExpression(Binary expression)
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
    public Object visitGroupingExpression(Grouping expression)
    {
        return expression.expression.accept(this);
    }

    @Override
    public Object visitListExpression(tracer.reader.dsl.Expression.List list)
    {
        if (list.values.size() != 3)
            throw new ProcessorException(list.listToken, "Vectors must be of length 3.");
        
        double x = (double) list.values.get(0).accept(this);
        double y = (double) list.values.get(1).accept(this);
        double z = (double) list.values.get(2).accept(this);
        return new Vector3(x, y, z);
    }

    @Override
    public Void visitBlockStatement(Block statement)
    {
        ArrayList<tracer.reader.property.Property> realProperties = new ArrayList<>();
        for (Statement.Property property : statement.properties)
        {
            property.accept(this);

            Object value = property.value.accept(this);

            PropertyValue.Type type = null;
            if (value instanceof BaseVector3) type = PropertyValue.Type.VECTOR3;
            else if (value instanceof String) type = PropertyValue.Type.STRING;
            else type = PropertyValue.Type.NUMBER;

            realProperties.add(new tracer.reader.property.Property(property.name.lexeme, type, value));
        }

        if (statement.name == null) blocks.add(new DSLBlock(statement.type.lexeme, realProperties));
        else blocks.add(new DSLBlock(statement.type.lexeme, statement.name.lexeme, realProperties));
        return null;
    }

    @Override
    public Void visitPropertyStatement(Property property)
    {
        // TODO: Validate property types and names here
        // System.out.println(property.name.lexeme + " = " + stringify(property.value.accept(this)));
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
