package gradient;

public class LogarithmicGradient implements NumericGradient{

	private final double start, base;
	
	public LogarithmicGradient(double start, double end, double percent) {
		this.start = start;
		this.base = Math.pow(end/start, 1 / percent);
	}
	
	public LogarithmicGradient(double start, double end) {
		this(start, end, 1);
	}

	public double getPercentFor(double value) {
		double aux = value / start;
		return Math.log(aux)/Math.log(base);
	}
	
	@Override
	public LogarithmicGradient scale(double scalar) {
		return new LogarithmicGradient(start * scalar, this.getEnd() * scalar);
	}
	
	@Override
	public LogarithmicGradient clone(){
		return new LogarithmicGradient(start, this.getEnd());
	}
	
	@Override 
	public Double valueAt(double percent) {
		return start * Math.pow(base, percent);
	}
	
}
