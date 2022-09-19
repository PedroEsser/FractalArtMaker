package guiUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

public class GUIUtils {

	public static final Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 16);
	
	public static Graphics2D getGraphicsIgnoringScaling(Graphics g) {
		final Graphics2D g2d = (Graphics2D) g;
		final AffineTransform t = g2d.getTransform();
		t.setToScale(1, 1);
		g2d.setTransform(t);
		return g2d;
	}
	
	public static Color getContrastColor(Color color) {
		double l = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
		return l >= 128 ? Color.black : Color.white;
	}
	
	public static String prettyDouble(double d, int decimals) {
		return String.format("%." + decimals + "f", d);
	}
	
	public static String prettyDouble(double d) {
		return prettyDouble(d, 5);
	}
	
	public static boolean areAlmostEqual(double maxError, double... vals) {
		for(int i = 0 ; i < vals.length - 1 ; i++) {
			for(int j = i+1 ; i < vals.length ; i++) {
				double dist = Math.abs(vals[i] - vals[j]);
				if(dist > 1e-06)
					return false;
			}
		}
		return true;
	}
	
	public static double roundIfCloseToMultipleOf(double value, double m, double maxDiff) {
		double sign = value < 0 ? -1 : 1;
		value = Math.abs(value);
		int quotient = (int)(value / m);
		double mod = value - quotient * m;
		if(mod < maxDiff)
			return quotient * m * sign;
		if(m - mod < maxDiff)
			return (quotient + 1) * m * sign;
		return value * sign;
	}
	
	public static double roundIfCloseToMultipleOf(double value, double m) {
		return roundIfCloseToMultipleOf(value, m, 1e-05);
	}
	
	public static JButton button(String name, Consumer<ActionEvent> action) {
		JButton b = new JButton(name);
		b.setFont(DEFAULT_FONT);
		b.addActionListener(e -> action.accept(e));
		return b;
	}
	
	public static JButton button(String name) {
		return button(name, a -> {});
	}
	
	public static JLabel label(String name, Font font) {
		JLabel b = new JLabel(name);
		b.setHorizontalAlignment(SwingConstants.CENTER);
		b.setFont(font);
		return b;
	}
	
	public static JLabel label(String name, int fontSize) {
		return label(name, new Font("Arial", Font.BOLD, fontSize));
	}
	
	public static JLabel label(String name) {
		return label(name, DEFAULT_FONT);
	}
	
	public static String popup(Component parentComponent, String message, String title) {
		return JOptionPane.showInputDialog(parentComponent, message, title, JOptionPane.OK_CANCEL_OPTION);
	}
	
}
