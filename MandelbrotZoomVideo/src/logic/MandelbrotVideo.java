package logic;

import optimizations.MandelbrotProducer;
import rangeUtils.Range;

public abstract class MandelbrotVideo extends Thread{

	public static final int DEFAULT_FPS = 30;
	public static final int DEFAULT_DURATION = 20;
	private final Range<MandelbrotFrame> zoom;
	protected final int totalFrames;

	public MandelbrotVideo(Range<MandelbrotFrame> zoom, int fps, double duration, String filePath) {
		this.zoom = zoom;
		this.totalFrames = (int)(fps * duration);
	}
	
	public MandelbrotVideo(Range<MandelbrotFrame> zoom, double duration, String filePath) {
		this(zoom, DEFAULT_FPS, duration, filePath);
	}
	
	public MandelbrotVideo(Range<MandelbrotFrame> zoom, String filePath) {
		this(zoom, DEFAULT_DURATION, filePath);
	}
	
	@Override
	public void run() {
		long timeStamp = System.currentTimeMillis();
		for(MandelbrotFrame frame : zoom.toDiscrete(totalFrames)) {
			MandelbrotProducer producer = new MandelbrotProducer(frame);
			producer.start();
			try {
				producer.join();
				this.encodeFrame(frame);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long timeDiff = System.currentTimeMillis() - timeStamp;
		System.out.println("Time elapsed: " + timeDiff / 1000 + "s");
		finished();
	}
	
	public abstract void encodeFrame(MandelbrotFrame frame);
	
	public abstract void finished();
	
}
