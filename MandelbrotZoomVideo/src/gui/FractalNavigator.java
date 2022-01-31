package gui;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.function.Consumer;

import gradient.Gradient;
import gradient.LinearGradient;
import gradient.LogarithmicGradient;
import logic.Complex;
import logic.FractalFrame;
import logic.FractalZoom;
import optimizations.ComplexPowerMandelbrotKernel;
import optimizations.FractalKernel;
import optimizations.MandelbrotKernel;
import optimizations.RealPowerMandelbrotKernel;

public class FractalNavigator {

	private static double PERCENT_STEP = 0.002;
	private double percent = 0;
	private FractalZoom zoom;
	private FractalFrame frame;
	private MyThreadPool producer;
	private Consumer<FractalFrame> frameUpdateCallback;
	
	private LogarithmicGradient deltaGradient = FractalZoom.DEFAULT_DELTA_RANGE.clone();
	//private LogarithmicGradient iterationGradient = FractalZoom.DEFAULT_MAX_ITERATION_RANGE.clone();
	
	public FractalNavigator(int width, int height, Consumer<FractalFrame> frameUpdateCallback) {
		this.zoom = new FractalZoom(width, height);
		//zoom.setFractal(new RealPowerMandelbrotKernel(2.5));
		this.frameUpdateCallback = frameUpdateCallback;
		producer = new MyThreadPool();
	}
	
	private synchronized void workAndUpdate(FractalFrame nextFrame) {
		producer.workOn(nextFrame);
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
		workAndUpdate(zoom.valueAt(percent));
	}
	
	public void setPercent(double percent) {
		this.percent = percent;
		update();
	}
	
	public double getPercent() {
		return percent;
	}
	
	public void move(Point p) {
		Complex newCenter = frame.toComplex(p);
		System.out.println(newCenter);
		zoom.setCenter(newCenter);
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
		zoom.setCenter(new Complex(re, im));
		zoom.setMaxIterationGradient(new LinearGradient(40, maxIterations, percent).truncateBelow(0));
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
		
		void workOn(FractalFrame nextFrame) {
			if(current == null) {
				current = new MyThread(nextFrame);
				current.start();
			}else {
				current.cancelled = true;
				next = new MyThread(nextFrame);
			}
		}
		
		private class MyThread extends Thread{
			
			private static final int chunk_size = 1 << 18;
			private FractalFrame nextFrame;
			private boolean cancelled;
			
			public MyThread(FractalFrame nextFrame) {
				super();
				this.nextFrame = nextFrame;
			}
			
			void workAndUpdate(FractalFrame nextFrame) {
//				int current = 0;
//				FractalKernel kernel = nextFrame.getKernel();
//				while(!cancelled && current < kernel.getSize()) {
//					FractalKernel k = kernel.copy(current);
//					k.executeSome(chunk_size);
//					current += chunk_size;
//				}
				nextFrame.getKernel().executeAll();
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
