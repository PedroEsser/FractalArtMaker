package guiUtils;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.util.function.Consumer;

import javax.swing.JPanel;

public abstract class Panel extends JPanel{

	protected double xScaling = 1;
	protected double yScaling = 1;
	private Consumer<MouseEvent> mousePressCallback, mouseDraggedCallback;
	private Consumer<MouseWheelEvent> mouseWheelCallBack;
	
	public Panel() {
		super();
		this.addMouseListener(getMouseAdapter());
		this.addMouseMotionListener(getMouseMotionAdapter());
		this.addMouseWheelListener(getMouseAdapter());
	}
	
	protected abstract void myPaint(Graphics2D g);
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		final Graphics2D g2d = (Graphics2D) g;
		final AffineTransform t = g2d.getTransform();
		xScaling = t.getScaleX();
		yScaling = t.getScaleY();
		t.setToScale(1, 1);
		g2d.setTransform(t);
		myPaint(g2d);
	}
	
	public void setMousePressCallback(Consumer<MouseEvent> mousePressCallback) {
		this.mousePressCallback = mousePressCallback;
	}
	
	public void setMouseDraggedCallback(Consumer<MouseEvent> mouseDraggedCallback) {
		this.mouseDraggedCallback = mouseDraggedCallback;
	}
	
	public void setMouseWheelCallback(Consumer<MouseWheelEvent> mouseWheelCallBack) {
		this.mouseWheelCallBack = mouseWheelCallBack;
	}
	
	public int getPanelWidth() {
		return (int)Math.round(xScaling * this.getWidth());
	}
	
	public int getPanelHeight() {
		return (int)Math.round(yScaling * this.getHeight());
	}
	
	public Point getPointOnPanel(Point p) {
    	p.x *= xScaling;
    	p.y *= yScaling;
    	return p;
	}
	
	private MouseAdapter getMouseAdapter() {
		return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (mousePressCallback != null) 
                	mousePressCallback.accept(e);
            }
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
            	if(mouseWheelCallBack != null)
            		mouseWheelCallBack.accept(e);
            }
        };
	}
	
	private MouseMotionAdapter getMouseMotionAdapter() {
		return new MouseMotionAdapter() {
			@Override
            public void mouseDragged(MouseEvent e) {
            	if (mouseDraggedCallback != null) 
            		mouseDraggedCallback.accept(e);
            }
		};
	}
	
	private class MyMouseEvent extends MouseEvent{

		public MyMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int xAbs, int yAbs,
				int clickCount, boolean popupTrigger, int button) {
			super(source, id, when, modifiers, x, y, xAbs, yAbs, clickCount, popupTrigger, button);
		}

		
		
	}
}
