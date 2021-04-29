package gradients;

import java.awt.Color;

import rangeUtils.Constant;
import rangeUtils.LinearRange;
import rangeUtils.NumericRange;
import rangeUtils.Range;

public class HSBGradient implements Gradient{

	private Range<Double> hueRange, saturationRange, brightnessRange;
	
	public HSBGradient(Range<Double> hueRange, Range<Double> saturationRange, Range<Double> brightnessRange) {
		this.hueRange = hueRange;
		this.saturationRange = saturationRange;
		this.brightnessRange = brightnessRange;
	}
	
	public HSBGradient(NumericRange hueRange) {
		this(hueRange, new Constant<>(1d), new Constant<>(1d));
	}
	
	public HSBGradient(float startHue, float endHue, float startSaturation, float endSaturation, float startBrightness, float endBrightness) {
		this(new LinearRange(startHue, endHue), new LinearRange(startSaturation, endSaturation), new LinearRange(startBrightness, endBrightness));
	}
	
	public HSBGradient(Color start, Color end) {
		float[] hsbStart = Color.RGBtoHSB(start.getRed(), start.getGreen(), start.getBlue(), null);
		float[] hsbEnd = Color.RGBtoHSB(end.getRed(), end.getGreen(), end.getBlue(), null);
		this.hueRange = new LinearRange(hsbStart[0], hsbEnd[0]);
		this.saturationRange = new LinearRange(hsbStart[1], hsbEnd[1]);;
		this.brightnessRange = new LinearRange(hsbStart[2], hsbEnd[2]);;
	}
	
	public HSBGradient(double startHue, double endHue) {
		this(new LinearRange(startHue, endHue));
	}

	public HSBGradient() {
		this(0, 1);
	}

	@Override
	public Color valueAt(double percent) {
		return Color.getHSBColor(hueRange.valueAt(percent).floatValue(), saturationRange.valueAt(percent).floatValue(), brightnessRange.valueAt(percent).floatValue());
	}
	
}
