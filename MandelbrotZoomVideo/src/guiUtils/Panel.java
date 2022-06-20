package guiUtils;

import static guiUtils.GUIUtils.X_SCALING;
import static guiUtils.GUIUtils.Y_SCALING;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.function.Consumer;

import javax.swing.JPanel;

public abstract class Panel extends JPanel{

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
		myPaint(GUIUtils.ignoredWindowsScaleGraphics(g));
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
	
}
