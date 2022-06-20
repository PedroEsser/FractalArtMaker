package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import gpuColorGradients.ColorGradient;
import guiUtils.ImagePanel;

public class GradientVisualizer extends ImagePanel{

	private ColorGradient gradient;
	
	public GradientVisualizer(ColorGradient gradient) {
		this.gradient = gradient;
	}
	
	private void paintImage() {
		new Thread(() -> {
			this.img = new BufferedImage(this.getPanelWidth(), this.getPanelHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = this.img.getGraphics();
			
			int x = 0;
			for(Color c : gradient.toGradient().toDiscrete(this.img.getWidth())) {
				g.setColor(c);
				g.drawLine(x, 0, x++, this.img.getHeight());
			}
			if(!needsResizing())
				SwingUtilities.invokeLater(() -> repaint());
		}).start();
	}
	
	public ColorGradient getGradient() {
		return gradient;
	}

	@Override
	protected void myResize() {
		paintImage();
	}
	
}
