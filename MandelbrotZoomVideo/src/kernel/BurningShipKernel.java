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
		
		if(iterations > 1)
			for(int a = 0 ; a < iterations ; a++) {
				aux = currentRE;
				currentRE = iterateRE(currentRE, currentIM) + constantRE;
				currentIM = iterateIM(aux, currentIM) + constantIM;
			}
			
		constantRE = currentRE;
		constantIM = currentIM;
		iterations = 0;
		currentRE = 0;
		currentIM = 0;
		
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
		
		if(iterations >= maxIterations[0]) {
			int in = (width * j + i) * 3;
			data[in + 0] = 0;
			data[in + 1] = 0;
			data[in + 2] = 0;
		}else {
			float iterationScore = (float)(iterations + 1 - Math.log(Math.log(Math.sqrt(currentRE*currentRE + currentIM*currentIM))) * LOG2_RECIPROCAL[0]);
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			int rgb = MultiGradient.colorAt(iterationScore * norm[0], gradient);
			FractalKernel.saveToColor(rgb, (width * j + i), data);
		}
	}

	@Override
	public double iterateRE(double currentRE, double currentIM) {
		double re = currentRE * currentRE - currentIM * currentIM;
		return re;
	}

	@Override
	public double iterateIM(double currentRE, double currentIM) {
		double im = 2 * currentRE * currentIM;
		return abs(im);
	}
	
	public double abs(double d) {
		if(d < 0)
			return -d;
		return d;
	}
	
}
