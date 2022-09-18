package gui;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;

import features.FractalParameterToggler;
import features.InfoFeature;
import gpuColorGradients.GradientFactory;
import gpuColorGradients.MultiGradient;

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
		this.frame = new MyFrame(false, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
		
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
		visualizer.addKeyStroke(getKeyStroke("PLUS"), "offset+", e -> offsetGradient(1f/512));
		visualizer.addKeyStroke(getKeyStroke("MINUS"), "offset-", e -> offsetGradient(-1f/512));
		visualizer.addKeyStroke(getKeyStroke("DOWN"), "decParameter", e -> toggler.dec());
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
		frame = frame.toggleFullscreen();
	}
	
	public void offsetGradient(float dOffset) {
		MultiGradient g = visualizer.getGradient();
		g.offseted(g.getOffset() + dOffset);
		visualizer.getNavigator().update();
	}

	public FractalVisualizer getVisualizer() {
		return visualizer;
	}
	
	private class MyFrame extends JFrame{
		
		public MyFrame(boolean fullScreen, GraphicsDevice device) {
			super();
			
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Rectangle r = device.getDefaultConfiguration().getBounds();
			if(fullScreen) { 
			    this.setUndecorated(true);
			    this.setSize(r.width, r.height);
			    this.setLocation(r.x, r.y);
			}else {
				this.setSize(DEFAULT_NAVIGATOR_SIZE);
				this.setLocation(r.x + (r.width - this.getWidth())/2, r.y + (r.height - this.getHeight())/2);
			}
			
			this.add(visualizer);
			setVisible(true);
		}
		
		public MyFrame() {
			this(false, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
		}
		
		public MyFrame toggleFullscreen() {
			this.dispose();
			return new MyFrame(!this.isUndecorated(), this.getGraphicsConfiguration().getDevice());
		}
		
	}
	
}
	
	
	
