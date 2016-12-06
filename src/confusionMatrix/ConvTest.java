package confusionMatrix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import confusionMatrix.CsvField;
import confusionMatrix.CsvFile;
import confusionMatrix.CsvRecord;
import confusionMatrix.CsvParser;



public class ConvTest {
	private static final Logger logger = Logger.getLogger(ConvTest.class.getName());

	// Define class for Confusion Matrix
	private static class ConfMatrix {
		private int[][] matrix;
		private int numRecords=10;
		
		public int getNumRecords() {
			return numRecords;
		}
		
		private void initMatrix() {
			for(int i=0; i < numRecords; i++) {
				for(int j=0; j < numRecords; j++) {
					matrix[i][j] = 0;
				}
			}
		}
		
		public ConfMatrix(int n) {
			numRecords = n;
			matrix = new int[n][n];
			initMatrix();
		}
		
		public void updateMatrix(int i, int j) {
			matrix[i][j]++;
		}
		
		public int[][] returnMatrix() {
			return matrix;
		}
		
	}
	
	static int NUMINTENTS=-1;
	static String CSV_FILENAME_Test=null;
	static String CONFMATRIX_FILENAME=null;
	static String USER_ID;
	static String USER_PASS;
	static String BASIC_AUTH;
	static String CONV_URL;
	
	public static void getProperties(String fileName) {
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(fileName));
			NUMINTENTS = Integer.parseInt(properties.getProperty("numIntents"));
			CSV_FILENAME_Test = properties.getProperty("test_csv_filename");
			CONFMATRIX_FILENAME = properties.getProperty("confmatrix_filename");
			USER_ID = properties.getProperty("userid", "");
			USER_PASS = properties.getProperty("userpass", "");
			CONV_URL = properties.getProperty("conv_url");
			BASIC_AUTH = "Basic " + new String(Base64.encodeBase64((USER_ID+":"+USER_PASS).getBytes()));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			logger.log(Level.SEVERE,"no arguments specified, properties file name needed");
			System.exit(1);
		}
		String propertiesFileName = args[0];
		long start = System.currentTimeMillis();

		logger.log(Level.INFO,"Parsing properties file: " + propertiesFileName);
		getProperties(propertiesFileName);

		logger.log(Level.INFO,"Testing NLC using file: " + CSV_FILENAME_Test);
		
		String resultsCSVfile = CSV_FILENAME_Test.replace(".csv", "_results.csv");
		List<String> testCSVdata = readCSV(CSV_FILENAME_Test);
		
		Map<String,Integer> class2int = mapString2Int(testCSVdata);

		logger.log(Level.INFO,"Testing using data in file: " + CSV_FILENAME_Test);
		testClassifier(testCSVdata, resultsCSVfile,class2int); 
		logger.log(Level.INFO,"Output of Testing NLC is written to file: " + resultsCSVfile);

		long end = System.currentTimeMillis();
		logger.log(Level.INFO, "Total time: " + ((end - start) / 1000) + " seconds.");
	}	

	// Read CSV file and return a list of strings where each string is of the following
	// format: {"text": "San Francisco","classes": ["travel"]}
	public static List<String> readCSV(String fileName) {
		Charset charset = Charset.defaultCharset();
		List<String> classList = new ArrayList<String>();
		File in = new File(fileName);
		CsvFile file;
		try {
			file = new CsvParser().parse(in.toURI().toURL(), charset);
			CsvRecord headers = null;
			int tIndex = -1;
			int yesnoIndex = -1;
			int recordNumber = 0;
			String title = null;
			
			String yesnoValue = null;
			
			for (CsvRecord record : file) {
				recordNumber++;
				logger.log(Level.INFO,"processing record: " +recordNumber);
				if (headers == null) {
					headers = record;
					int fieldIndex = 0;
					for (CsvField field : headers.fields()) {
						if (field.value().toLowerCase().equals("text")) {
							tIndex = fieldIndex;
	                    } else if(field.value().toLowerCase().equals("class")) {
	                    	yesnoIndex = fieldIndex;
	                    }
						fieldIndex++;
					}
				} else {
					title = record.fields().get(tIndex).value();
					yesnoValue = record.fields().get(yesnoIndex).value();
					String row = "{\"text\": " + "\"" + title + "\"" + ", \"classes\": [\"" + yesnoValue + "\"]}";
					row = row.replaceAll("[\\x00-\\x09\\x10\\x11\\x12\\x14-\\x1F\\x7F]", "");
					classList.add(row);
				}
			}	
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return classList;
	}

	/*
	 * Create HTTP connection
	 */
	static public HttpURLConnection createConnection(String address, String method) throws IOException {
		URL url = new URL(address);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		if (method == "PUT" || method == "POST") {
		    connection.setDoOutput(true);
		}
		connection.setRequestMethod(method);
		connection.setRequestProperty("Authorization", BASIC_AUTH);
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Content-Type", "application/json");
		return connection;
	}
	
	// Test using classifier
	public static void testClassifier(List<String> testSet, String resultsFile, Map<String,Integer> class2int) {
		BufferedWriter out;
		ObjectMapper mapper = new ObjectMapper();
		JsonNode nd=null;
		String text = null;
		String cls = null;
		JsonNode CONV_cls = null;
		ConfMatrix confmat = new ConfMatrix(NUMINTENTS);
		
	    String classify_url = CONV_URL;
		logger.log(Level.INFO,"classify URL: " + classify_url);
		try {
			out = new BufferedWriter(new FileWriter(resultsFile));
			out.write("text,class,guess,correct,confidence\n");
			int qIndex=0;
			int guess=0;
			for(String testQ: testSet) {
				logger.log(Level.INFO,"question index: " +qIndex++);
				nd = mapper.readValue(testQ, JsonNode.class);
				text = nd.get("text").toString();
				cls = nd.get("classes").get(0).toString();
				
				CONV_cls = testClassifierQ(text);
				out.write(text + "," + cls + ",");
				if(!class2int.containsKey(cls)) {
					logger.log(Level.SEVERE,"class: " + cls + " not mapped to int");
				}
				int clsIndex = class2int.get(cls);
				for(int i=0; i<CONV_cls.size(); i++) {
					String ans = CONV_cls.get(i).get("intent").toString();
					if(ans.equals(cls)) {
						guess = 1;
					} else {
						guess = 0;
					}
					out.write(ans + "," + guess + "," + CONV_cls.get(i).get("confidence").toString() + ",");
				}
				String className = CONV_cls.get(0).get("intent").toString();
				if(!class2int.containsKey(className)) {
					logger.log(Level.SEVERE,"class: " + className + " not mapped to int");
				}
				int ansIndex = class2int.get(className);
				
				
				confmat.updateMatrix(clsIndex, ansIndex);
				out.write("\n");
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] classIndex = new String[class2int.size()];
		int index = -1;
		for(String key: class2int.keySet()) {
			index = class2int.get(key);
			classIndex[index] = key;
		}
		
		String confmatrixFile = CONFMATRIX_FILENAME;
		BufferedWriter out1;
		try {
			out1 = new BufferedWriter(new FileWriter(confmatrixFile));
			int[][] mat = confmat.returnMatrix();
			int i=-1;
			out1.write(",");
			for(i=0; i < confmat.getNumRecords(); i++) {
				out1.write(classIndex[i] + ",");
			}	
			out1.write("Recall" + "\n");
			
			int j=-1;
			for(i=0; i < confmat.getNumRecords(); i++) {
				int rowsum = 0;
				out1.write(classIndex[i] + ",");
				for(j=0; j < confmat.getNumRecords(); j++) {
					out1.write(mat[i][j] + ",");
					rowsum += mat[i][j];
				}
				double recall = (double)mat[i][i]/(double)rowsum;
				out1.write(recall + "\n");
			}
			out1.write("Precision"+",");
			for(j=0; j<confmat.getNumRecords(); j++) {
				int colsum=0;
				for(i=0; i <confmat.getNumRecords(); i++) {
					colsum += mat[i][j];
				}
				double precision = (double)mat[j][j]/(double)colsum;
				out1.write(precision+",");
			}
			
			out1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static JsonNode testClassifierQ(String testQ) {
		ObjectMapper mapper = new ObjectMapper();		
		String s = "{\"input\": {\"text\": " + testQ + "}}";
		JsonNode cls=null;
		try {
			JsonNode nd = mapper.readValue(s, JsonNode.class);
			logger.log(Level.INFO,"testing Conversation using node: " + nd);
			String classify_url = CONV_URL ;
			
			HttpURLConnection connection = createConnection(classify_url,"POST");
			OutputStream wr = connection.getOutputStream();
				wr.write(nd.toString().getBytes());
				wr.flush();
				wr.close();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED &&
					connection.getResponseCode() != HttpURLConnection.HTTP_OK && 
						connection.getResponseCode() != HttpURLConnection.HTTP_MOVED_TEMP) {
				throw new RuntimeException("Failed : HTTP error code : "
					+ connection.getResponseCode());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(connection.getInputStream())));
			
			JsonNode resp;
			String output;
			String response="";
			while ((output = br.readLine()) != null) {
				response += output;
			}
			
			resp = mapper.readValue(response, JsonNode.class);
			cls = resp.get("intents");
			connection.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cls;
	}
	
	
	public static Map<String,Integer> mapString2Int(List<String> testSet) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode nd=null;
		String clsName = null;
		Map<String,Integer> class2int = new HashMap<String,Integer>();
		
		for(String testQ: testSet) {
			System.out.println("testQ: " + testQ);
			try {
				nd = mapper.readValue(testQ, JsonNode.class);
				clsName = nd.get("classes").get(0).toString();
				if(class2int.containsKey(clsName)) {
					continue; 
				} else {
					class2int.put(clsName, class2int.size());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return class2int;
	}
}
