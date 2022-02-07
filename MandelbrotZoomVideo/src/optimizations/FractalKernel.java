package optimizations;

import java.util.Arrays;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;

import gpuColorGradients.ColorGradient;
import gpuColorGradients.MultiGradient;
import logic.FractalFrame;

public abstract class FractalKernel extends Kernel {

	public final float[] LOG2_RECIPROCAL = new float[] { (float)(1 / Math.log(2)) };
	
	private FractalFrame frame;
	protected int[] offset;
	protected int[] maxIterations;
	protected double[] re, im;
	protected byte[] chunkData;
	protected int[] gradient;
	protected float[] norm;
	
	public FractalKernel() {
		super();
	}
	
	public FractalKernel(FractalFrame frame, int offset) {
		super();
		setFrame(frame);
		this.offset = new int[] {offset};
	}
	
	public FractalKernel(FractalKernel kernel, int offset) {
		super();
		this.frame = kernel.frame;
		this.re = kernel.re;
		this.im = kernel.im;
		this.maxIterations = kernel.maxIterations;
		this.offset = new int[] {offset};
		this.gradient = kernel.gradient;
	}
	
	public void setFrame(FractalFrame frame) {
		this.frame = frame;
		double[][] coords = frame.getCoords();
		this.re = coords[0];
		this.im = coords[1];
		this.maxIterations = new int[] {frame.getMaxIterations()};
		this.offset = new int[] {0};
		this.gradient = frame.getGradient().toPrimitive();
		this.norm = new float[] {frame.getNorm()};
	}
	
	public void setNorm(float norm) {
		this.norm = new float[] {norm};
	}
	
	public int getSize() {
		return frame.getWidth() * frame.getHeight();
	}
	
	public void executeSome(int n_pixels) {
		long t = System.currentTimeMillis();
		
		boolean all = n_pixels == getSize();
		if(all) {
			chunkData = frame.getData();
			execute(n_pixels);
		}else {
			n_pixels = Math.min(getSize() - getOffset(), n_pixels);
			chunkData = new byte[n_pixels];
			execute(n_pixels);
			copyData();
		}
		
		long diff = System.currentTimeMillis() - t;
		System.out.println(this.getTargetDevice().getShortDescription() + ", " + diff + " millis passed");
	}
	
	public void executeAll() {
		executeSome(getSize());
	}
	
	public int getOffset() {
		return offset[0];
	}
	
	public void copyData() {
		byte[] data = frame.getData();
		for(int i = 0 ; i < chunkData.length ; i++) {
			data[i + offset[0]] = chunkData[i];
		}
	}
	
	public static void saveToColor(int rgb, int index, byte[] data) {
		int i = index * 3;
		data[i + 0] = (byte)(rgb & 0xFF);
		data[i + 1] = (byte)(rgb >> 8 & 0xFF);
		data[i + 2] = (byte)(rgb >> 16 & 0xFF);
	}
	
	public abstract FractalKernel copy(int offset);
	
}
