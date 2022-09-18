package gradient;

public class LogarithmicGradient implements NumericGradient{

	private final LinearGradient range;
	
	public LogarithmicGradient(double start, double end) {
		range = new LinearGradient(Math.exp(start), Math.exp(end));
	}
	
	@Override
	public Double valueAt(double percent) {
		return Math.log(range.valueAt(percent));
	}

	@Override
	public double getPercentFor(double value) {
		return 0;
	}

}
