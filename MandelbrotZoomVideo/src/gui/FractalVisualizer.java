package gui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import features.Feature;
import features.FractalOrbitVisualizer;
import features.VisualFeature;
import fractal.FractalFrame;
import gpuColorGradients.ColorGradient;
import gpuColorGradients.MultiGradient;
import guiUtils.ImagePanel;

public class FractalVisualizer extends ImagePanel {
	
	private FractalNavigator navigator;
	private FractalFrame frame;
	private final List<VisualFeature> features;
	public final FractalOrbitVisualizer orbitVisualizer;
	
	public FractalVisualizer(MultiGradient gradient) {
		super();
		this.features = new ArrayList<VisualFeature>();
		this.orbitVisualizer = new FractalOrbitVisualizer(this);
		this.features.add(orbitVisualizer);
		navigator = new FractalNavigator(0, 0, frame -> updateFrame(frame), gradient);
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		this.setMousePressCallback(e -> {
			Point p = this.getPointOnImage(e.getPoint());
			if (SwingUtilities.isLeftMouseButton(e)) 
            	navigator.moveAndUpdate(p);
            else if (SwingUtilities.isRightMouseButton(e)) 
            	orbitVisualizer.orbitAt(p);
		});
		this.setMouseDraggedCallback(e -> {
			Point p = this.getPointOnImage(e.getPoint());
			if (SwingUtilities.isRightMouseButton(e))
            	orbitVisualizer.orbitAt(p);
		});
		this.setMouseWheelCallback(e -> {
			Point p = this.getPointOnImage(e.getPoint());
			//navigator.move(p);
			navigator.zoom(e.getUnitsToScroll(), p);
		});
		//this.addMouseWheelListener(e -> navigator.zoom(e.getUnitsToScroll()));
		this.addKeyStroke(KeyStroke.getKeyStroke("O"), "orbit", e -> orbitVisualizer.toggle());
	}
	
	public void addKeyStroke(KeyStroke stroke, String actionName, Consumer<ActionEvent> action) {
		this.getInputMap(JComponent.WHEN_FOCUSED).put(stroke, actionName);
		this.getActionMap().put(actionName, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				action.accept(e);
			}
		});
	}
	
	public void setFrame(FractalFrame frame) {
		this.frame = frame;
	}

	public MultiGradient getGradient() {
		return frame.getGradient();
	}
	
	public void updateFrame(FractalFrame frame) {
		this.frame = frame;
		update();
	}
	
	public void updateGradient(MultiGradient gradient) {
		navigator.setGradient(gradient);
		update();
	}
	
	public void produceAndUpdateFrame(FractalFrame frame) {
		frame.calculateAll();
		updateFrame(frame);
	}
	
	public void produceAndUpdateFrame() {
		produceAndUpdateFrame(new FractalFrame(WIDTH, HEIGHT));
	}
	
	public void update() {
		if(frame != null) {
			BufferedImage newImg = frame.toImage();
			for(VisualFeature f : features)
				f.show(newImg);
			updateImage(newImg);
		}
			
	}
	
	public void addFeature(VisualFeature f) {
		features.add(f);
	}

	@Override
	protected void myResize() {
		navigator.resize(this.getPanelWidth(), this.getPanelHeight());
	}
	
	public FractalNavigator getNavigator() {
		return navigator;
	}
	
	public FractalFrame getFrame() {
		return frame;
	}
	
}
