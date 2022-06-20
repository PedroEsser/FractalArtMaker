package video;

import java.awt.image.BufferedImage;
import java.util.Iterator;

import fractal.FractalFrame;
import gpuColorGradients.ColorGradient;
import gpuColorGradients.HSBGradient;
import gradient.Constant;
import gradient.Gradient;
import kernel.FractalProducer;

import java.awt.Color;

import utils.ImageUtils;

public abstract class FractalVideo extends Thread{
	
	public static final int DEFAULT_FPS = 30;
	public static final int DEFAULT_DURATION = 20;
	
	private final FractalProducer producer;
	private final Gradient<FractalFrame> zoom;
	public final int totalFrames;
	protected int currentFrame;
	private boolean paused = false;

	public FractalVideo(Gradient<FractalFrame> zoom, int fps, double duration, String filePath) {
		this.zoom = zoom;
		this.totalFrames = (int)(fps * duration);
		this.producer = new FractalProducer(zoom, this.totalFrames);
	}
	
	public FractalVideo(Gradient<FractalFrame> zoom, double duration, String filePath) {
		this(zoom, DEFAULT_FPS, duration, filePath);
	}
	
	public FractalVideo(Gradient<FractalFrame> zoom, String filePath) {
		this(zoom, DEFAULT_DURATION, filePath);
	}
	
	
	public synchronized boolean togglePause() {
		paused = !paused;
		if(producer != null)
			if(paused) 	producer.pause();
			else 		producer.unpause();
		if(!paused)
			notifyAll();
		return paused;
	}
	
	@Override
	public void run() {
		long timeStamp = System.currentTimeMillis();
		
		if(zoom instanceof Constant) {
//			FractalFrame frame = zoom.getStart();
//			frame.calculateAll();
//			for(ColorGradient gradient : gradientRange.toDiscrete(totalFrames)) {
//				this.encodeImage(frame.toImage());
//				currentFrame++;
//			}
		}else {			
			producer.start();
			FractalFrame current = producer.getNextFrame();
			while(current != null) {
				long t = System.currentTimeMillis();
				BufferedImage result = current.toImage();
//				System.out.println(System.currentTimeMillis() - t + " millis passed coloring image");
//				t = System.currentTimeMillis();
				this.encodeImage(result);
//				System.out.println(System.currentTimeMillis() - t + " millis TO ENCODE");
				currentFrame++;
				current = producer.getNextFrame();
			}
//
//			for(FractalFrame frame : zoom.toDiscrete(totalFrames)) {
//				produceAndWait(frame);
//				this.encodeImage(frame.toImage(gradientIterator.next(), norm));
//				currentFrame++;
//			}
		}
		
		
		long timeDiff = System.currentTimeMillis() - timeStamp;
		System.out.println("Time elapsed: " + secondsConversion(timeDiff / 1000));
		finished();
	}
	
	public static String secondsConversion(long seconds) {
		String hours = "" + seconds / 3600;
		String minutes = "" + seconds / 60 % 60;
		String secondsString = "" + seconds % 60;
		return hours + "h " + minutes + "m " + secondsString + "s";
	}
	
	public abstract void encodeImage(BufferedImage img);
	
	public abstract void finished();
	
}
