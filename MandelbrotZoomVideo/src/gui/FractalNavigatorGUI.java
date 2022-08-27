package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import features.FractalParameterToggler;
import features.InfoFeature;
import gpuColorGradients.GradientFactory;
import static javax.swing.KeyStroke.getKeyStroke;


public class FractalNavigatorGUI{

	public static final Dimension DEFAULT_NAVIGATOR_SIZE = new Dimension(800, 600);
	private final FractalVisualizer visualizer;
	private final InfoFeature info;
	public final FractalParameterToggler toggler;
	private MyFrame frame;
	
	public FractalNavigatorGUI() {
		this.visualizer = new FractalVisualizer(this);
		this.info = new InfoFeature(this);
		this.toggler = new FractalParameterToggler(this);
		this.visualizer.addFeature(info);
		this.frame = new MyFrame(false);
		
		addKeyStrokes();
	}
	
	public void addKeyStrokes() {
		visualizer.addKeyStroke(getKeyStroke("T"), "info", e -> info.toggle());
		visualizer.addKeyStroke(getKeyStroke("M"), "menu", e -> new MenuGUI(this));
		visualizer.addKeyStroke(getKeyStroke("I"), "imageSave", e -> new ImageSaver(visualizer));
		visualizer.addKeyStroke(getKeyStroke("V"), "videoSave", e -> new VideoMaker(visualizer));
		visualizer.addKeyStroke(getKeyStroke("G"), "gradient", e -> randomiseGradient());
		visualizer.addKeyStroke(getKeyStroke("RIGHT"), "toggleRightParameter", e -> toggler.toggleRight());
		visualizer.addKeyStroke(getKeyStroke("LEFT"), "toggleLeftParameter", e -> toggler.toggleLeft());
		visualizer.addKeyStroke(getKeyStroke("UP"), "incParameter", e -> toggler.inc());
		visualizer.addKeyStroke(getKeyStroke("DOWN"), "decParameter", e -> toggler.dec());
//		visualizer.addKeyStroke(getKeyStroke("PLUS"), "increaseInitialIterations", e -> init.incInitialIterations());
//		visualizer.addKeyStroke(getKeyStroke("MINUS"), "decreaseInitialIterations", e -> init.decInitialIterations());
		visualizer.addKeyStroke(getKeyStroke("F"), "fullScreen", e -> toggleFullscreen());
		
		for(int i = 0 ; i <= 9 ; i++) {
			final int d = i;
			visualizer.addKeyStroke(getKeyStroke("" + d), "toPercent" + d, e -> goToPercent(d));
		}
			
	}
	
	public void randomiseGradient() {
		visualizer.updateGradient(GradientFactory.randomiseGradient());
	}
	
	public void goToPercent(int d) {
		visualizer.getNavigator().setPercent((double)d / 9);
	}
	
	public void toggleFullscreen() {
		frame.dispose();
		frame = new MyFrame(!frame.fullScreen);
	}

	public FractalVisualizer getVisualizer() {
		return visualizer;
	}
	
	private class MyFrame extends JFrame{
		
		private boolean fullScreen;
		
		public MyFrame(boolean fullScreen) {
			super();
			this.fullScreen = fullScreen;
			
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			if(fullScreen) { 
			    this.setUndecorated(true);
			    this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
			}else {
				this.setSize(DEFAULT_NAVIGATOR_SIZE);
				this.setLocationRelativeTo(null);
			}
			
			this.add(visualizer);
			setVisible(true);
		}
		
	}
	
}
	
	
	
