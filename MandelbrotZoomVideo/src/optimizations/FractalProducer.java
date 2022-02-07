package optimizations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;

import gradient.Gradient;
import logic.FractalFrame;

public class FractalProducer extends Thread {
	
	private static final int N_CONSUMERS = 2;
	private final List<FractalConsumer> consumers;
	public static final int DEFAULT_BUFFER_SIZE = 16;
	private final int bufferSize;
	private boolean paused;
	private final MyBuffer buffer;
	private final int nFrames;
	
	public FractalProducer(Gradient<FractalFrame> zoom, int nFrames, int bufferSize) {
		super();
		this.bufferSize = bufferSize;
		this.buffer = new MyBuffer(zoom.toDiscrete(nFrames).iterator());
		this.consumers = new ArrayList<>();
		this.nFrames = nFrames;
		for(int i = 0 ; i < N_CONSUMERS ; i++) 
			consumers.add(new FractalConsumer());
	}
	
	public FractalProducer(Gradient<FractalFrame> zoom, int nFrames) {
		this(zoom, nFrames, DEFAULT_BUFFER_SIZE);
	}
	
	public synchronized FractalFrame getNextFrame() {
		return buffer.getNextFrame();
	}
	
	public static List<OpenCLDevice> getAllProcessors(){
		List<OpenCLDevice> processors = OpenCLDevice.listDevices(Device.TYPE.GPU);
		//processors.addAll(OpenCLDevice.listDevices(Device.TYPE.CPU));
		return processors;
	}
	
	public void pause() {
		this.paused = true;
	}
	
	public void unpause() {
		this.paused = false;
		buffer.resume();
	}
	
	@Override
	public void run() {
		FractalTask currentTask = buffer.getNextTask();
		while(currentTask != null) {
			FractalKernel kernel = currentTask.frame.getKernel();
			try {
				kernel.executeAll();
				buffer.finished(currentTask);
				currentTask = buffer.getNextTask();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		super.run();
	}
	
	private class MyBuffer{
		
		private final Iterator<FractalFrame> it;
		private final FractalFrame[] buffer;
		private int lowerBound, upperBound;
		
		public MyBuffer(Iterator<FractalFrame> it) {
			this.it = it;
			buffer = new FractalFrame[bufferSize];
		}
		
		public synchronized void resume() {
			notifyAll();
		}
		
		public synchronized FractalFrame getNextFrame() {
			if(lowerBound == nFrames)
				return null;
			while(buffer[bufferIndex(lowerBound)] == null)
				try {
					//System.out.println("Waiting for frame, window size = " + windowSize());
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			FractalFrame f = buffer[bufferIndex(lowerBound)];
			buffer[bufferIndex(lowerBound++)] = null;
			notifyAll();
			//System.out.println("Got frame " + lowerBound + ", window size = " + windowSize());
			return f;
		}
		
		public synchronized FractalTask getNextTask() {
			while(paused) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(!it.hasNext())
				return null;
			while(windowSize() == bufferSize)
				try {
					//System.out.println("Waiting for task, window size = " + windowSize());
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			notifyAll();
			return new FractalTask(it.next(), upperBound++);
		}
		
		private synchronized void finished(FractalTask currentTask) {
			//System.out.println("Finished task, "  + currentTask.index + ", " + lowerBound + ", " + upperBound);
			buffer[bufferIndex(currentTask.index)] = currentTask.frame;
			notifyAll();
		}
		
		private int windowSize() {
			return upperBound - lowerBound + 1;
		}
		
		private int bufferIndex(int index) {
			return index % bufferSize;
		}
		
	}
	
	private class FractalConsumer extends Thread{
		
		public FractalConsumer() {
		}
		
		@Override
		public void run() {
			
		}
		
	}
	
	private class FractalTask{
		
		private FractalFrame frame;
		private int index;
		
		public FractalTask(FractalFrame frame, int index) {
			this.frame = frame;
			this.index = index;
		}
		
	}
	
}
