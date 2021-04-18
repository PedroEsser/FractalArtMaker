package logic;

import rangeUtils.LogarithmicRange;
import rangeUtils.NumericRange;
import rangeUtils.Range;

public class MandelbrotZoom implements Range<MandelbrotFrame>{

	public static final NumericRange DEFAULT_DELTA_RANGE = new LogarithmicRange(0.005, 1E-14);
	private int width, height;
	private final Complex center;
	private final NumericRange deltaRange;
	
	public MandelbrotZoom(Complex center, int width, int height, NumericRange deltaRange) {
		this.width = width;
		this.height = height;
		this.center = center;
		this.deltaRange = deltaRange;
	}
	
	public MandelbrotZoom(Complex center, int width, int height, double initialDelta, double finalDelta) {
		this(center, width, height, new LogarithmicRange(initialDelta, finalDelta));
	}
	
	public MandelbrotZoom(Complex center, int width, int height, double finalDelta) {
		this(center, width, height, DEFAULT_DELTA_RANGE.valueAt(0), finalDelta);
	}
	
	public MandelbrotZoom(Complex center, int width, int height) {
		this(center, width, height, DEFAULT_DELTA_RANGE.valueAt(1));
	}
	
	@Override
	public MandelbrotFrame valueAt(double percent) {
		return new MandelbrotFrame(center, width, height, deltaRange.valueAt(percent));
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
}
