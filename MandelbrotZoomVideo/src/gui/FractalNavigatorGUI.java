package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import fractals.BurningShip;
import fractals.FractalCeption;
import fractals.MandelbrotSet;
import gradient.Gradient;
import gradients.GradientFactory;
import logic.Complex;
import logic.FractalZoom;
import utils.ImageUtils;
import video.FractalVideo;
import video.FractalZoomMP4;

public class FractalNavigatorGUI extends JFrame{

	public static final Dimension DEFAULT_NAVIGATOR_SIZE = new Dimension(800, 600);
	private double offset = 0;
	private Gradient<Color> gradient;
	private final FractalVisualizer visualizer;
	private MenuGUI menu;
	
	private int test = 0;
	private FractalVideo video;
	
	private Thread loopGradient;
	
//	private MandelbrotNavigator navigator; 
	
	public FractalNavigatorGUI(Gradient<Color> gradient) {
		this.gradient = gradient;
		this.visualizer = new FractalVisualizer(gradient);
		
		this.setSize(DEFAULT_NAVIGATOR_SIZE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.add(visualizer);
		setVisible(true);
		addKeyStrokes();
		
		//initializeGUI();
	}
	
	public void addKeyStrokes() {
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("T"), "info", e -> visualizer.toggleInfo());
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("L"), "loop", e -> toggleLoop());
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("M"), "menu", e -> openMenu());
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("I"), "imageSave", e -> saveImage());
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("V"), "videoSave", e -> saveVideo());
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("S"), "stillSave", e -> saveStillVideo());
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("P"), "toggleVideo", e -> video.togglePause());
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("G"), "gradient", e -> {
			//gradient = GradientFactory.randomGradient(20, 2, .1);
			gradient = GradientFactory.hotAndColdGradient(20, 5, 1, 0.1).loop();
			offset = 0;
			visualizer.updateGradient(gradient);
		});
		
		visualizer.addKeyStroke(KeyStroke.getKeyStroke('+'), "c+", e -> {
			FractalZoom zoom = visualizer.getNavigator().getZoom();
			zoom.setFractal(new FractalCeption(++test, MandelbrotSet.mandelbrotFractal(new Complex(7, 0.1))));
			System.out.println(test);
			visualizer.getNavigator().update();
		});
		visualizer.addKeyStroke(KeyStroke.getKeyStroke('-'), "c-", e -> {
			FractalZoom zoom = visualizer.getNavigator().getZoom();
			zoom.setFractal(new FractalCeption(--test, MandelbrotSet.mandelbrotFractal(new Complex(7, 0.1))));
			System.out.println(test);
			visualizer.getNavigator().update();
		});
		
		for(int i = 0 ; i <= 9 ; i++) {
			final int d = i;
			visualizer.addKeyStroke(KeyStroke.getKeyStroke("" + d), "toPercent" + d, e -> goToPercent(d));
		}
			
	}
	
	public void toggleLoop() {
		if(gradientLoopOn())
			loopGradient.interrupt();
		else {
			loopGradient = new Thread(() -> {
				try {
					while(true) {
						Thread.sleep(0);
						if(visualizer != null) {
							offset += 0.0001;
							visualizer.updateGradient(gradient.offset(offset));
						}
					}
				} catch (Exception e) {
				}
			});
			loopGradient.start();
		}
	}
	
	public boolean gradientLoopOn() {
		return loopGradient != null && loopGradient.isAlive();
	}
	
	public void openMenu() {
		if(menu != null)
			menu.dispose();
		menu = new MenuGUI(this);
	}
	
	public void goToPercent(int d) {
		visualizer.getNavigator().setPercent((double)d / 9);
	}
	
	public void saveImage() {
		ImageUtils.saveImage(visualizer.getImg(), ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\MandelbrotStuff\\images/Mandelbrot.png"));
	}
	
	public void saveVideo() {
		String videoPath = ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\MandelbrotStuff\\mp4/Mandelbrot.mp4");
		try {
			FractalZoom zoom = visualizer.getNavigator().getZoom().clone();
			zoom.setWidth(1920*2);
			zoom.setHeight(1080*2);
			video = new FractalZoomMP4(zoom, 60, 50, videoPath);
			if(gradientLoopOn())
				video.setGradientRange(p -> gradient.offset(p));
			else
				video.setGradient(gradient);
			video.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveStillVideo() {
		String videoPath = ImageUtils.getNextFileName("C:\\Users\\pedro\\Desktop\\MandelbrotStuff\\mp4/Mandelbrot.mp4");
		try {
			FractalZoom zoom = visualizer.getNavigator().getZoom().clone();
			zoom.setWidth(1920);
			zoom.setHeight(1080);
			FractalZoomMP4 mp4 = new FractalZoomMP4(zoom.valueAt(visualizer.getNavigator().getPercent()), 60, 50, videoPath);
			mp4.setGradientRange(p -> gradient.offset(p));
			mp4.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Gradient<Color> getGradient() {
		return gradient;
	}
	
	public FractalVisualizer getVisualizer() {
		return visualizer;
	}
	
}
	
	
	
