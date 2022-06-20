package gpuColorGradients;

import java.awt.Color;
import static gpuColorGradients.GradientUtils.*;

public class RGBGradient extends ColorGradient{

	private int rgbStart, rgbEnd;
	
	public RGBGradient(Color start, Color end) {
		super(RGB);
		this.rgbStart = start.getRGB();
		this.rgbEnd = end.getRGB();
	}
	
	public RGBGradient(int startRed, int startGreen, int startBlue, int endRed, int endGreen, int endBlue) {
		this(new Color(startRed, startGreen, startBlue), new Color(endRed, endGreen, endBlue));
	}
	
	public RGBGradient(int startGray, int endGray) {
		this(startGray, startGray, startGray, endGray, endGray, endGray);
	}
	
	public RGBGradient() {
		this(0, 0xFFFFFFFF);
	}
	
	public static int colorAt1(float percent, int index, int[] gradient) {
		int startRed = gradient[index] >> 16 & MASK;
		int startGreen = gradient[index] >> 8 & MASK;
		int startBlue = gradient[index] & MASK;
		int endRed = gradient[index+1] >> 16 & MASK;
		int endGreen = gradient[index+1] >> 8 & MASK;
		int endBlue = gradient[index+1] & MASK;
		
		int red = (int)(startRed + percent * (endRed - startRed));
		int green = (int)(startGreen + percent * (endGreen - startGreen));
		int blue = (int)(startBlue + percent * (endBlue - startBlue));
		
		return toRGB(red, green, blue);
	}
	
	public static int skip() {
		return 2;
	}

	@Override
	public int[] toPrimitive() {
		return concatBase(new int[] {rgbStart, rgbEnd});
	}
	
}
