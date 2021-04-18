package gradients;

import java.awt.Color;

import rangeUtils.LinearRange;

public class RGBGradient implements Gradient{

	private LinearRange redRange, greenRange, blueRange;
	
	public RGBGradient(LinearRange redRange, LinearRange greenRange, LinearRange blueRange) {
		this.redRange = redRange;
		this.greenRange = greenRange;
		this.blueRange = blueRange;
	}
	public RGBGradient(float startRed, float endRed, float startGreen, float endGreen, float startBlue, float endBlue) {
		this(new LinearRange(startRed, endRed), new LinearRange(startGreen, endGreen), new LinearRange(startBlue, endBlue));
	}
	
	public RGBGradient(Color a, Color b) {	//GrayScale
		this(a.getRed() / 255f, b.getRed() / 255f, a.getGreen() / 255f, b.getGreen() / 255f, a.getBlue() / 255f, b.getBlue() / 255f);
	}

	public RGBGradient(float startGray, float endGray) {	//GrayScale
		this(startGray, endGray, startGray, endGray, startGray, endGray);
	}
	
	public RGBGradient() {	//GrayScale
		this(0, 1);
	}

	@Override
	public Color valueAt(double percent) {
		return new Color(redRange.valueAt(percent).floatValue(), greenRange.valueAt(percent).floatValue(), blueRange.valueAt(percent).floatValue());
	}
	
}
