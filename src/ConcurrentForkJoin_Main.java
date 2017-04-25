import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ConcurrentForkJoin_Main implements Calculation {
	private ForkJoinPool instance;
	private EvaluateSumForkJoinTask task;
	private long result = 0;
	
	public ConcurrentForkJoin_Main() {
		instance = new ForkJoinPool(Constants.PROCESSORS);
	}
	
	@Override
	public long getResult() {
		return result;
	}
	
	@Override
	public void calculation() throws InterruptedException, ExecutionException {
		instance.invoke(task);
		result = task.getResult();
	}
	
	@Override
	public void setArray() throws IOException {
		BufferedReader in = Constants.getBuffer();
		assert in != null;
		
		String line;
		List<String> content = new ArrayList<>();
		
		while ((line = in.readLine()) != null) content.add(line);
		task = new EvaluateSumForkJoinTask(new EvaluateCalculateString(content));
	}
	
	@Override
	public void end() {
		instance.shutdown();
	}
}

class EvaluateSumForkJoinTask extends RecursiveTask<Long> {
	private EvaluateCalculateString string;
	private long result;
	
	public EvaluateSumForkJoinTask(EvaluateCalculateString string) {
		this.string = string;
	}
	
	@Override
	protected Long compute() {
		if (string.isLast() || string.nothing()) {
			result = string.isMatch() ? 1: 0;
		} else {
			try {
				EvaluateSumForkJoinTask worker1 = new EvaluateSumForkJoinTask(string.halfLeft());
				EvaluateSumForkJoinTask worker2 = new EvaluateSumForkJoinTask(string.halfRight());
				worker1.fork();
				result = worker2.compute() + worker1.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public long getResult() {
		return result;
	}
}

class EvaluateCalculateString {
	private static final EvaluateCalculateString DEFAULT = new EvaluateCalculateString(new ArrayList<>());
	private List<String> content;
	private int size;
	
	public EvaluateCalculateString(List<String> content) {
		this.content = content;
		size = content.size();
		
	}
	
	public List<String> getContent() {
		return content;
	}
	
	public boolean isMatch() {
		return !nothing() && content.get(0).contains(Constants.CHECK_STRING);
	}
	
	public boolean isLast() {
		return size == 1;
	}
	
	public boolean nothing() {
		return size == 0;
	}
	
	public long getSize() {
		return size;
	}
	
	public EvaluateCalculateString halfLeft() {
		return new EvaluateCalculateString(content.subList(0, size / 2));
	}
	
	public EvaluateCalculateString halfRight() {
		return new EvaluateCalculateString(content.subList(size / 2, size));
	}
}
