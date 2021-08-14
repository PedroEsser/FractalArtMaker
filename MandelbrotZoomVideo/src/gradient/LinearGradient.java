package gradient;

public class LinearGradient implements NumericGradient{

	public final double start, range;

	public LinearGradient(double start, double end) {
		this.start = start;
		this.range = end - start;
	}
	
	@Override
	public LinearGradient scale(double scalar) {
		return new LinearGradient(start * scalar, this.getEnd() * scalar);
	}
	
	public LinearGradient offsetValue(double offset) {
		return new LinearGradient(start + offset, this.getEnd() + offset);
	}
	
	@Override
	public Double valueAt(double percent) {
		return start + range * percent;
	}
	
	@Override
	public LinearGradient clone(){
		return new LinearGradient(start, this.getEnd());
	}
	
}
