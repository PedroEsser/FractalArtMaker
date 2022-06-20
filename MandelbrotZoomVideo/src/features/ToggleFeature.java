package features;

import gui.FractalVisualizer;

public abstract class ToggleFeature extends VisualFeature{

	public ToggleFeature(FractalVisualizer vis) {
		super(vis);
	}

	protected boolean toggle = false;
	
	public boolean isOn() {
		return toggle;
	}
	
	public void toggle(boolean toggle) {
		this.toggle = toggle;
		this.vis.update();
	}
	
	public void toggle() {
		toggle(!toggle);
	}
	
}
