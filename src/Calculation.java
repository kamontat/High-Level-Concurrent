import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author kamontat
 * @version 1.0
 * @since Tue 25/Apr/2017 - 3:03 PM
 */
public interface Calculation {
	default String getClassName() {
		String s = this.getClass().getName();
		StringBuilder newString = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (Character.isUpperCase(c)) newString.append(" ").append(c);
			else if (c == '_') newString.append("");
			else newString.append(c);
		}
		return newString.toString().trim();
	}
	
	long getResult();
	
	void calculation() throws InterruptedException, ExecutionException;
	
	void setArray() throws IOException;
	
	void end(); // some class didn't implement
}
