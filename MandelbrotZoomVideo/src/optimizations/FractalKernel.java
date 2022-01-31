package optimizations;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;

import logic.FractalFrame;

public abstract class FractalKernel extends Kernel {

	public final float[] LOG2_RECIPROCAL = new float[] { (float)(1 / Math.log(2)) };
	
	private FractalFrame frame;
	protected int[] offset;
	protected int[] maxIterations;
	protected double[] re, im;
	protected float[] chunkData;
	
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
	}
	
	public void setFrame(FractalFrame frame) {
		this.frame = frame;
		double[][] coords = frame.getCoords();
		this.re = coords[0];
		this.im = coords[1];
		this.maxIterations = new int[] {frame.getMaxIterations()};
		this.offset = new int[] {0};
	}
	
	public int getSize() {
		return frame.getData().length;
	}
	
	public void executeSome(int n_pixels) {
		long t = System.currentTimeMillis();
		
		boolean all = n_pixels == getSize();
		if(all) {
			chunkData = frame.getData();
			execute(n_pixels);
		}else {
			n_pixels = Math.min(getSize() - getOffset(), n_pixels);
			chunkData = new float[n_pixels];
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
		float[] data = frame.getData();
		for(int i = 0 ; i < chunkData.length ; i++) {
			data[i + offset[0]] = chunkData[i];
		}
	}
	
	public abstract FractalKernel copy(int offset);
	
}
