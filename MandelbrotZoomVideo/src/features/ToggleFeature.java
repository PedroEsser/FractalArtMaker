package features;

import gui.FractalNavigatorGUI;
import gui.FractalVisualizer;

public abstract class ToggleFeature extends VisualFeature{

	public ToggleFeature(FractalNavigatorGUI gui) {
		super(gui);
	}

	protected boolean toggle = false;
	
	public boolean isOn() {
		return toggle;
	}
	
	public void toggle(boolean toggle) {
		this.toggle = toggle;
		this.gui.getVisualizer().update();
	}
	
	public void toggle() {
		toggle(!toggle);
	}
	
}
