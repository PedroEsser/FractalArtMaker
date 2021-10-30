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

import colorGradients.ColorGradient;
import colorGradients.RGBGradient;
import gradient.LinearGradient;
import gradient.Gradient;
import guiUtils.JTuple;
import guiUtils.LabelPanelTuple;
import guiUtils.LabelValueTuple;
import guiUtils.NumericGradientPanel;
import logic.Complex;
import logic.FractalFrame;

public class MenuGUI extends JFrame{

	public static final Dimension DEFAULT_MENU_SIZE = new Dimension(800, 500);
	private final FractalNavigatorGUI nav;
	
	LabelValueTuple iterations;
	LabelValueTuple delta;
	LabelValueTuple re;
	LabelValueTuple im;
	GradientVisualizer visualizer;
	
	public MenuGUI(FractalNavigatorGUI nav) {
		super("Menu");
		this.nav = nav;
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(5, 1, 30, 20));
		
		iterations = new LabelValueTuple("Iterations:", 0);
		mainPanel.add(iterations);
		
		delta = new LabelValueTuple("Delta:", 0);
		mainPanel.add(delta);
		
		re = new LabelValueTuple("Re:", 0);
		im = new LabelValueTuple("Im:", 0);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(1, 2, 20, 20));
		centerPanel.add(re);
		centerPanel.add(im);
		JTuple centerTuple = new JTuple(new JLabel("Center:"), centerPanel);
		mainPanel.add(centerTuple);
		
		visualizer = new GradientVisualizer(nav.getGradient());
		JPanel panel = new LabelPanelTuple("Gradient: ", visualizer);
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
			iterations.setValue(frame.getMaxIterations());
			delta.setValue(frame.getDelta());
			Complex center = frame.getCenter();
			re.setValue(center.getRe());
			im.setValue(-center.getIm());
		}
		super.setVisible(b);
	}
	
	private void updateNavigator(ActionEvent e) {
		this.setVisible(false);
		
		double iterations = this.iterations.getValue();
		double delta = this.delta.getValue();
		double re = this.re.getValue();
		double im = this.im.getValue();
		
		nav.getVisualizer().getNavigator().setParameters(iterations, delta, re, -im);
	}
	
}
