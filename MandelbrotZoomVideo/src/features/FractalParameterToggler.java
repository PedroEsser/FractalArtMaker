package features;

import java.util.List;

import fractalKernels.FractalParameter;
import gradient.Constant;
import gradient.Gradient;
import gui.FractalNavigatorGUI;
import gui.FractalVisualizer;
import utils.Tuple;

public class FractalParameterToggler extends Feature{

	private int index = 0;
	
	public FractalParameterToggler(FractalNavigatorGUI gui) {
		super(gui);
	}
	
	private List<Tuple<String, Gradient<Double>>>  getParameters(){
		return getCurrentZoom().getParameterGradients();
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
	
	public Tuple<String,Gradient<Double>> getSelectedParameter() {
		checkIndex();
		return getParameters().get(index);
	}
	
	
	public void inc() {
		Tuple<String,Gradient<Double>> selected = getSelectedParameter();
		FractalParameter par = getCurrentFrame().getKernel().getParameter(selected.t);
		par.inc();
		getParameters().set(index, new Tuple<String, Gradient<Double>>(selected.t, new Constant<Double>(par.getValue())));
		gui.getVisualizer().getNavigator().update();
	}
	
	public void dec() {
		Tuple<String,Gradient<Double>> selected = getSelectedParameter();
		FractalParameter par = getCurrentFrame().getKernel().getParameter(selected.t);
		par.dec();
		getParameters().set(index, new Tuple<String, Gradient<Double>>(selected.t, new Constant<Double>(par.getValue())));
		gui.getVisualizer().getNavigator().update();
	}

}
