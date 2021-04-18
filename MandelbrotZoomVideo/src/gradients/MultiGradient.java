package gradients;

import java.awt.Color;
import java.util.ArrayList;

import rangeUtils.MultiRange;

public class MultiGradient extends MultiRange<Color> implements Gradient{
	
	public MultiGradient() {
		super();
	}
	
	public MultiGradient(Gradient... gradients) {
		super(gradients);
	}

}
