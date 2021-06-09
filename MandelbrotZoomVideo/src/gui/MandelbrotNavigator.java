package gui;

import java.awt.Point;
import java.util.function.Consumer;

import logic.Complex;
import logic.MandelbrotFrame;
import logic.MandelbrotZoom;
import optimizations.MandelbrotProducer;
import utils.Rectangle;

public class MandelbrotNavigator {

	private static double PERCENT_STEP = 0.002;
	private double percent = 0;
	private MandelbrotZoom zoom;
	private MandelbrotFrame frame;
	private MandelbrotProducer producer;
	private Consumer<MandelbrotFrame> frameUpdateCallback;
	
	public MandelbrotNavigator(int width, int height, Consumer<MandelbrotFrame> frameUpdateCallback) {
		this.zoom = new MandelbrotZoom(width, height);
		this.frameUpdateCallback = frameUpdateCallback;
	}
	
	private void workAndUpdate(MandelbrotFrame nextFrame, Rectangle... areas) {
		if(producer != null && producer.isAlive()) {
			producer.interrupt();
		}
		producer = new MandelbrotProducer(nextFrame, areas);
		producer.setCallBack(() -> {
			frame = nextFrame;
			frameUpdateCallback.accept(frame);
		});
		producer.start();
	}
	
	private void workAndUpdateAll(MandelbrotFrame nextFrame) {
		workAndUpdate(nextFrame, nextFrame.allPoints());
	}
	
	public void resize(int width, int height) {
		zoom.setWidth(width);
		zoom.setHeight(height);
		setPercent(percent);
	}
	
	public void zoom(int units) {
		double newPercent = percent - units * PERCENT_STEP;
		setPercent(newPercent);
	}
	
	public void setPercent(double percent) {
		this.percent = percent;
		workAndUpdateAll(zoom.valueAt(percent));
	}
	
	public void move(Point p) {
		Complex newCenter = frame.toComplex(p);
		System.out.println(newCenter);
		zoom.setCenter(newCenter);
		MandelbrotFrame nextFrame = zoom.valueAt(percent);
		
		int w = frame.getWidth();
		int h = frame.getHeight();
		int dx = p.x - w / 2;
		int dy = p.y - h / 2;
		Rectangle r = new Rectangle(Math.max(0, dx), Math.max(0, dy), Math.min(w + dx, w), Math.min(h + dy, h));
		nextFrame.copyData(frame, r, -dx, -dy);
		  
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
		workAndUpdate(nextFrame, r1, r2);
	}
	
	public MandelbrotZoom getZoom() {
		return zoom;
	}

	public void setZoom(MandelbrotZoom zoom) {
		this.zoom = zoom;
	}

	public MandelbrotFrame getFrame() {
		return frame;
	}

	public void setFrame(MandelbrotFrame frame) {
		this.frame = frame;
	}
	
	public String[] getInfo() {
		String[] info = new String[4];
		Complex center = frame.getCenter();
		info[0] = "Re: " + center.getRe();
		info[1] = "Im: " + -center.getIm();
		info[2] = "Delta : " + frame.getDelta();
		info[3] = "Iterations : " + frame.getMaxIterations();
		
		return info;
	}
	
}
