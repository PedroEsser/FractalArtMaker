package guiUtils;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

import gradient.Gradient;

public class NumericRangePanel extends JPanel{

	private Gradient<Double> numericRange;
	private LabelTextFieldTuple start, end;
	
	public NumericRangePanel(Gradient<Double> numericRange) {
		super();
		this.numericRange = numericRange;
		this.setLayout(new GridLayout(1, 2, 0, 20));
		this.start = new LabelTextFieldTuple("Start", numericRange.getStart().toString());
		this.end = new LabelTextFieldTuple("End", numericRange.getEnd().toString());
		this.add(start);
		this.add(end);
		this.setMinimumSize(new Dimension(600, 60));
		this.setMaximumSize(new Dimension(600, 100));
	}
	
}
