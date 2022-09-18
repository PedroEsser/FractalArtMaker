package guiUtils;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class JTuple<L extends JComponent, R extends JComponent> extends Weighted1DPanel{

	private L left;
	private R right;
	
	public JTuple(L left, R right) {
		super();
		this.left = left;
		this.right = right;
		this.left.setFont(new Font("Arial", Font.BOLD, 20));
		this.right.setFont(new Font("Arial", Font.BOLD, 20));
		this.addComponent(this.left, 1);
		this.addComponent(this.right, 5);
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
