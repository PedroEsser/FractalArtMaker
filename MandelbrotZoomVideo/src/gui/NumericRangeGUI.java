package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import rangeUtils.Range;

public class NumericRangeGUI extends JPanel{

	private Range<Double> numericRange;
	private LabelTextFieldTuple start, end;
	
	public NumericRangeGUI(Range<Double> numericRange) {
		super();
		this.numericRange = numericRange;
		this.setLayout(new GridLayout(1, 2));
		this.start = new LabelTextFieldTuple("Start", numericRange.getStart().toString());
		this.end = new LabelTextFieldTuple("End", numericRange.getEnd().toString());
		this.add(start);
		this.add(end);
		Dimension dim = new Dimension(600, 60);
		this.setMinimumSize(new Dimension(600, 60));
		this.setMaximumSize(new Dimension(600, 100));
	}
	
	public class LabelTextFieldTuple extends JPanel{
		
		final JLabel label;
		final JTextField textField;
		
		public LabelTextFieldTuple(String label, String value) {
			super();
			
			this.label = new JLabel(label, SwingConstants.CENTER);
			this.label.setFont(new Font("Arial", Font.BOLD, 16));
			this.textField = new JTextField(value);
			this.textField.setFont(new Font("Arial", Font.BOLD, 16));
			
			this.setLayout(new GridLayout(1, 2));
			this.add(this.label);
			this.add(textField);
			
		}
		
		public void setLabelText(String label) {
			this.label.setText(label);
		}
		
		public void setTextFieldText(String label) {
			this.textField.setText(label);
		}
		
	}
	
}
