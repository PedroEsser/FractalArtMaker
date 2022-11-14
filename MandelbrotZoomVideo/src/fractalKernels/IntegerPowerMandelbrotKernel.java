package fractalKernels;

import gpuColorGradients.MultiGradient;

public class IntegerPowerMandelbrotKernel extends FractalKernel {

	protected int power;
	
	public IntegerPowerMandelbrotKernel(int power) {
		super();
		this.addParameter("Power", power, 1);
	}
	
	public IntegerPowerMandelbrotKernel() {
		this(2);
	}
	
	@Override
	protected void loadParameterValues() {
		this.power = this.getParameter("Power").getValueAsInt();
		super.loadParameterValues();
	}
	
	@Override
	public void run() {		
		int width = this.width;
		
		int i = getGlobalId(0);
		int j = getGlobalId(1);
		
		int iterations = pre_iterations;
		
		double aux = 0;
		double re = 0;
		double im = 0;
		double currentRE = 0;
		double currentIM = 0;
		double constantRE = topLeftRE + this.delta * i;
		double constantIM = topLeftIM + this.delta * j;
		int power2 = 0;
	
		for(int a = 0 ; a < iterations ; a++) {
			re = 1;
			im = 0;
			for(power2 = 1 ; power2 <= power ; power2 <<= 1) {
				if((power2 & power) != 0) {
					aux = re;
					re = re * currentRE - im * currentIM;
					im = aux * currentIM + im * currentRE;
				}
				aux = currentRE;
				currentRE = currentRE * currentRE - currentIM * currentIM;
				currentIM = 2 * aux * currentIM;
			}
			currentRE = re + constantRE;
			currentIM = im + constantIM;
		}
		
			
		constantRE = currentRE;
		constantIM = currentIM;
		
		currentRE = 0;
		currentIM = 0;
		iterations = 0;
		
		while(currentRE * currentRE + currentIM * currentIM <= escapeRadius && iterations < maxIterations) {
			re = 1;
			im = 0;
			for(power2 = 1 ; power2 <= power ; power2 <<= 1) {
				if((power2 & power) != 0) {
					aux = re;
					re = re * currentRE - im * currentIM;
					im = aux * currentIM + im * currentRE;
				}
				aux = currentRE;
				currentRE = currentRE * currentRE - currentIM * currentIM;
				currentIM = 2 * aux * currentIM;
			}
			currentRE = re + constantRE;
			currentIM = im + constantIM;
			
			iterations+=1;
		}
		int rgb = 0;
		if(iterations < maxIterations) {
			float iterationScore = (float)(iterations + 1 - log(log(sqrt(currentRE * currentRE + currentIM * currentIM)))/log(2));	// log magic makes gradient smooth :)
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
		double re = 1;
		double im = 0;
		double aux = 0;
		for(int power2 = 1 ; power2 <= power; power <<= 1) {
			if((power2 & power) != 0) {
				aux = re;
				re = re * currentRE - im * currentIM;
				im = aux * currentIM + im * currentRE;
			}
			aux = currentRE;
			currentRE = currentRE * currentRE - currentIM * currentIM;
			currentIM = 2 * aux * currentIM;
		}
		return re + constantRE;
	}

	@Override
	public double iterateIM(double currentRE, double currentIM, double constantRE, double constantIM) {
		double re = 1;
		double im = 0;
		double aux = 0;
		for(int power2 = 1 ; power2 <= power; power <<= 1) {
			if((power2 & power) != 0) {
				aux = re;
				re = re * currentRE - im * currentIM;
				im = aux * currentIM + im * currentRE;
			}
			aux = currentRE;
			currentRE = currentRE * currentRE - currentIM * currentIM;
			currentIM = 2 * aux * currentIM;
		}
		return im + constantIM;
	}

	@Override
	public String getName() {
		return "Integer Power Mandelbrot";
	}
	
}
