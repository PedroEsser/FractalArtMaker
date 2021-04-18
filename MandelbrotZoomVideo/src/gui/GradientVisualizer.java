package gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import gradients.Gradient;

public class GradientVisualizer extends JPanel{

	private Gradient gradient;
	
	public GradientVisualizer(Gradient gradient) {
		this.gradient = gradient;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int x = 0;
		for(Color c : gradient.getIterable(this.getWidth())) {
			g.setColor(c);
			g.drawLine(x, 0, x++, this.getHeight());
		}
	}
	
}
