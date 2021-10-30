package optimizations;

import java.util.ArrayList;
import java.util.List;

import com.aparapi.Kernel.EXECUTION_MODE;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;

import logic.FractalFrame;

public class DeviceManager extends Thread{
	
	private List<FractalConsumer> consumers;
	private final FractalKernel kernel;
	private int offset = 0;
	private boolean paused, cancelled;
	private int chunkSize;
	
	public DeviceManager(FractalKernel kernel, List<OpenCLDevice> devices) {
		this.kernel = kernel;
		consumers = new ArrayList<>();
		devices.sort((a, b) -> a.compareTo(b));
		for(OpenCLDevice d : devices) {
			EXECUTION_MODE mode = d.getType() == OpenCLDevice.TYPE.GPU ? EXECUTION_MODE.GPU : EXECUTION_MODE.CPU;
			consumers.add(new FractalConsumer(d, mode));
		}
		this.chunkSize = kernel.getSize() / consumers.size() + 1;
	}
	
	public DeviceManager(FractalKernel kernel) {
		this(kernel, getAllProcessors());
	}
	
	public DeviceManager(FractalFrame frame) {
		this(frame.getFractal(), getAllProcessors());
	}
	
	public static List<OpenCLDevice> getAllProcessors(){
		List<OpenCLDevice> processors = OpenCLDevice.listDevices(Device.TYPE.GPU);
		processors.addAll(OpenCLDevice.listDevices(Device.TYPE.CPU));
		return processors;
	}
	
	public void pause() {
		this.paused = true;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void cancel() {
		this.cancelled = true;
	}
	
	public synchronized void unpause() {
		this.paused = false;
		notifyAll();
	}
	
	private synchronized void checkPause() {
		while(paused) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private synchronized FractalTask getNextTask(int chunk) {
		if(isCancelled())
			return null;
		checkPause();
		if(offset + chunk > kernel.getSize()){
			if(offset == kernel.getSize())
				return null;
			chunk = kernel.getSize() - offset;
		}
		FractalTask task = new FractalTask(kernel.copy(offset), chunk);
		offset += chunk;
		return task;
	}
	
	@Override
	public void run() {
		
		//long t = System.currentTimeMillis();
		
		for(FractalConsumer cons : consumers)
			cons.start();
		for(FractalConsumer cons : consumers)
			try {
				cons.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		//long diff = System.currentTimeMillis() - t;
		//System.out.println((double)diff / 1000 + " seconds passed");
	}
	
	
	private class FractalConsumer extends Thread{
		
		private final OpenCLDevice device;
		private final EXECUTION_MODE mode;
		//private int chunkSize = 1 << 18;
		
		
		public FractalConsumer(OpenCLDevice device, EXECUTION_MODE mode) {
			this.device = device;
			this.mode = mode;
		}
		
		@Override
		public void run() {
			FractalTask currentTask = getNextTask(chunkSize);
			while(currentTask != null) {
				currentTask.doTask(device, mode);
				currentTask = getNextTask(chunkSize);
			}
			super.run();
		}
		
	}
	
	private class FractalTask{
		
		FractalKernel kernel;
		int chunk;
		
		public FractalTask(FractalKernel kernel, int chunk) {
			this.kernel = kernel;
			this.chunk = chunk;
		}
		
		public void doTask(OpenCLDevice device, EXECUTION_MODE mode) {
			kernel.setExecutionModeWithoutFallback(mode);
			Range r = device.createRange(chunk);
			kernel.myExecute(r);
		}
		
	}
	
}
