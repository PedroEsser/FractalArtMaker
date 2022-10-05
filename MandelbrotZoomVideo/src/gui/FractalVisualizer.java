package gui;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import features.FractalOrbitVisualizer;
import features.VisualFeature;
import fractal.FractalFrame;
import gpuColorGradients.GradientFactory;
import gpuColorGradients.MultiGradient;
import guiUtils.ImagePanel;

public class FractalVisualizer extends ImagePanel {
	
	private FractalNavigator navigator;
	private FractalFrame frame;
	private final List<VisualFeature> features;
	public final FractalOrbitVisualizer orbitVisualizer;
	
	public FractalVisualizer(FractalNavigatorGUI gui) {
		super();
		this.features = new ArrayList<VisualFeature>();
		this.orbitVisualizer = new FractalOrbitVisualizer(gui);
		this.features.add(orbitVisualizer);
		navigator = new FractalNavigator(0, 0, frame -> updateFrame(frame));
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		this.setMousePressCallback(e -> {
			Point p = this.getPointOnPanel(e.getPoint());
			if (SwingUtilities.isMiddleMouseButton(e)) 
				orbitVisualizer.orbitAt(p);
            else if (SwingUtilities.isRightMouseButton(e)) 
            	navigator.moveAndUpdate(p);
		});
		this.setMouseDraggedCallback(e -> {
			Point p = this.getPointOnPanel(e.getPoint());
			if (SwingUtilities.isMiddleMouseButton(e)) 
				orbitVisualizer.orbitAt(p);
		});
		this.setMouseWheelCallback(e -> {
//			Point p = this.getPointOnImage(e.getPoint());-1.0120864027672762
//			navigator.zoom(e.getUnitsToScroll(), p);-4.4355735998857025E-6
			navigator.zoom(e.getUnitsToScroll());
		});
		this.addKeyStroke(KeyStroke.getKeyStroke("O"), "orbit", e -> orbitVisualizer.toggle());
		update();
	}
	
	public void addKeyStroke(KeyStroke stroke, String actionName, Consumer<ActionEvent> action) {
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, actionName);
		this.getActionMap().put(actionName, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				action.accept(e);
			}
		});
	}
	
	public MultiGradient getGradient() {
		return navigator.getFrame().getGradient();
	}
	
	public void updateGradient(MultiGradient gradient) {
		navigator.setGradient(gradient);
		update();
	}
	
	public void updateFrame(FractalFrame frame) {
		this.frame = frame;
		update();
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
	protected void myResize(Graphics2D g) {
		navigator.resize(this.getPanelWidth(), this.getPanelHeight());
	}
	
	public FractalNavigator getNavigator() {
		return navigator;
	}
	
	public FractalFrame getFrame() {
		return navigator.getFrame();
	}
	
}
