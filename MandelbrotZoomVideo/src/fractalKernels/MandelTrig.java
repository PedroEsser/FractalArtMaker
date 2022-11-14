package fractalKernels;

import gpuColorGradients.MultiGradient;

public class MandelTrig extends MandelbrotKernel {

	private double powerRE = 2;
	private double powerIM = 0;
	
	public MandelTrig() {
		super();
		addParameter("Re(power)", 2, 1f/16);
		addParameter("Im(power)", 0, 1f/16);
	}
	
	@Override
	protected void loadParameterValues() {
		powerRE = getParameter("Re(power)").getValue();
		powerIM = getParameter("Im(power)").getValue();
		super.loadParameterValues();
	}
	
	@Override
	public void run() {		
		int width = this.width;
		
		int i = getGlobalId(0);
		int j = getGlobalId(1);
		double constantRE = this.delta * (i - width/2);
		double constantIM = this.delta * (j - height/2);
		double aux = constantRE;
		constantRE = cos(angle)*aux - sin(angle)*constantIM + centerRE;
		constantIM = cos(angle)*constantIM + sin(angle)*aux + centerIM;
		
		int iterations = pre_iterations;
		
		double currentRE = 0;
		double currentIM = 0;
		
		double angle = 0;
		double lnR = 0;
		double radius = 0;
		double newAngle = 0;
		
		if(iterations > 1) {
			for(int a = 0 ; a < iterations ; a++) {
				aux = currentRE;
				currentRE = iterateRE(currentRE, currentIM, constantRE, constantIM);
				currentIM = iterateIM(aux, currentIM, constantRE, constantIM);
			}
			constantRE = currentRE;
			constantIM = currentIM;
		}
		
		currentRE = 1e-150;
		currentIM = 1e-150;
		iterations = 0;
		
		while(currentRE * currentRE + currentIM * currentIM <= 16 && iterations < maxIterations) {
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
	public String getName() {
		return "MandelTrig";
	}
	
	@Override
	public double iterateRE(double currentRE, double currentIM, double constantRE, double constantIM) {
		return tan(super.iterateRE(currentRE, currentIM, constantRE, constantIM));
	}
	
	@Override
	public double iterateIM(double currentRE, double currentIM, double constantRE, double constantIM) {
		double im = super.iterateIM(currentRE, currentIM, constantRE, constantIM);
		return cos(im) + sin(im);
	}
}
