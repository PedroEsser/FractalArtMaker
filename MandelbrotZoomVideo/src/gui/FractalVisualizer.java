package gui;

import static guiUtils.GUIUtils.getContrastColor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import gradient.Gradient;
import guiUtils.ImagePanel;
import logic.FractalFrame;

public class FractalVisualizer extends ImagePanel {
	
	private FractalNavigator navigator;
	private FractalFrame frame;
	private Gradient<Color> gradient = FractalFrame.DEFAULT_GRADIENT;
	private boolean showInfo;
	
	public FractalVisualizer(Gradient<Color> gradient) {
		super();
		this.gradient = gradient;
		navigator = new FractalNavigator(0, 0, frame -> updateFrame(frame));
		this.setMousePressCallback(e -> {
			if (e.getButton() == MouseEvent.BUTTON1) {		//LEFT_CLICK
            	Point p = getPointOnImage(e.getPoint());
            	navigator.move(p);
            }
		});
		this.addMouseWheelListener(e -> navigator.zoom(e.getUnitsToScroll()));
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
	
	public FractalVisualizer() {
		this(FractalFrame.DEFAULT_GRADIENT);
	}
	
	@Override
	protected void myPaint(Graphics2D g) {
		super.myPaint(g);
		showInfo(g);
	}
	
	public void toggleInfo() {
		showInfo = !showInfo;
		repaint();
	}
	
	private void showInfo(Graphics2D g) {
		if(showInfo && frame != null){
			g.setFont(new Font("Arial", Font.BOLD, 15));
			g.setColor(Color.WHITE);
			int y = 20;
			for(String info : navigator.getInfo()) {
				g.drawString(info, 5, y);
				y+= 15;
			}
			drawCrosshair(g);
		}
	}
	
	private void drawCrosshair(Graphics2D g) {
		int x = this.getPanelWidth() / 2;
		int y = this.getPanelHeight() / 2;
		Color colorXY = new Color(this.img.getRGB(x, y));
		g.setColor(getContrastColor(colorXY));
		g.fillRect(x - 10, y, 21, 1);
		g.fillRect(x, y - 10, 1, 21);
	}
	
	public void setFrame(FractalFrame frame) {
		this.frame = frame;
	}
	
	public void setGradient(Gradient<Color> gradient) {
		this.gradient = gradient;
	}
	
	public void updateFrame(FractalFrame frame) {
		this.frame = frame;
		update();
	}
	
	public void updateGradient(Gradient<Color> gradient) {
		this.gradient = gradient;
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
		if(frame != null) 
			updateImage(frame.toImage(gradient));
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
