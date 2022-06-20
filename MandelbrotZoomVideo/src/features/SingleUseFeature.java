package features;

import java.awt.image.BufferedImage;

import gui.FractalVisualizer;

public class SingleUseFeature extends VisualFeature{

	public SingleUseFeature(FractalVisualizer vis) {
		super(vis);
	}

	protected boolean used = false;
	
	public void activate() {
		used = false;
		this.vis.update();
	}
	
	@Override
	public void show(BufferedImage g) {
		if(!used)
			used = true;
	}

}
