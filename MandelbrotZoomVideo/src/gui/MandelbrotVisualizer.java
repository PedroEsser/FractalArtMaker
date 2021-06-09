package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import logic.MandelbrotFrame;
import rangeUtils.Range;
import static gui.GUIUtils.getContrastColor;

public class MandelbrotVisualizer extends ImagePanel {
	
	private MandelbrotNavigator navigator;
	private MandelbrotFrame frame;
	private Range<Color> gradient = MandelbrotFrame.DEFAULT_GRADIENT;
	private boolean showInfo;
	
	public MandelbrotVisualizer(Range<Color> gradient) {
		super();
		this.gradient = gradient;
		navigator = new MandelbrotNavigator(0, 0, frame -> updateFrame(frame));
		this.setMousePressCallback(p -> navigator.move(p));
		this.addMouseWheelListener(e -> navigator.zoom(e.getUnitsToScroll()));
		//System.out.println(this.getInputMap().keys());
		this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("I"), "info");
		this.getActionMap().put("info", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleInfo();
			}
		});
	}
	
	public MandelbrotVisualizer() {
		this(MandelbrotFrame.DEFAULT_GRADIENT);
	}
	
	@Override
	void myPaint(Graphics2D g) {
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
	
	public void setFrame(MandelbrotFrame frame) {
		this.frame = frame;
	}
	
	public void setGradient(Range<Color> gradient) {
		this.gradient = gradient;
	}
	
	public void updateFrame(MandelbrotFrame frame) {
		this.frame = frame;
		update();
	}
	
	public void updateGradient(Range<Color> gradient) {
		this.gradient = gradient;
		update();
	}
	
	public void produceAndUpdateFrame(MandelbrotFrame frame) {
		frame.calculateAll();
		updateFrame(frame);
	}
	
	public void produceAndUpdateFrame() {
		produceAndUpdateFrame(new MandelbrotFrame(WIDTH, HEIGHT));
	}
	
	public void update() {
		if(frame != null) 
			updateImage(frame.toImage(gradient));
	}

	@Override
	protected void myResize() {
		navigator.resize(this.getPanelWidth(), this.getPanelHeight());
	}
	
}
