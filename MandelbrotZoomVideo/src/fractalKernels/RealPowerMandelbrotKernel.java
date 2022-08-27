package fractalKernels;

import gpuColorGradients.MultiGradient;

public class RealPowerMandelbrotKernel extends FractalKernel{

	private double power;
	
	public RealPowerMandelbrotKernel(double power) {
		super();
		this.addParameter("Power", power, 0.125);
	}
	
	public RealPowerMandelbrotKernel() {
		this(2);
	}
	
	@Override
	protected void loadParameterValues() {
		this.power = getParameter("Power").getValue();
		super.loadParameterValues();
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
		
		if(iterations > 1) {
			for(int a = 0 ; a < iterations ; a++) {
				angle = atan2(currentIM, currentRE) * power;
				radius = pow(currentRE * currentRE + currentIM * currentIM, power/2);
				currentRE = cos(angle) * radius + constantRE;
				currentIM = sin(angle) * radius + constantIM;
			}
			constantRE = currentRE;
			constantIM = currentIM;
		}
			
		currentRE = constantRE;
		currentIM = constantIM;
		iterations = 1;
		
		while(radius <= escapeRadius && iterations < maxIterations) {
			angle = atan2(currentIM, currentRE) * power;
			radius = pow(currentRE * currentRE + currentIM * currentIM, power/2);
			currentRE = cos(angle) * radius + constantRE;
			currentIM = sin(angle) * radius + constantIM;

			iterations+=1;
		}
		int rgb = 0;
		if(iterations < maxIterations) {
			float iterationScore = (float)(iterations + 1 - log(log(sqrt(currentRE * currentRE + currentIM * currentIM)))/log(2));
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			rgb = MultiGradient.colorAt(iterationScore * norm, gradient);
		}
		i = (width * j + i) * 3;
		data[i + 0] = (byte)(rgb & 0xFF);
		data[i + 1] = (byte)(rgb >> 8 & 0xFF);
		data[i + 2] = (byte)(rgb >> 16 & 0xFF);
	}
	
	public double iterateRE(double currentRE, double currentIM, double constantRE, double constantIM) {
		double angle = Math.atan2(currentIM, currentRE) * power;
		double radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), power);
		return Math.cos(angle) * radius + constantRE;
	}
	
	public double iterateIM(double currentRE, double currentIM, double constantRE, double constantIM) {
		double angle = Math.atan2(currentIM, currentRE) * power;
		double radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), power);
		return Math.sin(angle) * radius + constantIM;
	}
	
	@Override
	public String getName() {
		return "Real Power Mandelbrot";
	}
	
}

