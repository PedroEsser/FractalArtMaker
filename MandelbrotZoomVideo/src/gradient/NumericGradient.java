package gradient;

public interface NumericGradient extends Gradient<Double>{
	
	public double getPercentFor(double value);
	
	default Gradient<Double> scale(double scalar) {
		return p -> valueAt(p) * scalar;
	}
	
}
