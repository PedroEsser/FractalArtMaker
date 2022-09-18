package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import gpuColorGradients.ColorGradient;
import gradient.Gradient;
import guiUtils.ImagePanel;
import guiUtils.Panel;

public class GradientVisualizer extends Panel{

	private ColorGradient gradient;
	
	public GradientVisualizer(ColorGradient gradient) {
		this.gradient = gradient;
	}
	
	public void setGradient(ColorGradient gradient) {
		this.gradient = gradient;
		repaint();
	}
	
	public ColorGradient getGradient() {
		return gradient;
	}
	
	@Override
	protected void myPaint(Graphics2D g) {
		Rectangle clip = g.getClipBounds();
		if(gradient != null){
			for(int x = 0 ; x < clip.width ; x++) {
				g.setColor(gradient.colorAt((float)x/clip.width));
				g.drawLine(clip.x + x, clip.y, clip.x + x, clip.height + clip.y);
			}
		}else {
			g.setColor(Color.black);
			g.fill(clip);
		}
	}
	
	public double percentAtX(int x) {
		return x * xScaling / this.getPanelWidth();
	}
	
	public Color colorAtX(int x) {
		return gradient.colorAt((float)(x*xScaling) / this.getPanelWidth());
	}
	
	public static void showcase(ColorGradient gradient) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(800, 200);
		f.setLocationRelativeTo(null);
		f.add(new GradientVisualizer(gradient));
		f.setVisible(true);
	}
	
}
