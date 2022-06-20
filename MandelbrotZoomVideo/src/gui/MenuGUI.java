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

import colorGradients_deprecated.ColorGradient;
import colorGradients_deprecated.RGBGradient;
import fractal.Complex;
import fractal.FractalFrame;
import fractal.FractalZoom;
import gradient.LinearGradient;
import gradient.Gradient;
import guiUtils.JTuple;
import guiUtils.LabelOptionsTuple;
import guiUtils.LabelTuple;
import guiUtils.LabelValueTuple;
import guiUtils.NumericGradientPanel;
import kernel.BurningShipKernel;
import kernel.ComplexPowerMandelbrotKernel;
import kernel.IntegerPowerMandelbrotKernel;
import kernel.MandelTrig;
import kernel.MandelbrotKernel;
import kernel.RealPowerMandelbrotKernel;

public class MenuGUI extends JFrame{

	public static final Dimension DEFAULT_MENU_SIZE = new Dimension(800, 500);
	private final FractalNavigatorGUI nav;
	
	private static String[] FRACTAL_VARIANTS = {"Mandelbrot", "IntegerPowerMandelbrot", "RealPowerMandelbrot", "ComplexPowerMandelbrot", "BurningShip", "MandelTrig"};
	
	LabelValueTuple iterations;
	LabelValueTuple delta;
	LabelValueTuple re;
	LabelValueTuple im;
	GradientVisualizer visualizer;
	LabelOptionsTuple fractalTypes;
	
	public MenuGUI(FractalNavigatorGUI nav) {
		super("Menu");
		this.nav = nav;
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(6, 1, 0, 8));
		
		iterations = new LabelValueTuple("Iterations:", 0);
		mainPanel.add(iterations);
		
		delta = new LabelValueTuple("Delta:", 0);
		mainPanel.add(delta);
		
		re = new LabelValueTuple("Re:", 0);
		im = new LabelValueTuple("Im:", 0);
		re.setBorder(null);
		im.setBorder(null);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(1, 2, 20, 20));
		centerPanel.add(re);
		centerPanel.add(im);
		LabelTuple<JPanel> centerTuple = new LabelTuple("Center:", centerPanel);
		mainPanel.add(centerTuple);
		
		visualizer = new GradientVisualizer(nav.getVisualizer().getGradient());
		JPanel panel = new LabelTuple("Gradient: ", visualizer);
		mainPanel.add(panel);
		
		fractalTypes = new LabelOptionsTuple("Fractal:", FRACTAL_VARIANTS);
		fractalTypes.getRight().addActionListener(e -> handleFractalSelection());
		mainPanel.add(fractalTypes);
		
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
	
	private void handleFractalSelection() {
		String selected = fractalTypes.getRight().getSelectedItem().toString();
		FractalZoom zoom = nav.getVisualizer().getNavigator().getZoom();
		switch(selected) {
		case "Mandelbrot":zoom.setFractal(new MandelbrotKernel());
			break;
		case "IntegerPowerMandelbrot":zoom.setFractal(new IntegerPowerMandelbrotKernel(3));
			break;
		case "RealPowerMandelbrot":zoom.setFractal(new RealPowerMandelbrotKernel(2.5));
			break;
		case "ComplexPowerMandelbrot":zoom.setFractal(new ComplexPowerMandelbrotKernel(2, 0.001));
			break;
		case "BurningShip":zoom.setFractal(new BurningShipKernel());
			break;
		case "MandelTrig":zoom.setFractal(new MandelTrig());
			break;
		}
	}
	
}
