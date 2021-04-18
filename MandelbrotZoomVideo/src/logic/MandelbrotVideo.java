package logic;

import java.awt.image.BufferedImage;

public abstract class MandelbrotVideo extends Thread{

	private static final int DEFAULT_FPS = 30;
	private static final int DEFAULT_DURATION = 30;
	private final MandelbrotZoom zoom;
	private final int fps, totalFrames;
	private final String filePath;
	private int currentFrame = 0;

	public MandelbrotVideo(MandelbrotZoom zoom, int fps, double duration, String filePath) {
		this.zoom = zoom;
		this.fps = fps;
		this.totalFrames = (int)(fps * duration);
		this.filePath = filePath;
	}
	
	public MandelbrotVideo(MandelbrotZoom zoom, double duration, String filePath) {
		this(zoom, DEFAULT_FPS, duration, filePath);
	}
	
	public MandelbrotVideo(MandelbrotZoom zoom, String filePath) {
		this(zoom, DEFAULT_DURATION, filePath);
	}
	
	@Override
	public void run() {
		super.run();
	}
	
	public abstract void encodeFrame(BufferedImage frame);
	
	
	
}
