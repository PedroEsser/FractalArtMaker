package guiUtils;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LabelPanelTuple extends JTuple<JLabel, JPanel>{
	
	public LabelPanelTuple(String label, JPanel panel) {
		super(new JLabel(label, SwingConstants.CENTER), panel);
	}
	
	
	
}
