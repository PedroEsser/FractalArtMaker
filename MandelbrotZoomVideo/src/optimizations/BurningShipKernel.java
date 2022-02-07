package optimizations;

import gpuColorGradients.MultiGradient;

public class BurningShipKernel extends FractalKernel{

	private final boolean[] absFlags;
	
	public BurningShipKernel(boolean absRE, boolean absIM) {
		super();
		this.absFlags = new boolean[] {absRE, absIM};
	}
	
	public BurningShipKernel() {
		this(true, true);
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
			currentRE = absFlags[0] ? abs(currentRE) : currentRE;
			currentIM = absFlags[1] ? abs(currentIM) : currentIM;
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
			currentRE = absFlags[0] ? abs(currentRE) : currentRE;
			currentIM = absFlags[1] ? abs(currentIM) : currentIM;
			aux = currentRE;
			currentRE = currentRE * currentRE - currentIM * currentIM + constantRE;
			currentIM = 2 * aux * currentIM + constantIM;
			currentRE = absFlags[0] ? abs(currentRE) : currentRE;
			currentIM = absFlags[1] ? abs(currentIM) : currentIM;
			
			float iterationScore = (float)(iterations + 1 - Math.log(Math.log(Math.sqrt(currentRE * currentRE + currentIM * currentIM))) * LOG2_RECIPROCAL[0]);
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			int rgb = MultiGradient.colorAt(iterationScore * norm[0], gradient);
			int in = getGlobalId() * 3;
			chunkData[in + 0] = (byte)(rgb & 0xFF);
			chunkData[in + 1] = (byte)(rgb >> 8 & 0xFF);
			chunkData[in + 2] = (byte)(rgb >> 16 & 0xFF);
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
