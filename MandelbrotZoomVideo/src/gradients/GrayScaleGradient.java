package gradients;

import java.awt.Color;

public class GrayScaleGradient implements Gradient{

	private float startGray, range;
	
	public GrayScaleGradient(float startGray, float endGray) {
		this.startGray = startGray;
		this.range = endGray - startGray;
	}
	
	public GrayScaleGradient() {
		this(0, 1);
	}
	
	@Override
	public Color valueAt(double percent) {
		try {
			float gray = startGray + (float)percent * range;
			gray = gray - (int)gray;
			return new Color(gray, gray, gray);
		}catch(IllegalArgumentException e) {
			System.out.println(percent);
			return null;
		}
	}

}
