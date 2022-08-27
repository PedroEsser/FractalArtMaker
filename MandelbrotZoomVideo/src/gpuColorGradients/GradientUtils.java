package gpuColorGradients;

public class GradientUtils {
	
//	public static final int DENOMINATOR = 1 << 20;		//this is sort of resolution
	
	public static int toRGB(int r, int g, int b) {
		return 0x00000000 | (r << 16) | (g << 8) | (b << 0);
	}
	
	public static int fromFloatTo255(float f) {
		return (int)(f*255);
	}
//	
//	public static float from255ToFloat(int i) {
//		return (float)i / 255f;
//	}
	
	public static float bounced(float percent) {
		int p = floor(percent);
		percent -= p;
		return p % 2 == 0 ? percent : 1 - percent;
	}
	
	public static float looped(float percent) {
		return percent - floor(percent);
	}
	
	public static float truncated(float value, float below, float above) {
		return value < below ? below : value > above ? above : value;
	}
	
	public static float truncated(float value) {
		return truncated(value, 0, 1);
	}
	
	public static int floor(float a) {
		int f = (int)a;
		if(a >= 0 || a == f)
			return f;
		return f-1;
	}
	
//	public static float toMyFloat(int i) {
//		return (float)i / DENOMINATOR;
//	}
//	
//	public static int toMyInt(float f) {
//		return (int)(f * DENOMINATOR);
//	}
	
	public static void assertInRange(float start, float end, float... vals) {
		for(float val : vals)
			assert val >= start && val <= end;
	}
	
}
