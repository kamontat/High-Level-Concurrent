import annotation.NotImplemented;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

// using concurrent HashMap
public class ConcurrentHashMap_Main implements Calculation {
	private static ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();
	private long result;
	
	@Override
	public long getResult() {
		return result;
	}
	
	@Override
	public void calculation() throws InterruptedException, ExecutionException {
		result = map.reduceValuesToLong(10, value -> value.contains(Constants.CHECK_STRING) ? 1L: 0L, 0, (left, right) -> left + right);
	}
	
	@Override
	public void setArray() throws IOException {
		BufferedReader reader = Constants.getBuffer();
		assert reader != null;
		
		String line = "";
		int i = 0;
		while ((line = reader.readLine()) != null) {
			map.put(i++, line);
		}
	}
	
	@Override
	@NotImplemented
	public void end() {
	}
}