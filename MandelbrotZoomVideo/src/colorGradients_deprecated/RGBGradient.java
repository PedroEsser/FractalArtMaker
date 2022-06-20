package colorGradients_deprecated;

import java.awt.Color;

import gradient.Constant;
import gradient.LinearGradient;
import gradient.NumericGradient;
import gradient.Gradient;

public class RGBGradient implements ColorGradient{

	private Gradient<Double> redRange, greenRange, blueRange;
	private static double DEFAULT_GRAY_DELTA = 0.1;
	
	public RGBGradient(Gradient<Double> redRange, Gradient<Double> greenRange, Gradient<Double> blueRange) {
		this.redRange = redRange;
		this.greenRange = greenRange;
		this.blueRange = blueRange;
	}
	
	public RGBGradient(double startRed, double endRed, double startGreen, double endGreen, double startBlue, double endBlue) {
		this(new LinearGradient(startRed, endRed), new LinearGradient(startGreen, endGreen), new LinearGradient(startBlue, endBlue));
	}
	
	public RGBGradient(Color a, Color b) {
		this(a.getRed() / 255d, b.getRed() / 255d, a.getGreen() / 255d, b.getGreen() / 255d, a.getBlue() / 255d, b.getBlue() / 255d);
	}
	
	public RGBGradient(Color c) {
		this(new Constant<>(c.getRed() / 255d), new Constant<>(c.getGreen() / 255d), new Constant<>(c.getBlue() / 255d));
	}

	public RGBGradient(Gradient<Double> grayRange) {	//GrayScale
		this(grayRange, grayRange, grayRange);
	}
	
	public RGBGradient(double startGray, double endGray) {	//GrayScale
		this(new LinearGradient(startGray, endGray));
	}
	
	public RGBGradient() {	//GrayScale
		this(0, 1);
	}

	public static Gradient<Color> grayAround(double gray, double grayDelta) {
		return new RGBGradient().bounce(gray - grayDelta, gray + grayDelta);
	}
	
	public static Gradient<Color> grayAround(double gray) {
		return grayAround(gray, DEFAULT_GRAY_DELTA);
	}
	
	@Override
	public Color valueAt(double percent) {
		return new Color(redRange.valueAt(percent).floatValue(), greenRange.valueAt(percent).floatValue(), blueRange.valueAt(percent).floatValue());
	}
	
}
