package confusionMatrix;

/**
 * A field, or column value, in the CSV file.
 */
public interface CsvField {
	/**
	 * Returns the column position of this field, starting at zero.
	 * 
	 * @return The column position of this field.
	 */
	int number();
	
	/**
	 * Returns the value of this field.
	 * 
	 * @return The value of this field.
	 */
	String value();
}
