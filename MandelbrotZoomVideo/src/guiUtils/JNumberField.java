package guiUtils;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class JNumberField extends JTextField{

	private final Color defaultColor;
	
	public JNumberField(double value) {
		super();
		this.setText(value + "");
		this.getDocument().addDocumentListener(new MyDocumentListener());
		this.defaultColor = this.getForeground();
	}
	
	public double getValue() {
		return Double.parseDouble(getText());
	}
	
	public boolean validParse() {
		try {
			getValue();
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}
	
	private void validateText() {
		this.setForeground(validParse() ? defaultColor : Color.red);
	}
	
	private class MyDocumentListener implements DocumentListener{

		@Override
		public void insertUpdate(DocumentEvent e) {
			validateText();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			validateText();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			validateText();
		}
		
	}
	
}
