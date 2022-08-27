package features;

import java.awt.image.BufferedImage;

import fractal.FractalFrame;
import gui.FractalNavigatorGUI;
import gui.FractalVisualizer;

public abstract class Feature {

	protected final FractalNavigatorGUI gui;
	
	public Feature(FractalNavigatorGUI gui) {
		this.gui = gui;
	}
	
	protected FractalFrame getCurrentFrame() {
		return this.gui.getVisualizer().getFrame();
	}
	
}