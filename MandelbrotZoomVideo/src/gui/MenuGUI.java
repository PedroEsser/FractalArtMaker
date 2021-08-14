package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import gradient.LinearGradient;
import gradient.Gradient;
import gradients.ColorGradient;
import gradients.RGBGradient;
import guiUtils.LabelPanelTuple;
import guiUtils.LabelTextFieldTuple;
import guiUtils.NumericRangePanel;
import logic.Complex;
import logic.FractalFrame;

public class MenuGUI extends JFrame{

	public static final Dimension DEFAULT_MENU_SIZE = new Dimension(800, 500);
	private final FractalNavigatorGUI nav;
	
	LabelTextFieldTuple iterations;
	LabelTextFieldTuple delta;
	LabelTextFieldTuple re;
	LabelTextFieldTuple im;
	GradientVisualizer visualizer;
	
	public MenuGUI(FractalNavigatorGUI nav) {
		super("Menu");
		this.nav = nav;
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(5, 1, 30, 20));
		
		iterations = new LabelTextFieldTuple("Iterations:", "");
		mainPanel.add(iterations);
		
		delta = new LabelTextFieldTuple("Delta:", "");
		mainPanel.add(delta);
		
		re = new LabelTextFieldTuple("Re:", "");
		im = new LabelTextFieldTuple("Im:", "");
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(1, 2, 20, 20));
		centerPanel.add(re);
		centerPanel.add(im);
		LabelPanelTuple centerTuple = new LabelPanelTuple("Center:", centerPanel);
		mainPanel.add(centerTuple);
		
		visualizer = new GradientVisualizer(nav.getGradient());
		JPanel panel = new LabelPanelTuple("Current Gradient: ", visualizer);
		mainPanel.add(panel);
		
		JButton updateButton = new JButton("Update");
		updateButton.setFont(new Font("Arial", Font.BOLD, 20));
		updateButton.addActionListener(e -> updateNavigator(e));
		mainPanel.add(updateButton);
		this.add(mainPanel); 
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setSize(DEFAULT_MENU_SIZE);
		setVisible(true);
	}
	
	@Override
	public void dispose() {
		this.setVisible(false);
	}
	
	@Override
	public void setVisible(boolean b) {
		if(b) {
			FractalFrame frame = nav.getVisualizer().getNavigator().getFrame();
			iterations.setTextFieldText(frame.getMaxIterations() + "");
			delta.setTextFieldText(frame.getDelta() + "");
			Complex center = frame.getCenter();
			re.setTextFieldText(center.getRe() + "");
			im.setTextFieldText(-center.getIm() + "");
		}
		super.setVisible(b);
	}
	
	private void updateNavigator(ActionEvent e) {
		this.setVisible(false);
		
		double iterations = Double.parseDouble(this.iterations.getTextFieldText());
		double delta = Double.parseDouble(this.delta.getTextFieldText());
		double re = Double.parseDouble(this.re.getTextFieldText());
		double im = Double.parseDouble(this.im.getTextFieldText());
		
		nav.getVisualizer().getNavigator().setParameters(iterations, delta, re, -im);
	}
	
}
