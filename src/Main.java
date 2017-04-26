import api.Stopwatch;

import java.io.FileNotFoundException;
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
		
		System.out.print("Test File Location: ");
		Constants.TEST_FILE = new java.io.File(s.nextLine());
		if (!Constants.TEST_FILE.isFile()) throw new FileNotFoundException();
		
		System.out.printf("|%-6s|%-12s|%-16s|%35s|\n", "Number", "Abbreviation", "Name", "Average Time (include reading file)");
		System.out.printf("|%-6d|%-12s|%-16s|%35s|\n", 1, "E", "Executor Service", "~29 seconds");
		System.out.printf("|%-6d|%-12s|%-16s|%35s|\n", 2, "F", "ForkJoin", "~15 seconds");
		System.out.printf("|%-6d|%-12s|%-16s|%35s|\n", 3, "H", "Hash Map", "~15 seconds");
		System.out.printf("|%-6d|%-12s|%-16s|%35s|\n", 4, "A", "Run All", ">1 minutes ");
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
		watch.start();
		calculation.setArray();
		// start calculation
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