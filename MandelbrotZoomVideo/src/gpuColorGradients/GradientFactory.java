package gpuColorGradients;

import java.util.Random;

import gradient.Gradient;
import static gpuColorGradients.GradientUtils.*;

public class GradientFactory {

	private static Random rand = new Random();
	
//	public static ColorGradient warmGradient() {
//		return new HSBGradient(rand.nextInt(128), rand.nextInt(128), 200 + rand.nextInt(56), 200 + rand.nextInt(56), 200 + rand.nextInt(56), 200 + rand.nextInt(56));
//	}
//	
//	public static ColorGradient coolGradient() {
//		return new HSBGradient(100 + rand.nextInt(100), 100 + rand.nextInt(100), 200 + rand.nextInt(56), 200 + rand.nextInt(56), 200 + rand.nextInt(56), 200 + rand.nextInt(56));
//	}
//	
//	public static MultiGradient randomMixWarmAndCool(double probWarm, int n) {
//		MultiGradient mg = new MultiGradient();
//		for(int i = 0 ; i < n ; i++)
//			if(rand.nextGaussian() < probWarm) {
//				ColorGradient g = rand.nextFloat() < probWarm ? warmGradient() : coolGradient();
//				g.bounce(1 + rand.nextInt(4));
//				mg.addGradient(g);
//			}
//		return mg;
//	}
//	
//	public static ColorGradient randomMixWarmAndCool() {
//		return randomMixWarmAndCool(0.5f, rand.nextInt(60) + 20);
//	}
//	
//	
//	public static ColorGradient graydient() {
//		return new RGBGradient(rand.nextInt(256), rand.nextInt(256));
//	}
//	
//	public static MultiGradient randomSmoothGrays(int n) {
//		MultiGradient mg = new MultiGradient();
//		int lastGray = rand.nextInt(256);
//		for(int i = 0 ; i < n ; i++) {
//			int newGray = rand.nextInt(256);
//			mg.addGradient(new RGBGradient(lastGray, newGray));
//			lastGray = newGray;
//		}
//		return mg;
//	}
//	
//	public static MultiGradient randomSmoothHueGradients(int n) {
//		MultiGradient mg = new MultiGradient();
//		int lastHue = rand.nextInt(256);
//		for(int i = 0 ; i < n ; i++) {
//			int newHue = rand.nextInt(256);
//			ColorGradient g = new HSBGradient(lastHue, newHue, 255, 255, 255, 255);
//			//g.bounce(2);
//			mg.addGradient(g);
//			lastHue = newHue;
//		}
//		return mg;
//	}
//	
//	public static MultiGradient randomSmoothHSBGradients(int n) {
//		MultiGradient mg = new MultiGradient();
//		int lastHSB = rand.nextInt();
//		for(int i = 0 ; i < n ; i++) {
//			int newHSB = rand.nextInt();
//			ColorGradient g = new HSBGradient(lastHSB, newHSB);
//			g.bounce(1 + rand.nextInt(4)*2);
//			mg.addGradient(g);
//			lastHSB = newHSB;
//		}
//		return mg;
//	}
//	
//	public static MultiGradient randomHSBGradients(int n) {
//		MultiGradient mg = new MultiGradient();
//		int lastHSB = rand.nextInt();
//		for(int i = 0 ; i < n ; i++) {
//			int newHSB = rand.nextInt();
//			ColorGradient g = new HSBGradient(lastHSB, newHSB);
//			g.loop(1 + rand.nextInt(4)*2);
//			mg.addGradient(g);
//			lastHSB = newHSB;
//		}
//		return mg;
//	}
//	
//	public static MultiGradient randomGradients(int n, double probGray) {
//		MultiGradient mg = new MultiGradient();
//		for(int i = 0 ; i < n ; i++) 
//			if(rand.nextDouble() < probGray)
//				mg.addGradient(graydient());
//			else
//				mg.addGradient(randomGradient());
//			
//		return mg;
//	}
//	
//	public static ColorGradient randomGradients() {
//		return randomGradients(rand.nextInt(60) + 20, 0.2);
//	}
//	
//	public static ColorGradient randomGradient() {
//		return new HSBGradient(rand.nextInt(256), rand.nextInt(256), 255, 255, 255, 255);
//	}
//	
//	public static ColorGradient darkOrLightGradient(boolean toDark) {
//		return new HSBGradient(rand.nextInt(256), rand.nextInt(256), 255, toDark ? 255 : 0, toDark ? 0 : 255, 255);
//	}
//	
//	public static MultiGradient test(int n) {
//		MultiGradient result = new MultiGradient();
//		for(int i = 0 ; i < n ; i++) {
//			result.addGradient(darkOrLightGradient(rand.nextBoolean()).bounce(2*(1+rand.nextInt(5))));
//		}
//		return result;
//	}
	
	public static ThreeChannelGradient mettalicGradient(float startHue, float rangeHue) {
		return new ThreeChannelGradient(looped(startHue), 1, 0, rangeHue, -1, 1);
	}
	
	public static ThreeChannelGradient darkOrLightGradient(boolean toDark, float startHue, float rangeHue) {
		return new ThreeChannelGradient(looped(startHue), 1, toDark ? 0 : 1, rangeHue, toDark ? 0 : -1, toDark ? 1 : 0);
	}
	
	public static ThreeChannelGradient darkOrLightGradient(boolean toDark, float hue) {
		return darkOrLightGradient(toDark, hue, 0);
	}
	
	public static ThreeChannelGradient darkOrLightGradient(boolean toDark) {
		return darkOrLightGradient(toDark, rand.nextFloat(), rand.nextFloat()*2 - 1);
	}
	
	public static MultiGradient testV2(int n, int loops, double percentDark, boolean constantHue) {
		MultiGradient result = new MultiGradient();
		for(int i = 0 ; i < n ; i++) {
			result.addGradient((constantHue ? darkOrLightGradient(rand.nextFloat() < percentDark, rand.nextFloat()) : darkOrLightGradient(rand.nextFloat() < percentDark)).loop(loops));
		}
		return result;
	}
	
	public static MultiGradient randomiseGradient() {
		MultiGradient result;
//		if(rand.nextDouble() < 1d/3)
			//result = testV2((int)(Math.random() * 150) + 10, 25, false, true);
			result = testV2(10, 60, .5, true);
//		if(rand.nextBoolean())
//			result = GradientFactory.randomSmoothHSBGradients(rand.nextInt(100)+1);
//		else
//			result = randomMixWarmAndCool(0.2, rand.nextInt(200)+1);
		//result.bounce((float)(Math.random() * Math.random() * 8) + 1);
		return result;
	}
	
}
