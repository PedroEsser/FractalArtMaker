package features;

import java.awt.image.BufferedImage;

import fractal.FractalFrame;
import fractal.FractalZoom;
import gui.FractalNavigatorGUI;
import gui.FractalVisualizer;

public abstract class Feature {

	protected final FractalNavigatorGUI gui;
	
	public Feature(FractalNavigatorGUI gui) {
		this.gui = gui;
	}
	
	protected FractalZoom getCurrentZoom() {
		return this.gui.getVisualizer().getNavigator().getZoom();
	}
	
	protected FractalFrame getCurrentFrame() {
		return this.gui.getVisualizer().getFrame();
	}
	
}