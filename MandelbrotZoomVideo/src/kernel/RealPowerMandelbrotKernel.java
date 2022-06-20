package kernel;

import gpuColorGradients.ColorGradient;
import gpuColorGradients.MultiGradient;

public class RealPowerMandelbrotKernel extends FractalKernel{

	private final double[] realPower;
	
	public RealPowerMandelbrotKernel(double power) {
		super();
		this.realPower = new double[] {power};
	}

	@Override
	public void run() {
		int width = this.width[0];
		
		int i = getGlobalId(0);
		int j = getGlobalId(1);
		
		int iterations = initial_iterations[0];
		
		double angle = 0;
		double radius = 0;
		
		double currentRE = 0;
		double currentIM = 0;
		double constantRE = topLeft[0] + this.delta[0] * i;
		double constantIM = topLeft[1] + this.delta[0] * j;
		
		for(int a = 0 ; a < iterations ; a++) {
			angle = Math.atan2(currentIM, currentRE) * realPower[0];
			radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), realPower[0]);
			currentRE = Math.cos(angle) * radius + constantRE;
			currentIM = Math.sin(angle) * radius + constantIM;
		}
		
		constantRE = currentRE;
		constantIM = currentIM;
			
		currentRE = 0;
		currentIM = 0;
		iterations = 0;
		
		while(radius <= escapeRadius[0] && iterations < maxIterations[0]) {
			angle = Math.atan2(currentIM, currentRE) * realPower[0];
			radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), realPower[0]);
			currentRE = Math.cos(angle) * radius + constantRE;
			currentIM = Math.sin(angle) * radius + constantIM;
			
			angle = Math.atan2(currentIM, currentRE) * realPower[0];
			radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), realPower[0]);
			currentRE = Math.cos(angle) * radius + constantRE;
			currentIM = Math.sin(angle) * radius + constantIM;
			
			angle = Math.atan2(currentIM, currentRE) * realPower[0];
			radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), realPower[0]);
			currentRE = Math.cos(angle) * radius + constantRE;
			currentIM = Math.sin(angle) * radius + constantIM;
			
			angle = Math.atan2(currentIM, currentRE) * realPower[0];
			radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), realPower[0]);
			currentRE = Math.cos(angle) * radius + constantRE;
			currentIM = Math.sin(angle) * radius + constantIM;
			
			angle = Math.atan2(currentIM, currentRE) * realPower[0];
			radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), realPower[0]);
			currentRE = Math.cos(angle) * radius + constantRE;
			currentIM = Math.sin(angle) * radius + constantIM;

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
	
	public double iterateRE(double currentRE, double currentIM) {
		double angle = Math.atan2(currentIM, currentRE) * realPower[0];
		double radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), realPower[0]);
		return Math.cos(angle) * radius;
	}
	
	public double iterateIM(double currentRE, double currentIM) {
		double angle = Math.atan2(currentIM, currentRE) * realPower[0];
		double radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), realPower[0]);
		return Math.sin(angle) * radius;
	}
	
}

