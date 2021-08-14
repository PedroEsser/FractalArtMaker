package gradient;

public interface NumericGradient extends Gradient<Double>{

	public default NumericGradient scale(double scalar) {
		return percent -> this.valueAt(percent) * scalar;
	}
	
}
