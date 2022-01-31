package logic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.UIManager;

import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import com.aparapi.opencl.OpenCL;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import colorGradients.GradientFactory;
import colorGradients.HSBGradient;
import colorGradients.RGBGradient;
import fractals.Fractal;
import fractals.FractalCeption;
import fractals.MandelbrotSet;
import gradient.Constant;
import gradient.LinearGradient;
import gradient.LogarithmicGradient;
import gradient.MultiGradient;
import gradient.Gradient;
import gui.FractalNavigatorGUI;
import optimizations.ComplexPowerMandelbrotKernel;
import optimizations.FractalKernel;
import optimizations.FractalProducer;
import optimizations.MandelbrotKernel;
import utils.ImageUtils;
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
		
		-1.372647849616051 -1.0168188958287422E-4  glitchy windows	Time elapsed: 153h 26m 54s
	 */
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception e) {
			e.printStackTrace();
		}
//		FractalFrame f = new FractalFrame(new Complex(-.75, 0), 1500, 1500, 0.001, 4000);
//		
//		FractalKernel m = new MandelbrotKernel(f);
//		
////		m.executeAll();
//		
//		int inc = 100000;
//		int passes = (int)Math.ceil((double)m.getSize() / inc);
//		long time = 0;
//		for(int i = 0 ; i < passes ; i++) {
//			long t = System.currentTimeMillis();
//			m = m.copy(i*inc);
//			m.executeSome(inc);
//			time += System.currentTimeMillis() - t;
//			ImageUtils.saveImage(f.toImage(), ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\MandelbrotStuff\\images/Mandelbrot.png"));
//		}
//		System.out.println("Total time: " + time + " millis");
//		DeviceManager ma = new DeviceManager(m);
//		ma.start();
//		try {
//			ma.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		ImageUtils.saveImage(f.toImage(), ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\MandelbrotStuff\\images/Mandelbrot.png"));
//		
		
//		for(OpenCLDevice d : OpenCLDevice.listDevices(Device.TYPE.CPU)) {
//			m = new ComplexPowerMandelbrotKernel(f, new Complex(2, 0.01));
//			m.setExecutionMode(EXECUTION_MODE.CPU);
//			for(int j = 0 ; j < passes ; j++) {
//				time += m.executeSome(d, inc);
//			}
//			System.out.println("Total time: " + time + " ms");
//			ImageUtils.saveImage(f.toImage(), ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\MandelbrotStuff\\images/Mandelbrot.png"));
//		}
		
		//FractalZoomMP4.test();
		
		Gradient<Color> g = costumGradient();
		g = GradientFactory.randomGradient(20, 1, .2);
		//g = GradientFactory.hotAndColdGradient(10, 3, 1, 0.2);
		new FractalNavigatorGUI(g);
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
