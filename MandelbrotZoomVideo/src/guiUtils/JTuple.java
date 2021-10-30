package guiUtils;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class JTuple<L extends JComponent, R extends JComponent> extends JPanel{

	private L left;
	private R right;
	
	public JTuple(L left, R right) {
		this.left = left;
		this.right = right;
		this.setLayout(new BorderLayout(10, 20));
		this.left.setFont(new Font("Arial", Font.BOLD, 20));
		this.right.setFont(new Font("Arial", Font.BOLD, 20));
		this.add(this.left, BorderLayout.WEST);
		this.add(this.right, BorderLayout.CENTER);
	}
	
	public L getLeft() {
		return left;
	}
	
	public R getRight() {
		return right;
	}
	
}
