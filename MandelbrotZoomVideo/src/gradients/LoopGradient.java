package gradients;

import java.awt.Color;

import rangeUtils.LoopRange;

public class LoopGradient extends LoopRange<Color> implements Gradient{
	
	public LoopGradient(Gradient gradient, double loopCount) {
		super(gradient, loopCount);
	}
	
	public LoopGradient(Gradient gradient) {
		this(gradient, 2);
	}

}
