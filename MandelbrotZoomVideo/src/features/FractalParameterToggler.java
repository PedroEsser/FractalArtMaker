package features;

import java.util.List;

import fractalKernels.FractalParameter;
import gui.FractalNavigatorGUI;
import gui.FractalVisualizer;

public class FractalParameterToggler extends Feature{

	private int index = 0;
	
	public FractalParameterToggler(FractalNavigatorGUI gui) {
		super(gui);
	}
	
	private List<FractalParameter> getParameters(){
		return getCurrentFrame().getKernel().getFractalParameters();
	}
	
	private void checkIndex() {
		if(index > getParameters().size())
			index = 0;
	}
	
	public void toggleRight() {
		index = (index + 1) % getParameters().size();
		gui.getVisualizer().update();
	}
	
	public void toggleLeft() {
		index = (index - 1 + getParameters().size()) % getParameters().size();
		gui.getVisualizer().update();
	}
	
	public FractalParameter getSelectedParameter() {
		checkIndex();
		return getParameters().get(index);
	}
	
	public void inc() {
		getSelectedParameter().inc();
		gui.getVisualizer().getNavigator().update();
	}
	
	public void dec() {
		getSelectedParameter().dec();
		gui.getVisualizer().getNavigator().update();
	}

}
