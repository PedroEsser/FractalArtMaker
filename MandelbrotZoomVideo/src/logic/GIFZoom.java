package logic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import gradients.Gradient;
import optimizations.VideoOutput;
import rangeUtils.LinearRange;
import utils.GifSequenceWriter;
import utils.ImageUtils;

public class GIFZoom extends MandelbrotZoom implements VideoOutput{
	
	private static final int DEFAULT_FPS = 30;
	private static final double DEFAULT_ZOOM_SPEED = 3;
	private final int fps, totalFrames;
	//private final GifSequenceWriter gif;
	//private final ImageOutputStream out;
	private int currentFrame = 0;
	private LinearRange maxIterationRange;

	public GIFZoom(Complex center, int width, int height, double initialDelta, double finalDelta, double zoomSpeed, int fps, LinearRange maxIterationRange, String path) throws FileNotFoundException, IOException {
		super(center, width, height, initialDelta, finalDelta);
		this.fps = fps;
		//this.out = new FileImageOutputStream(new File(path));
		//this.gif = new GifSequenceWriter(out, BufferedImage.TYPE_3BYTE_BGR, 1000 / fps, false);
		double totalZoom = initialDelta / finalDelta;
		double duration = Math.log(totalZoom) / Math.log(zoomSpeed);
		System.out.println("GIF of " + duration + " seconds.");
		totalFrames = (int)(duration * fps);
	}
	
	public GIFZoom(Complex center, int width, int height, double finalDelta, String path) throws FileNotFoundException, IOException {
		this(center, width, height, MandelbrotZoom.DEFAULT_DELTA_RANGE.getStart(), finalDelta, DEFAULT_ZOOM_SPEED, DEFAULT_FPS, null, path);
	}
	
	public GIFZoom(Complex center, int width, int height, double finalDelta, double zoomSpeed, String path) throws FileNotFoundException, IOException {
		this(center, width, height, MandelbrotZoom.DEFAULT_DELTA_RANGE.getStart(), finalDelta, zoomSpeed, DEFAULT_FPS, null, path);
	}
	
	public void createZoom(String path, Gradient gradient, LinearRange maxIterationRange) {
		try {
			ImageOutputStream out = new FileImageOutputStream(new File(path));
			GifSequenceWriter g = new GifSequenceWriter(out, BufferedImage.TYPE_3BYTE_BGR, 1000 / fps, false);
			for(int i = 0 ; i < totalFrames ; i++) {
				double percent = (double)i / totalFrames;
				MandelbrotFrame currentFrame = this.valueAt(percent);
				if(maxIterationRange != null)
					currentFrame.calculateAll(maxIterationRange.valueAt(percent).intValue());
				else
					currentFrame.calculateAll(MandelbrotFrame.DEFAULT_MAX_ITERATION);
				g.writeToSequence(currentFrame.toImage(gradient));
				if(i % 10 == 0)
					System.out.println("Frame " + i + " of " + totalFrames + " saved.");
			}
			g.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createZoom(String path, LinearRange maxIterationRange) {
		this.createZoom(path, MandelbrotFrame.DEFAULT_GRADIENT, maxIterationRange);
	}
	
	public void createZoom(String path, Gradient gradient) {
		this.createZoom(path, gradient, null);
	}
	
	public void createZoom(String path) {
		this.createZoom(path, MandelbrotFrame.DEFAULT_GRADIENT, null);
	}

	@Override
	public void sendFrame(MandelbrotFrame frame) {
//		double percent = (double)currentFrame++ / totalFrames;
//		MandelbrotFrame currentFrame = this.getFrameAt(percent);
//		if(maxIterationRange != null)
//			currentFrame.calculateAll((int)maxIterationRange.valueAt(percent));
//		else
//			currentFrame.calculateAll(MandelbrotFrame.DEFAULT_MAX_ITERATION);
//		if(i % 10 == 0)
//			System.out.println("Frame " + i + " of " + totalFrames + " saved.");
		
		//gif.writeToSequence(frame.toImage(gradient));
	}

}
