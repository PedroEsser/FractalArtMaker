package optimizations;

import logic.FractalFrame;
import utils.Rectangle;

public class FractalTask{

	private final Rectangle r;
	private final FractalFrame frame;
	
	public FractalTask(Rectangle r, FractalFrame frame) {
		this.r = r;
		this.frame = frame;
	}
	
	public void doTask() {
		//frame.calculate(r);
	}
	
}
