package gradient;

public class GradientUtils {

	public static double truncateBelow(double val, double min) {
		return val < min ? min : val;
	}
	
	public static double truncateAbove(double val, double max) {
		return val > max ? max : val;
	}
	
	public static double truncate(double val, double min, double max) {
		return val < min ? min : val > max ? max : val;
	}
	
	
	public static double bounce(double percent) {
		int p = floor(percent);
		percent -= p;
		return p % 2 == 0 ? percent : 1 - percent;
	}
	
	public static double loop(double percent) {
		return percent - floor(percent);
	}
	
	public static int floor(double a) {
		int f = (int)a;
		if(a >= 0 || a == f)
			return f;
		return f-1;
	}
	
}
