package gpuColorGradients;

import static gpuColorGradients.GradientUtils.*;

import java.awt.Color;
import java.io.Serializable;

import gradient.Gradient;

public abstract class ColorGradient implements Serializable {
	
	public static final int LOOP_BIT = 0x00000001;
	
	protected float[] gradient;
	
	public ColorGradient(int dataSize, boolean loop, float range) {
		gradient = new float[dataSize];
		if(loop)
			loop(range);
		else
			bounce(range);
	}
	
	public ColorGradient(ColorGradient g) {
		gradient = g.gradient.clone();
	}
	
	public ColorGradient loop(float range) {
		gradient[0] = (int)gradient[0] | LOOP_BIT;
		gradient[1] = range;
		return this;
	}
	
	public ColorGradient bounce(float range) {
		gradient[0] = (int)gradient[0] & ~LOOP_BIT;
		gradient[1] = range;
		return this;
	}
	
	public float[] getGradientData() {
		return gradient;
	}
	
	public boolean isLoop() {
		return isLoop(gradient, 0);
	}
	
	public float getRange() {
		return getRange(gradient, 0);
	}
	
	public static boolean isLoop(float[] gradient, int index) {
		return ((int)gradient[index] & LOOP_BIT) != 0;
	}
	
	public static float getRange(float[] gradient, int index) {
		return gradient[index + 1];
	}
	
	public static float calculatePercent(float percent, float[] gradient, int index) {
		float p = percent * gradient[index + 1];
		return ((int)gradient[index] & LOOP_BIT) != 0 ? looped(p) : bounced(p);
	}
	
	public abstract ColorGradient copy();
	
	public abstract Color colorAt(double percent);
	
	public abstract Gradient<Color> toGradient();
	
}
