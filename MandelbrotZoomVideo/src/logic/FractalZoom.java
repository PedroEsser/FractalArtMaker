package logic;

import fractals.Fractal;
import fractals.MandelbrotSet;
import gradient.Constant;
import gradient.LinearGradient;
import gradient.LogarithmicGradient;
import gradient.NumericGradient;
import gradient.Gradient;

public class FractalZoom implements Gradient<FractalFrame>{

	public static final Gradient<Fractal> DEFAULT_FRACTAL_RANGE = new Constant<Fractal>(new MandelbrotSet());
	public static final LogarithmicGradient DEFAULT_DELTA_RANGE = new LogarithmicGradient(0.01, 1E-16);
	public static final LogarithmicGradient DEFAULT_MAX_ITERATION_RANGE = new LogarithmicGradient(100, 4000);
	
	private int width, height;
	private Gradient<Complex> centerRange;
	private Gradient<Double> deltaRange;
	private Gradient<Double> maxIterationRange;
	private Gradient<Fractal> fractalRange;

	public FractalZoom(Gradient<Complex> centerRange, int width, int height, Gradient<Double> deltaRange, Gradient<Double> maxIterationRange, Gradient<Fractal> fractalRange) {
		this.centerRange = centerRange;
		this.width = width;
		this.height = height;
		this.deltaRange = deltaRange;
		this.maxIterationRange = maxIterationRange;
		this.fractalRange = fractalRange;
	}
	
	public FractalZoom(Complex center, int width, int height, Gradient<Double> deltaRange, Gradient<Double> maxIterationRange) {
		this(new Constant<Complex>(center), width, height, deltaRange, maxIterationRange, DEFAULT_FRACTAL_RANGE);
	}
	
	public FractalZoom(Complex center, int width, int height, double initialDelta, double finalDelta, int initialMaxIteration, int finalMaxIteration) {
		this(center, width, height, new LogarithmicGradient(initialDelta, finalDelta), new LinearGradient(initialMaxIteration, finalMaxIteration));
	}
	
	public FractalZoom(Complex center, int width, int height, double initialDelta, double finalDelta) {
		this(center, width, height, new LogarithmicGradient(initialDelta, finalDelta));
	}
	
	public FractalZoom(Complex center, int width, int height, Gradient<Double> deltaRange) {
		this(center, width, height, deltaRange, DEFAULT_MAX_ITERATION_RANGE);
	}
	
	public FractalZoom(Complex center, int width, int height) {
		this(center, width, height, DEFAULT_DELTA_RANGE);
	}
	
	public FractalZoom(int width, int height) {
		this(new Complex(), width, height, DEFAULT_DELTA_RANGE, DEFAULT_MAX_ITERATION_RANGE);
	}
	
	@Override
	public FractalFrame valueAt(double percent) {
		return new FractalFrame(centerRange.valueAt(percent), width, height, deltaRange.valueAt(percent), maxIterationsAt(percent), fractalRange.valueAt(percent));
	}
	
	@Override
	public FractalZoom invert() {
		return new FractalZoom(centerRange.invert(), width, height, deltaRange.invert(), maxIterationRange.invert(), fractalRange.invert());
	}
	
	public FractalZoom clone() {
		return new FractalZoom(centerRange, width, height, deltaRange, maxIterationRange, fractalRange);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public Gradient<Complex> getCenterRange() {
		return centerRange;
	}

	public Gradient<Double> getDeltaRange() {
		return deltaRange;
	}

	public Gradient<Double> getMaxIterationRange() {
		return maxIterationRange;
	}
	
	public Gradient<Fractal> getFractalRange() {
		return fractalRange;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setCenterRange(Gradient<Complex> centerRange) {
		this.centerRange = centerRange;
	}

	public void setDeltaRange(Gradient<Double> deltaRange) {
		this.deltaRange = deltaRange;
	}

	public void setMaxIterationRange(Gradient<Double> maxIterationRange) {
		this.maxIterationRange = maxIterationRange;
	}
	
	public void setFractalRange(Gradient<Fractal> fractalRange) {
		this.fractalRange = fractalRange;
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
	
	public void setFractal(Fractal fractal) {
		this.fractalRange = new Constant<>(fractal);
	}
	
	public int maxIterationsAt(double percent) {
		return maxIterationRange.valueAt(percent).intValue();
	}
	
}
