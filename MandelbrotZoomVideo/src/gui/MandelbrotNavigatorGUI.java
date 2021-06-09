package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import gradients.GradientFactory;
import logic.Complex;
import logic.MandelbrotFrame;
import logic.MandelbrotZoom;
import optimizations.MandelbrotProducer;
import rangeUtils.Range;
import utils.Rectangle;

public class MandelbrotNavigatorGUI extends JFrame{

	public static final Dimension DEFAULT_NAVIGATOR_SIZE = new Dimension(800, 600);
	private double offset = 0;
	private Range<Color> gradient;
	private final ImagePanel visualizer;
	
//	private MandelbrotNavigator navigator; 
	
	public MandelbrotNavigatorGUI(Range<Color> gradient) {
		this.gradient = gradient;
		this.visualizer = new MandelbrotVisualizer(gradient);
		
		this.setSize(DEFAULT_NAVIGATOR_SIZE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.add(visualizer);
		setVisible(true);
		initializeGUI();
	}
	
	
	public void initializeGUI() {
//		this.navigator = new MandelbrotNavigator(visualizer.getPanelWidth(), visualizer.getPanelHeight(), getImageUpdater());
//		this.navigator.setPercent(0);
//		new Thread(() -> {
//			try {
//				while(true) {
//					//Thread.sleep(100);
//					if(visualizer != null) {
//						offset += 0.0001;
//						visualizer.setGradient(gradient.offset(offset));
//						visualizer.update();
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}).start();
	}
	
}
	
	
	
