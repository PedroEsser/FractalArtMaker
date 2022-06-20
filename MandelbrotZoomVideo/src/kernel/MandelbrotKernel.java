package kernel;

import fractal.FractalFrame;
import gpuColorGradients.MultiGradient;

public class MandelbrotKernel extends FractalKernel{
	
	public MandelbrotKernel() {
		super();
	}
	
	public MandelbrotKernel(FractalFrame frame, int initial_iterations) {
		super(frame, initial_iterations);
	}
	
	public final double[] current = new double[2];
	
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
		
		while(currentRE * currentRE + currentIM * currentIM <= escapeRadius[0] && iterations < maxIterations[0]) {
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

			iterations+=5;		//5 iterations at once will make it run a little bit faster
		}
		int rgb = 0;
		if(iterations < maxIterations[0]) {
			float iterationScore = (float)(iterations + 1 - log(log(sqrt(currentRE * currentRE + currentIM * currentIM)))/log(2));
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			rgb = MultiGradient.colorAt(iterationScore * norm[0], gradient);
		}
		
//		current[0] = 0;
//		current[1] = 0;
//		
//		while(current[0] * current[0] + current[1] * current[1] <= escapeRadius[0] && iterations < maxIterations[0]) {
//			iterate(current, constantRE, constantIM);
//			iterate(current, constantRE, constantIM);
//			iterate(current, constantRE, constantIM);
//			iterate(current, constantRE, constantIM);
//			iterate(current, constantRE, constantIM);
//
//			iterations+=5;
//		}
//		int rgb = 0;
//		if(iterations < maxIterations[0]) {
//			float iterationScore = (float)(iterations + 1 - log(log(sqrt(current[0] * current[0] + current[1] * current[1])))/log(2));
//			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
//			rgb = MultiGradient.colorAt(iterationScore * norm[0], gradient);
//		}
		
		i = (width * j + i) * 3;
		data[i + 0] = (byte)(rgb & 0xFF);
		data[i + 1] = (byte)(rgb >> 8 & 0xFF);
		data[i + 2] = (byte)(rgb >> 16 & 0xFF);
	}
	
	private void iterate(double[] current, double constantRE, double contantIM) {
		double aux = current[0];
		current[0] = current[0] * current[0] - current[1] * current[1] + constantRE;
		current[1] = 2 * aux * current[1] + contantIM;
	}
	
	public double iterateRE(double currentRE, double currentIM) {
		return currentRE * currentRE - currentIM * currentIM;
	}
	
	public double iterateIM(double currentRE, double currentIM) {
		return 2 * currentRE * currentIM;
	}

}
