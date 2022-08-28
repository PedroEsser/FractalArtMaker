package fractalKernels;

import gpuColorGradients.MultiGradient;

public class BurningShipKernel extends FractalKernel{
	
	@Override
	public void run() {
		int width = this.width;
		
		int i = getGlobalId(0);
		int j = getGlobalId(1);
		
		int iterations = pre_iterations;
		
		double aux = 0;
		double currentRE = 0;
		double currentIM = 0;
		double constantRE = topLeftRE + this.delta * i;
		double constantIM = topLeftIM + this.delta * j;
	
		if(iterations > 1) {
			for(int a = 0 ; a < iterations ; a++) {
				aux = currentRE;
				currentRE = iterateRE(currentRE, currentIM, constantRE, constantIM);
				currentIM = iterateIM(aux, currentIM, constantRE, constantIM);
			}
			constantRE = currentRE;
			constantIM = currentIM;
		}
		
		currentRE = 0;
		currentIM = 0;
		iterations = 0;
		
		while(currentRE*currentRE + currentIM*currentIM <= 10 && iterations < maxIterations) {
			aux = currentRE;
			currentRE = iterateRE(currentRE, currentIM, constantRE, constantIM);
			currentIM = iterateIM(aux, currentIM, constantRE, constantIM);
			
			iterations+=1;
		}
		
		int rgb = 0;
		if(iterations < maxIterations) {
			float iterationScore = (float)(iterations + 1 - log(log(sqrt(currentRE * currentRE + currentIM * currentIM)))/log(2));
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			rgb = MultiGradient.colorAtPercent(iterationScore * norm, gradient);
		}
		
		i = (width * j + i) * 3;
		data[i + 0] = (byte)(rgb & 0xFF);
		data[i + 1] = (byte)(rgb >> 8 & 0xFF);
		data[i + 2] = (byte)(rgb >> 16 & 0xFF);
	}

	@Override
	public double iterateRE(double currentRE, double currentIM, double constantRE, double constantIM) {
		return currentRE * currentRE - currentIM * currentIM + constantRE;
	}

	@Override
	public double iterateIM(double currentRE, double currentIM, double constantRE, double constantIM) {
		return abs(2 * currentRE * currentIM) + constantIM;
	}
	
	public double abs(double d) {
		if(d < 0)
			return -d;
		return d;
	}

	@Override
	public String getName() {
		return "Burning Ship";
	}
	
}
