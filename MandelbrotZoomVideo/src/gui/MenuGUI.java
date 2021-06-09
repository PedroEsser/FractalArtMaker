package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import gradients.Gradient;
import gradients.RGBGradient;
import logic.MandelbrotFrame;
import rangeUtils.LinearRange;
import rangeUtils.Range;

public class MenuGUI extends JFrame{

	public static final Dimension DEFAULT_MENU_SIZE = new Dimension(800, 500);
	
	public MenuGUI(Range<Color> gradient) {
		super("Menu");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(5, 1, 5, 5));
		
		
		//mainPanel.add(new MandelbrotVisualizer(gradient));
		for(int i = 0 ; i < 4 ; i++) {
			JPanel panel = new LabelPanelTuple("Boom:", new NumericRangeGUI(new LinearRange(0, i)));
			mainPanel.add(panel);
		}
		JPanel panel = new LabelPanelTuple("Current Gradient: ", new GradientVisualizer(gradient));
		mainPanel.add(panel);
		this.add(mainPanel); 
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setSize(DEFAULT_MENU_SIZE);
		setVisible(true);
	}
	
	
}
