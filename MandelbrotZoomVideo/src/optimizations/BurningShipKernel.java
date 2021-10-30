package optimizations;

import logic.FractalFrame;

public class BurningShipKernel extends FractalKernel{

	private final boolean[] absFlags;
	
	public BurningShipKernel(boolean[] absFlags) {
		super();
		this.absFlags = absFlags;
	}
	
	public BurningShipKernel() {
		this(new boolean[] {true, true});
	}

	@Override
	public void run() {
		int width = getGlobalSize(0);
		int i = getGlobalId(0);
		int j = getGlobalId(1);
		int index = j * width + i;
		
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
			currentRE = absFlags[0] ? abs(currentRE) : currentRE;
			currentIM = absFlags[1] ? abs(currentIM) : currentIM;
			iterations++;
		}
		
		if(iterations == maxIterations[0]) {
			chunkData[getGlobalId()] = maxIterations[0];
		}else {
			
			aux = currentRE;
			currentRE = currentRE * currentRE - currentIM * currentIM + constantRE;
			currentIM = 2 * aux * currentIM + constantIM;
			currentRE = absFlags[0] ? abs(currentRE) : currentRE;
			currentIM = absFlags[1] ? abs(currentIM) : currentIM;
			aux = currentRE;
			currentRE = currentRE * currentRE - currentIM * currentIM + constantRE;
			currentIM = 2 * aux * currentIM + constantIM;
			currentRE = absFlags[0] ? abs(currentRE) : currentRE;
			currentIM = absFlags[1] ? abs(currentIM) : currentIM;
			
			float iterationScore = (float)(iterations + 1 - Math.log(Math.log(Math.sqrt(currentRE * currentRE + currentIM * currentIM))) * LOG2_RECIPROCAL[0]);
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			chunkData[getGlobalId()] = iterationScore;
		}
	}
	
	protected double abs(double a) {
		return a < 0 ? -a : a;
	}

	@Override
	public FractalKernel copy(int offset) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
