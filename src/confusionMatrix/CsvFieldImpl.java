package confusionMatrix;

import confusionMatrix.CsvField;

public class CsvFieldImpl implements CsvField {
	private final int number;
	private final String value;
	
	CsvFieldImpl(String value, int number) {
		this.value = value;
		this.number = number;
	}
	
	@Override
	public int number() {
		return number;
	}
	
	@Override
	public String value() {
		return value;
	}
}
