package confusionMatrix;

import java.io.IOException;


import java.util.Iterator;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import confusionMatrix.CsvFile;
import confusionMatrix.CsvRecord;

public class CsvFileImpl implements CsvFile {
	private final CSVParser parser;
	
	CsvFileImpl(CSVParser parser) {
		this.parser = parser;
	}
	
	@Override
	public void close() throws IOException {
		parser.close();
	}
	
	@Override
	public Iterator<CsvRecord> iterator() {
		return new Iterator<CsvRecord>() {
			private final Iterator<CSVRecord> iterator = parser.iterator();
			
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public CsvRecord next() {
				return new CsvRecordImpl(iterator.next());
			}

			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}
}
