package confusionMatrix;

import java.io.IOException;



import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import confusionMatrix.CsvFile;

/**
 * A CSV parser for accessing the contents of a CSV file as a stream.
 * <p/>
 * The CSV file must comply with RFC 4180. The following general format 
 * description is taken directly from the RFC.
 * <ol>
 *   <li>Each record is located on a separate line, delimited by a line break 
 *   (CRLF).</li>
 *   <li>The last record in the file may or may not have an ending line break.</li>
 *   <li>There maybe an optional header line appearing as the first line of the 
 *   file with the same format as normal record lines. This header will contain 
 *   names corresponding to the fields in the file and should contain the same 
 *   number of fields as the records in the rest of the file (the presence or 
 *   absence of the header line should be indicated via the optional "header" 
 *   parameter of this MIME type).</li>
 *   <li>Within the header and each record, there may be one or more fields, 
 *   separated by commas. Each line should contain the same number of fields 
 *   throughout the file. Spaces are considered part of a field and should not 
 *   be ignored.  The last field in the record must not be followed by a comma.</li>
 *   <li>Each field may or may not be enclosed in double quotes (however some 
 *   programs, such as Microsoft Excel, do not use double quotes at all). 
 *   If fields are not enclosed with double quotes, then double quotes may not 
 *   appear inside the fields.</li>
 *   <li>Fields containing line breaks (CRLF), double quotes, and commas should 
 *   be enclosed in double-quotes.</li>
 *   <li>If double-quotes are used to enclose fields, then a double-quote 
 *   appearing inside a field must be escaped by preceding it with another 
 *   double quote.</li>
 * </ol>
 * The following code provides a basic usage example.
 * <pre>
 * // Parse the source CSV file.
 * CsvFile file = new CsvParser().parse(new File("sample.csv").toURI().toURL(), Charset.defaultCharset());
 * // Retrieve the headers.
 * CsvRecord headers = file.iterator().next();
 * // Iterate over the records and associate each one to its header.
 * for (CsvRecord record : file) {
 *   for (CsvField field : record.fields()) {
 *     // Print the header.
 *     System.out.print(headers.fields().get(field.number()).value());
 *     System.out.print(" : ");
 *     // Print the value.
 *     System.out.println(field.value());
 *   }
 * }
 *</pre>
 */
public class CsvParser {
	/**
	 * Parse the provided CSV file.
	 * <p/>
	 * It is assumed that the source CSV complies with RFC 4180.
	 * 
	 * @param url - The source CSV file.
	 * @param charset - The character set used by the source CSV file.
	 * @return The parsed CSV file.
	 * @throws IllegalArgumentException If either <code>url</code> or <code>
	 *         charset</code> is null.
	 * @throws IOException If an error occurs during parsing.
	 */
	public CsvFile parse(URL url, Charset charset) throws IOException {
		return new CsvFileImpl(CSVParser.parse(url, charset, CSVFormat.RFC4180));
	}
}
