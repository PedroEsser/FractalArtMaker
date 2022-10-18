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
import gradient.SinusoidalGradient;

public class JGradient extends Weighted1DPanel{
	
	private final LabelValueTuple valuePanels[];
	private final LabelOptionsTuple grothTypePanel;
	
//	public JGradient(double start, double end, boolean displayGrowth, GrowthRate gr) {
//		super();
//		valuePanels = new LabelValueTuple[4];
//		valuePanels[0] = new LabelValueTuple("Start:", start);
//		valuePanels[1] = new LabelValueTuple("End:", end);
//		valuePanels[2] = new LabelValueTuple("Phase offset:", 0);
//		valuePanels[3] = new LabelValueTuple("Frequency:", 1);
//		grothTypePanel = new LabelOptionsTuple("Growth", GrowthRate.values());
//		grothTypePanel.setSelectCallback(s -> handleSelect(s));
//		grothTypePanel.setOption(gr.name());
//		for(LabelValueTuple t : valuePanels)
//			this.addComponent(t, 5);
//		if(displayGrowth)
//			this.addComponent(grothTypePanel, 1);
//	}
	
	public JGradient(Gradient<Double> gradient, boolean displayGrowth) {
		super();
		GrowthRate gr = growthRateFor(gradient);
		System.out.println(gradient + ",  " + gr);
		valuePanels = new LabelValueTuple[4];
		for(int i = 0 ; i < 4 ; i++)
			valuePanels[i] = new LabelValueTuple("", 0);
		
		if(gr.name().equals(GrowthRate.SINUSOIDAL.name())) {
			SinusoidalGradient s = (SinusoidalGradient)gradient;
			valuePanels[0].setValue(s.getOffset());
			valuePanels[1].setValue(s.getAmplitude());
			valuePanels[2].setValue(s.getPhaseOffset());
			valuePanels[3].setValue(s.getFrequency());
		}else {
			valuePanels[0].setValue(gradient.getStart());
			valuePanels[1].setValue(gradient.getEnd());
		}
		
		grothTypePanel = new LabelOptionsTuple("Growth", GrowthRate.values());
		grothTypePanel.setSelectCallback(s -> handleSelect(s));
		grothTypePanel.setOption(gr.name());
		for(LabelValueTuple t : valuePanels)
			this.addComponent(t, 5);
		if(displayGrowth)
			this.addComponent(grothTypePanel, 1);
	}
	
	public JGradient(Gradient<Double> gradient) {
		this(gradient, true);
	}
	
	public JGradient(double start, double end) {
		this(new LinearGradient(start, end));
	}
	
	private void handleSelect(String option) {
		if(option.equals(GrowthRate.CONSTANT.name())) {
			valuePanels[0].setLabel("Value");
			for(int i = 1 ; i < 4 ; i++)
				valuePanels[i].setVisible(false);
		}else if(option.equals(GrowthRate.SINUSOIDAL.name())){
			valuePanels[0].setLabel("Offset");
			valuePanels[1].setLabel("Phase offset");
			valuePanels[2].setLabel("Amplitude");
			valuePanels[3].setLabel("Frequency");
			
			for(int i = 1 ; i < 4 ; i++)
				valuePanels[i].setVisible(true);
		}else {
			valuePanels[0].setLabel("Start");
			valuePanels[1].setLabel("End");
			valuePanels[1].setVisible(true);
			for(int i = 2 ; i < 4 ; i++)
				valuePanels[i].setVisible(false);
		}
		updateUI();
	}
	
	public Gradient<Double> getGradient() {
		if(grothTypePanel.getSelectedOption().equals(GrowthRate.LINEAR.toString())) 
			return new LinearGradient(valuePanels[0].getValue(), valuePanels[1].getValue());
		else if(grothTypePanel.getSelectedOption().equals(GrowthRate.EXPONENTIAL.toString()))
			return new ExponentialGradient(valuePanels[0].getValue(), valuePanels[1].getValue());
		else if(grothTypePanel.getSelectedOption().equals(GrowthRate.CONSTANT.toString()))
			return new Constant<Double>(valuePanels[0].getValue());
		else if(grothTypePanel.getSelectedOption().equals(GrowthRate.SINUSOIDAL.toString()))
			return new SinusoidalGradient(valuePanels[0].getValue(), valuePanels[1].getValue(), valuePanels[2].getValue(), valuePanels[3].getValue());
		return null;
	}
	
	private static GrowthRate growthRateFor(Gradient<Double> gradient, int confidence) {
		DiscreteGradient<Double> g = gradient.toDiscrete(confidence);
		
		if(gradient instanceof SinusoidalGradient)
			return GrowthRate.SINUSOIDAL;
		
		if(gradient instanceof LinearGradient || isLinear(g))
			return g.valueAt(1) - g.valueAt(0) == 0 ? GrowthRate.CONSTANT : GrowthRate.LINEAR;
		
		if(gradient instanceof ExponentialGradient || isExponential(g)) 
			return GrowthRate.EXPONENTIAL;
		
		return GrowthRate.SINUSOIDAL;
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
		return growthRateFor(gradient, 13);
	}
	
	public enum GrowthRate{
		LINEAR, EXPONENTIAL, CONSTANT, SINUSOIDAL;
	}
	
}
