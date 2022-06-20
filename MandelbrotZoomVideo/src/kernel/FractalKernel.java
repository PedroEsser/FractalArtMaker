package kernel;

import java.util.Arrays;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.Device.TYPE;

import fractal.Complex;
import fractal.FractalFrame;

import com.aparapi.device.OpenCLDevice;

import gpuColorGradients.ColorGradient;
import gpuColorGradients.MultiGradient;

public abstract class FractalKernel extends Kernel {

	public final float[] LOG2_RECIPROCAL = new float[] { (float)(1 / Math.log(2)) };
	
	private FractalFrame frame;
	protected double[] topLeft, delta;
	protected byte[] data;
	protected int[] maxIterations, gradient, width;
	protected float[] norm;
	protected int[] initial_iterations = new int[] {1};
	protected double[] escapeRadius = new double[] {100};
	
	public FractalKernel() {
		super();
	}
	
	public FractalKernel(int initial_iterations) {
		super();
		this.initial_iterations = new int[] {initial_iterations};
	}
	
	public FractalKernel(FractalFrame frame, int initial_iterations) {
		super();
		setFrame(frame);
		this.initial_iterations = new int[] {initial_iterations};
	}
	
	public void setFrame(FractalFrame frame) {
		this.frame = frame;
		this.width = new int[] {frame.getWidth()};
		this.topLeft = frame.complexAt(0, 0);
		this.delta = new double[] {frame.getDelta()};
		this.maxIterations = new int[] {frame.getMaxIterations()};
		this.gradient = frame.getGradient().toPrimitive();
		this.norm = new float[] {frame.getNorm()};
	}
	
	public void setNorm(float norm) {
		this.norm = new float[] {norm};
	}
	
	public void setInitial_iterations(int initial_iterations) {
		this.initial_iterations = new int[] {initial_iterations};
	}
	
	public int getSize() {
		return frame.getWidth() * frame.getHeight();
	}
	
	public void executeAll() {
		assert frame == null : "No FractalFrame has been set to this Kernel!";
			
		long t = System.currentTimeMillis();
		
		data = frame.getData();
		execute(Range.create2D(frame.getWidth(), frame.getHeight()));
		
		long diff = System.currentTimeMillis() - t;
		
		System.out.println(this.getTargetDevice().getShortDescription() + ", " + diff + " millis passed");
	}
	
	public int getColor(int iterations, double[] complex) {
		float iterationScore = (float)(iterations + 1 - Math.log(Math.log(Math.sqrt(complex[0]*complex[0] + complex[1]*complex[1]))) * LOG2_RECIPROCAL[0]);
		iterationScore =  iterationScore < 0 ? 0 : iterationScore;
		return MultiGradient.colorAt(iterationScore * norm[0], gradient);
	}
	
	public static void saveToColor(int rgb, int index, byte[] data) {
		index = index * 3;
		data[index + 0] = (byte)(rgb & 0xFF);
		data[index + 1] = (byte)(rgb >> 8 & 0xFF);
		data[index + 2] = (byte)(rgb >> 16 & 0xFF);
	}
	
	public abstract double iterateRE(double currentRE, double currentIM);
	
	public abstract double iterateIM(double currentRE, double currentIM);
	
	public static double amplitudeSquared(double[] complex) {
		return complex[0] * complex[0] + complex[1] * complex[1];
	}
	
}
