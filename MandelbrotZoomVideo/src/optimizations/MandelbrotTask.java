package optimizations;

import logic.MandelbrotFrame;
import utils.Rectangle;

public class MandelbrotTask extends Thread{

	private final Rectangle r;
	private final MandelbrotFrame f;
	private final int maxIterations;
	
	public MandelbrotTask(Rectangle r, MandelbrotFrame f, int maxIterations) {
		this.r = r;
		this.f = f;
		this.maxIterations = maxIterations;
	}
	
	@Override
	public void run() {
		f.calculate(r, maxIterations);
	}
	
}
