package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

import gradients.Gradient;
import gradients.RGBGradient;
import rangeUtils.Range;

public class MenuGUI extends JFrame{

	public static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(800, 600);
	private GradientVisualizer backGround;
	
	public MenuGUI(Range<Color> gradient) {
		super();
		this.setSize(DEFAULT_WINDOW_SIZE);
		setBackground(gradient);
		backGround.setLayout(new GridLayout(5, 1));
		
//		GradientVisualizer test = new GradientVisualizer(new RGBGradient());
//		test.setLayout(new BorderLayout());
//		
//		JButton button1 = new JButton("Bruh");
//		test.add(button1, BorderLayout.EAST);
//		
//		JButton button2 = new JButton("Bruh2");
//		test.add(button2, BorderLayout.WEST);
//		
//		backGround.add(test);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
	}
	
	private void setBackground(Range<Color> gradient) {
		if(backGround != null)
			this.remove(backGround);
		
		this.backGround = new GradientVisualizer(gradient);
		this.add(backGround);
	}
	
}
