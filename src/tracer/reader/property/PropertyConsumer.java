package tracer.reader.property;

import java.util.List;

// Represents an object that can be constructed from a list of properties.
public interface PropertyConsumer
{
    void apply(List<Property> properties) throws InvalidPropertyException;
}
