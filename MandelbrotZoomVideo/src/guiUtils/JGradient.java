package guiUtils;

import java.util.Iterator;

import javax.swing.JPanel;

import gradient.Gradient;
import gradient.LinearGradient;
import gradient.LogarithmicGradient;
import gradient.Constant;
import gradient.DiscreteGradient;
import gradient.ExponentialGradient;
import gradient.NumericGradient;

public class JGradient extends Weighted1DPanel{
	
	private final LabelValueTuple startPanel, endPanel;
	private final LabelOptionsTuple grothTtypePanel;
	
	public JGradient(double start, double end, boolean displayGrowth, GrowthRate gr) {
		super();
		startPanel = new LabelValueTuple("Start:", start);
		endPanel = new LabelValueTuple("End:", end);
		grothTtypePanel = new LabelOptionsTuple("Growth", GrowthRate.values());
		grothTtypePanel.setSelectCallback(s -> handleSelect(s));
		grothTtypePanel.setOption(gr.name());
		this.addComponent(startPanel, 5);
		this.addComponent(endPanel, 5);
		if(displayGrowth)
			this.addComponent(grothTtypePanel, 1);
	}
	
	public JGradient(double start, double end, boolean displayGrowth) {
		this(start, end, displayGrowth, GrowthRate.LINEAR);
	}
	
	public JGradient(Gradient<Double> gradient) {
		this(gradient.getStart(), gradient.getEnd(), true, growthRateFor(gradient));
	}
	
	public JGradient(double start, double end) {
		this(new LinearGradient(start, end));
	}
	
	private void handleSelect(String option) {
		if(option.equals(GrowthRate.CONSTANT.name())) {
			startPanel.setLabel("Value");
			endPanel.setVisible(false);
		}else {
			startPanel.setLabel("Start");
			endPanel.setVisible(true);
		}
		updateUI();
	}
	
	public Gradient<Double> getGradient() {
		if(grothTtypePanel.getSelectedOption().equals(GrowthRate.LINEAR.toString())) 
			return new LinearGradient(startPanel.getValue(), endPanel.getValue());
		else if(grothTtypePanel.getSelectedOption().equals(GrowthRate.EXPONENTIAL.toString()))
			return new ExponentialGradient(startPanel.getValue(), endPanel.getValue());
		else if(grothTtypePanel.getSelectedOption().equals(GrowthRate.CONSTANT.toString()))
			return new Constant<Double>(startPanel.getValue());
		return new LogarithmicGradient(startPanel.getValue(), endPanel.getValue());
	}
	
	private static GrowthRate growthRateFor(Gradient<Double> gradient, int confidence) {
		DiscreteGradient<Double> g = gradient.toDiscrete(confidence);
		
		if(isLinear(g))
			return g.valueAt(1) - g.valueAt(0) == 0 ? GrowthRate.CONSTANT : GrowthRate.LINEAR;
		
		if(isExponential(g)) 
			return GrowthRate.EXPONENTIAL;
		
		return GrowthRate.LOGARITHMIC;
	}
	
	private static boolean isLinear(DiscreteGradient<Double> g) {
		double diff = g.valueAt(1) - g.valueAt(0);
		for(int i = 1 ; i < g.getSteps() ; i++) {
			if(g.valueAt(i) - g.valueAt(i-1) != diff)
				return false;
		}
		return true;
	}
	
	private static boolean isExponential(DiscreteGradient<Double> g) {
		double ratio = g.valueAt(1) / g.valueAt(0);
		for(int i = 2 ; i < g.getSteps() ; i++) {
			if(!GUIUtils.areAlmostEqual(g.valueAt(i) / g.valueAt(i-1), ratio))
				return false;
		}
		return true;
	}
	
	private static GrowthRate growthRateFor(Gradient<Double> gradient) {
		return growthRateFor(gradient, 10);
	}
	
	public enum GrowthRate{
		LINEAR, EXPONENTIAL, CONSTANT, LOGARITHMIC;
	}
	
}
