package video;

import java.awt.image.BufferedImage;
import java.util.Iterator;

import gradients.HSBGradient;

import java.awt.Color;

import logic.MandelbrotFrame;
import optimizations.MandelbrotProducer;
import rangeUtils.Constant;
import rangeUtils.Range;

public abstract class MandelbrotVideo extends Thread{
	
	public static final int DEFAULT_FPS = 30;
	public static final int DEFAULT_DURATION = 20;
	public static final Range<Range<Color>> DEFAULT_GRADIENT_RANGE = new Constant<Range<Color>>(new HSBGradient());
	
	private final Range<MandelbrotFrame> zoom;
	private Range<Range<Color>> gradientRange;
	protected final int totalFrames;
	protected int currentFrame;

	public MandelbrotVideo(Range<MandelbrotFrame> zoom, Range<Range<Color>> gradientRange, int fps, double duration, String filePath) {
		this.zoom = zoom;
		this.gradientRange = gradientRange;
		this.totalFrames = (int)(fps * duration);
	}
	
	public MandelbrotVideo(Range<MandelbrotFrame> zoom, Range<Range<Color>> gradientRange, double duration, String filePath) {
		this(zoom, gradientRange, DEFAULT_FPS, duration, filePath);
	}
	
	public MandelbrotVideo(Range<MandelbrotFrame> zoom, Range<Range<Color>> gradientRange, String filePath) {
		this(zoom, gradientRange, DEFAULT_DURATION, filePath);
	}
	
	public MandelbrotVideo(Range<MandelbrotFrame> zoom, String filePath) {
		this(zoom, DEFAULT_GRADIENT_RANGE, filePath);
	}
	
	public void setGradientRange(Range<Range<Color>> gradientRange) {
		this.gradientRange = gradientRange;
	}
	
	public void setGradient(Range<Color> gradient) {
		this.gradientRange = new Constant<Range<Color>>(gradient);
	}
	
	private void produceAndWait(MandelbrotFrame frame) {
		MandelbrotProducer producer = new MandelbrotProducer(frame);
		producer.start();
		
		try {
			producer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		long timeStamp = System.currentTimeMillis();
		
		if(zoom instanceof Constant) {
			MandelbrotFrame frame = zoom.getStart();
			produceAndWait(frame);
			for(Range<Color> gradient : gradientRange.toDiscrete(totalFrames)) {
				this.encodeImage(frame.toImage(gradient));
				if(++currentFrame % 10 == 0)
					System.out.println("Frame " + currentFrame + " of " + totalFrames + " saved.");
			}
		}else {
			Iterator<Range<Color>> gradientIterator = gradientRange.toDiscrete(totalFrames).iterator();
			for(MandelbrotFrame frame : zoom.toDiscrete(totalFrames)) {
				produceAndWait(frame);
				this.encodeImage(frame.toImage(gradientIterator.next()));
				if(++currentFrame % 10 == 0)
					System.out.println("Frame " + currentFrame + " of " + totalFrames + " saved.");
			}
		}
		
		
		long timeDiff = System.currentTimeMillis() - timeStamp;
		System.out.println("Time elapsed: " + timeDiff / 1000 + "s");
		finished();
	}
	
	
	
	public abstract void encodeImage(BufferedImage img);
	
	public abstract void finished();

	
}
