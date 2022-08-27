package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import gpuColorGradients.ColorGradient;
import gpuColorGradients.ThreeChannelGradient;
import gradient.Gradient;
import guiUtils.ImagePanel;

public class GradientVisualizer extends ImagePanel{

	private Gradient<Color> gradient;
	
	public GradientVisualizer(Gradient<Color> gradient) {
		this.gradient = gradient;
	}
	
	private void paintImage() {
		new Thread(() -> {
			this.img = new BufferedImage(this.getPanelWidth(), this.getPanelHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = this.img.getGraphics();
			
			int x = 0;
			for(Color c : gradient.toDiscrete(this.img.getWidth())) {
				g.setColor(c);
				g.drawLine(x, 0, x++, this.img.getHeight());
			}
			if(!needsResizing())
				SwingUtilities.invokeLater(() -> repaint());
		}).start();
	}
	
	public Gradient<Color> getGradient() {
		return gradient;
	}

	@Override
	protected void myResize() {
		paintImage();
	}
	
	public static void showcase(Gradient<Color> gradient) {
		JFrame f = new JFrame();
		f.setSize(800, 200);
		f.setLocationRelativeTo(null);
		f.add(new GradientVisualizer(gradient));
		f.setVisible(true);
	}
	
}
