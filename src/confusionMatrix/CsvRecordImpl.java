package confusionMatrix;

import java.util.ArrayList;


import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import confusionMatrix.CsvField;
import confusionMatrix.CsvRecord;

public class CsvRecordImpl implements CsvRecord {
	private final List<CsvField> fields;
	private final long number;
	
	CsvRecordImpl(CSVRecord record) {
		List<CsvField> fields = new ArrayList<>(record.size());
		for (int i = 0; i < record.size(); i++) {
			fields.add(new CsvFieldImpl(record.get(i), i));
		}
		this.fields = fields;
		// The current underlying implementation (i.e. Apache Commons CSV)
		// starts the record numbering at one. Here we make the decision to
		// start it at zero to be consistent with the field numbering.
		this.number = record.getRecordNumber() - 1;
	}

	@Override
	public List<CsvField> fields() {
		return Collections.unmodifiableList(fields);
	}

	@Override
	public long number() {
		return number;
	}
}
