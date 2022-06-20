package kernel;

import fractal.Complex;
import fractal.FractalFrame;
import gpuColorGradients.MultiGradient;

public class IntegerPowerMandelbrotKernel extends FractalKernel {

	private final int[] power;
	
	public IntegerPowerMandelbrotKernel(int power) {
		this(power, 1);
	}
	
	public IntegerPowerMandelbrotKernel(int power, int initial_iterations) {
		super(initial_iterations);
		this.power = new int[] {power};
	}
	
	@Override
	public void run() {		
		int width = this.width[0];
		
		int i = getGlobalId(0);
		int j = getGlobalId(1);
		
		int iterations = initial_iterations[0];
		
		double aux = 0;
		double re = 0;
		double im = 0;
		double currentRE = 0;
		double currentIM = 0;
		double constantRE = topLeft[0] + this.delta[0] * i;
		double constantIM = topLeft[1] + this.delta[0] * j;
		int power2 = 0;
	
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
			re = 1;
			im = 0;
			for(power2 = 1 ; power2 <= power[0] ; power2 <<= 1) {
				if((power2 & power[0]) != 0) {
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
		if(iterations < maxIterations[0]) {
			float iterationScore = (float)(iterations + 1 - log(log(sqrt(currentRE * currentRE + currentIM * currentIM)))/log(2));	// log magic makes gradient smooth :)
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
		double re = 1;
		double im = 0;
		double aux = 0;
		int power2 = 1;
		while(power2 <= power[0]) {
			if((power2 & power[0]) != 0) {
				aux = re;
				re = re * currentRE - im * currentIM;
				im = aux * currentIM + im * currentRE;
			}
			aux = currentRE;
			currentRE = currentRE * currentRE - currentIM * currentIM;
			currentIM = 2 * aux * currentIM;
			power2 <<= 1;
		}
		return re;
	}

	@Override
	public double iterateIM(double currentRE, double currentIM) {
		double re = 1;
		double im = 0;
		double aux = 0;
		int power2 = 1;
		while(power2 <= power[0]) {
			if((power2 & power[0]) != 0) {
				aux = re;
				re = re * currentRE - im * currentIM;
				im = aux * currentIM + im * currentRE;
			}
			aux = currentRE;
			currentRE = currentRE * currentRE - currentIM * currentIM;
			currentIM = 2 * aux * currentIM;
			power2 <<= 1;
		}
		return im;
	}

}
