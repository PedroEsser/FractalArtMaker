package features;

import java.awt.image.BufferedImage;

import gui.FractalNavigatorGUI;
import gui.FractalVisualizer;

public class SingleUseFeature extends VisualFeature{

	public SingleUseFeature(FractalNavigatorGUI gui) {
		super(gui);
	}

	protected boolean used = false;
	
	public void activate() {
		used = false;
		this.gui.getVisualizer().update();
	}
	
	@Override
	public void show(BufferedImage g) {
		if(!used)
			used = true;
	}

}
