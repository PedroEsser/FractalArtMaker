package gpuColorGradients;

import java.awt.Color;

import gradient.Gradient;
import static gpuColorGradients.GradientUtils.*;

public abstract class ColorGradient {
	
	private int type;
	private float start, range;
	
	public ColorGradient(int type, float start, float end, boolean isLoop) {
		this.type = type;
		if(isLoop) 
			loop(start, end);
		else 
			bounce(start, end);
	}
	
	public ColorGradient(int type) {
		this(type, 0, 1, false);
	}
	
	public abstract int[] toPrimitive();
	
	protected int[] getBase() {
		int[] base = new int[3];
		base[0] = type;
		base[1] = toMyInt(start);
		base[2] = toMyInt(range);
		return base;
	}
	
	protected int[] concatBase(int[] arr) {
		int[] result = new int[arr.length + 3];
		result[0] = type;
		result[1] = toMyInt(start);
		result[2] = toMyInt(range);
		for(int i = 0 ; i < arr.length ; i++)
			result[3 + i] = arr[i];
		return result;
	}
	
	public Gradient<Color> toGradient(){
		return p -> new Color(genericColorAt((float)p, 0, toPrimitive()));
	}
	
	public void loop(float start, float end) {
		this.type |= LOOP_MASK;
		this.start = start;
		this.range = end - start;
	}
	
	public void bounce(float start, float end) {
		this.type &= ~LOOP_MASK;
		this.start = start;
		this.range = end - start;
	}
	
	public void loop(float end) {
		loop(0, end);
	}
	
	public void bounce(float end) {
		bounce(0, end);
	}
	
	public void setStart(float start) {
		this.start = start;
	}
	
	public void setEnd(float end) {
		this.range = end - start;
	}
	
	public static ColorGradient constant(int rgb) {
		return new RGBGradient(rgb, rgb);
	}
	
	public static float percentFor(float p, int index, int[] gradient) {
		float start = toMyFloat(gradient[index + 1]);
		float range = toMyFloat(gradient[index + 2]);
		float percent = start + range * p;
		if((gradient[index] & LOOP_MASK) != 0) 
			return looped(percent);
		return bounced(percent);
	}
	
	public static int genericColorAt(float percent, int index, int[] gradient) {
		int type = gradient[index] & TYPE_MASK;
		float p = percentFor(percent, index, gradient);
		index += 3;
		
		if(type == RGB) 		return RGBGradient.colorAt1(p, index, gradient);
		if(type == HSB) 		return HSBGradient.colorAt2(p, index, gradient);
		
		return -1;
	}

	public static final int RGB = 0;
	public static final int HSB = 1;
	public static final int MULTI = 2;
	public static final int TYPE_MASK = 0x000000ff;
	public static final int LOOP_MASK = 0x80000000;
	
	public static boolean isLoop(int type) {
		return (type & LOOP_MASK) != 0;
	}
	
	public static final int MASK = 0xFF;
	
}
