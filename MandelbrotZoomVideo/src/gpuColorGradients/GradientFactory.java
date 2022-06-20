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
	
	public static MultiGradient randomMixWarmAndCool(double probWarm, int n) {
		MultiGradient mg = new MultiGradient();
		for(int i = 0 ; i < n ; i++)
			if(rand.nextGaussian() < probWarm)
				mg.addGradient(rand.nextGaussian() < probWarm ? warmGradient() : coolGradient());
		return mg;
	}
	
	public static ColorGradient randomMixWarmAndCool() {
		return randomMixWarmAndCool(0.5f, rand.nextInt(60) + 20);
	}
	
	
	public static ColorGradient graydient() {
		return new RGBGradient(rand.nextInt(256), rand.nextInt(256));
	}
	
	public static MultiGradient randomSmoothGrays(int n) {
		MultiGradient mg = new MultiGradient();
		int lastGray = rand.nextInt(256);
		for(int i = 0 ; i < n ; i++) {
			int newGray = rand.nextInt(256);
			mg.addGradient(new RGBGradient(lastGray, newGray));
			lastGray = newGray;
		}
		return mg;
	}
	
	public static MultiGradient randomSmoothHueGradients(int n) {
		MultiGradient mg = new MultiGradient();
		int lastHue = rand.nextInt(256);
		for(int i = 0 ; i < n ; i++) {
			int newHue = rand.nextInt(256);
			ColorGradient g = new HSBGradient(lastHue, newHue, 255, 255, 255, 255);
			//g.bounce(2);
			mg.addGradient(g);
			lastHue = newHue;
		}
		return mg;
	}
	
	public static MultiGradient randomSmoothHSBGradients(int n) {
		MultiGradient mg = new MultiGradient();
		int lastHSB = rand.nextInt();
		for(int i = 0 ; i < n ; i++) {
			int newHSB = rand.nextInt();
			ColorGradient g = new HSBGradient(lastHSB, newHSB);
			g.bounce(1 + rand.nextInt(4)*2);
			mg.addGradient(g);
			lastHSB = newHSB;
		}
		return mg;
	}
	
	public static MultiGradient randomHSBGradients(int n) {
		MultiGradient mg = new MultiGradient();
		int lastHSB = rand.nextInt();
		for(int i = 0 ; i < n ; i++) {
			int newHSB = rand.nextInt();
			ColorGradient g = new HSBGradient(lastHSB, newHSB);
			g.loop(1 + rand.nextInt(4)*2);
			mg.addGradient(g);
			lastHSB = newHSB;
		}
		return mg;
	}
	
	public static MultiGradient randomGradients(int n, double probGray) {
		MultiGradient mg = new MultiGradient();
		for(int i = 0 ; i < n ; i++) 
			if(rand.nextDouble() < probGray)
				mg.addGradient(graydient());
			else
				mg.addGradient(randomGradient());
			
		return mg;
	}
	
	public static ColorGradient randomGradients() {
		return randomGradients(rand.nextInt(60) + 20, 0.2);
	}
	
	public static ColorGradient randomGradient() {
		return new HSBGradient(rand.nextInt(256), rand.nextInt(256), 255, 255, 255, 255);
	}
	
	public static MultiGradient randomiseGradient() {
		MultiGradient result;
//		if(rand.nextDouble() < 1d/3)
//			result = GradientFactory.randomMixWarmAndCool();
		if(rand.nextBoolean())
			result = GradientFactory.randomSmoothHSBGradients(rand.nextInt(100)+1);
		else
			result = GradientFactory.randomSmoothHSBGradients(rand.nextInt(40)+1);
		result.bounce((float)(Math.random() * 10));
		return result;
	}
	
}
