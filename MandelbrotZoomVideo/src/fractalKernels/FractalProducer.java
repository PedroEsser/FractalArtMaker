package fractalKernels;

import java.util.Iterator;
import java.util.List;

import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;

import fractal.FractalFrame;
import gradient.Gradient;

public class FractalProducer extends Thread {
	
	public static final int DEFAULT_BUFFER_SIZE = 16;
	private final int bufferSize;
	private boolean paused;
	private final MyBuffer buffer;
	private final int nFrames;
	private boolean cancelled = false;
	
	public FractalProducer(Gradient<FractalFrame> zoom, int nFrames, int bufferSize) {
		super();
		this.bufferSize = bufferSize;
		this.buffer = new MyBuffer(zoom.toDiscrete(nFrames).iterator());
		this.nFrames = nFrames;
	}
	
	public FractalProducer(Gradient<FractalFrame> zoom, int nFrames) {
		this(zoom, nFrames, DEFAULT_BUFFER_SIZE);
	}
	
	public FractalFrame getNextFrame() {
		return buffer.getNextFrame();
	}
	
	public void pause() {
		this.paused = true;
	}
	
	public void unpause() {
		this.paused = false;
		buffer.resume();
	}
	
	public void cancel() {
		cancelled = true;
	}
	
	public boolean cancelled() {
		return cancelled;
	}
	
	@Override
	public void run() {
		FractalTask currentTask = buffer.getNextTask();
		while(currentTask != null && !cancelled) {
			FractalKernel kernel = currentTask.frame.getKernel();
			try {
				kernel.loadAndExecute();
				buffer.finishedTask(currentTask);
				currentTask = buffer.getNextTask();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
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
					wait();
				} catch (InterruptedException e) {
					return null;
				}
			FractalFrame f = buffer[bufferIndex(lowerBound)];
			buffer[bufferIndex(lowerBound++)] = null;
			notifyAll();
			return f;
		}
		
		public synchronized FractalTask getNextTask() {
			while(paused) {
				try {
					wait();
				} catch (InterruptedException e) {
					return null;
				}
			}
			if(!it.hasNext()) 
				return null;
				
			while(windowSize() == bufferSize)
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			notifyAll();
			return new FractalTask(it.next(), upperBound++);
		}
		
		private synchronized void finishedTask(FractalTask task) {
			buffer[bufferIndex(task.index)] = task.frame;
			notifyAll();
		}
		
		private int windowSize() {
			return upperBound - lowerBound + 1;
		}
		
		private int bufferIndex(int index) {
			return index % bufferSize;
		}
		
	}
	private class FractalTask{
		
		private final FractalFrame frame;
		private final int index;
		
		public FractalTask(FractalFrame frame, int index) {
			this.frame = frame;
			this.index = index;
		}
		
	}
	
}
