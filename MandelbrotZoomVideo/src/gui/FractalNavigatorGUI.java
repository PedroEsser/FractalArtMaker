package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import gpuColorGradients.ColorGradient;
import gpuColorGradients.GradientFactory;
import gradient.Gradient;
import logic.Complex;
import logic.FractalZoom;
import utils.ImageUtils;
import video.FractalVideo;
import video.FractalZoomMP4;

public class FractalNavigatorGUI extends JFrame{

	public static final Dimension DEFAULT_NAVIGATOR_SIZE = new Dimension(800, 600);
	private ColorGradient gradient;
	private final FractalVisualizer visualizer;
	private MenuGUI menu;
	private FractalVideo video;
	
	private Thread loopGradient;
	private double shift = 1;
	
//	private MandelbrotNavigator navigator; 
	
	public FractalNavigatorGUI(ColorGradient gradient) {
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
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("M"), "menu", e -> openMenu());
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("I"), "imageSave", e -> saveImage());
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("V"), "videoSave", e -> new VideoMaker(visualizer));
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("P"), "toggleVideo", e -> video.togglePause());
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("PLUS"), "incShift", e -> shift += .02);
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("MINUS"), "decShift", e -> shift -= .02);
		visualizer.addKeyStroke(KeyStroke.getKeyStroke("G"), "gradient", e -> randomiseGradient());
		
		for(int i = 0 ; i <= 9 ; i++) {
			final int d = i;
			visualizer.addKeyStroke(KeyStroke.getKeyStroke("" + d), "toPercent" + d, e -> goToPercent(d));
		}
			
	}
	
	public void randomiseGradient() {
		if(Math.random() < .5)
			gradient = GradientFactory.randomMixWarmAndCool();
		else
			gradient = GradientFactory.randomGradients();
		gradient.bounce(10);
		visualizer.updateGradient(gradient);
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
	
	public ColorGradient getGradient() {
		return gradient;
	}
	
	public FractalVisualizer getVisualizer() {
		return visualizer;
	}
	
}
	
	
	
