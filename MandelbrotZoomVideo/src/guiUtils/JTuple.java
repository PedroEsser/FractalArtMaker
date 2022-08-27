package guiUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class JTuple<L extends JComponent, R extends JComponent> extends JPanel{

	private L left;
	private R right;
	
	public JTuple(L left, R right) {
		this.left = left;
		this.right = right;
//		this.setLayout(new BorderLayout(10, 20));
		this.setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.BOTH;
		cons.weighty = 1;
		cons.weightx = 1;
		this.left.setFont(new Font("Arial", Font.BOLD, 20));
		this.right.setFont(new Font("Arial", Font.BOLD, 20));
//		this.right.setBackground(Color.WHITE);
//		this.add(this.left, BorderLayout.WEST);
//		this.add(this.right, BorderLayout.CENTER);
		cons.gridy = 0;
		cons.gridx = 0;
		this.add(this.left, cons);
		cons.gridy = 0;
		cons.gridx = 1;
		cons.weightx = 5;
		this.add(this.right, cons);
		this.left.setBorder(new EmptyBorder(0,10,0,0));
		this.right.setBorder(new EmptyBorder(0,10,0,0));
		this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
	}
	
	public L getLeft() {
		return left;
	}
	
	public R getRight() {
		return right;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.left.setEnabled(enabled);
		this.right.setEnabled(enabled);
	}
	
}
