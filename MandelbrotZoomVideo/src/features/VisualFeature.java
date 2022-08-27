package features;

import java.awt.image.BufferedImage;

import fractal.FractalFrame;
import gui.FractalNavigatorGUI;
import gui.FractalVisualizer;

public abstract class VisualFeature extends Feature{

	public VisualFeature(FractalNavigatorGUI gui) {
		super(gui);
	}

	public abstract void show(BufferedImage g);
	
}
