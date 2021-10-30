//package optimizations;
//
//import com.aparapi.Range;
//import com.aparapi.Kernel.EXECUTION_MODE;
//import com.aparapi.device.OpenCLDevice;
//
//
//
//public class FractalConsumer extends Thread{
//		
//		private final OpenCLDevice device;
//		private final EXECUTION_MODE mode;
//		//private int chunkSize = 1 << 18;
//		
//		
//		public FractalConsumer(OpenCLDevice device, EXECUTION_MODE mode) {
//			this.device = device;
//			this.mode = mode;
//		}
//		
//		@Override
//		public void run() {
//			FractalTask currentTask = getNextTask(chunkSize);
//			while(currentTask != null) {
//				currentTask.doTask(device, mode);
//				currentTask = getNextTask(chunkSize);
//			}
//			super.run();
//		}
//		
//	}
//	
//	private class FractalTask{
//		
//		FractalKernel kernel;
//		int chunk;
//		
//		public FractalTask(FractalKernel kernel, int chunk) {
//			this.kernel = kernel;
//			this.chunk = chunk;
//		}
//		
//		public void doTask(OpenCLDevice device, EXECUTION_MODE mode) {
//			kernel.setExecutionModeWithoutFallback(mode);
//			Range r = device.createRange(chunk);
//			kernel.myExecute(r);
//		}
//		
//	}
//	
