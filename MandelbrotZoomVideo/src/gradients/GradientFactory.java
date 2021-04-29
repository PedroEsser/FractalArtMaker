package gradients;

import java.awt.Color;
import java.util.Random;

import rangeUtils.MultiRange;
import rangeUtils.Range;

public class GradientFactory {

	
	public static Range<Color> fadeColors(boolean hsb, Color... colors){
		assert colors.length > 1 : "Fade must have more than 1 color.";
		Gradient[] gradients = new Gradient[colors.length - 1];
		for(int i = 1 ; i < colors.length ; i++) {
			if(hsb)
				gradients[i - 1] = new HSBGradient(colors[i - 1], colors[i]);
			else
				gradients[i - 1] = new RGBGradient(colors[i - 1], colors[i]);
		}
			
		return new MultiRange<Color>(gradients);
	}
	
	public static Range<Color> fadeColors(Color... colors){
		return fadeColors(true, colors);
	}
	
	public static Range<Color> random(){
		Random rand = new Random();
		Color[] randomColors = new Color[30];
		for(int i = 0 ; i < randomColors.length ; i++) {
			Color c = Color.getHSBColor((float)Math.random() * .7f + .7f, 1f, 1f);
			randomColors[i] = c;
		}
		return fadeColors(randomColors).loop(2);
	}
	
}
