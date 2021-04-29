package gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import gradients.Gradient;
import rangeUtils.Range;

public class GradientVisualizer extends JPanel{

	private Range<Color> gradient;
	
	public GradientVisualizer(Range<Color> gradient) {
		this.gradient = gradient;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int x = 0;
		for(Color c : gradient.toDiscrete(this.getWidth())) {
			g.setColor(c);
			g.drawLine(x, 0, x++, this.getHeight());
		}
	}
	
}
