package logic;

import java.awt.Color;

import gradients.Gradient;
import gradients.HSBGradient;
import gradients.LoopGradient;
import gradients.MultiGradient;
import gradients.RGBGradient;
import gui.MenuGUI;
import rangeUtils.LinearRange;
import rangeUtils.LogarithmicRange;
import utils.ImageUtils;

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
	 */
	
	public static void main(String[] args) {
		new MenuGUI(costumGradient());
		Complex center = new Complex(-1.6743680857561216, -0.0057050142440923345);
		MandelbrotZoom zoom = new MandelbrotZoom(center, 800, 600);
		//MandelbrotFrame m = new MandelbrotFrame(center, 800, 600, 0.00002);
		MandelbrotFrame m = zoom.random();
		m.calculateAll(1000);
		Gradient g = costumGradient();
		ImageUtils.saveImage(m.toImage(g), "C:\\Users\\pedro\\Desktop\\mandelbrotImages/Mandelbrot.png");
		GIFZoom gif;
		try {//Complex center, int width, int height, double initialDelta, double finalDelta, double zoomSpeed, String path
			gif = new GIFZoom(center, 500, 500, 1E-14, 5, "");
			gif.createZoom(ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\mandelbrotImages\\gif/Mandelbrot.gif"), g, new LinearRange(100, 2000));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Gradient costumGradient() {
		Gradient g1 = new RGBGradient();
		g1 = new LoopGradient(g1, 2);
		Gradient g2 = new HSBGradient(.9f, .4f);//.9f, .4f
		g2 = new LoopGradient(g2, 2.5);
		MultiGradient mg = new MultiGradient();
		mg.addGradient(g1, 0.2f);
		mg.addGradient(g2, 0.5f);
		Gradient g3 = new HSBGradient(Color.RED, Color.GREEN);
		mg.addGradient(g3, 0.4f);
		LogarithmicRange r1 = new LogarithmicRange(1, 3);
		Gradient g4 = new HSBGradient(r1);
		LinearRange r2 = new LinearRange(0, 1);
		Gradient g5 = new HSBGradient(r2);
		LoopGradient lg = new LoopGradient(mg, 20);
		//return new MultiGradient(g4, g5);
		return mg;
	}
	
}
