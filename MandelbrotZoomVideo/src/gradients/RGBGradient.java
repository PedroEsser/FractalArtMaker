package gradients;

import java.awt.Color;

import rangeUtils.Constant;
import rangeUtils.LinearRange;
import rangeUtils.NumericRange;
import rangeUtils.Range;

public class RGBGradient implements Gradient{

	private Range<Double> redRange, greenRange, blueRange;
	
	public RGBGradient(Range<Double> redRange, Range<Double> greenRange, Range<Double> blueRange) {
		this.redRange = redRange;
		this.greenRange = greenRange;
		this.blueRange = blueRange;
	}
	
	public RGBGradient(double startRed, double endRed, double startGreen, double endGreen, double startBlue, double endBlue) {
		this(new LinearRange(startRed, endRed), new LinearRange(startGreen, endGreen), new LinearRange(startBlue, endBlue));
	}
	
	public RGBGradient(Color a, Color b) {
		this(a.getRed() / 255d, b.getRed() / 255d, a.getGreen() / 255d, b.getGreen() / 255d, a.getBlue() / 255d, b.getBlue() / 255d);
	}
	
	public RGBGradient(Color c) {
		this(new Constant<>(c.getRed() / 255d), new Constant<>(c.getGreen() / 255d), new Constant<>(c.getBlue() / 255d));
	}

	public RGBGradient(double startGray, double endGray) {	//GrayScale
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
