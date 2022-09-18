package new_engine;

import java.util.function.Consumer;

import fractalKernels.FractalKernel;

public class KernelHandler {

	private static FractalTask currentTask;
	private static FractalTask nextTask;
	
	public static FractalTask doTask(FractalKernel kernel, Consumer<Boolean> callback) {
		FractalTask task = new FractalTask(kernel, callback);
		if(currentTask == null) {
			currentTask = task;
			currentTask.start();
		}else
			nextTask = task;
		return task;
	}
	
	private static void runNextTask() {
		currentTask = nextTask;
		nextTask = null;
		if(currentTask != null)
			currentTask.start();
	}
	
	static class FractalTask extends Thread{
		FractalKernel kernel;
		Consumer<Boolean> callback;
		boolean cancelled;
		public FractalTask(FractalKernel kernel, Consumer<Boolean> callback) {
			this.kernel = kernel;
			this.callback = callback;
		}
		
		public void cancel() {
			cancelled = true;
		}
		
		@Override
		public void run() {
			kernel.executeAll();
			callback.accept(cancelled);
			runNextTask();
		}
	}
	
}
