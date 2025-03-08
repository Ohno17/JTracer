package tracer.vector;

import java.util.Iterator;

/**
 * Represents a generic vector of size 3.
 */
public abstract class BaseVector3<T extends BaseVector3<?>> implements Iterable<Double>
{

    public final double x;
    public final double y;
    public final double z;

    public BaseVector3(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double magnitude()
    {
        return Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public T normalize()
    {
        return createNew(this.x / magnitude(), this.y / magnitude(), this.z / magnitude());
    }

    public T negative()
    {
        return createNew(-x, -y, -z);
    }

    public T add(BaseVector3<?> other)
    {
        return createNew(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public T subtract(BaseVector3<?> other)
    {
        return createNew(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public T multiply(Matrix3x3 other)
    {
        return createNew(this.dot(other.a), this.dot(other.b), this.dot(other.c));
    }

    public T multiply(BaseVector3<?> other)
    {
        return createNew(this.x * other.x, this.y * other.y, this.z * other.z);
    }

    public T multiply(double s)
    {
        return createNew(this.x * s, this.y * s, this.z * s);
    }

    public T divide(double s)
    {
        return createNew(this.x / s, this.y / s, this.z / s);
    }

    public T cross(BaseVector3<?> other)
    {
        return createNew(
            (this.y * other.z) - (this.z * other.y),
            (this.z * other.x) - (this.x * other.z),
            (this.x * other.y) - (this.y * other.x)
        );
    }

    public double dot(BaseVector3<?> other)
    {
        return (x * other.x) + (y * other.y) + (z * other.z);
    }

    public double getComponent(int index) throws IndexOutOfBoundsException
    {
        switch (index)
        {
            case 0: return this.x;
            case 1: return this.y;
            case 2: return this.z;
        }
        throw new IndexOutOfBoundsException("Vector3 only has valid indexes: 0, 1, 2. (Given: '" + index + "')");
    }

    @Override
    public Iterator<Double> iterator()
    {
        Iterator<Double> iterator = new Iterator<Double>()
        {

            private int index = 0;

            @Override
            public boolean hasNext()
            {
                return index < 3;
            }

            @Override
            public Double next()
            {
                return getComponent(index);
            }

        };
        return iterator;
    }

    protected abstract T createNew(double x, double y, double z);
    
}
