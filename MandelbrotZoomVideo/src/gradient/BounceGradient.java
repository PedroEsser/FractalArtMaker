package gradient;

public class BounceGradient<T> implements Gradient<T>{

	private final Gradient<Double> numericRange;
	private final Gradient<T> range;
	
	public BounceGradient(Gradient<T> range, Gradient<Double> numericRange) {
		this.range = range;
		this.numericRange = numericRange;
	}
	
	public BounceGradient(Gradient<T> range, double start, double end) {
		this(range, new LinearGradient(start, end));
	}
	
	public BounceGradient(Gradient<T> range, double loopCount) {
		this(range, 0, loopCount);
	}
	
	public BounceGradient(Gradient<T> range) {
		this(range, 2);
	}
	
	@Override
	public T valueAt(double percent) {
		double aux = numericRange.valueAt(percent);
		return this.range.valueAt(GradientUtils.bounce(aux));
	}
	
}
