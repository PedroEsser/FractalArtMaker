package fractal;

import fractalKernels.FractalKernel;
import fractals_deprecated.Complex;
import gpuColorGradients.MultiGradient;
import gpuColorGradients.ThreeChannelGradient;
import gradient.Constant;
import gradient.LinearGradient;
import gradient.LogarithmicGradient;
import gradient.Gradient;

public class FractalZoom implements Gradient<FractalFrame>{

	public static final Gradient<FractalKernel> DEFAULT_FRACTAL_GRADIENT = new Constant<FractalKernel>(FractalFrame.DEFAULT_FRACTAL);
	public static final LogarithmicGradient DEFAULT_DELTA_GRADIENT = new LogarithmicGradient(2E-2, 1E-16);
	public static final Gradient<Double> DEFAULT_MAX_ITERATION_GRADIENT = new LogarithmicGradient(20, 1000).truncateBelow(0);
	public static final Gradient<MultiGradient> DEFAULT_COLOR_GRADIENT = new Constant<MultiGradient>(FractalFrame.DEFAULT_GRADIENT);
	
	
	private int width, height;
	private float norm;
	private Gradient<Complex> centerGradient;
	private Gradient<Double> deltaGradient;
	private Gradient<Double> maxIterationGradient;
	private Gradient<FractalKernel> fractalGradient;
	public Gradient<MultiGradient> colorGradient;

	public FractalZoom(Gradient<Complex> centerGradient, int width, int height, Gradient<Double> deltaGradient, 
						Gradient<Double> maxIterationGradient, Gradient<FractalKernel> fractalGradient, Gradient<MultiGradient> colorGradient) {
		this.centerGradient = centerGradient;
		setDimensions(width, height);
		this.deltaGradient = deltaGradient;
		this.maxIterationGradient = maxIterationGradient;
		this.fractalGradient = fractalGradient;
		this.colorGradient = colorGradient;
		this.norm = 1 / maxIterationGradient.getEnd().floatValue();
	}
	
	public FractalZoom(Complex center, int width, int height, Gradient<Double> deltaGradient, Gradient<Double> maxIterationGradient) {
		this(new Constant<Complex>(center), width, height, deltaGradient, maxIterationGradient, DEFAULT_FRACTAL_GRADIENT, DEFAULT_COLOR_GRADIENT);
	}
	
	public FractalZoom(Complex center, int width, int height, double initialDelta, double finalDelta, int initialMaxIteration, int finalMaxIteration) {
		this(center, width, height, new LogarithmicGradient(initialDelta, finalDelta), new LinearGradient(initialMaxIteration, finalMaxIteration));
	}
	
	public FractalZoom(Complex center, int width, int height, double initialDelta, double finalDelta) {
		this(center, width, height, new LogarithmicGradient(initialDelta, finalDelta));
	}
	
	public FractalZoom(Complex center, int width, int height, Gradient<Double> deltaGradient) {
		this(center, width, height, deltaGradient, DEFAULT_MAX_ITERATION_GRADIENT);
	}
	
	public FractalZoom(Complex center, int width, int height) {
		this(center, width, height, DEFAULT_DELTA_GRADIENT);
	}
	
	public FractalZoom(int width, int height) {
		this(new Complex(), width, height);
	}
	
	@Override
	public FractalFrame valueAt(double percent) {
		return new FractalFrame(centerGradient.valueAt(percent), width, height, deltaGradient.valueAt(percent), maxIterationsAt(percent), colorGradient.valueAt(percent), fractalGradient.valueAt(percent), getNorm());
	}
	
	@Override
	public FractalZoom invert() {
		return new FractalZoom(centerGradient.invert(), width, height, deltaGradient.invert(), maxIterationGradient.invert(), fractalGradient.invert(), colorGradient.invert());
	}
	
	@Override
	public FractalZoom loop(double end){
		return new FractalZoom(centerGradient.loop(end), width, height, deltaGradient.loop(end), maxIterationGradient.loop(end), fractalGradient.loop(end), colorGradient.loop(end));
	}
	
	@Override
	public FractalZoom bounce() {
		return new FractalZoom(centerGradient.bounce(), width, height, deltaGradient.bounce(), maxIterationGradient.bounce(), fractalGradient.bounce(), colorGradient.bounce());
	}
	
	public FractalZoom clone() {
		return new FractalZoom(centerGradient, width, height, deltaGradient, maxIterationGradient, fractalGradient, colorGradient);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public Gradient<Complex> getCenterGradient() {
		return centerGradient;
	}

	public Gradient<Double> getDeltaGradient() {
		return deltaGradient;
	}

	public Gradient<Double> getMaxIterationGradient() {
		return maxIterationGradient;
	}
	
	public void setNorm(float norm) {
		this.norm = norm;
	}
	
	public float getNorm() {
		return norm;
	}
	
	public Gradient<FractalKernel> getFractalGradient() {
		return fractalGradient;
	}
	
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public void setCenterGradient(Gradient<Complex> centerGradient) {
		this.centerGradient = centerGradient;
	}

	public void setDeltaGradient(Gradient<Double> deltaGradient) {
		this.deltaGradient = deltaGradient;
	}

	public void setMaxIterationGradient(Gradient<Double> maxIterationGradient) {
		this.maxIterationGradient = maxIterationGradient;
		this.norm = 1 / maxIterationGradient.getEnd().floatValue();
	}
	
	public void setFractalGradient(Gradient<FractalKernel> fractalGradient) {
		this.fractalGradient = fractalGradient;
	}
	
	public void setColorGradient(Gradient<MultiGradient> colorGradient) {
		this.colorGradient = colorGradient;
	}
	
	public void setCenter(Complex center) {
		this.centerGradient = new Constant<>(center);
	}

	public void setDelta(double delta) {
		this.deltaGradient = new Constant<>(delta);
	}

	public void setMaxIteration(double maxIterations) {
		this.maxIterationGradient = new Constant<>(maxIterations);
	}
	
	public void setFractal(FractalKernel fractal) {
		this.fractalGradient = new Constant<>(fractal);
	}
	
	public void setGradient(MultiGradient gradient) {
		this.colorGradient = new Constant<MultiGradient>(gradient);
	}
	
	public int maxIterationsAt(double percent) {
		return maxIterationGradient.valueAt(percent).intValue();
	}
	
	public FractalZoom rescale(double scale) {
		Gradient<Double> newDeltaGradient = deltaGradient.transform(d -> d/scale);
		int newWidth = (int)(width * scale);
		int newHeight = (int)(height * scale);
		return new FractalZoom(centerGradient, newWidth, newHeight, newDeltaGradient, maxIterationGradient, fractalGradient, colorGradient);
	}
	
}
