package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;

public class GUIUtils {

	public static final double X_SCALING;
	public static final double Y_SCALING;
	
	static {
		GraphicsConfiguration asdf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		AffineTransform asfd2 = asdf.getDefaultTransform();
		X_SCALING = asfd2.getScaleX();
		Y_SCALING = asfd2.getScaleY();
	}
	
	
	public static Graphics2D ignoredWindowsScaleGraphics(Graphics g) {
		final Graphics2D g2d = (Graphics2D) g;
		final AffineTransform t = g2d.getTransform();
		t.setToScale(1, 1);
		g2d.setTransform(t);
		return g2d;
	}
	
	public static Color getContrastColor(Color color) {
		double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
		return y >= 128 ? Color.black : Color.white;
	}
	
}
