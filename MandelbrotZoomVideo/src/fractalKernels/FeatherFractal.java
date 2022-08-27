package fractalKernels;

import fractals_deprecated.Complex;
import gpuColorGradients.MultiGradient;

public class FeatherFractal extends FractalKernel {

	private final double[] complexPower = new double[] {0, 0};
	private final double[] offset = new double[] {1, 0};
	
	public FeatherFractal() {
		super();
		this.addParameter("RE(power)", 3, 0.0625);
		this.addParameter("IM(power)", 0, 0.0625);
		this.addParameter("RE(offset)", 1, 0.0625);
		this.addParameter("IM(offset)", 0, 0.0625);
	}
	
	@Override
	protected void loadParameterValues() {
		complexPower[0] = getParameter("RE(power)").getValue();
		complexPower[1] = getParameter("IM(power)").getValue();
		offset[0] = getParameter("RE(offset)").getValue();
		offset[1] = getParameter("IM(offset)").getValue();
		super.loadParameterValues();
	}
	
	@Override
	public void run() {		
		int width = this.width;
		
		int i = getGlobalId(0);
		int j = getGlobalId(1);
		
		int iterations = pre_iterations;
		
		double aux = 0;
		double currentRE = 0;
		double currentIM = 0;
		double zRE3 = 0;
		double zIM3 = 0;
		double wRE = 0;
		double wIM = 0;
		double constantRE = topLeftRE + this.delta * i;
		double constantIM = topLeftIM + this.delta * j;
		
		double angle = 0;
		double lnR = 0;
		double radius = 0;
		double newAngle = 0;
		
		if(iterations > 1) {
			for(int a = 0 ; a < iterations ; a++) {
				angle = atan2(currentIM, currentRE);
				lnR = log(sqrt(currentRE * currentRE + currentIM * currentIM));
				radius = exp(lnR * complexPower[0] - complexPower[1] * angle);
				newAngle = lnR * complexPower[1] + complexPower[0] * angle;
				zRE3 = cos(newAngle) * radius;
				zIM3 = sin(newAngle) * radius;
//				zRE3 = currentRE*currentRE*currentRE - 3*currentRE*currentIM*currentIM;
//				zIM3 = 3*currentRE*currentRE*currentIM - currentIM*currentIM*currentIM;
				wRE = offset[0] + currentRE*currentRE;
				wIM = offset[1] - currentIM*currentIM;
				wRE = 1 + currentRE*currentRE;
				wIM = -currentIM*currentIM;
				aux = wRE*wRE + wIM*wIM;
				wRE /= aux;
				wIM /= aux;
				currentRE = zRE3 * wRE - zIM3 * wIM + constantRE;
				currentIM = zIM3 * wRE + zRE3 * wIM + constantIM;
			}
			constantRE = currentRE;
			constantIM = currentIM;
		}
		
		currentRE = 1E-150;
		currentIM = 1E-150;
		
		iterations = 0;
		
		
		while(currentRE * currentRE + currentIM * currentIM <= escapeRadius && iterations < maxIterations) {
			if(iterations % mod != (mod-1)) {
				aux = currentRE;
				currentRE = currentRE * currentRE - currentIM * currentIM + constantRE;
				currentIM = 2 * aux * currentIM + constantIM;
			}else {
				angle = atan2(currentIM, currentRE);
				lnR = log(sqrt(currentRE * currentRE + currentIM * currentIM));
				radius = exp(lnR * complexPower[0] - complexPower[1] * angle);
				newAngle = lnR * complexPower[1] + complexPower[0] * angle;
				zRE3 = cos(newAngle) * radius;
				zIM3 = sin(newAngle) * radius;
//				zRE3 = currentRE*currentRE*currentRE - 3*currentRE*currentIM*currentIM;
//				zIM3 = 3*currentRE*currentRE*currentIM - currentIM*currentIM*currentIM;
				wRE = offset[0] + currentRE*currentRE;
				wIM = offset[1] - currentIM*currentIM;
				aux = wRE*wRE + wIM*wIM;
				wRE /= aux;
				wIM /= aux;
				currentRE = zRE3 * wRE - zIM3 * wIM + constantRE;
				currentIM = zIM3 * wRE + zRE3 * wIM + constantIM;
			}
			
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
	
	@Override
	public String getName() {
		return "Feather Fractal";
	}

	@Override
	public double iterateRE(double currentRE, double currentIM, double constantRE, double constantIM) {
		double zRE3 = currentRE*currentRE*currentRE - 3*currentRE*currentIM*currentIM;
		double zIM3 = 3*currentRE*currentRE*currentIM - currentIM*currentIM*currentIM;
		double wRE = 1 + currentRE*currentRE;
		double wIM = -currentIM*currentIM;
		double aux = wRE*wRE + wIM*wIM;
		wRE /= aux;
		wIM /= aux;
		return zRE3 * wRE - zIM3 * wIM + constantRE;
	}

	@Override
	public double iterateIM(double currentRE, double currentIM, double constantRE, double constantIM) {
		double zRE3 = currentRE*currentRE*currentRE - 3*currentRE*currentIM*currentIM;
		double zIM3 = 3*currentRE*currentRE*currentIM - currentIM*currentIM*currentIM;
		double wRE = 1 + currentRE*currentRE;
		double wIM = -currentIM*currentIM;
		double aux = wRE*wRE + wIM*wIM;
		wRE /= aux;
		wIM /= aux;
		return zIM3 * wRE + zRE3 * wIM + constantIM;
	}

	

}
