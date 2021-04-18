package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

import gradients.Gradient;

public class MenuGUI extends JFrame{

	public static final int DEFAULT_WIDTH = 600;
	public static final int DEFAULT_HEIGHT = 800;
	private GradientVisualizer backGround;
	
	public MenuGUI(Gradient gradient) {
		super();
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setBackground(gradient);
		add(backGround);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void setBackground(Gradient gradient) {
		if(backGround != null)
			this.remove(backGround);
		
		this.backGround = new GradientVisualizer(gradient);
		this.add(backGround);
	}
	
}
