package fractalKernels;

import java.util.ArrayList;
import java.util.List;

import com.aparapi.Kernel;
import com.aparapi.Range;

import fractal.FractalFrame;
import gpuColorGradients.MultiGradient;
import static gpuColorGradients.GradientUtils.*;

public abstract class FractalKernel extends Kernel {

	private final List<FractalParameter> fractalParameters = new ArrayList<>();
	
	protected FractalFrame frame;
	protected double centerRE, centerIM, delta, angle;
	protected byte[] data;
	protected float[] gradient;
	protected int maxIterations, width, height;
	protected float norm;
	protected int pre_iterations = 1;
	protected double escapeRadius = 100, morphPower = 0;
	
	public FractalKernel() {
		super();
		addParameter("Pre Iterations", 1, 1);
		addParameter("Escape Radius", 100, 10);
		addParameter("angle", 0, 1d/64);
		addParameter("Gradient offset", 0, 1d/64);
		addParameter("C power morph", 1, 1f/128);
	}
	
	public FractalKernel(FractalFrame frame) {
		super();
		setFrame(frame);
	}
	
	public void setFrame(FractalFrame frame) {
		this.frame = frame;
		this.width = frame.getWidth();
		this.height = frame.getHeight();
		this.centerRE = frame.getCenter().getRe();
		this.centerIM = frame.getCenter().getIm();
		this.delta = frame.getDelta();
		this.maxIterations = frame.getMaxIterations();
		this.norm = frame.getNorm();
	}
	
	public void setNorm(float norm) {
		this.norm = norm;
	}
	
	protected void addParameter(FractalParameter par) {
		this.fractalParameters.add(par);
	}

	protected void addParameter(String name, double value, double inc) {
		this.addParameter(new FractalParameter(name, value, inc));
	}
	
	public void editParameter(String name, double value, double inc) {
		for(FractalParameter p : fractalParameters)
			if(p.name.equals(name)) {
				p.setValue(value);
				p.setInc(inc);
				break;
			}
	}
	
	public void editParameter(String name, double value) {
		editParameter(name, value, getParameter(name).getInc());
	}
	
	public void editParameter(FractalParameter par) {
		editParameter(par.name, par.getValue(), par.getInc());
	}
	
	public FractalParameter getParameter(String name) {
		for(FractalParameter f : fractalParameters)
			if(f.name.equals(name))
				return f;
		return null;
	}
	
	public List<FractalParameter> getFractalParameters() {
		return fractalParameters;
	}
	
	public int getSize() {
		return frame.getWidth() * frame.getHeight();
	}
	
	public void loadAndExecute() {
		loadParameterValues();
		executeAll();
	}
	
	protected void executeAll() {
		data = frame.getData();
		long t = System.currentTimeMillis();
		execute(Range.create2D(frame.getWidth(), frame.getHeight()));
		long diff = System.currentTimeMillis() - t;
		System.out.println(this.getTargetDevice().getShortDescription() + ", " + diff + " millis passed");
	}
	
	public int getColor(int iterations, double[] complex) {
		float iterationScore = (float)(iterations + 1 - Math.log(Math.log(Math.sqrt(complex[0]*complex[0] + complex[1]*complex[1]))) / log(2));
		iterationScore =  iterationScore < 0 ? 0 : iterationScore;
		return MultiGradient.colorAtPercent(iterationScore * norm, gradient);
	}
	
	public static void saveColor(int rgb, int index, byte[] data) {
		index = index * 3;
		data[index + 0] = (byte)(rgb & 0xFF);
		data[index + 1] = (byte)(rgb >> 8 & 0xFF);
		data[index + 2] = (byte)(rgb >> 16 & 0xFF);
	}
	
	public abstract String getName();
	
	public abstract double iterateRE(double currentRE, double currentIM, double constantRE, double constantIM);
	
	public abstract double iterateIM(double currentRE, double currentIM, double constantRE, double constantIM);
	
	protected void loadParameterValues() {
		this.pre_iterations = this.getParameter("Pre Iterations").getValueAsInt();
		this.escapeRadius = this.getParameter("Escape Radius").getValue();
		this.angle = getParameter("angle").getValue() * Math.PI * 2;
		this.gradient = frame.getGradient().offseted((float)getParameter("Gradient offset").getValue()).getGradientData();
		this.morphPower = getParameter("C power morph").getValue();
	}
	
	public static double amplitudeSquared(double[] complex) {
		return complex[0] * complex[0] + complex[1] * complex[1];
	}
	
}
