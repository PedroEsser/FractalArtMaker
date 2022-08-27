package gpuColorGradients;

import static gpuColorGradients.GradientUtils.*;

import java.awt.Color;

import gradient.Gradient;

public abstract class ColorGradientV2 {
	
	public static final int LOOP_BIT = 0x00000001;
	
	protected float[] gradient;
	
	public ColorGradientV2(int dataSize, boolean loop, float range) {
		gradient = new float[dataSize];
		if(loop)
			loop(range);
		else
			bounce(range);
	}
	
	public ColorGradientV2 loop(float range) {
		gradient[0] = (int)gradient[0] | LOOP_BIT;
		gradient[1] = range;
		return this;
	}
	
	public ColorGradientV2 bounce(float range) {
		gradient[0] = (int)gradient[0] & ~LOOP_BIT;
		gradient[1] = range;
		return this;
	}
	
	public float[] getGradientData() {
		return gradient;
	}
	
	
	public static boolean isLoop(float[] gradient, int index) {
		return ((int)gradient[index] & LOOP_BIT) != 0;
	}
	
	public static float getRange(float[] gradient, int index) {
		return gradient[index + 1];
	}
	
	public static float calculatePercent(float percent, float[] gradient, int index) {
		float p = percent * getRange(gradient, index);
		return isLoop(gradient, index) ? looped(p) : bounced(p);
	}
	
	public abstract Gradient<Color> toGradient();
	
}
