package optimizations;

import logic.MandelbrotFrame;
import utils.Rectangle;

public class MandelbrotTask{

	private final Rectangle r;
	private final MandelbrotFrame frame;
	
	public MandelbrotTask(Rectangle r, MandelbrotFrame frame) {
		this.r = r;
		this.frame = frame;
	}
	
	public void doTask() {
		frame.calculate(r);
	}
	
}
