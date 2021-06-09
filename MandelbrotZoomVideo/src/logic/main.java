package logic;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import gradients.HSBGradient;
import gradients.RGBGradient;
import gui.MandelbrotNavigatorGUI;
import gui.MenuGUI;
import rangeUtils.Constant;
import rangeUtils.MultiRange;
import rangeUtils.Range;
import utils.ImageUtils;
import video.MandelbrotZoomGIF;

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
		-1.7397117812187641, -1.9843558305281984E-4
		-1.7401535031229785, 0.02803388339155191
	 */
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Range<Color> g = costumGradient();
		//g = GradientFactory.random();
		new MenuGUI(g);
		new MandelbrotNavigatorGUI(g);
		Complex center = new Complex(-1.7798681101676581, -9.273263144254261E-9);
		MandelbrotZoom zoom = new MandelbrotZoom(center, 1920, 1080);
//		zoom.setDelta(2E-6);
//		zoom.setMaxIteration(1000);
		
		MandelbrotFrame frame = new MandelbrotFrame(center, 800, 600, 7.38E-9, 2000);
//		for(int i = 0 ; i < 60 ; i++) {
//			MandelbrotFrame frame = zoom.random();
//			frame.calculateAll();
//			ImageUtils.saveImage(frame.toImage(g), ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\MandelbrotStuff\\images/Mandelbrot.png"));
//		}
		
//		String gifPath = ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\MandelbrotStuff\\gifs/Mandelbrot.gif");
//		try {
//			MandelbrotZoomGIF gif = new MandelbrotZoomGIF(frame, 20, 15, gifPath);
//			gif.setGradientRange(p -> g.offset(p));
//			gif.start();		//171
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		String videoPath = ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\MandelbrotStuff\\mp4/Mandelbrot.mp4");
//		try {
//			MandelbrotZoomMP4 mp4 = new MandelbrotZoomMP4(zoom, 30, 30, videoPath);
//			mp4.setGradient(g);
//			mp4.start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public static Range<Color> costumGradient() {
		Range<Color> g1 = new HSBGradient(.5, .75).slice(10);
		Range<Color> g2 = new RGBGradient(.2, 0).slice(10);
		Range<Color> g3 = new HSBGradient(.9, 1.2).slice(10);
		MultiRange<Color> g4 = new MultiRange<>(g1, g2, g3, g2);
		return g4.slice(2);
	}
	
}
