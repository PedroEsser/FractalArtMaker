package guiUtils;

import java.awt.Component;
import java.lang.reflect.Parameter;

import javax.swing.JScrollPane;

import fractal.FractalZoom;
import fractalKernels.FractalKernel;
import fractalKernels.FractalParameter;
import gradient.Gradient;
import utils.Tuple;

public class FractalParametersGUI extends JScrollPane{
	
	private FractalZoom zoom;
	private Weighted1DPanel parameterPanel;
	
	public FractalParametersGUI(FractalZoom zoom) {
		super();
		parameterPanel = new Weighted1DPanel(false);
		setZoom(zoom);
		this.setViewportView(parameterPanel);
		this.getVerticalScrollBar().setUnitIncrement(8);
	}
	
	public FractalZoom getZoom() {
		return zoom;
	}
	
	public void setZoom(FractalZoom zoom) {
		this.zoom = zoom;
		parameterPanel.removeAll();
		for(Tuple<String, Gradient<Double>> t : zoom.getParameterGradients()) {
			LabelTuple<JGradient> lt = new LabelTuple<JGradient>(t.t, new JGradient(t.u));
			parameterPanel.addComponent(lt);
		}
		updateUI();
	}
	
	public void updateParameters() {
		for(Component c : parameterPanel.getComponents()) {
			LabelTuple<JGradient> t = (LabelTuple<JGradient>)c;
			zoom.setParameter(t.getLabel(), t.getRight().getGradient());
		}
	}

}
