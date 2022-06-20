package guiUtils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;

public class GUIUtils {

	public static double X_SCALING;
	public static double Y_SCALING;
	
	private static void adjustScalings() {
		GraphicsConfiguration asdf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		AffineTransform asfd2 = asdf.getDefaultTransform();
		X_SCALING = asfd2.getScaleX();
		Y_SCALING = asfd2.getScaleY();
	}
	
	public static Graphics2D ignoredWindowsScaleGraphics(Graphics g) {
		adjustScalings();
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
	
//	public static Color getContrastColor(Color color) {
//		float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
//		hsb[0] = (hsb[0] + .5f) % 1;
//		hsb[1] = 1;
//		hsb[2] = 1 - hsb[2];
//		return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
//	}
	
}
