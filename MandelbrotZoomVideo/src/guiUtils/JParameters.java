package guiUtils;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.naming.NameNotFoundException;

public class JParameters extends Weighted1DPanel{

	public JParameters(boolean horizontal) {
		super(horizontal);
	}

	public JParameters() {
		this(true);
	}
	
	public void addParameter(String name, double value, Consumer<Double> changeCallback) {
		LabelValueTuple t = new LabelValueTuple(name, value);
		if(changeCallback != null)
			t.setValueChangeListener(changeCallback);
		this.addComponent(t);
	}
	
	public void addParameter(String name, double value) {
		this.addParameter(name, value, null);
	}
	
	public LabelValueTuple findParameter(String name) {
		for(Component c : this.getComponents())
			if(c instanceof LabelValueTuple && ((LabelValueTuple)c).getLabel().equals(name))
				return (LabelValueTuple)c;
		return null;
	}
	
	public double getValueFor(String name) {
		LabelValueTuple par = findParameter(name);
		if(par != null)
			return par.getValue();
		return -1;
	}
	
	public void setValueFor(String name, double value) {
		LabelValueTuple par = findParameter(name);
		if(par != null)
			par.setValue(value);
	}
	
	public List<LabelValueTuple> getParameters() {
		List<LabelValueTuple> pars = new ArrayList<LabelValueTuple>();
		for(Component c : this.getComponents())
			if(c instanceof LabelValueTuple)
				pars.add((LabelValueTuple)c);
		return pars;
	}
	
}
