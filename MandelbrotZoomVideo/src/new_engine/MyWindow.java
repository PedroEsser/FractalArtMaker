package new_engine;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;


public class MyWindow extends JFrame{
	
	public static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(800, 600);
	
	public MyWindow(boolean fullScreen, GraphicsDevice device) {
		super();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Rectangle r = device.getDefaultConfiguration().getBounds();
		if(fullScreen) { 
		    this.setUndecorated(true);
		    this.setSize(r.width, r.height);
		    this.setLocation(r.x, r.y);
		}else {
			this.setSize(DEFAULT_WINDOW_SIZE);
			this.setLocation(r.x + (r.width - this.getWidth())/2, r.y + (r.height - this.getHeight())/2);
		}
	}
	
	public MyWindow() {
		this(false, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
	}
	
	public MyWindow toggleFullscreen() {
		MyWindow newWindow = new MyWindow(!this.isUndecorated(), this.getGraphicsConfiguration().getDevice());
		for(Component c : this.getComponents())
			newWindow.add(c);
		this.dispose();
		newWindow.showWindow();
		return newWindow;
	}
	
	public void showWindow() {
		this.setVisible(true);
	}
	
}
