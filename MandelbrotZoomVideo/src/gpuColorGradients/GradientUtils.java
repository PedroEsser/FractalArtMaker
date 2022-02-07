package gpuColorGradients;

public class GradientUtils {
	
	public static final int DENOMINATOR = 1 << 20;
	
	public static int toRGB(int r, int g, int b) {
		return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
	}
	
	public static int to255(float f) {
		return (int)(f*255);
	}
	
	public static float bounced(float percent) {
		int p = floor(percent);
		percent -= p;
		return p % 2 == 0 ? percent : 1 - percent;
	}
	
	public static float looped(float percent) {
		return percent - floor(percent);
	}
	
	public static int floor(float a) {
		int f = (int)a;
		if(a >= 0 || a == f)
			return f;
		return f-1;
	}
	
	public static float toMyFloat(int i) {
		return (float)i / DENOMINATOR;
	}
	
	public static int toMyInt(float f) {
		return (int)(f * DENOMINATOR);
	}
	
}
