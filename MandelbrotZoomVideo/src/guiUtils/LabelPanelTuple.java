package guiUtils;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LabelPanelTuple extends JPanel{

	private JLabel label;
	private JPanel panel;
	
	public LabelPanelTuple(String label, JPanel panel) {
		this.setLayout(new BorderLayout(10, 30));
		this.label = new JLabel(label, SwingConstants.CENTER);
		this.label.setFont(new Font("Arial", Font.BOLD, 20));
		this.panel = panel;
		this.add(this.label, BorderLayout.WEST);
		this.add(panel, BorderLayout.CENTER);
	}
	
	
	
}
