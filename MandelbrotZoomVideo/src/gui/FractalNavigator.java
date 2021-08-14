package gui;

import java.awt.Point;
import java.util.function.Consumer;

import fractals.BurningShip;
import fractals.MandelbrotSet;
import gradient.LogarithmicGradient;
import logic.Complex;
import logic.FractalFrame;
import logic.FractalZoom;
import optimizations.FractalProducer;
import utils.Rectangle;

public class FractalNavigator {

	private static double PERCENT_STEP = 0.002;
	private double percent = 0;
	private FractalZoom zoom;
	private FractalFrame frame;
	private FractalProducer producer;
	private Consumer<FractalFrame> frameUpdateCallback;
	
	private LogarithmicGradient deltaRange = FractalZoom.DEFAULT_DELTA_RANGE.clone();
	private LogarithmicGradient iterationRange = FractalZoom.DEFAULT_MAX_ITERATION_RANGE.clone();
	
	public FractalNavigator(int width, int height, Consumer<FractalFrame> frameUpdateCallback) {
		this.zoom = new FractalZoom(width, height);
		zoom.setFractal(new MandelbrotSet());
		this.frameUpdateCallback = frameUpdateCallback;
	}
	
	private synchronized void workAndUpdate(FractalFrame nextFrame, Rectangle... areas) {
		if(producer != null && producer.isAlive()) {
			producer.interrupt();
		}
		producer = new FractalProducer(nextFrame, areas);
		producer.setCallBack(() -> {
			frame = nextFrame;
			frameUpdateCallback.accept(frame);
		});
		producer.start();
	}
	
	private void workAndUpdateAll(FractalFrame nextFrame) {
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
	
	public void update() {
		workAndUpdateAll(zoom.valueAt(percent));
	}
	
	public void setPercent(double percent) {
		this.percent = percent;
		workAndUpdateAll(zoom.valueAt(percent));
	}
	
	public double getPercent() {
		return percent;
	}
	
	public void move(Point p) {
		Complex newCenter = frame.toComplex(p);
		System.out.println(newCenter);
		zoom.setCenter(newCenter);
		FractalFrame nextFrame = zoom.valueAt(percent);
		
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
	
	public FractalZoom getZoom() {
		return zoom;
	}

	public void setZoom(FractalZoom zoom) {
		this.zoom = zoom;
	}
	
	public double getPercentFor(double delta) {
		return deltaRange.getPercentFor(delta);
	}

	public FractalFrame getFrame() {
		return frame;
	}

	public void setFrame(FractalFrame frame) {
		this.frame = frame;
	}
	
	public void setParameters(double maxIterations, double delta, double re, double im) {
		double percent = deltaRange.getPercentFor(delta);
		double itOffset = maxIterations / iterationRange.valueAt(percent);
		iterationRange = iterationRange.scale(itOffset);
		zoom.setCenter(new Complex(re, im));
		zoom.setMaxIterationRange(iterationRange);
		setPercent(percent);
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
