package logic;

import rangeUtils.Constant;
import rangeUtils.LinearRange;
import rangeUtils.LogarithmicRange;
import rangeUtils.NumericRange;
import rangeUtils.Range;

public class MandelbrotZoom implements Range<MandelbrotFrame>{

	public static final NumericRange DEFAULT_DELTA_RANGE = new LogarithmicRange(0.005, 1E-14);
	public static final NumericRange DEFAULT_MAX_ITERATION_RANGE = new LinearRange(100, 1600);
	
	private int width, height;
	private Range<Complex> centerRange;
	private Range<Double> deltaRange;
	private Range<Double> maxIterationRange;

	public MandelbrotZoom(Range<Complex> centerRange, int width, int height, Range<Double> deltaRange, Range<Double> maxIterationRange) {
		this.centerRange = centerRange;
		this.width = width;
		this.height = height;
		this.deltaRange = deltaRange;
		this.maxIterationRange = maxIterationRange;
	}
	
	public MandelbrotZoom(Complex center, int width, int height, Range<Double> deltaRange, Range<Double> maxIterationRange) {
		this(new Constant<Complex>(center), width, height, deltaRange, maxIterationRange);
	}
	
	public MandelbrotZoom(Complex center, int width, int height, double initialDelta, double finalDelta, int initialMaxIteration, int finalMaxIteration) {
		this(center, width, height, new LogarithmicRange(initialDelta, finalDelta), new LinearRange(initialMaxIteration, finalMaxIteration));
	}
	
	public MandelbrotZoom(Complex center, int width, int height, double initialDelta, double finalDelta) {
		this(center, width, height, new LogarithmicRange(initialDelta, finalDelta));
	}
	
	public MandelbrotZoom(Complex center, int width, int height, Range<Double> deltaRange) {
		this(center, width, height, deltaRange, DEFAULT_MAX_ITERATION_RANGE);
	}
	
	public MandelbrotZoom(Complex center, int width, int height) {
		this(center, width, height, DEFAULT_DELTA_RANGE);
	}
	
	public MandelbrotZoom(Range<Complex> centerRange, int width, int height) {
		this(centerRange, width, height, DEFAULT_DELTA_RANGE, DEFAULT_MAX_ITERATION_RANGE);
	}
	
	public MandelbrotZoom(int width, int height) {
		this(new Complex(), width, height, DEFAULT_DELTA_RANGE, DEFAULT_MAX_ITERATION_RANGE);
	}
	
	@Override
	public MandelbrotFrame valueAt(double percent) {
		return new MandelbrotFrame(centerRange.valueAt(percent), width, height, deltaRange.valueAt(percent), maxIterationRange.valueAt(percent).intValue());
	}
	
	@Override
	public MandelbrotZoom invert() {
		return new MandelbrotZoom(centerRange.invert(), width, height, deltaRange.invert(), maxIterationRange.invert());
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public Range<Complex> getCenterRange() {
		return centerRange;
	}

	public Range<Double> getDeltaRange() {
		return deltaRange;
	}

	public Range<Double> getMaxIterationRange() {
		return maxIterationRange;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setCenterRange(Range<Complex> centerRange) {
		this.centerRange = centerRange;
	}

	public void setDeltaRange(Range<Double> deltaRange) {
		this.deltaRange = deltaRange;
	}

	public void setMaxIterationRange(Range<Double> maxIterationRange) {
		this.maxIterationRange = maxIterationRange;
	}
	
	public void setCenter(Complex center) {
		this.centerRange = new Constant<>(center);
	}

	public void setDelta(double delta) {
		this.deltaRange = new Constant<>(delta);
	}

	public void setMaxIteration(double maxIterations) {
		this.maxIterationRange = new Constant<>(maxIterations);
	}
	
}
