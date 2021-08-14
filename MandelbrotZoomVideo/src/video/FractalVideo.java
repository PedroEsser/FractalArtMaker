package video;

import java.awt.image.BufferedImage;
import java.util.Iterator;

import gradient.Constant;
import gradient.Gradient;
import gradients.HSBGradient;

import java.awt.Color;

import logic.FractalFrame;
import optimizations.FractalProducer;
import utils.ImageUtils;

public abstract class FractalVideo extends Thread{
	
	public static final int DEFAULT_FPS = 30;
	public static final int DEFAULT_DURATION = 20;
	public static final Gradient<Gradient<Color>> DEFAULT_GRADIENT_RANGE = new Constant<Gradient<Color>>(new HSBGradient());
	
	private final Gradient<FractalFrame> zoom;
	private Gradient<Gradient<Color>> gradientRange;
	protected final int totalFrames;
	protected int currentFrame;
	protected FractalProducer currentProducer;
	private boolean paused = false;

	public FractalVideo(Gradient<FractalFrame> zoom, Gradient<Gradient<Color>> gradientRange, int fps, double duration, String filePath) {
		this.zoom = zoom;
		this.gradientRange = gradientRange;
		this.totalFrames = (int)(fps * duration);
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
	
	private void produceAndWait(FractalFrame frame) {
		currentProducer = new FractalProducer(frame, paused);
		currentProducer.start();
		try {
			currentProducer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void togglePause() {
		if(paused)
			currentProducer.unpause();
		else
			currentProducer.pause();
		paused = !paused;
	}
	
	@Override
	public void run() {
		long timeStamp = System.currentTimeMillis();
		
		if(zoom instanceof Constant) {
			FractalFrame frame = zoom.getStart();
			produceAndWait(frame);
			for(Gradient<Color> gradient : gradientRange.toDiscrete(totalFrames)) {
				this.encodeImage(frame.toImage(gradient));
				if(++currentFrame % 10 == 0)
					System.out.println("Frame " + currentFrame + " of " + totalFrames + " saved.");
			}
		}else {
			Iterator<Gradient<Color>> gradientIterator = gradientRange.toDiscrete(totalFrames).iterator();
			for(FractalFrame frame : zoom.toDiscrete(totalFrames)) {
				produceAndWait(frame);
				this.encodeImage(frame.toImage(gradientIterator.next()));
				if(++currentFrame % 10 == 0)
					System.out.println("Frame " + currentFrame + " of " + totalFrames + " saved.");
			}
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
