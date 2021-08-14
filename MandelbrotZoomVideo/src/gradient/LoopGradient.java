package gradient;

public class LoopGradient<T> implements Gradient<T>{

	private Gradient<T> range;
	private final Gradient<Double> numericRange;
	
	public LoopGradient(Gradient<T> range, Gradient<Double> numericRange) {
		this.range = range;
		this.numericRange = numericRange;
	}
	
	public LoopGradient(Gradient<T> range, double start, double end) {
		this(range, new LinearGradient(start, end));
	}
	
	public LoopGradient(Gradient<T> range, double loopCount) {
		this(range, 0, loopCount);
	}
	
	public LoopGradient(Gradient<T> range) {
		this(range, 2);
	}

	@Override
	public T valueAt(double percent) {
		return range.valueAt(GradientUtils.loop(numericRange.valueAt(percent)));
	}

}
