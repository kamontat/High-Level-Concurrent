package api;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * timer/stopwatch object that for measure elapsed time running in java.
 * <p>
 * Contains
 * <ol>
 * <li>parse timer api: {@link #MStoS(long)} or {@link #NStoS(long)}</li>
 * <li>Error message: {@link #getError()} or {@link #getErrors()}</li>
 * <li>toString: {@link #toString()} printing format</li>
 * <li>History: {@link History} class (default is <b>save</b>)</li>
 * </ol>
 * PS Before start new timer, don't forget to reset the old value. <br>
 * <p>
 * Warning: this class have some little calculate time, The average is about 30-1000 ns or more sometimes <b>BUT</b> at first calculation will take about <b>1000</b> time slower than other and will slower if print result to screen. <br>
 * <p>
 * From my calculation 2000 times,
 * The result is <br>
 * <b>Maximum</b> at {@code 140000 ns}, <br>
 * <b>Minimum</b> at {@code 36 ns}, and <br>
 * <b>Average</b> is around {@code 120 ns} <br>
 *
 * @author kamontat
 * @version 3.4
 * @since 9/22/2016 AD - 10:57 AM
 */
public class Stopwatch {
	private static long ERROR_ELAPSED = -1L;
	private static long DEFAULT_VALUE = 0L;
	
	private static String NEVER_START = "Never start!";
	private static String NEVER_STOP = "Never stop!";
	private static String DONT_STOP = "Don't stop previous running (call *reset* method first).";
	
	/**
	 * (helper) convert millisecond to second (using {@link TimeUnit}) method
	 *
	 * @param ms
	 * 		millisecond
	 * @return second
	 */
	public static long MStoS(long ms) {
		return TimeUnit.MILLISECONDS.toSeconds(ms);
	}
	
	/**
	 * (helper) convert nanosecond to second (using {@link TimeUnit}) method
	 *
	 * @param ns
	 * 		nanosecond
	 * @return second
	 */
	public static long NStoS(long ns) {
		return TimeUnit.NANOSECONDS.toSeconds(ns);
	}
	
	/**
	 * type of saving time for calculation.
	 */
	public enum Type {
		/**
		 * support until {@code nanosecond}
		 */
		DETAIL,
		/**
		 * support until {@code millisecond}
		 */
		NOT_DETAIL;
		
		@Override
		public String toString() {
			return toTitleCase(name().replace("_", " ").toLowerCase(Locale.ENGLISH));
		}
		
		private String toTitleCase(String input) {
			input = input.toLowerCase();
			char c = input.charAt(0);
			String s = String.valueOf(c);
			String f = s.toUpperCase();
			return f + input.substring(1);
		}
	}
	
	/**
	 * initial with new type, new save feature, new +ty print.
	 *
	 * @param type
	 * 		new type (DEFAULT = {@link Type#DETAIL}).
	 * @param onSave
	 * 		true if want to save data when reset (DEFAULT = {@code true}).
	 * @param jsonPrinting
	 * 		true if want to formatting print string as json format (DEFAULT = {@code true}).
	 */
	public Stopwatch(Type type, boolean onSave, boolean jsonPrinting) {
		this.type = type;
		start = DEFAULT_VALUE;
		stop = DEFAULT_VALUE;
		error = new ArrayList<>();
		save = onSave;
		if (save) {
			history = new History();
			history.setPrintBeautify(jsonPrinting);
		}
	}
	
	/**
	 * new type and is save? {@link Stopwatch#Stopwatch(Type, boolean, boolean)}.
	 *
	 * @param type
	 * 		new type (DEFAULT = {@link Type#DETAIL}).
	 * @param onSave
	 * 		true if want to save data when reset (DEFAULT = {@code true}).
	 */
	public Stopwatch(Type type, boolean onSave) {
		this(type, onSave, true);
	}
	
	/**
	 * new type {@link Stopwatch#Stopwatch(Type, boolean, boolean)}.
	 *
	 * @param type
	 * 		new type (DEFAULT = {@link Type#DETAIL}).
	 */
	public Stopwatch(Type type) {
		this(type, true, true);
	}
	
	/**
	 * call {@link Stopwatch#Stopwatch(Type, boolean, boolean)} with all parameter is default.
	 */
	public Stopwatch() {
		this(Type.DETAIL, true, true);
	}
	
	private Type type;
	private long start;
	private long stop;
	private boolean save;
	
	private List<String> error;
	
	/**
	 * History will auto saved, when user call
	 * <ul>
	 * <li>{@link #reset()}</li>
	 * <li>{@link #reset(Type)}</li>
	 * <li>{@link #toString()}</li>
	 * </ul>
	 */
	private History history;
	
	/**
	 * start clock! (may cause error) <br>
	 * Error: {@link #DONT_STOP}
	 */
	public void start() {
		if (stop != DEFAULT_VALUE) addError(DONT_STOP);
		
		removeError(NEVER_START);
		if (type == Type.DETAIL) start = System.nanoTime();
		else if (type == Type.NOT_DETAIL) start = System.currentTimeMillis();
	}
	
	/**
	 * stop clock! (may cause error) <br>
	 * Error: {@link #NEVER_START}
	 *
	 * @return elapsed in {@code second}
	 */
	public long stop() {
		if (start == DEFAULT_VALUE) {
			addError(NEVER_START);
			return ERROR_ELAPSED;
		}
		
		removeError(NEVER_STOP);
		if (type == Type.DETAIL) stop = System.nanoTime();
		else if (type == Type.NOT_DETAIL) stop = System.currentTimeMillis();
		return getSecond();
	}
	
	/**
	 * value return depend on type of watch, <br>
	 * To get in second unit, use {@link #getSecond()} instead <br>
	 * If didn't stop before this method also stop for you. (may cause error) <br>
	 * Error: {@link #NEVER_START}
	 *
	 * @return millisecond or nanosecond
	 */
	public long getElapsed() {
		if (!isStart()) addError(NEVER_START);
		else if (!isStop()) stop();
		if (!save && hasError()) return ERROR_ELAPSED;
		else if (save && hasError()) return getTimeByType(history.last());
		
		return stop - start;
	}
	
	public String getUnit() {
		return type == Type.DETAIL ? "ns": "ms";
	}
	
	/**
	 * get elapsed on second unit, <br>
	 * Learn more {@link #getElapsed()} method for error occurred
	 *
	 * @return elapsed time or error
	 */
	public long getSecond() {
		return type == Type.DETAIL ? NStoS(getElapsed()): MStoS(getElapsed());
	}
	
	private long getTimeByType(Time t) {
		return type == Type.DETAIL ? t.toNANO(): t.toMILLI();
	}
	
	private boolean isStart() {
		return start != DEFAULT_VALUE;
	}
	
	private boolean isStop() {
		return stop != DEFAULT_VALUE;
	}
	
	/* --------------- Error Management --------------- */
	
	private void addError(String errorMessage) {
		if (!error.contains(errorMessage)) error.add(errorMessage);
	}
	
	private void removeError(String errorMessage) {
		if (error.contains(errorMessage)) error.remove(errorMessage);
	}
	
	/**
	 * get error message to string format, <br>
	 * To get array of error use {@link #getErrors()} instead
	 *
	 * @return error message (separate by {@code &})
	 */
	public String getError() {
		if (!hasError()) return "";
		StringBuilder s = null;
		for (String e : error)
			if (s == null) s = new StringBuilder().append(e);
			else s.append(" & ").append(e);
		return s == null ? "": s.toString();
	}
	
	public String[] getErrors() {
		return error.toArray(new String[error.size()]);
	}
	
	/**
	 * check is error occurred
	 *
	 * @return true if have some error
	 */
	public boolean hasError() {
		return error.size() != 0;
	}
	
	private void clearError() {
		error.clear();
	}
	
	/**
	 * reset start stop and all error occurred. <br>
	 * With same type, To change new type use {@link #reset(Type)} instead
	 */
	public void reset() {
		reset(null);
	}
	
	/**
	 * reset current value, assign new type({@link Type}) of watch and save value to history (if save {@code on})
	 *
	 * @param type
	 * 		new type or {@code null} if want old type (can use {@link #reset()}) instead
	 */
	public void reset(Type type) {
		if (save) {
			if (this.type == Type.DETAIL) history.add(stop - start, TimeUnit.NANOSECONDS);
			else if (this.type == Type.NOT_DETAIL) history.add(stop - start, TimeUnit.MILLISECONDS);
		}
		if (type != null) this.type = type;
		start = DEFAULT_VALUE;
		stop = DEFAULT_VALUE;
		clearError();
	}
	
	public void reset(Type type, String s) {
	
	}
	
	/**
	 * clear everything of the object system
	 */
	public void resetAll() {
		reset();
		clearHistory();
	}
	
	/* --------------- Save Management --------------- */
	
	/**
	 * start save when user call {@link #reset()} method
	 */
	public void startSaving() {
		save = true;
	}
	
	/**
	 * stop save, For good design you should call {@link #clearHistory()} after call this method
	 */
	public void stopSave() {
		save = false;
	}
	
	/**
	 * get current history
	 *
	 * @return current {@link History}
	 */
	public History loadHistory() {
		try {
			return (History) history.clone();
		} catch (CloneNotSupportedException | ClassCastException e) {
			e.printStackTrace();
		}
		return new History();
	}
	
	/**
	 * clear all old history
	 */
	public void clearHistory() {
		history.clearAll();
	}
	
	/**
	 * print in format: <pre>{@code
	 * Type: detail|not detail
	 * Time: xxxx s(xxxxx ns|ms)
	 * }</pre>
	 * <br>
	 * or json format (include history too, <b>BUT</b> if don't want to print history call {@link #stopSave()} before print)
	 * And ths method already reset watch
	 *
	 * @return string
	 */
	@Override
	public String toString() {
		String out = "{\n\t\"Type\": \"" + type + "\",\n\t\"Time\": \"" + getSecond() + " s" + "(" + getElapsed() + (type == Type.DETAIL ? " ns": " ms") + ")\",\n\t";
		if (save) out += "\"History\": " + history.toString().replace("\n", "\n\t") + "\n}";
		else out += "\n}";
		
		if (!history.printBeautify) {
			out = out.replace("\"", "").replace("{", "").replace("}", "").replace("\t", "");
		}
		reset();
		return out;
	}
	
	/**
	 * History of this stopwatch, <b>DEFAULT</b> print is {@code json format printing}<br>
	 * <table style="width:100%">
	 * <tr>
	 * <th>Feature</th>
	 * <th>Method</th>
	 * </tr>
	 * <tr>
	 * <td>Calculation Average</th>
	 * <td>{@link History#getAverage()} | {@link History#getAverage(TimeUnit)}</th>
	 * </tr>
	 * <tr>
	 * <td>Calculation Minimum</th>
	 * <td>{@link History#getMinimum()}</th>
	 * </tr>
	 * <tr>
	 * <td>Calculation Maximum</th>
	 * <td>{@link History#getMaximum()}</th>
	 * </tr>
	 * </table>
	 */
	public class History implements Cloneable {
		private boolean printBeautify;
		private List<Time> history;
		
		private History() {
			this.history = new ArrayList<>();
			printBeautify = true;
		}
		
		/**
		 * printing with format pass {@code true}; otherwise, pass {@code false}
		 *
		 * @param printBeautify
		 * 		{@code true} if you want to print as json format
		 */
		public History setPrintBeautify(boolean printBeautify) {
			this.printBeautify = printBeautify;
			return this;
		}
		
		/**
		 * add new time
		 *
		 * @param time
		 * 		time to add
		 */
		private void add(Time time) {
			history.add(time);
		}
		
		/**
		 * add new time with raw value
		 *
		 * @param value
		 * 		time value
		 * @param u
		 * 		unit of the value
		 */
		private void add(long value, TimeUnit u) {
			add(new Time(value, u));
		}
		
		private void clearAll() {
			history.clear();
		}
		
		/**
		 * the result average time format will be {@link TimeUnit#NANOSECONDS}
		 *
		 * @return Average Time
		 */
		public Time getAverage() {
			return getAverage(TimeUnit.NANOSECONDS);
		}
		
		/**
		 * getting result average time on your passing format
		 *
		 * @param unit
		 * 		time unit of result
		 * @return Average Time
		 */
		public Time getAverage(TimeUnit unit) {
			double value = history.stream().mapToLong(Time::toNANO).average().orElse(0);
			return new Time(value, unit);
		}
		
		/**
		 * get minimum of all saving time
		 *
		 * @return minimum time
		 */
		public Time getMinimum() {
			return history.stream().min((Comparator.comparingLong(Time::toNANO))).orElse(Time.DEFAULT);
		}
		
		/**
		 * get maximum of all saving time
		 *
		 * @return maximum time
		 */
		public Time getMaximum() {
			return history.stream().max((Comparator.comparingLong(Time::toNANO))).orElse(Time.DEFAULT);
		}
		
		@Override
		protected Object clone() throws CloneNotSupportedException {
			return super.clone();
		}
		
		@Override
		public String toString() {
			String s = Arrays.toString(history.toArray(new Time[history.size()]));
			if (printBeautify) return s.replace("[", "[\n\t").replace(",", ",\n\t").replace("]", "\n]");
			else return s.replace("\"", "");
		}
		
		/**
		 * get last saving time
		 *
		 * @return Time that save latest
		 */
		private Time last() {
			return history.get(history.size() - 1);
		}
	}
	
	/**
	 * Time class for saving elapsed times.
	 */
	public static class Time {
		private static Time DEFAULT = new Time(0, TimeUnit.NANOSECONDS);
		
		private double value;
		private TimeUnit unit;
		
		private Time(long value, TimeUnit unit) {
			this.value = value;
			this.unit = unit;
		}
		
		private Time(double value, TimeUnit unit) {
			this.value = value;
			this.unit = unit;
		}
		
		/**
		 * get value {@link Long} format
		 *
		 * @return value
		 */
		public long getValue() {
			return Math.round(value);
		}
		
		/**
		 * get value {@link Double} format
		 *
		 * @return value
		 */
		public double getValueD() {
			return value;
		}
		
		/**
		 * get time unit
		 *
		 * @return unit of the time
		 */
		public TimeUnit getUnit() {
			return unit;
		}
		
		/**
		 * convert every value unit of this class to nanosecond
		 *
		 * @return nanosecond
		 */
		public long toNANO() {
			return unit.toNanos(getValue());
		}
		
		/**
		 * convert every value unit of this class to millisecond
		 *
		 * @return millisecond
		 */
		public long toMILLI() {
			return unit.toMillis(getValue());
		}
		
		@Override
		public String toString() {
			return "\"" + (Math.ceil(value % 1) != 0 ? String.format("%.2f", getValueD()): String.format("%d", getValue())) + " " + unit.name().toLowerCase(Locale.ENGLISH) + "\"";
		}
	}
}