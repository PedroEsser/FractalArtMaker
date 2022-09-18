package gui;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import fractal.FractalFrame;
import fractal.FractalZoom;
import fractalKernels.BurningShipKernel;
import fractalKernels.ComplexPowerMandelbrotKernel;
import fractalKernels.FractalKernel;
import fractalKernels.FractalParameter;
import fractalKernels.IntegerPowerMandelbrotKernel;
import fractalKernels.MandelTrig;
import fractalKernels.MandelbrotKernel;
import fractalKernels.RealPowerMandelbrotKernel;
import fractals_deprecated.Complex;
import fractals_deprecated.ComplexGradient;
import gpuColorGradients.MultiGradient;
import gradient.Gradient;
import gradient.LinearGradient;
import gradient.ExponentialGradient;
import gradient.NumericGradient;

public class FractalNavigator {

	private static double PERCENT_STEP = 0.002;
	private double percent = 0;
	private FractalZoom zoom;
	private FractalFrame frame;
	private MyThreadPool producer;
	private Consumer<FractalFrame> frameUpdateCallback;
	
	private ExponentialGradient deltaGradient;
	
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
		zoom.setDimensions(width, height);
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
	
	public void setParameters(Gradient<Double> maxIterations, double delta, double re, double im, FractalKernel fractal) {
		double percent = deltaGradient.getPercentFor(delta);
		zoom.setCenter(new Complex(re, im));
		zoom.setMaxIterationGradient(p -> Math.max(0, maxIterations.valueAt(p)));
		zoom.setFractal(fractal);
		setPercent(percent);
	}
	
	public List<String> getInfo() {
		ArrayList<String> info = new ArrayList<>();
		Complex center = frame.getCenter();
		ExponentialGradient deltas = (ExponentialGradient)this.zoom.getDeltaGradient();
		
		double zoom = deltas.getStart() / deltas.valueAt(percent);
		info.add("Re: " + center.getRe());
		info.add("Im: " + -center.getIm());
		info.add("Zoom: " + new DecimalFormat("0.#####E0").format(zoom) + " (" + new DecimalFormat("0.#####").format(percent*100) + "%)");
		info.add("Iterations: " + frame.getMaxIterations());
		info.add("Gradient offset: " + new DecimalFormat("0.#####").format(frame.getGradient().getOffset()));
		
		return info;
	}
	
	public double zoomToDelta(double zoom) {
		return this.zoom.getDeltaGradient().getStart() / zoom;
	}
	
	public double deltaToZoom(double delta) {
		return this.zoom.getDeltaGradient().getStart() / delta;
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
