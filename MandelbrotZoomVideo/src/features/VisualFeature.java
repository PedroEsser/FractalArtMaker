package features;

import java.awt.image.BufferedImage;

import fractal.FractalFrame;
import gui.FractalVisualizer;

public abstract class VisualFeature extends Feature{

	public VisualFeature(FractalVisualizer vis) {
		super(vis);
	}

	public abstract void show(BufferedImage g);
	
}
