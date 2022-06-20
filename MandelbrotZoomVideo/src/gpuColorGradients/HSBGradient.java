package gpuColorGradients;

import java.awt.Color;
import static gpuColorGradients.GradientUtils.*;

public class HSBGradient extends ColorGradient{

	private final int hsbStart, hsbEnd;
	
	public HSBGradient(int hsbStart, int hsbEnd) {
		super(HSB);
		this.hsbStart = hsbStart;
		this.hsbEnd = hsbEnd;
	}
	
	public HSBGradient(int hueStart, int hueEnd, int satStart, int satEnd, int briStart, int briEnd) {
		this(toRGB(hueStart, satStart, briStart), toRGB(hueEnd, satEnd, briEnd));
	}
	
	public HSBGradient(Color start, Color end) {
		super(HSB);
		float[] hsbStarta = Color.RGBtoHSB(start.getRed(), start.getGreen(), start.getBlue(), null);
		float[] hsbEnda = Color.RGBtoHSB(end.getRed(), end.getGreen(), end.getBlue(), null);
		this.hsbStart = toRGB(to255(hsbStarta[0]), to255(hsbStarta[1]), to255(hsbStarta[2]));
		this.hsbEnd = toRGB(to255(hsbEnda[0]), to255(hsbEnda[1]), to255(hsbEnda[2]));
	}
	
	public HSBGradient() {
		this(0, 255, 255, 255, 255, 255);
	}
	
	public static int colorAt2(float percent, int index, int[] gradient) {
		int startHue = gradient[index] >> 16 & MASK;
		int startSaturation = gradient[index] >> 8 & MASK;
		int startBrightness = gradient[index] & MASK;
		int endHue = gradient[index+1] >> 16 & MASK;
		int endSaturation = gradient[index+1] >> 8 & MASK;
		int endBrightness = gradient[index+1] & MASK;
		
		float hue = (startHue + percent * (endHue - startHue))/255;
		float saturation = (startSaturation + percent * (endSaturation - startSaturation))/255;
		float brightness = (startBrightness + percent * (endBrightness - startBrightness))/255;
		int r = 0;
		int g = 0;
		int b = 0;
        if (saturation == 0) {
            r = (int) (brightness * 255.0f + 0.5f);
            g = r;
            b = r;
        } else {
            float hf = looped(hue) * 6.0f;
            float f = looped(hf);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            int h = (int)hf;
            if(h == 0) {
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (t * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
            }else if(h == 1) {
                r = (int) (q * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
            }else if(h == 2) {
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (t * 255.0f + 0.5f);
            }else if(h == 3) {
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (q * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
            }else if(h == 4) {
                r = (int) (t * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
            }else {
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (q * 255.0f + 0.5f);
            }
        }
        return toRGB(r, g, b);
	}
	
	public static int skip() {
		return 2;
	}

	@Override
	public int[] toPrimitive() {
		return concatBase(new int[] {hsbStart, hsbEnd});
	}
	
}
