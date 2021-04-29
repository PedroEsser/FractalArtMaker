package optimizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import logic.MandelbrotFrame;
import utils.Rectangle;

public class MandelbrotProducer extends Thread {
	
	private static final int CONSUMERS = 5;
	
	private final MandelbrotFrame frame;
	private int currentY;
	private final List<Rectangle> areasToScan;
	private Runnable callBack;
	
	public MandelbrotProducer(MandelbrotFrame frame, Rectangle... areasToScan) {
		this.frame = frame;
		this.areasToScan = new ArrayList<>(Arrays.asList(areasToScan));
		currentY = currentArea().y1;
	}
	
	public MandelbrotProducer(MandelbrotFrame frame) {
		this(frame, frame.allPoints());
	}
	
	public synchronized MandelbrotTask getTask() {
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
		return new MandelbrotTask(r, frame);
	}
	
	private Rectangle currentArea() {
		return areasToScan.get(0);
	}
	
	public void setCallBack(Runnable callBack) {
		this.callBack = callBack;
	}
	
	@Override
	public void run() {
		List<MandelbrotConsumer> consumers = new ArrayList<MandelbrotConsumer>(CONSUMERS);
		for(int i = 0 ; i < CONSUMERS ; i++) 
			consumers.add(new MandelbrotConsumer(this));
		
		consumers.forEach(c -> c.start());
		boolean interrupted = false;
		for(MandelbrotConsumer mc : consumers) {
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
