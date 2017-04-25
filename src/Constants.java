import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;

/**
 * @author kamontat
 * @version 1.0
 * @since Tue 25/Apr/2017 - 1:37 PM
 */
public class Constants {
	public static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
	
	public static final File TEST_FILE = Paths.get(".", "02all.nt").toFile();
	public static final String CHECK_STRING = "<http://www.w3.org/2001/XMLSchema#string>";
	
	public static BufferedReader getBuffer() {
		try {
			return new BufferedReader(new FileReader(Constants.TEST_FILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
