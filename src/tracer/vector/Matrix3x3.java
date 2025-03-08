package tracer.vector;

import java.util.Iterator;

/**
 * Represents a 3x3 matrix.
 */
public class Matrix3x3 implements Iterable<Vector3>
{

    public Vector3 a; // Row 1
    public Vector3 b; // Row 2
    public Vector3 c; // Row 3

    /**
     * Represents the orientation of the given vectors that will be placed in the matrix.
     */
    public static enum Orientation
    {
        ROWS, COLUMNS
    }

    public Matrix3x3()
    {
        this.a = new Vector3(0);
        this.b = new Vector3(0);
        this.c = new Vector3(0);
    }

    public Matrix3x3(Vector3 a, Vector3 b, Vector3 c, Orientation orientation)
    {
        switch (orientation)
        {
            case COLUMNS:
                this.a = new Vector3(a.x, b.x, c.x);
                this.b = new Vector3(a.y, b.y, c.y);
                this.c = new Vector3(a.z, b.z, c.z);
                break;
            default:
            case ROWS:
                this.a = a;
                this.b = b;
                this.c = c;
                break;
        }
    }

    public Vector3 getComponent(int index) throws IndexOutOfBoundsException
    {
        switch (index)
        {
            case 0: return this.a;
            case 1: return this.b;
            case 2: return this.c;
        }
        throw new IndexOutOfBoundsException("Matrix3x3 only has valid indexes: 0, 1, 2. (Given: '" + index + "')");
    }

    @Override
    public Iterator<Vector3> iterator()
    {
        Iterator<Vector3> iterator = new Iterator<Vector3>()
        {

            private int index = 0;

            @Override
            public boolean hasNext()
            {
                return index < 3;
            }

            @Override
            public Vector3 next()
            {
                return getComponent(index);
            }

        };
        return iterator;
    }

    @Override
    public String toString()
    {
        String[][] matrixStrings = new String[3][3];
        int[] columnMaxLengths = new int[3];

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                matrixStrings[i][j] = Double.toString(this.getComponent(i).getComponent(j));
            }
        }

        for (int i = 0; i < 3; i++)
        {
            columnMaxLengths[i] = Math.max(matrixStrings[0][i].length(), Math.max(matrixStrings[1][i].length(), matrixStrings[2][i].length()));
        }

        StringBuilder output = new StringBuilder();

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                output.append(String.format("%-" + (columnMaxLengths[j] + 1) + "s", matrixStrings[i][j]));
            }
            if (i != 2) output.append('\n');
        }
        
        return output.toString();
    }
    
}
