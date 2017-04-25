import api.Stopwatch;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author kamontat
 * @version 1.0
 * @since Tue 25/Apr/2017 - 3:16 PM
 */
public class Main {
	public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
		Stopwatch watch = new Stopwatch();
		
		// Calculation calculation = new ConcurrentExecutorService_Main();
		// Calculation calculation = new ConcurrentForkJoin_Main();
		Calculation calculation = new ConcurrentHashMap_Main();
		
		calculation.setArray();
		watch.start();
		calculation.calculation();
		watch.stop();
		watch.stopSave();
		System.out.println(calculation.getClassName() + " @time: " + watch.getSecond() + " s");
		watch.startSaving();
		watch.reset();
		System.out.println(calculation.getResult());
		calculation.end();
	}
}