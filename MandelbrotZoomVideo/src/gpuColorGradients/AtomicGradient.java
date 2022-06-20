package gpuColorGradients;

import static gpuColorGradients.GradientUtils.*;

public class AtomicGradient extends ColorGradient{

	protected static final int COMPONENT_0_MASK = 0x01000000;
	protected int start, diff;
	
	public AtomicGradient(int type, int start, int end) {
		super(type);
	}
	
	public AtomicGradient(int type, int start1, int start2, int start3, int diff1, int diff2, int diff3) {
		super(type);
		this.start = toRGB(start1, start2, start3);
		this.diff = 0;
		this.diff = diff1 < 0 ? diff | (COMPONENT_0_MASK << 0) : diff;
		this.diff = diff2 < 0 ? diff | (COMPONENT_0_MASK << 1) : diff;
		this.diff = diff3 < 0 ? diff | (COMPONENT_0_MASK << 2) : diff;
		diff1 = Math.abs(diff1);
		diff2 = Math.abs(diff2);
		diff3 = Math.abs(diff3);
		assert diff1 < 256 && diff2 < 256 && diff3 < 256 : "Diffs out of allowed range [-255 -> 255]";
		this.diff |= toRGB(diff1, diff2, diff3);
	}
	
	public AtomicGradient(int type, float start1, float start2, float start3, float diff1, float diff2, float diff3) {
		this(type, to255(start1), to255(start2), to255(start3), to255(diff1), to255(diff2), to255(diff3));
	}

	public static int getNthComponent(int color, int n) {
		return (color >> (2-n)*8) & MASK;
	}
	
	public static int getNthDiffComponent(int diff, int n) {
		int d = getNthComponent(diff, n);
		if((diff & (COMPONENT_0_MASK << n)) == 0)
			return d;
		return -d;
	}
	
	public void test() {
		System.out.println(getNthComponent(start, 0));
		System.out.println(getNthComponent(start, 1));
		System.out.println(getNthComponent(start, 2));
		System.out.println(from255ToFloat(getNthDiffComponent(diff, 0)));
		System.out.println(from255ToFloat(getNthDiffComponent(diff, 1)));
		System.out.println(from255ToFloat(getNthDiffComponent(diff, 2)));
	}
	
	@Override
	public int[] toPrimitive() {
		return concatBase(new int[] {start, diff});
	}

}
