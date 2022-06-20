package colorGradients_deprecated;

import java.awt.Color;

import gradient.Constant;
import gradient.LinearGradient;
import gradient.NumericGradient;
import gradient.Gradient;

public class HSBGradient implements ColorGradient{

	public static double DEFAULT_DELTA = 0.1;
	private Gradient<Double> hueRange, saturationRange, brightnessRange;
	
	public HSBGradient(Gradient<Double> hueRange, Gradient<Double> saturationRange, Gradient<Double> brightnessRange) {
		this.hueRange = hueRange;
		this.saturationRange = saturationRange;
		this.brightnessRange = brightnessRange;
	}
	
	public HSBGradient(Gradient<Double> hueRange) {
		this(hueRange, new Constant<>(1d), new Constant<>(1d));
	}
	
	public HSBGradient(double startHue, double endHue, double startSaturation, double endSaturation, double startBrightness, double endBrightness) {
		this(new LinearGradient(startHue, endHue), new LinearGradient(startSaturation, endSaturation), new LinearGradient(startBrightness, endBrightness));
	}
	
	public HSBGradient(Color start, Color end) {
		float[] hsbStart = Color.RGBtoHSB(start.getRed(), start.getGreen(), start.getBlue(), null);
		float[] hsbEnd = Color.RGBtoHSB(end.getRed(), end.getGreen(), end.getBlue(), null);
		this.hueRange = new LinearGradient(hsbStart[0], hsbEnd[0]);
		this.saturationRange = new LinearGradient(hsbStart[1], hsbEnd[1]);
		this.brightnessRange = new LinearGradient(hsbStart[2], hsbEnd[2]);
	}
	
	public HSBGradient(double startHue, double endHue) {
		this(new LinearGradient(startHue, endHue));
	}

	public HSBGradient() {
		this(0, 1);
	}

	@Override
	public Color valueAt(double percent) {
		return Color.getHSBColor(hueRange.valueAt(percent).floatValue(), saturationRange.valueAt(percent).floatValue(), brightnessRange.valueAt(percent).floatValue());
	}
	
	public static HSBGradient hueAround(Color c) {
		return hueAround(c, DEFAULT_DELTA);
	}
	
	public static HSBGradient hueAround(Color c, double hueDelta) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		return new HSBGradient(new LinearGradient(hsb[0] - hueDelta, hsb[0] + hueDelta), new Constant<Double>((double)hsb[1]), new Constant<Double>((double)hsb[2]));
	}
	
	public static HSBGradient hueAround(double hue, double hueDelta) {
		return new HSBGradient(hue - hueDelta, hue + hueDelta);
	}
	
	public static HSBGradient gradientAround(Color c) {
		return gradientAround(c, DEFAULT_DELTA);
	}
	
	public static HSBGradient gradientAround(Color c, double delta) {
		return gradientAround(c, delta, delta, delta);
	}
	
	public static HSBGradient gradientAround(Color c, double hueDelta, double saturationDelta, double brightnessDelta) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		return new HSBGradient(
				new LinearGradient(hsb[0] - hueDelta, 			hsb[0] + hueDelta), 
				new LinearGradient(hsb[1] - saturationDelta, 	hsb[1] + saturationDelta), 
				new LinearGradient(hsb[2] - brightnessDelta, 	hsb[2] + brightnessDelta));
	}
	
	
}
