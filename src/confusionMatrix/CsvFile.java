package confusionMatrix;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

/**
 * A representation of a streamed CSV file.
 * <p/>
 * The contents of the CSV file are presented as a stream rather than in memory.
 * Records must be accessed one at a time, {@link #iterator() iteratively} and 
 * in order. CSV files should be {@link #close() closed} when finished.
 *
 */
public interface CsvFile extends Closeable, Iterable<CsvRecord> {
	/**
     * Closes this CSV file and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * @throws IOException If this CSV file could not be closed for any reason.
     */
	@Override
    public void close() throws IOException;
    
	/**
     * Returns an iterator over the set of records within this CSV file.
     * <p/>
     * Subsequent calls will not return a new iterator pointing to the beginning
     * of the stream. All iterators for this CSV file will point to the next
     * record to be processed from the stream.
     *
     * @return An iterator over the set of records within this CSV file.
     */
	@Override
    Iterator<CsvRecord> iterator();
}
