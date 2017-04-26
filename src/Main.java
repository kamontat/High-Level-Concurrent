import api.Stopwatch;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author kamontat
 * @version 1.0
 * @since Tue 25/Apr/2017 - 3:16 PM
 */
public class Main {
	public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
		Scanner s = new Scanner(System.in);
		System.out.println("1) Executor Service (E)");
		System.out.println("2) ForkJoin (F)");
		System.out.println("3) Hash Map (H)");
		System.out.println("4) All (A)");
		System.out.print("Which program(E|F|H|A): ");
		Character c = s.next().toLowerCase().charAt(0);
		switch (c) {
			case 'e':
				run(new ConcurrentExecutorService_Main());
				break;
			case 'f':
				run(new ConcurrentForkJoin_Main());
				break;
			case 'h':
				run(new ConcurrentHashMap_Main());
				break;
			case 'a':
				run(new ConcurrentExecutorService_Main());
				run(new ConcurrentForkJoin_Main());
				run(new ConcurrentHashMap_Main());
				break;
			default:
				System.out.println("Nothing");
				break;
		}
	}
	
	public static void run(Calculation calculation) throws IOException, ExecutionException, InterruptedException {
		Stopwatch watch = new Stopwatch(Stopwatch.Type.DETAIL, false, false);
		// read array
		calculation.setArray();
		// start calculation
		watch.start();
		calculation.calculation();
		// stop
		watch.stop();
		// print time
		System.out.println(calculation.getClassName());
		// print result
		System.out.println("result: " + calculation.getResult());
		// end calculation
		calculation.end();
		// print watch
		System.out.println("@Time:");
		System.out.println(watch.toString());
	}
}