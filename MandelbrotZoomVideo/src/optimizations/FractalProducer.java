package optimizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import logic.FractalFrame;
import utils.Rectangle;

public class FractalProducer extends Thread {
	
	private static final int CONSUMERS = 5;
	
	public boolean paused;
	private final FractalFrame frame;
	private int currentY;
	private final List<Rectangle> areasToScan;
	private Runnable callBack;
	
	List<FractalConsumer> consumers;
	
	public FractalProducer(FractalFrame frame, boolean paused, Rectangle... areasToScan) {
		this.frame = frame;
		this.paused = paused;
		this.areasToScan = new ArrayList<>(Arrays.asList(areasToScan));
		currentY = currentArea().y1;
	}
	
	public FractalProducer(FractalFrame frame, boolean paused) {
		this(frame, paused, frame.allPoints());
	}
	
	public FractalProducer(FractalFrame frame, Rectangle... areasToScan) {
		this(frame, false, areasToScan);
	}
	
	public synchronized FractalTask getTask() {
		checkPause();
		if(areasToScan.isEmpty())
			return null;
		Rectangle current = currentArea();
		if(currentY >= current.y2) {
			areasToScan.remove(0);
			if(areasToScan.isEmpty())
				return null;
			current = currentArea();
			currentY = current.y1;
		}
		Rectangle r = new Rectangle(current.x1, currentY, current.x2, ++currentY);
		return new FractalTask(r, frame);
	}
	
	private Rectangle currentArea() {
		return areasToScan.get(0);
	}
	
	public void setCallBack(Runnable callBack) {
		this.callBack = callBack;
	}
	
	public synchronized void checkPause() {
		while(paused) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void pause() {
		System.out.println("pausing");
		this.paused = true;
	}
	
	public synchronized void unpause() {
		System.out.println("unpausing");
		this.paused = false;
		notifyAll();
	}
	
	@Override
	public void run() {
		consumers = new ArrayList<FractalConsumer>(CONSUMERS);
		for(int i = 0 ; i < CONSUMERS ; i++) 
			consumers.add(new FractalConsumer(this));
		
		consumers.forEach(c -> c.start());
		boolean interrupted = false;
		for(FractalConsumer mc : consumers) {
			try {
				mc.join();
			} catch (InterruptedException e) {
				interrupted = true;
				return;
			}
		}
		if(!interrupted && callBack != null) {
			callBack.run();
		}
			
	}
	
}
