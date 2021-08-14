package guiUtils;

import static guiUtils.GUIUtils.X_SCALING;
import static guiUtils.GUIUtils.Y_SCALING;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.JPanel;

public abstract class Panel extends JPanel{

	private Consumer<MouseEvent> mousePressCallback;
	
	public Panel() {
		super();
		this.addMouseListener(getMouseAdapter());
	}
	
	abstract void myPaint(Graphics2D g);
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		myPaint(GUIUtils.ignoredWindowsScaleGraphics(g));
	}
	
	public void setMousePressCallback(Consumer<MouseEvent> mousePressCallback) {
		this.mousePressCallback = mousePressCallback;
	}
	
	public int getPanelWidth() {
		return (int)Math.round(X_SCALING * this.getWidth());
	}
	
	public int getPanelHeight() {
		return (int)Math.round(Y_SCALING * this.getHeight());
	}
	
	public Point getPointOnImage(Point p) {
    	p.x *= X_SCALING;
    	p.y *= Y_SCALING;
    	return p;
	}
	
	private MouseAdapter getMouseAdapter() {
		return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (mousePressCallback != null) {
                	mousePressCallback.accept(e);
                }
            }
        };
	}
	
}
