package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/*
 * Helper class to retrieve properties
 *
 * NOTE: This code was borrowed from a Stack Overflow thread. Some modifications have been made.
 * SOURCE: https://stackoverflow.com/questions/36723839/get-int-float-boolean-and-string-from-properties/36724160
 */
public class PropertiesLoaderBorrowedCode
{
    private Properties props = new Properties();

    public PropertiesLoaderBorrowedCode(String file) {
	try (FileInputStream propStream = new FileInputStream(file)) {
	    props.load(propStream);
	} catch (IOException e) {
	    System.err.println("Could not locate properties file" + e);
	}
    }

    /**
     * Retrieve a value from the prop file.
     * @param name the name of the property.
     * @param type the data type.
     * @return the property value
     */
    public Object getValue(String name, Class<?> type) {
	String value = props.getProperty(name);
	if (value == null)
	    throw new IllegalArgumentException("Missing configuration value: " + name);
	// Chain of ininstance checks: Seems appropriate enough in this context given that most comparisons are between primitive data types.
	if (type == String.class)
	    return value;
	if (type == boolean.class)
	    return Boolean.parseBoolean(value);
	if (type == int.class)
	    return Integer.parseInt(value);
	if (type == float.class)
	    return Float.parseFloat(value);
	if (type == double.class) {
	    return Double.parseDouble(value);
	}
	throw new IllegalArgumentException("Unknown configuration value type: " + type.getName());
    }
}
