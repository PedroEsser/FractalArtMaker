package guiUtils;

import java.lang.reflect.Parameter;

import fractalKernels.FractalParameter;

public class FractalParameterGUI extends JTuple<LabelValueTuple, LabelValueTuple>{

	private final FractalParameter parameter;
	
	public FractalParameterGUI(FractalParameter p) {
		super(new LabelValueTuple(p.name, p.getValue()), new LabelValueTuple("Inc", p.getInc()));
		this.parameter = p;
	}
	
	public FractalParameter getParameter() {
		parameter.setValue(getLeft().getValue());
		parameter.setInc(getRight().getValue());
		return parameter;
	}

}
