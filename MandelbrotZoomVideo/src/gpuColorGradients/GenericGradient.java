package gpuColorGradients;

import static gpuColorGradients.GradientUtils.*;

public class GenericGradient {

	public static final int MASK = 0x000000FF;
	public static final int SIGN_MASK = 0x01000000;
	
	public static final int OFFSET_MASK = 0x000003FF;
	public static final int RANGE_MASK = 0x1FFFFC00;
	public static final int RANGE_DENOMINATOR = 1 << 10;
	public static final int RANGE_SIGN_BIT = 0x20000000;
	public static final int RGB_BIT = 0x80000000;
	public static final int LOOP_BIT = 0x40000000;
	
	protected int header, colorStart, colorRange;
	
	public GenericGradient(boolean rgb, int start1, int start2, int start3, int range1, int range2, int range3) {
		if(rgb)	header |= RGB_BIT;
		else	header &= ~RGB_BIT;
		
		this.colorStart = toRGB(start1, start2, start3);
		this.colorRange = 0;
		this.colorRange = range1 < 0 ? colorRange | (SIGN_MASK << 0) : colorRange;
		this.colorRange = range2 < 0 ? colorRange | (SIGN_MASK << 1) : colorRange;
		this.colorRange = range3 < 0 ? colorRange | (SIGN_MASK << 2) : colorRange;
		range1 = Math.abs(range1);
		range2 = Math.abs(range2);
		range3 = Math.abs(range3);
		assert range1 < 256 && range2 < 256 && range3 < 256 : "Diffs out of allowed range [-255, 255]";
		this.colorRange |= toRGB(range1, range2, range3);
		loop(1);
	}
	
	public GenericGradient(boolean rgb, float start1, float start2, float start3, float diff1, float diff2, float diff3) {
		this(rgb, fromFloatTo255(start1), fromFloatTo255(start2), fromFloatTo255(start3), fromFloatTo255(diff1), fromFloatTo255(diff2), fromFloatTo255(diff3));
	}
	
	public GenericGradient loop(float start, float end) {
		this.header |= LOOP_BIT;
		this.setOffset(start);
		this.setRange(end - start);
		return this;
	}
	
	public GenericGradient bounce(float start, float end) {
		this.header &= ~LOOP_BIT;
		this.setOffset(start);
		this.setRange(end - start);
		return this;
	}
	
	public GenericGradient loop(float end) {
		return loop(0, end);
	}
	
	public GenericGradient bounce(float end) {
		return bounce(0, end);
	}
	
	public void setOffset(float offset) {				//offset = [0, 1]
		assert 0 <= offset && 1 > offset : "Offset needs to be in the interval [0, 1]";
		int int_offset = (int)(offset * 1024);		//*1024, since we use 10 bits to store the offset
		header &= ~OFFSET_MASK;
		header |= int_offset;
	}
	
	public static float getOffset(int header) {
		int int_offset = (header & OFFSET_MASK);
		return (float)int_offset/RANGE_DENOMINATOR;
	}
	
	public void setRange(float range) {
		assert range < 1024 && range > -1024 : "Range needs to be in the interval [-1024, 1024]";
		if(range < 0)
			header |= RANGE_SIGN_BIT;
		int int_range = (int)(Math.abs(range) * RANGE_DENOMINATOR);		
		header &= ~RANGE_MASK;
		header |= int_range << 10;						//<<10, since we use 10 bits to store the offset
	}
	
	public static float getRange(int header) {
		int int_range = (header & RANGE_MASK) >> 10;
		if((header & RANGE_SIGN_BIT) != 0)
			int_range *= -1;
		return (float)int_range/RANGE_DENOMINATOR;
	}
	
	public static boolean isLoop(int header) {
		return (header & LOOP_BIT) != 0;
	}
	
	public static boolean isRGB(int header) {
		return (header & RGB_BIT) != 0;
	}
	
	public static int getNthComponent(int color, int n) {
		return (color >> (2-n)*8) & MASK;
	}
	
	public static int getNthSignedComponent(int diff, int n) {
		int d = getNthComponent(diff, n);
		if((diff & (SIGN_MASK << n)) == 0)
			return d;
		return -d;
	}
	
	public static float getActualPercentAt(float percent, int[] gradient, int index) {
		int header = gradient[index];
		
		float p = getOffset(header) + percent * getRange(header);
		return isLoop(header) ? looped(p) : bounced(p);
	}
	
	public static float getNthComponentAt(float percent, int n, int[] gradient, int index) {
		int start = getNthComponent(gradient[index], n);
		int range = getNthSignedComponent(gradient[index+1], n);
		return from255ToFloat(start) + from255ToFloat(range) * percent;
	}
	
	public int[] toPrimitive() {
		return new int[] {header, colorStart, colorRange};
	}
	
	public void test(float percent) {
		System.out.println("Start: (" + getNthComponent(colorStart, 0) + ", " + getNthComponent(colorStart, 1) + ", " + getNthComponent(colorStart, 2) + ")");
		System.out.println("RGB ranges: (" + 	from255ToFloat(getNthSignedComponent(colorRange, 0)) + ", " + 
											from255ToFloat(getNthSignedComponent(colorRange, 1)) + ", " + 
											from255ToFloat(getNthSignedComponent(colorRange, 2)) + ")");
		System.out.println("RGB: " + isRGB(header));
		System.out.println("Loop: " + isLoop(header));
		System.out.println("Offset: " + getOffset(header));
		System.out.println("Range: " + getRange(header));
		int[] prim = toPrimitive();
		float p = getActualPercentAt(percent, prim, 0);
		for(int i = 0 ; i < 3 ; i++)
			System.out.println(i + "th Component at percent " + p + " = " + getNthComponentAt(p, i, prim, 1));
	}

}
