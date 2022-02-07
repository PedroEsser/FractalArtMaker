package optimizations;

import gpuColorGradients.ColorGradient;
import gpuColorGradients.MultiGradient;
import logic.FractalFrame;

public class MandelbrotKernel extends FractalKernel{
	
	public MandelbrotKernel(FractalFrame frame) {
		super(frame, 0);
	}
	
	public MandelbrotKernel(MandelbrotKernel k, int offset) {
		super(k, offset);
	}
	
	public MandelbrotKernel() {
		super();
	}
	
	@Override
	public void run() {
		int width = re.length;
		int index = getGlobalId() + offset[0];
		int i = index % width;
		int j = index / width;
		
		double constantRE = re[i];
		double constantIM = im[j];
		
		double currentRE = 0;
		double currentIM = 0;
		double aux = 0;
		
		int iterations = 0;
		
		while(currentRE * currentRE + currentIM * currentIM <= 10 && iterations < maxIterations[0]) {
			aux = currentRE;
			currentRE = currentRE * currentRE - currentIM * currentIM + constantRE;
			currentIM = 2 * aux * currentIM + constantIM;
			aux = currentRE;
//			currentRE = Math.tan(currentRE);	
//			currentIM = Math.sin(currentIM) + Math.cos(currentIM);
			iterations++;
		}
		
		if(iterations == maxIterations[0]) {
			int in = getGlobalId() * 3;
			chunkData[in + 0] = 0;
			chunkData[in + 1] = 0;
			chunkData[in + 2] = 0;
		}else {
			aux = currentRE;
			currentRE = currentRE * currentRE - currentIM * currentIM + constantRE;
			currentIM = 2 * aux * currentIM + constantIM;
			aux = currentRE;
			currentRE = currentRE * currentRE - currentIM * currentIM + constantRE;
			currentIM = 2 * aux * currentIM + constantIM;
			
			float iterationScore = (float)(iterations + 1 - Math.log(Math.log(Math.sqrt(currentRE * currentRE + currentIM * currentIM))) * LOG2_RECIPROCAL[0]);
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			int rgb = MultiGradient.colorAt(iterationScore * norm[0], gradient);
			int in = getGlobalId() * 3;
			chunkData[in + 0] = (byte)(rgb & 0xFF);
			chunkData[in + 1] = (byte)(rgb >> 8 & 0xFF);
			chunkData[in + 2] = (byte)(rgb >> 16 & 0xFF);
		}
	}

	@Override
	public FractalKernel copy(int offset) {
		return new MandelbrotKernel(this, offset);
	}

}
