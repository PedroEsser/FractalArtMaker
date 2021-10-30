package optimizations;

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
		
		while(currentRE * currentRE + currentIM * currentIM <= 4 && iterations < maxIterations[0]) {
			aux = currentRE;
			currentRE = currentRE * currentRE - currentIM * currentIM + constantRE;
			currentIM = 2 * aux * currentIM + constantIM;
			iterations++;
		}
		
		if(iterations == maxIterations[0]) {
			chunkData[getGlobalId()] = maxIterations[0];
		}else {
			
			aux = currentRE;
			currentRE = currentRE * currentRE - currentIM * currentIM + constantRE;
			currentIM = 2 * aux * currentIM + constantIM;
			aux = currentRE;
			currentRE = currentRE * currentRE - currentIM * currentIM + constantRE;
			currentIM = 2 * aux * currentIM + constantIM;
			
			float iterationScore = (float)(iterations + 1 - Math.log(Math.log(Math.sqrt(currentRE * currentRE + currentIM * currentIM))) * LOG2_RECIPROCAL[0]);
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			chunkData[getGlobalId()] = iterationScore;
		}
	}

	@Override
	public FractalKernel copy(int offset) {
		return new MandelbrotKernel(this, offset);
	}

}
