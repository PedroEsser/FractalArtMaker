package gpuColorGradients;

import java.util.Random;

public class GradientFactory {

	private static Random rand = new Random();
	
	public static ColorGradient warmGradient() {
		return new HSBGradient((int)(rand.nextFloat() * 128), (int)(rand.nextFloat() * 128), 255, 255, 255, 255);
	}
	
	public static ColorGradient coolGradient() {
		return new HSBGradient(128 + (int)(rand.nextFloat() * 128), 128 + (int)(rand.nextFloat() * 128), 255, 255, 255, 255);
	}
	
	public static ColorGradient randomMixWarmAndCool(double probWarm, int n) {
		MultiGradient mg = new MultiGradient();
		for(int i = 0 ; i < n ; i++)
			if(rand.nextGaussian() < probWarm)
				mg.addGradient(rand.nextGaussian() < probWarm ? warmGradient() : coolGradient());
		return mg;
	}
	
	public static ColorGradient randomMixWarmAndCool() {
		return randomMixWarmAndCool(0.5f, rand.nextInt(60) + 20);
	}
	
	public static ColorGradient randomGradients(int n) {
		MultiGradient mg = new MultiGradient();
		for(int i = 0 ; i < n ; i++)
				mg.addGradient(randomGradient());
		return mg;
	}
	
	public static ColorGradient randomGradients() {
		return randomGradients(rand.nextInt(60) + 20);
	}
	
	public static ColorGradient randomGradient() {
		return new HSBGradient((int)(rand.nextFloat() * 256), (int)(rand.nextFloat() * 256), 255, 255, 255, 255);
	}
	
}
