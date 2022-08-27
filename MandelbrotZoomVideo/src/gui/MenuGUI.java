package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.nio.charset.MalformedInputException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import colorGradients_deprecated.ColorGradient;
import colorGradients_deprecated.RGBGradient;
import fractal.FractalFrame;
import fractal.FractalZoom;
import fractalKernels.BurningShipKernel;
import fractalKernels.ComplexPowerMandelbrotKernel;
import fractalKernels.FeatherFractal;
import fractalKernels.FractalKernel;
import fractalKernels.FractalParameter;
import fractalKernels.HybridFractal;
import fractalKernels.IntegerPowerMandelbrotKernel;
import fractalKernels.MandelTrig;
import fractalKernels.MandelbrotKernel;
import fractalKernels.RealPowerMandelbrotKernel;
import fractals_deprecated.Complex;
import gradient.LinearGradient;
import gradient.Gradient;
import guiUtils.FractalParameterGUI;
import guiUtils.JTuple;
import guiUtils.LabelOptionsTuple;
import guiUtils.LabelTuple;
import guiUtils.LabelValueTuple;
import guiUtils.NumericGradientPanel;
import scala.annotation.meta.param;

public class MenuGUI extends JFrame{

	public static final Dimension DEFAULT_MENU_SIZE = new Dimension(800, 500);
	private final FractalNavigatorGUI nav;
	
	private static String[] FRACTAL_VARIANTS = {"Mandelbrot", "Integer Power Mandelbrot", "Real Power Mandelbrot", 
			"Complex Power Mandelbrot", "Burning Ship", "MandelTrig", "Feather Fractal", "Hybrid Fractal"};
	
	JPanel mainPanel;
	LabelValueTuple iterations;
	LabelValueTuple delta;
	LabelValueTuple re;
	LabelValueTuple im;
	GradientVisualizer visualizer;
	LabelOptionsTuple fractalTypes;
	JPanel parameters;
	
	public MenuGUI(FractalNavigatorGUI nav) {
		super("Menu");
		this.nav = nav;
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(7, 1, 0, 8));
		
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
		FractalKernel fractal = nav.getVisualizer().getNavigator().getFrame().getKernel();
		fractalTypes.setOption(fractal.getName());
		fractalTypes.getRight().addActionListener(e -> fillParameters(getSelectedFractal()));
		mainPanel.add(fractalTypes);
		
		parameters = new JPanel();
		fillParameters(nav.getVisualizer().getFrame().getKernel());
		mainPanel.add(parameters);
		
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
		
		nav.getVisualizer().getNavigator().setParameters(iterations, delta, re, -im, getFractal());
	}
	
	private void fillParameters(FractalKernel kernel) {
		parameters.removeAll();
		List<FractalParameter> pars = kernel.getFractalParameters();
		parameters.setLayout(new GridLayout(pars.size(), 1, 0, 4));
		pars.forEach(p -> {
			FractalParameterGUI par = new FractalParameterGUI(p);
			parameters.add(par);
		});
		mainPanel.repaint();
	}
	
	private FractalKernel getFractal() {
		FractalKernel fractal = getSelectedFractal();
		for(Component c: parameters.getComponents()) {
			FractalParameterGUI parGUI = (FractalParameterGUI)c;
			fractal.editParameter(parGUI.getParameter());
		}
		return fractal;
	}
	
	private FractalKernel getSelectedFractal() {
		String selected = fractalTypes.getSelectedOption();
		switch(selected) {
			case "Mandelbrot":					return new MandelbrotKernel();
			case "Integer Power Mandelbrot":	return new IntegerPowerMandelbrotKernel();
			case "Real Power Mandelbrot":		return new RealPowerMandelbrotKernel();
			case "Complex Power Mandelbrot":	return new ComplexPowerMandelbrotKernel();
			case "Burning Ship":				return new BurningShipKernel();
			case "MandelTrig":					return new MandelTrig();
			case "Feather Fractal":				return new FeatherFractal();
			case "Hybrid Fractal":				return new HybridFractal();
			default: 							return null;
		}
	}
	
}
