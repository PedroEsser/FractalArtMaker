package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import logic.Complex;
import logic.MandelbrotFrame;
import logic.MandelbrotZoom;
import optimizations.MandelbrotProducer;
import rangeUtils.Constant;
import rangeUtils.Range;
import utils.Rectangle;

public class MandelbrotNavigator extends JFrame{

	private static double PERCENT_STEP = 0.002;
	private double percent = 0;
	private double offset = 0;
	private Range<Color> gradient;
	private MandelbrotZoom zoom;
	private MandelbrotFrame frame;
	private final MandelbrotVisualizer panel;
	private MandelbrotProducer producer;
	
	
	public MandelbrotNavigator(Range<Color> gradient) {
		this.gradient = gradient;
		this.panel = new MandelbrotVisualizer(this);
		
		this.setSize(MenuGUI.DEFAULT_WINDOW_SIZE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.add(panel);
		setVisible(true);
	}
	
	public void initializeVisualizer() {
		zoom = new MandelbrotZoom(new Complex(0,0), panel.getWidth(), panel.getWidth()).invert();
		setPercent(0);
		new Thread(() -> {
			try {
				while(true) {
					//Thread.sleep(100);
					offset += 0.001;
					setImage(frame.toImage(gradient.offset(offset)));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(offset);
			}
		}).start();
	}
	
	private void workAndUpdate(Rectangle... areas) {
		if(producer != null && producer.isAlive()) {
			producer.interrupt();
		}
		producer = new MandelbrotProducer(frame, areas);
		producer.setCallBack(() -> {
			//frame.drawPixels(img, gradient, areas);
			setImage(frame.toImage(gradient.offset(offset)));
		});
		producer.start();
	}
	
	private void setImage(BufferedImage img) {
		panel.updateImage(img);
	}
	
	private void workAndUpdateAll() {
		workAndUpdate(frame.allPoints());
	}
	
	public void checkResize() {
		if(wasResized()) {
			resize();
			workAndUpdateAll();
		}
	}
	
	private boolean wasResized() {
		return frame.getWidth() != panel.getWidth() || frame.getHeight() != panel.getHeight();
	}
	
	private void resize() {
		zoom.setWidth(panel.getWidth());
		zoom.setHeight(panel.getHeight());
		setPercent(percent);
	}
	
	public void setPercent(double percent) {
		this.percent = percent;
		frame = zoom.valueAt(percent);
		workAndUpdateAll();
	}
	
	public void zoom(int units) {
		double newPercent = percent - units * PERCENT_STEP;
		setPercent(newPercent);
	}
	
	public void move(Point p) {
		MandelbrotFrame previousFrame = frame;
		Complex newCenter = frame.toComplex(p);
		zoom.setCenter(newCenter);
		System.out.println(newCenter);
		frame = zoom.valueAt(percent);
		
		int w = frame.getWidth();
		int h = frame.getHeight();
		int dx = p.x - w / 2;
		int dy = p.y - h / 2;
		Rectangle r = new Rectangle(Math.max(0, dx), Math.max(0, dy), Math.min(w + dx, w), Math.min(h + dy, h));
		frame.copyData(previousFrame, r, -dx, -dy);
		  
		Rectangle r1 = new Rectangle();
		Rectangle r2 = new Rectangle();
		  
		if(dx > 0){
		  r1.x1 = w - dx;
		  r1.x2 = w;
		  r2.x1 = 0;
		  r2.x2 = w - dx;
		}else{
		  r1.x1 = 0;
		  r1.x2 = - dx;
		  r2.x1 = - dx;
		  r2.x2 = w;
		}
		
		r1.y1 = 0;
		r1.y2 = h;
		
		if(dy > 0){
		  r2.y1 = h - dy;
		  r2.y2 = h;
		}else{
		  r2.y1 = 0;
		  r2.y2 = - dy;
		}
		workAndUpdate(r1, r2);
	}
		 
}
	
	
	
