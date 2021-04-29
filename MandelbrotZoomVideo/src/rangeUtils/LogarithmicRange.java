package rangeUtils;

public class LogarithmicRange implements NumericRange{

	private final double start, base;
	
	public LogarithmicRange(double start, double end) {
		this.start = start;
		this.base = end/start;
	}

	@Override
	public Double valueAt(double percent) {
		return start * Math.pow(base, percent);
	}

}
