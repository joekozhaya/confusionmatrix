package confusionMatrix;

import java.util.List;

/**
 * A record, or row, in the CSV file.
 */
public interface CsvRecord {
	/**
	 * Returns the fields of this record in order.
	 * 
	 * @return The fields of this record.
	 */
	List<CsvField> fields();
	
	/**
	 * Returns the row position of this record, starting at zero.
	 * 
	 * @return The row position of this record.
	 */
	long number();
}
