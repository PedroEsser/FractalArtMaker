package video;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;

import fractal.FractalFrame;
import fractalKernels.FractalProducer;
import gradient.Gradient;
import utils.ImageUtils;

public abstract class FractalVideo extends Thread{
	
	public static final int DEFAULT_FPS = 30;
	public static final int DEFAULT_DURATION = 20;
	
	private final FractalProducer producer;
	public final int totalFrames;
	protected final int fps;
	protected int currentFrame;
	private boolean paused = false;
	protected JProgressBar progress;
	private final List<PostProcessing> postProcessings = new ArrayList<>();

	public FractalVideo(Gradient<FractalFrame> zoom, int fps, double duration, String filePath, JProgressBar progress) {
		this.fps = fps;
		this.totalFrames = (int)(fps * duration);
		this.producer = new FractalProducer(zoom, this.totalFrames);
		this.progress = progress;
		addPostProcessing(new FadePostProcessing(2));
	}
	
	public void cancel() {
		producer.cancel();
		this.interrupt();
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
		
		producer.start();
		FractalFrame current = producer.getNextFrame();
		while(current != null) {
			BufferedImage result = current.toImage();
			this.applyPostProcessing(result);
			this.encodeImage(result);
			progress.setString("Frame " + currentFrame + " of " + totalFrames + " rendered.");
			progress.setValue(currentFrame);
			currentFrame++;
			current = producer.getNextFrame();
		}
		
		if(!producer.cancelled()) {
			finished();
			long timeDiff = System.currentTimeMillis() - timeStamp;
			progress.setString("Time elapsed: " + secondsConversion(timeDiff / 1000));
		}
	}
	
	public static String secondsConversion(long seconds) {
		String hours = "" + seconds / 3600;
		String minutes = "" + seconds / 60 % 60;
		String secondsString = "" + seconds % 60;
		return hours + "h " + minutes + "m " + secondsString + "s";
	}
	
	public void addPostProcessing(PostProcessing pp) {
		postProcessings.add(pp);
	}
	
	private void applyPostProcessing(BufferedImage img) {
		for(PostProcessing pp : postProcessings)
			pp.doProcessing(img);
	}
	
	public abstract void encodeImage(BufferedImage img);
	
	public abstract void finished();
	
	private class FadePostProcessing implements PostProcessing{

		private final int fadingFrames;
		
		public FadePostProcessing(double fadeTime) {
			this.fadingFrames = (int)(fadeTime*fps);
		}
		
		@Override
		public void doProcessing(BufferedImage img) {
			int framesLeft = totalFrames - currentFrame;
			if(framesLeft < fadingFrames) {
				double percent = (double)framesLeft/fadingFrames;
				ImageUtils.processImage(img, c -> fadedColor(c, percent));
			}
		}
		
		private Color fadedColor(Color c, double percentFade) {
			int r = (int)(c.getRed() * percentFade);
			int g = (int)(c.getGreen() * percentFade);
			int b = (int)(c.getBlue() * percentFade);
			return new Color(r, g, b);
		}
		
	}
	
}
