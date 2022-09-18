package guiUtils;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

public class Weighted1DPanel extends JPanel{

	private GridBagConstraints constraints;
	private final boolean horizontal;
	
	public Weighted1DPanel(boolean horizontal, int... insets) {
		super();
		this.horizontal = horizontal;
		this.setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weighty = 1;
		constraints.weightx = 1;
		assert insets.length == 4 : "Must input 4 insets";
		constraints.insets = new Insets(insets[0], insets[1], insets[2], insets[3]);
	}
	
	public Weighted1DPanel(boolean horizontal) {
		this(horizontal, 0, 0, 0, 0);
	}
	
	public Weighted1DPanel() {
		this(true);
	}
	
	public void addComponent(Component comp, double weight) {
		if(horizontal) {
			constraints.gridx++;
			constraints.weightx = weight;
		}else {
			constraints.gridy++;
			constraints.weighty = weight;
		}
		this.add(comp, constraints);
	}
	
	public void addComponent(Component comp) {
		this.addComponent(comp, 1);
	}
	
}
