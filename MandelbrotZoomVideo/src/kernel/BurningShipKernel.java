package kernel;

import gpuColorGradients.MultiGradient;

public class BurningShipKernel extends FractalKernel{
	
	public BurningShipKernel() {
		super();
	}

	@Override
	public void run() {
		int width = this.width[0];
		
		int i = getGlobalId(0);
		int j = getGlobalId(1);
		
		int iterations = initial_iterations[0];
		
		double aux = 0;
		double currentRE = 0;
		double currentIM = 0;
		double constantRE = topLeft[0] + this.delta[0] * i;
		double constantIM = topLeft[1] + this.delta[0] * j;
	
		for(int a = 0 ; a < iterations ; a++) {
			aux = currentRE;
			currentRE = iterateRE(currentRE, currentIM) + constantRE;
			currentIM = iterateIM(aux, currentIM) + constantIM;
		}
			
		constantRE = currentRE;
		constantIM = currentIM;
		
		currentRE = 0;
		currentIM = 0;
		iterations = 0;
		
		while(currentRE*currentRE + currentIM*currentIM <= 10 && iterations < maxIterations[0]) {
			aux = currentRE;
			currentRE = iterateRE(currentRE, currentIM) + constantRE;
			currentIM = iterateIM(aux, currentIM) + constantIM;
			aux = currentRE;
			currentRE = iterateRE(currentRE, currentIM) + constantRE;
			currentIM = iterateIM(aux, currentIM) + constantIM;
			aux = currentRE;
			currentRE = iterateRE(currentRE, currentIM) + constantRE;
			currentIM = iterateIM(aux, currentIM) + constantIM;
			aux = currentRE;
			currentRE = iterateRE(currentRE, currentIM) + constantRE;
			currentIM = iterateIM(aux, currentIM) + constantIM;
			aux = currentRE;
			currentRE = iterateRE(currentRE, currentIM) + constantRE;
			currentIM = iterateIM(aux, currentIM) + constantIM;
			
			iterations+=5;
		}
		
		int rgb = 0;
		if(iterations < maxIterations[0]) {
			float iterationScore = (float)(iterations + 1 - log(log(sqrt(currentRE * currentRE + currentIM * currentIM)))/log(2));
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			rgb = MultiGradient.colorAt(iterationScore * norm[0], gradient);
		}
		
		i = (width * j + i) * 3;
		data[i + 0] = (byte)(rgb & 0xFF);
		data[i + 1] = (byte)(rgb >> 8 & 0xFF);
		data[i + 2] = (byte)(rgb >> 16 & 0xFF);
	}

	@Override
	public double iterateRE(double currentRE, double currentIM) {
		return currentRE * currentRE - currentIM * currentIM;
	}

	@Override
	public double iterateIM(double currentRE, double currentIM) {
		return abs(2 * currentRE * currentIM);
	}
	
	public double abs(double d) {
		if(d < 0)
			return -d;
		return d;
	}
	
}
