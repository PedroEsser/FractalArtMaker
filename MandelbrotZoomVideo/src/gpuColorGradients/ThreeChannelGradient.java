package gpuColorGradients;
import static gpuColorGradients.GradientUtils.*;

import java.awt.Color;
import java.io.Serializable;

import gradient.Gradient;

public class ThreeChannelGradient extends ColorGradient implements Serializable{

	public static final int DATA_SIZE = 8;	//header, range, 3x start, 3x range
	public static final int HSB_BIT = 0x00000002;
	
	public ThreeChannelGradient(boolean hsb, boolean loop, float range, float start1, float start2, float start3, float range1, float range2, float range3) {
		super(DATA_SIZE, loop, range);
		assertInRange(0, 1, start1, start2, start3);
		assertInRange(-1, 1, range1, range2, range3);
		
		if(hsb)
			setHSB();
		else
			setRGB();
		
		gradient[2] = start1;
		gradient[3] = start2;
		gradient[4] = start3;
		gradient[5] = range1;
		gradient[6] = range2;
		gradient[7] = range3;
	}
	
	public ThreeChannelGradient(ThreeChannelGradient g) {
		super(g);
	}
	
	public ThreeChannelGradient(boolean isHSB, float start1, float start2, float start3, float range1, float range2, float range3) {
		this(isHSB, true, 1, start1, start2, start3, range1, range2, range3);
	}
	
	public ThreeChannelGradient(float start1, float start2, float start3, float range1, float range2, float range3) {
		this(true, start1, start2, start3, range1, range2, range3);
	}
	
	public ThreeChannelGradient() {
		this(0, 1, 1, 1, 0, 0);
	}
	
	public ThreeChannelGradient setHSB() {
		gradient[0] = (int)gradient[0] | HSB_BIT;
		return this;
	}
	
	public ThreeChannelGradient setRGB() {
		gradient[0] = (int)gradient[0] & ~HSB_BIT;
		return this;
	}
	
	public static boolean isHSB(float[] gradient, int index) {
		return ((int)gradient[index] & HSB_BIT) != 0;
	}
	
	public boolean isHSB() {
		return isHSB(gradient, 0);
	}
	
	public static float nthChannelAtPercent(float percent, int n, float[] gradient, int index) {
		float c = gradient[index+2 + n] + percent * gradient[index+5 + n];
		return c == 1 ? 1 : looped(c);
	}
	
	public void setNthChannelStart(int n, float value) {
		gradient[2 + n] = value;
	}
	
	public void setNthChannelRange(int n, float value) {
		gradient[5 + n] = value;
	}
	
	public float getNthChannelStart(int n) {
		return gradient[2 + n];
	}
	
	public float getNthChannelRange(int n) {
		return gradient[5 + n];
	}
	
	public static int colorAtPercent(float percent, float[] gradient, int index) {
		float p = calculatePercent(percent, gradient, index);
		float channel0 = nthChannelAtPercent(p, 0, gradient, index);
		float channel1 = nthChannelAtPercent(p, 1, gradient, index);
		float channel2 = nthChannelAtPercent(p, 2, gradient, index);
		if(((int)gradient[index] & HSB_BIT) != 0)
			return hsbColor(channel0, channel1, channel2);
		return rgbColor(channel0, channel1, channel2);
	}
	
	public static int rgbColor(float red, float green, float blue) {
		return toRGB(fromFloatTo255(red), fromFloatTo255(green), fromFloatTo255(blue));
	}
	
	public static int hsbColor(float hue, float saturation, float brightness) {
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
	
	@Override
	public ThreeChannelGradient bounce(float range) {
		return (ThreeChannelGradient)super.bounce(range);
	}
	
	@Override
	public ThreeChannelGradient loop(float range) {
		return (ThreeChannelGradient)super.loop(range);
	}
	
	@Override
	public Gradient<Color> toGradient() {
		return p -> new Color(colorAtPercent((float)p, gradient, 0));
	}

	@Override
	public ThreeChannelGradient copy() {
		return new ThreeChannelGradient(this);
	}
	
	@Override
	public Color colorAt(double percent) {
		return new Color(colorAtPercent((float)percent, gradient, 0));
	}
	
}
