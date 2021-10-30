package guiUtils;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

import gradient.Gradient;

public class NumericGradientPanel extends JPanel{

	private Gradient<Double> numericRange;
	private LabelValueTuple start, end;
	
	public NumericGradientPanel(Gradient<Double> numericRange) {
		super();
		this.numericRange = numericRange;
		this.setLayout(new GridLayout(1, 2, 0, 20));
		this.start = new LabelValueTuple("Start", numericRange.getStart());
		this.end = new LabelValueTuple("End", numericRange.getEnd());
		this.add(start);
		this.add(end);
		this.setMinimumSize(new Dimension(600, 60));
		this.setMaximumSize(new Dimension(600, 100));
	}
	
}
