package gui;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.function.Consumer;

import fractal.Complex;
import fractal.ComplexGradient;
import fractal.FractalFrame;
import fractal.FractalZoom;
import gpuColorGradients.ColorGradient;
import gpuColorGradients.MultiGradient;
import gradient.Gradient;
import gradient.LinearGradient;
import gradient.LogarithmicGradient;
import kernel.BurningShipKernel;
import kernel.ComplexPowerMandelbrotKernel;
import kernel.FractalKernel;
import kernel.IntegerPowerMandelbrotKernel;
import kernel.MandelTrig;
import kernel.MandelbrotKernel;
import kernel.RealPowerMandelbrotKernel;

public class FractalNavigator {

	private static double PERCENT_STEP = 0.002;
	private double percent = 0;
	private FractalZoom zoom;
	private FractalFrame frame;
	private MyThreadPool producer;
	private Consumer<FractalFrame> frameUpdateCallback;
	
	private LogarithmicGradient deltaGradient;
	
	public FractalNavigator(int width, int height, Consumer<FractalFrame> frameUpdateCallback, MultiGradient gradient) {
		this.zoom = new FractalZoom(width, height);
		this.deltaGradient = FractalZoom.DEFAULT_DELTA_GRADIENT.clone();
		this.zoom.setGradient(gradient);
		
		this.frameUpdateCallback = frameUpdateCallback;
		producer = new MyThreadPool();
	}
	
	private void workAndUpdate(FractalFrame nextFrame) {
		producer.workOn(nextFrame);
	}
	
	public void workAndUpdate() {
		producer.workOn(frame);
	}
	
	public void resize(int width, int height) {
		zoom.setWidth(width);
		zoom.setHeight(height);
		setPercent(percent);
	}
	
	public void zoom(int units, Point p) {
		Complex c = frame.toComplex(p.x, p.y);
		percent = percent - units * PERCENT_STEP;
		FractalFrame nextFrame = zoom.valueAt(percent);
		Point nextP = nextFrame.toPoint(c);
		Point diff = new Point(nextP.x - p.x, nextP.y - p.y);
		zoom.setCenter(nextFrame.toComplex(nextFrame.getWidth()/2 + diff.x, nextFrame.getHeight()/2 + diff.y));
		update();
	}
	
	public void zoom(int units) {
		double newPercent = percent - units * PERCENT_STEP;
		setPercent(newPercent);
	}
	
	public void update() {
		workAndUpdate(zoom.valueAt(percent));
	}
	
	public void setPercent(double percent) {
		this.percent = percent;
		update();
	}
	
	public void setGradient(MultiGradient gradient) {
		zoom.setGradient(gradient);
		update();
	}
	
	public double getPercent() {
		return percent;
	}
	
	public void move(Point p) {
		Complex newCenter = frame.toComplex(p.x, p.y);
		System.out.println(newCenter);
		zoom.setCenter(newCenter);
	}
	
	public void moveAndUpdate(Point p) {
		move(p);
		FractalFrame nextFrame = zoom.valueAt(percent);
		workAndUpdate(nextFrame);
	}
	
	public FractalZoom getZoom() {
		return zoom;
	}

	public void setZoom(FractalZoom zoom) {
		this.zoom = zoom;
	}
	
	public double getPercentFor(double delta) {
		return deltaGradient.getPercentFor(delta);
	}

	public FractalFrame getFrame() {
		return frame;
	}

	public void setFrame(FractalFrame frame) {
		this.frame = frame;
	}
	
	public void setParameters(double maxIterations, double delta, double re, double im) {
		double percent = deltaGradient.getPercentFor(delta);
		Gradient<Double> maxIGradient = zoom.getMaxIterationGradient();
		double maxIterRatio = maxIterations / maxIGradient.valueAt(percent);
		Gradient<Double> maxIter = new LinearGradient(maxIGradient.getStart() * maxIterRatio, maxIGradient.getEnd() * maxIterRatio);
//		if(percent > 0) 
//			maxIter = new LinearGradient(Math.min(40, maxIterations), maxIterations, percent);
//		else 
//			maxIter = new LinearGradient(Math.min(40, maxIterations), zoom.getMaxIterationGradient().getEnd());
		
		zoom.setCenter(new Complex(re, im));
		zoom.setMaxIterationGradient(maxIter.truncateBelow(0));
		setPercent(percent);
	}
	
	public String[] getInfo() {
		String[] info = new String[4];
		Complex center = frame.getCenter();
		Gradient<Double> deltas = this.zoom.getDeltaGradient();
		double zoom = deltas.getStart() / deltas.valueAt(percent);
		info[0] = "Re: " + center.getRe();
		info[1] = "Im: " + -center.getIm();
		info[2] = "Zoom : " + new DecimalFormat("0.#####E0").format(zoom);
		info[3] = "Iterations : " + frame.getMaxIterations();
		
		return info;
	}
	
	private class MyThreadPool{
		
		private MyThread current, next;
		
		private synchronized void workOn(FractalFrame nextFrame) {
			if(current == null) {
				current = new MyThread(nextFrame);
				current.start();
			}else {
				current.cancelled = true;
				next = new MyThread(nextFrame);
			}
		}
		
		private class MyThread extends Thread{
			
			private FractalFrame nextFrame;
			private boolean cancelled;
			
			public MyThread(FractalFrame nextFrame) {
				super();
				this.nextFrame = nextFrame;
			}
			
			void workAndUpdate(FractalFrame nextFrame) {
				nextFrame.calculateAll();
				
				if(!cancelled) {
					frame = nextFrame;
					frameUpdateCallback.accept(frame);
				}
			}
			
			@Override
			public void run() {
				workAndUpdate(nextFrame);
				if(next != null) {
					current = next;
					next = null;
					current.start();
				}else {
					current = null;
				}
				super.run();
			}
			
		}
		
	}
	
}
