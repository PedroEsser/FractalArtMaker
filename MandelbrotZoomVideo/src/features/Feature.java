package features;

import java.awt.image.BufferedImage;

import fractal.FractalFrame;
import gui.FractalVisualizer;

public abstract class Feature {

	protected final FractalVisualizer vis;
	
	public Feature(FractalVisualizer vis) {
		this.vis = vis;
	}
	
	protected FractalFrame getCurrentFrame() {
		return this.vis.getFrame();
	}
	
}