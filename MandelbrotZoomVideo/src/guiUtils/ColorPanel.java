package guiUtils;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ColorPanel extends JLabel{
	
	public ColorPanel() {
		super();
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		setFont(GUIUtils.DEFAULT_FONT);
	}
	
	public void setColor(Color color) {
		this.setBackground(color);
		setForeground(GUIUtils.getContrastColor(color));
		String s = "RGB(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";
		this.setText(s);
	}
	
}
