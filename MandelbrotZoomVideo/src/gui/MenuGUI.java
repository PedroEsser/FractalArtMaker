package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import fractal.FractalFrame;
import fractalKernels.*;
import fractals_deprecated.Complex;
import gpuColorGradients.GradientFactoryGUI;
import gradient.Gradient;
import gradient.NumericGradient;
import guiUtils.FractalParameterGUI;
import guiUtils.JGradient;
import guiUtils.JTuple;
import guiUtils.LabelOptionsTuple;
import guiUtils.LabelTuple;
import guiUtils.LabelValueTuple;
import guiUtils.Weighted1DPanel;

public class MenuGUI extends JFrame{

	public static final Dimension DEFAULT_MENU_SIZE = new Dimension(1000, 600);
	private final FractalNavigatorGUI nav;
	
	private static String[] FRACTAL_VARIANTS = {"Mandelbrot", "Integer Power Mandelbrot", "Real Power Mandelbrot", 
			"Complex Power Mandelbrot", "Burning Ship", "MandelTrig", "Feather Fractal", "Hybrid Fractal"};
	
	Weighted1DPanel mainPanel;
	LabelTuple<JGradient> iterations;
	LabelValueTuple delta;
	LabelValueTuple re;
	LabelValueTuple im;
	GradientVisualizer visualizer;
	LabelOptionsTuple fractalTypes;
	JPanel parameters;
	
	public MenuGUI(FractalNavigatorGUI nav) {
		super("Menu");
		this.nav = nav;
		mainPanel = new Weighted1DPanel(false);
		//mainPanel.setLayout(new GridLayout(8, 1, 0, 8));
		
		iterations = new LabelTuple("Iterations: ", new JGradient(nav.getVisualizer().getNavigator().getZoom().getMaxIterationGradient()));
		mainPanel.addComponent(iterations);
		
		delta = new LabelValueTuple("Delta:", 0);
		mainPanel.addComponent(delta);
		
		re = new LabelValueTuple("Re:", 0);
		im = new LabelValueTuple("Im:", 0);
		re.setBorder(null);
		im.setBorder(null);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(1, 2, 20, 20));
		centerPanel.add(re);
		centerPanel.add(im);
		LabelTuple<JPanel> centerTuple = new LabelTuple("Center:", centerPanel);
		mainPanel.addComponent(centerTuple);
		
		visualizer = new GradientVisualizer(nav.getVisualizer().getGradient());
		visualizer.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				GradientFactoryGUI factory = new GradientFactoryGUI(nav.getVisualizer().getGradient());
				factory.setUpdateCallback(g -> nav.getVisualizer().updateGradient(g));
			}
		});
		JPanel panel = new LabelTuple("Gradient: ", visualizer);
		mainPanel.addComponent(panel);
		
		fractalTypes = new LabelOptionsTuple("Fractal:", FRACTAL_VARIANTS);
		FractalKernel fractal = nav.getVisualizer().getNavigator().getFrame().getKernel();
		fractalTypes.setOption(fractal.getName());
		fractalTypes.getRight().addActionListener(e -> fillParameters(getSelectedFractal()));
		mainPanel.addComponent(fractalTypes);
		
		parameters = new JPanel();
		fillParameters(nav.getVisualizer().getFrame().getKernel());
		mainPanel.addComponent(parameters);
		
		JButton updateButton = new JButton("Update");
		updateButton.setFont(new Font("Arial", Font.BOLD, 20));
		updateButton.addActionListener(e -> updateNavigator(e));
		mainPanel.addComponent(updateButton);
		this.add(mainPanel); 
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setSize(DEFAULT_MENU_SIZE);
		this.setLocationRelativeTo(null);
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
			delta.setValue(frame.getDelta());
			Complex center = frame.getCenter();
			re.setValue(center.getRe());
			im.setValue(-center.getIm());
		}
		super.setVisible(b);
	}
	
	private void updateNavigator(ActionEvent e) {
		this.setVisible(false);
		
		Gradient<Double> iterations = this.iterations.getRight().getGradient();
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
			case "Mandelbrot":					return new MandelbrotNormalMapping();
			case "Integer Power Mandelbrot":	return new IntegerMandelbrotNormalMapping();
			case "Real Power Mandelbrot":		return new RealPowerNormalMapping();
			case "Complex Power Mandelbrot":	return new ComplexPowerMandelbrotKernel();
			case "Burning Ship":				return new BurningShipKernel();
			case "MandelTrig":					return new MandelTrig();
			case "Feather Fractal":				return new FeatherFractal();
			case "Hybrid Fractal":				return new HybridFractal();
			default: 							return null;
		}
	}
	
}
