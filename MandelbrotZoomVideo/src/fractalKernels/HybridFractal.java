package fractalKernels;

import gpuColorGradients.MultiGradient;

public class HybridFractal extends FractalKernel{

	private double multiplier = 0.5;
	private double power = 2;
	
	public HybridFractal() {
		super();
		addParameter("multiplier", 0.5, 0.01);
		addParameter("power", 2, 0.001);
	}
	
	@Override
	protected void loadParameterValues() {
		this.multiplier = getParameter("multiplier").getValue();
		this.power = getParameter("power").getValue();
		super.loadParameterValues();
	}
	
	@Override
	public String getName() {
		return "Hybrid Fractal";
	}

	@Override
	public double iterateRE(double currentRE, double currentIM, double constantRE, double constantIM) {
		return 0;
	}

	@Override
	public double iterateIM(double currentRE, double currentIM, double constantRE, double constantIM) {
		return 0;
	}

	@Override
	public void run() {		
		int width = this.width;
		
		int i = getGlobalId(0);
		int j = getGlobalId(1);
		
		int iterations = pre_iterations;
		
		double currentRE = 0;
		double currentIM = 0;
		double constantRE = topLeftRE + this.delta * i;
		double constantIM = topLeftIM + this.delta * j;
		double angle = 0;
		double radius = 0;
		double p = 0;
		
		for(int a = 0 ; a < iterations ; a++) {
			p = power + (a%mod * multiplier);
			angle = Math.atan2(currentIM, currentRE) * p;
			radius = Math.pow(currentRE * currentRE + currentIM * currentIM, p/2);
			currentRE = Math.cos(angle) * radius + constantRE;
			currentIM = Math.sin(angle) * radius + constantIM;
		}
		
		constantRE = currentRE;
		constantIM = currentIM;
			
		currentRE = 0;
		currentIM = 0;
		iterations = 0;
		
		while(radius <= escapeRadius && iterations < maxIterations) {
			p = power + (iterations%mod * multiplier);
			angle = Math.atan2(currentIM, currentRE) * p;
			radius = Math.pow(currentRE * currentRE + currentIM * currentIM, p/2);
			currentRE = Math.cos(angle) * radius + constantRE;
			currentIM = abs(Math.sin(angle) * radius + constantIM);

			iterations+=1;
		}
		int rgb = 0;
		if(iterations < maxIterations) {
			float iterationScore = (float)(iterations + 1 - log(log(sqrt(currentRE * currentRE + currentIM * currentIM)))/log(2));	// log magic makes gradient smooth :)
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			rgb = MultiGradient.colorAt(iterationScore * norm, gradient);
		}
		i = (width * j + i) * 3;
		data[i + 0] = (byte)(rgb & 0xFF);
		data[i + 1] = (byte)(rgb >> 8 & 0xFF);
		data[i + 2] = (byte)(rgb >> 16 & 0xFF);
	}

}
