package gradient;

public class ExponentialGradient implements NumericGradient{

	private final double start, base;
	
	public ExponentialGradient(double start, double end, double percent) {
		this.start = start;
		this.base = Math.pow(end/start, 1 / percent);
	}
	
	public ExponentialGradient(double start, double end) {
		this(start, end, 1);
	}

	@Override
	public double getPercentFor(double value) {
		double aux = value / start;
		return Math.log(aux)/Math.log(base);
	}
	
	@Override
	public ExponentialGradient scale(double scalar) {
		return new ExponentialGradient(start * scalar, this.getEnd() * scalar);
	}
	
	@Override
	public ExponentialGradient clone(){
		return new ExponentialGradient(start, this.getEnd());
	}
	
	@Override 
	public Double valueAt(double percent) {
		return start * Math.pow(base, percent);
	}
	
}
