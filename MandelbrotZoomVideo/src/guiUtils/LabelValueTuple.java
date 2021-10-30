package guiUtils;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class LabelValueTuple extends JTuple<JLabel, JTextField>{
	
	public LabelValueTuple(String label, double value) {
		super(new JLabel(label), new JTextField(value + ""));
	}
	
	public void setLabel(String label) {
		this.getLeft().setText(label);
	}
	
	public void setValue(double value) {
		this.getRight().setText(value + "");
	}
	
	public String getLabel() {
		return this.getLeft().getText();
	}
	
	public double getValue() {
		return Double.parseDouble(this.getRight().getText());
	}
	
}
