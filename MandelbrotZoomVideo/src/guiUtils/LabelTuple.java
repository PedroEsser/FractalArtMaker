package guiUtils;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class LabelTuple<T extends JComponent> extends JTuple<JLabel, T>{
	
	public LabelTuple(String label, T comp) {
		super(constructJLabel(label), comp);
	}
	
	private static JLabel constructJLabel(String label) {
		JLabel l = new JLabel(label, SwingConstants.LEFT);
//		l.setBorder(new EmptyBorder(0,10,0,0));
		return l;
	}
	
	public String getLabel() {
		return getLeft().getText();
	}
	
	public void setText(String text) {
		getLeft().setText(text);
	}
	
}
