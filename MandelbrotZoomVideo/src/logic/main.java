package logic;

import java.awt.Color;
import java.util.Iterator;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import fractals.MandelbrotSet;
import gradient.Constant;
import gradient.LinearGradient;
import gradient.LogarithmicGradient;
import gradient.MultiGradient;
import gradient.Gradient;
import gradient.GradientUtils;
import gradients.GradientFactory;
import gradients.HSBGradient;
import gradients.RGBGradient;
import gui.FractalNavigatorGUI;
import gui.MenuGUI;
import utils.ImageUtils;
import video.FractalZoomGIF;
import video.FractalZoomMP4;

public class main{

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
		-1.7397117812187641, -1.9843558305281984E-4
		-1.7401535031229785, 0.02803388339155191
		
		-0.06855445721100165, -1.1917221394518132 	3rd
		-0.3441938283976182, -0.878621484531144  	13th
	 */
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Gradient<Color> g = costumGradient();
		g = GradientFactory.randomGradient(20, 1, .2);
		g = GradientFactory.hotAndColdGradient(10, 3, 2, 0.2);
		g = GradientFactory.hotGradient().bounce(4);
		new FractalNavigatorGUI(g);
//		Complex center = new Complex(-0.20149587487712542, 0.6061653741214134);
//		Range<FractalFrame> zoom = new MandelbrotZoom(center, 1920, 1080, new LogarithmicRange(0.01, 1E-16), new LogarithmicRange(175, 7000));
//		
		//MandelbrotFrame frame = new MandelbrotFrame(center, 1920, 1080, 2.2E-11, 2400);
//		frame.calculateAll();
		//ImageUtils.saveImage(zoom.random().toImage(g), ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\MandelbrotStuff\\images/Mandelbrot.png"));
//		for(int i = 0 ; i < 1 ; i++) {
//			MandelbrotFrame frame = zoom.getEnd();
//			frame.calculateAll();
//			ImageUtils.saveImage(frame.toImage(lg.random()), ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\MandelbrotStuff\\images/Mandelbrot.png"));
//		}
		
//		String gifPath = ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\MandelbrotStuff\\gifs/Mandelbrot.gif");
//		try {
//			MandelbrotZoomGIF gif = new MandelbrotZoomGIF(zoom, 30, 10, gifPath);
//			gif.setGradientRange(p -> g.offset(p));
//			//gif.setGradient(g);
//			gif.start();		//171
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		String videoPath = ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\MandelbrotStuff\\mp4/Mandelbrot.mp4");
//		try {
//			MandelbrotZoomMP4 mp4 = new MandelbrotZoomMP4(zoom, 40, 50, videoPath);
//			//mp4.setGradientRange(p -> g.offset(p));
//			mp4.setGradient(g);
//			mp4.start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public static Gradient<Color> costumGradient() {
		Gradient<Color> g1 = new HSBGradient(.5, .75).loop(10);
		Gradient<Color> g2 = new RGBGradient(.2, 0).loop(10);
		Gradient<Color> g3 = new HSBGradient(.9, 1.2).loop(10);
		MultiGradient<Color> g4 = new MultiGradient<>(g1, g2, g3, g2);
		return g4.loop(3);
	}
	
	public static Gradient<Color> costumGradient2() {
		Gradient<Color> g1 = new HSBGradient(.7, .9).loop(4);
		Gradient<Color> g2 = new HSBGradient(.9, .4).loop(4);
		Gradient<Color> g3 = new HSBGradient(.4, .6).loop(4);
		Gradient<Color> g4 = new HSBGradient(1.1, .9).loop(4);
		Gradient<Color> g5 = new RGBGradient(.3, 0).loop(4);
		Gradient<Color> g6 = new RGBGradient(1, 0.7).loop(4);
		MultiGradient<Color> g7 = new MultiGradient<>(g1, g2, g3, g6, g5, g3, g4, g5, g6);
		return g7.loop(3);
	}
	
	public static Gradient<Color> costumGradient3() {
		Gradient<Color> g1 = HSBGradient.hueAround(new Color(128, 0, 128)).loop(3);
		Gradient<Color> g2 = RGBGradient.grayAround(0, 0.4).loop(3);
		Gradient<Color> g3 = HSBGradient.hueAround(Color.blue).loop(3);
		Gradient<Color> g4 = HSBGradient.hueAround(Color.orange).loop(3);
		Gradient<Color> g5 = RGBGradient.grayAround(1, 0.4).loop(3);
		Gradient<Color> g7 = HSBGradient.hueAround(Color.CYAN).loop(3);
		Gradient<Color> g8 = HSBGradient.hueAround(Color.red).loop(3);
		MultiGradient<Color> g9 = new MultiGradient<>(g1, g2, g3, g4, g5, g7, g5, g8, g5, g1);
		return g9.loop(6);
	}
	
}
