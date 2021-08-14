package guiUtils;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LabelTextFieldTuple extends JPanel{
	
	final JLabel label;
	final JTextField textField;
	
	public LabelTextFieldTuple(String label, String value) {
		super();
		
		this.label = new JLabel(label, SwingConstants.CENTER);
		this.label.setFont(new Font("Arial", Font.BOLD, 20));
		this.textField = new JTextField(value);
		this.textField.setFont(new Font("Arial", Font.BOLD, 16));
		
		this.setLayout(new BorderLayout(20, 0));
		this.add(this.label, BorderLayout.WEST);
		this.add(textField, BorderLayout.CENTER);
		
	}
	
	public void setLabelText(String label) {
		this.label.setText(label);
	}
	
	public void setTextFieldText(String label) {
		this.textField.setText(label);
	}
	
	public String getLabelText() {
		return this.label.getText();
	}
	
	public String getTextFieldText() {
		return this.textField.getText();
	}
	
}
