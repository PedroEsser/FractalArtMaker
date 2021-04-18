package rangeUtils;

public class LinearRange implements NumericRange{

	public final double start, range;

	public LinearRange(double start, double end) {
		this.start = start;
		this.range = end - start;
	}
	
	@Override
	public Double valueAt(double percent) {
		return start + range * percent;
	}
	
}
