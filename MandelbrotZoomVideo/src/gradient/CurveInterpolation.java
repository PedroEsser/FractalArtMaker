package gradient;

public class CurveInterpolation implements NumericGradient{
	
	private double start, range, curvature;
	
	public CurveInterpolation(double start, double end, double curvature) {
		super();
		this.start = start;
		this.range = end - start;
		this.curvature = curvature;
	}

	@Override
	public Double valueAt(double percent) {
		return start + Math.pow(percent, Math.pow(2, curvature)) * range;
	}

	@Override
	public double getPercentFor(double value) {
		return 0;
	}
	
	public double getCurvature() {
		return curvature;
	}

}
