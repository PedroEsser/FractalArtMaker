package features;

import static guiUtils.GUIUtils.getContrastColor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import fractal.FractalFrame;

public class CoordinateSystem{
	
	private static final int SPACING = 120;
	private static final Font font = new Font("Arial", Font.PLAIN, 12);

	public static void drawCoordinateSystem(BufferedImage img, FractalFrame f) {
		double inc = bestIncrement(f.getDelta());
		drawHorizontal(img, f, inc);
		drawVertical(img, f, inc);
	}
	
	private static void drawHorizontal(BufferedImage img, FractalFrame f, double inc) {
		double im = Math.floor(f.yToImag(0) / inc) * inc;
		int y = f.imagToY(im);
		int pixelInc = (int)(inc/f.getDelta());
		Graphics g = img.getGraphics();
		while(y < f.getHeight()) {
			g.setColor(new Color(255,  255,  255, 40));
			g.drawLine(0, y, f.getWidth(), y);
			int x = f.getWidth() - stringWidth(pretty(im), g)/2 - 8;
			Point coordY = new Point(x, y);
			writeCoord(im, coordY, img);
			im += inc;
			y += pixelInc;
		}
	}
	
	private static void drawVertical(BufferedImage img, FractalFrame f, double inc) {
		double re = Math.floor(f.xToReal(0) / inc) * inc;
		int x = f.realToX(re);
		int pixelInc = (int)(inc/f.getDelta());
		Graphics g = img.getGraphics();
		while(x < f.getWidth()) {
			g.setColor(new Color(255,  255,  255, 40));
			g.drawLine(x, 0, x, f.getHeight());
			Point coordX = new Point(x, img.getHeight() - 12);
			writeCoord(re, coordX, img);
			re += inc;
			x += pixelInc;
		}
	}
	
	private static double bestIncrement(double delta) {
		double aux = SPACING * delta;
		double log = Math.log10(aux);
		double x = Math.pow(10, log - Math.floor(log));
		
		if(x >= 1 && x < 2)			x = 1;
		else if(x >= 2 && x < 4) 	x = 2;
		else if(x >= 4 && x < 10) 	x = 5;
		return x * Math.pow(10, Math.floor(log));
	}

	private static void writeCoord(double value, Point p, BufferedImage img) {
		Graphics g = img.getGraphics();
	    FontMetrics metrics = g.getFontMetrics(font);
	    String text = pretty(value);
	    int stringWidth = metrics.stringWidth(text);
	    int x = p.x - stringWidth / 2;
	    int y = p.y - metrics.getHeight() / 2 + metrics.getAscent();
	    
	    Rectangle rect = new Rectangle(x, y - metrics.getAscent(), stringWidth, metrics.getHeight());
	    
	    g.setColor(getContrastColor(getAverageColor(img, rect)));
	    g.setFont(font);
	    g.drawString(text, x, y);
	}
	
	private static String pretty(double value) {
		String text = new DecimalFormat("0.########").format(value);
	    text.replaceFirst("0*$", "");
	    return text;
	}
	
	private static int stringWidth(String s, Graphics g) {
		return g.getFontMetrics(font).stringWidth(s);
	}
	
	private static Color getAverageColor(BufferedImage img, Rectangle rect) {
		int[] rgbSum = new int[3];
		int startX = Math.max(rect.x, 0), endX = Math.min(rect.x + rect.width, img.getWidth());
		int startY = Math.max(rect.y, 0), endY = Math.min(rect.y + rect.height, img.getHeight());
		for(int x = startX ; x < endX ; x++) {
			for(int y = startY ; y < endY ; y++) {
				rgbSum[0] += new Color(img.getRGB(x, y)).getRed();
				rgbSum[1] += new Color(img.getRGB(x, y)).getGreen();
				rgbSum[2] += new Color(img.getRGB(x, y)).getBlue();
			}
		}
		for(int i = 0 ; i < 3 ; i++)
			rgbSum[i] /= rect.height * rect.width;
		
		return new Color(rgbSum[0], rgbSum[1], rgbSum[2]);
	}
	
}
