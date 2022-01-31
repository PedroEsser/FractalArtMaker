package video;

import java.awt.image.BufferedImage;
import java.util.Iterator;

import colorGradients.HSBGradient;
import gradient.Constant;
import gradient.Gradient;

import java.awt.Color;

import logic.FractalFrame;
import optimizations.FractalProducer;
import utils.ImageUtils;

public abstract class FractalVideo extends Thread{
	
	public static final int DEFAULT_FPS = 30;
	public static final int DEFAULT_DURATION = 20;
	public static final Gradient<Gradient<Color>> DEFAULT_GRADIENT_RANGE = new Constant<Gradient<Color>>(new HSBGradient());
	
	private final FractalProducer producer;
	private final Gradient<FractalFrame> zoom;
	private Gradient<Gradient<Color>> gradientRange;
	public final int totalFrames;
	protected int currentFrame;
	private boolean paused = false;

	public FractalVideo(Gradient<FractalFrame> zoom, Gradient<Gradient<Color>> gradientRange, int fps, double duration, String filePath) {
		this.zoom = zoom;
		this.gradientRange = gradientRange;
		this.totalFrames = (int)(fps * duration);
		this.producer = new FractalProducer(zoom, this.totalFrames);
	}
	
	public FractalVideo(Gradient<FractalFrame> zoom, Gradient<Gradient<Color>> gradientRange, double duration, String filePath) {
		this(zoom, gradientRange, DEFAULT_FPS, duration, filePath);
	}
	
	public FractalVideo(Gradient<FractalFrame> zoom, Gradient<Gradient<Color>> gradientRange, String filePath) {
		this(zoom, gradientRange, DEFAULT_DURATION, filePath);
	}
	
	public FractalVideo(Gradient<FractalFrame> zoom, String filePath) {
		this(zoom, DEFAULT_GRADIENT_RANGE, filePath);
	}
	
	public void setGradientRange(Gradient<Gradient<Color>> gradientRange) {
		this.gradientRange = gradientRange;
	}
	
	public void setGradient(Gradient<Color> gradient) {
		this.gradientRange = new Constant<Gradient<Color>>(gradient);
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
		float norm = 1f / zoom.getEnd().getMaxIterations();
		
		if(zoom instanceof Constant) {
			FractalFrame frame = zoom.getStart();
			frame.calculateAll();
			for(Gradient<Color> gradient : gradientRange.toDiscrete(totalFrames)) {
				this.encodeImage(frame.toImage(gradient, norm));
				currentFrame++;
			}
		}else {
			Iterator<Gradient<Color>> gradientIterator = gradientRange.toDiscrete(totalFrames).iterator();
			
			producer.start();
			FractalFrame current = producer.getNextFrame();
			while(current != null) {
				//System.out.println("Encoding frame " + currentFrame);
				long t = System.currentTimeMillis();
//				System.out.println("before image");
				BufferedImage result = current.toImage(gradientIterator.next(), norm);
				System.out.println(System.currentTimeMillis() - t + " millis passed coloring image");
//				System.out.println("before encoding");
//				t = System.currentTimeMillis();
				//ImageUtils.saveImage(result, "C:\\Users\\pedro\\git\\Mandelbrot\\MandelbrotZoomVideo\\video\\Frame" + currentFrame + ".png");
				this.encodeImage(result);
//				System.out.println(System.currentTimeMillis() - t + " millis passed encoding image");
				currentFrame++;
//				System.out.println("before frame");
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
