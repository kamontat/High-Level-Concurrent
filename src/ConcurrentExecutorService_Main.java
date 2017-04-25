import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class ConcurrentExecutorService_Main implements Calculation {
	ExecutorService executor;
	Collection<Callable<Integer>> callables;
	private long result;
	
	public ConcurrentExecutorService_Main() {
		executor = Executors.newFixedThreadPool(Constants.PROCESSORS + 1);
		this.callables = new ArrayList<>();
	}
	
	@Override
	public long getResult() {
		return result;
	}
	
	@Override
	public void calculation() throws InterruptedException, ExecutionException {
		List<Future<Integer>> futureResultList = executor.invokeAll(callables);
		for (Future<Integer> f : futureResultList) {
			result += f.get();
		}
	}
	
	@Override
	public void setArray() throws IOException {
		BufferedReader reader = Constants.getBuffer();
		assert reader != null;
		String line = "";
		while ((line = reader.readLine()) != null) {
			callables.add(new CheckingCallable(line));
		}
	}
	
	@Override
	public void end() {
		executor.shutdown();
	}
}

class CheckingCallable implements Callable<Integer> {
	boolean target;
	
	public CheckingCallable(String line) {
		target = line.contains(Constants.CHECK_STRING);
	}
	
	@Override
	public Integer call() throws NumberFormatException, IOException {
		return target ? 1: 0;
	}
}