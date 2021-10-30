package optimizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.tools.DocumentationTool.DocumentationTask;

import com.aparapi.Kernel.EXECUTION_MODE;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import com.aparapi.internal.kernel.KernelManager.DeprecatedMethods;
import com.aparapi.internal.kernel.KernelManagers;

import gradient.Gradient;
import logic.FractalFrame;

public class FractalProducer extends Thread {
	
	private List<FractalConsumer> consumers;
	public static final int DEFAULT_BUFFER_SIZE = 8;
	private final int bufferSize;
	private boolean paused;
	private final int dataSize;
	private final MyBuffer buffer;
	private final int nFrames;
	
	public FractalProducer(Gradient<FractalFrame> zoom, int nFrames, List<OpenCLDevice> devices, int bufferSize) {
		super();
		this.dataSize = zoom.getStart().getFractal().getSize();
		this.bufferSize = bufferSize;
		this.buffer = new MyBuffer(zoom.toDiscrete(nFrames).iterator());
		this.consumers = new ArrayList<>(devices.size());
		this.nFrames = nFrames;
		for(OpenCLDevice d : devices) {
			EXECUTION_MODE mode = d.getType() == OpenCLDevice.TYPE.GPU ? EXECUTION_MODE.GPU : EXECUTION_MODE.JTP;
			consumers.add(new FractalConsumer(d, mode));
			break;
		}
//		for(int i = 0 ; i < 1 ; i++) {
//			consumers.add(new FractalConsumer());
//		}
	}
	
	public FractalProducer(Gradient<FractalFrame> zoom, int nFrames) {
		this(zoom, nFrames, getAllProcessors(), DEFAULT_BUFFER_SIZE);
	}
	
	public FractalFrame getNextFrame() {
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
		for(FractalConsumer cons : consumers)
			cons.start();
		for(FractalConsumer cons : consumers)
			try {
				cons.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
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
					//System.out.println("Waiting for frame");
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
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
					e.printStackTrace();
				}
			}
			if(!it.hasNext())
				return null;
			while(windowSize() == bufferSize)
				try {
					//System.out.println("Waiting for task");
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
			if(currentTask.index == lowerBound)
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
		
		private OpenCLDevice device;
		private EXECUTION_MODE mode;
		private int chunkSize = dataSize;
		
		
		public FractalConsumer(OpenCLDevice device, EXECUTION_MODE mode) {
			this.device = device;
			this.mode = mode;
		}
		
		public FractalConsumer() {
		}
		
		private void executeChunks(FractalKernel kernel) {
			int current = 0;
			while(current < dataSize) {
				kernel = kernel.copy(current);
				kernel.executeSome(Range.create(Math.min(chunkSize, dataSize - current)));
				current += chunkSize;
			}
		}
		
		@Override
		public void run() {
			FractalTask currentTask = buffer.getNextTask();
			while(currentTask != null) {
				Range r = device.createRange(chunkSize);
				FractalKernel kernel = currentTask.frame.getFractal();
				try {
					kernel.executeSome(Range.create(chunkSize));//Range.create(chunkSize)
					//executeChunks(kernel);
					buffer.finished(currentTask);
					currentTask = buffer.getNextTask();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			super.run();
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
