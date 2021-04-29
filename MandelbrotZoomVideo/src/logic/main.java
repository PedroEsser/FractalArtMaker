package logic;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;

import gradients.Gradient;
import gradients.GradientFactory;
import gradients.HSBGradient;
import gradients.RGBGradient;
import gui.MandelbrotNavigator;
import gui.MenuGUI;
import rangeUtils.Constant;
import rangeUtils.LinearRange;
import rangeUtils.LogarithmicRange;
import rangeUtils.LoopRange;
import rangeUtils.MultiRange;
import rangeUtils.Range;
import utils.ImageUtils;
import utils.Rectangle;

public class main {

	/*
	 addComplex(0.360240443437614363236125, -0.64131306106480317486037);
		addComplex(-0.7458471555429712, 0.12437509139737363);
		addComplex(-1.76938317919551501821384728608547378290, 0.0042368479187367722149265071713679970766);
		addComplex(-0.10345069804762344, -0.9270995203301644);
		-1.8537569487437844, 2.332861089191231E-6
		-1.6743680857561216, -0.0057050142440923345
		0.2503488805773595, 1.0276060968842138E-5
		-1.7493674404574746, 9.25510049074256E-9
		-1.4475067062921971, 0.005044822940268131
		0.001643721971153, -0.822467633298876
		-1.7397248925716058, 1.1513980444175365E-5
	 */
	
	public static void main(String[] args) {
		Range<Color> g = costumGradient();
		//g = GradientFactory.random();
		new MenuGUI(g);
		new MandelbrotNavigator(g);
//		Complex center = new Complex(-1.7397248925716058, 1.1513980444175365E-5);
//		MandelbrotZoom zoom = new MandelbrotZoom(center, 800, 600, MandelbrotZoom.DEFAULT_DELTA_RANGE, new LinearRange(100, 2500));
//		MandelbrotFrame m = zoom.getStart();
//		m.calculateAll();
//		
//		ImageUtils.saveImage(m.toImage(g), "C:\\Users\\pedro\\Desktop\\mandelbrotImages/Mandelbrot.png");
//		String gifPath = ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\mandelbrotImages\\gif/Mandelbrot.gif");
//		try {
//			GIFZoom gif = new GIFZoom(zoom, 25, 30, gifPath);
//			gif.setGradient(g);
//			gif.start();		//171
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public static Range<Color> costumGradient() {
		Range<Color> g1 = new HSBGradient(.5, .75).slice(10);
		Range<Color> g2 = new RGBGradient(.2, 0).slice(20);
		Range<Color> g3 = new HSBGradient(.9, 1.2).slice(10);
		MultiRange<Color> g4 = new MultiRange<>(g1, g2, g3, g2);
		return g4;
	}
	
}
