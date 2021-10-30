package colorGradients;

import java.awt.Color;
import java.util.Random;

import gradient.MultiGradient;
import gradient.Constant;
import gradient.Gradient;

public class GradientFactory {
	
	private static Random rand = new Random();
	
	public static Gradient<Color> costumGradient(int nGradients, int nLoops, int finalNLoops) {
		Gradient<Color>[] gradients = new Gradient[nGradients];
		for(int i = 0 ; i < nGradients ; i++) {
			gradients[i] = HSBGradient.hueAround(rand.nextDouble(), rand.nextDouble() * .3 + .1).bounce(nLoops);
		}
		MultiGradient<Color> mg = new MultiGradient<>(gradients);
		return mg.loop(finalNLoops);
	}
	
	public static Gradient<Color> randomGradient(int nGradients, int finalNLoops, double percentGray){
		Gradient<Color>[] gradients = new Gradient[nGradients];
		double grayAcc = percentGray;
		for(int i = 0 ; i < nGradients ; i++) {
			if(rand.nextDouble() < grayAcc) {
				gradients[i] = RGBGradient.grayAround(rand.nextDouble(), rand.nextDouble() * .3 + .1).bounce(rand.nextInt(10) + 1);
				grayAcc = percentGray;
			}else{
				gradients[i] = HSBGradient.hueAround(rand.nextDouble(), rand.nextDouble() * .3 + .1).bounce(rand.nextInt(10) + 1);
				grayAcc += percentGray;
			}
		}
		MultiGradient<Color> mg = new MultiGradient<>(gradients);
		return mg.loop(finalNLoops);
	}
	
	public static Gradient<Color> randomGradient(){
		return randomGradient(rand.nextInt(20) + 5, rand.nextInt(8) + 1, rand.nextDouble() * .4 + .1);
	}
	
	public static Gradient<Color> hotAndColdGradient(int nGradients, int nLoops, int finalNLoops, double percentGray) {
		Gradient<Color>[] gradients = new Gradient[nGradients];
		
		double grayAcc = percentGray;
		double aux = 0.5;
		
		for(int i = 0 ; i < nGradients ; i++) {
			if(rand.nextDouble() < grayAcc){
				if(rand.nextBoolean()) {
					gradients[i] = RGBGradient.grayAround(0, rand.nextDouble() * .3 + .1).bounce(nLoops);
				}else {
					gradients[i] = RGBGradient.grayAround(1, rand.nextDouble() * .3 + .1).bounce(nLoops);
				}
				grayAcc = percentGray;
			}else {
				if(rand.nextDouble() < aux) {
					gradients[i] = hotGradient().bounce(nLoops);
					aux *= 0.8;
				}else {
					gradients[i] = coldGradient().bounce(nLoops);
					aux /= 0.8;
				}
				grayAcc += percentGray;
			}
				
		}
		MultiGradient<Color> mg = new MultiGradient<>(gradients);
		return mg.loop(finalNLoops);
	}
	
	public static Gradient<Color> hotAndColdGradient(int nGradients, int finalNLoops, double percentGray) {
		Gradient<Color>[] gradients = new Gradient[nGradients];
		
		double grayAcc = percentGray;
		double aux = 0.5;
		
		for(int i = 0 ; i < nGradients ; i++) {
			if(rand.nextDouble() < grayAcc){
				if(rand.nextBoolean()) {
					gradients[i] = RGBGradient.grayAround(0, rand.nextDouble() * .3 + .1).bounce(rand.nextInt(10) + 1);
				}else {
					gradients[i] = RGBGradient.grayAround(1, rand.nextDouble() * .3 + .1).bounce(rand.nextInt(10) + 1);
				}
				grayAcc = percentGray;
			}else {
				if(rand.nextDouble() < aux) {
					gradients[i] = hotGradient().bounce(rand.nextInt(5) + 1);
					aux *= 0.8;
				}else {
					gradients[i] = coldGradient().bounce(rand.nextInt(5) + 1);
					aux /= 0.8;
				}
				grayAcc += percentGray;
			}
				
		}
		MultiGradient<Color> mg = new MultiGradient<>(gradients);
		return mg.loop(finalNLoops);
	}
	
	public static Gradient<Color> coldGradient(){
		return HSBGradient.hueAround(rand.nextDouble() * 0.1 + 0.56, rand.nextDouble() * .1 + .1);
	}
	
	public static Gradient<Color> hotGradient(){
		return HSBGradient.hueAround(rand.nextDouble() * 0.1 - 0.05, rand.nextDouble() * .1 + .1);
	}
	
	public static Gradient<Color> fromColors(int finalLoops, Color... colors){
		MultiGradient<Color> result = new MultiGradient<Color>();
		for(Color c : colors)
			result.addGradient(new Constant<Color>(c));
		return result.loop(finalLoops);
	}
	
	public static Gradient<Color> fromColors(Color... colors){
		return fromColors(1, colors);
	}
	
}
