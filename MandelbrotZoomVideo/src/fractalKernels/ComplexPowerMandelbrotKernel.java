package fractalKernels;

import fractals_deprecated.Complex;
import gpuColorGradients.MultiGradient;

public class ComplexPowerMandelbrotKernel extends FractalKernel{
	
	private double powerRE = 0;
	private double powerIM = 0;
	
	public ComplexPowerMandelbrotKernel(Complex c) {
		super();
		this.addParameter("Re(power)", c.getRe(), 0.05);
		this.addParameter("Im(power)", c.getIm(), 0.05);
	}
	
	public ComplexPowerMandelbrotKernel(double re, double im) {
		this(new Complex(re, im));
	}
	
	public ComplexPowerMandelbrotKernel() {
		this(2, 0);
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
		
		int iterations = pre_iterations;
		
		double currentRE = 1E-150;
		double currentIM = 1E-150;
		double constantRE = topLeftRE + this.delta * i;
		double constantIM = topLeftIM + this.delta * j;
		
		double angle = 0;
		double lnR = 0;
		double radius = 0;
		double newAngle = 0;
		
		for(int a = 0 ; a < iterations ; a++) {
			if(iterations%mod != (mod-1)) {
				angle = currentRE;
				currentRE = currentRE * currentRE - currentIM * currentIM + constantRE;
				currentIM = 2 * angle * currentIM + constantIM;
			}else {
				angle = atan2(currentIM, currentRE);
				lnR = log(sqrt(currentRE * currentRE + currentIM * currentIM));
				radius = exp(lnR * powerRE - powerIM * angle);
				newAngle = lnR * powerIM + powerRE * angle;
				currentRE = cos(newAngle) * radius + constantRE;
				currentIM = sin(newAngle) * radius + constantIM;
			}
		}
			
		constantRE = currentRE;
		constantIM = currentIM;
		iterations = 0;
			
		currentRE = 1E-150;
		currentIM = 1E-150;
		
		while(currentRE*currentRE + currentIM*currentIM <= 10 && iterations < maxIterations) {
			if(iterations%mod != (mod-1)) {
				angle = currentRE;
				currentRE = currentRE * currentRE - currentIM * currentIM + constantRE;
				currentIM = 2 * angle * currentIM + constantIM;
			}else {
				angle = atan2(currentIM, currentRE);
				lnR = log(sqrt(currentRE * currentRE + currentIM * currentIM));
				radius = exp(lnR * powerRE - powerIM * angle);
				newAngle = lnR * powerIM + powerRE * angle;
				currentRE = cos(newAngle) * radius + constantRE;
				currentIM = sin(newAngle) * radius + constantIM;
			}
			
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
		double angle = atan2(currentRE, currentIM);
		double lnR = log(sqrt(currentIM * currentIM + currentRE * currentRE));
		double radius = exp(lnR * powerRE - powerIM * angle);
		double newAngle = lnR * powerIM + powerRE * angle;
		return cos(newAngle) * radius + constantRE;
	}

	@Override
	public double iterateIM(double currentRE, double currentIM, double constantRE, double constantIM) {
		double angle = atan2(currentRE, currentIM);
		double lnR = log(sqrt(currentIM * currentIM + currentRE * currentRE));
		double radius = exp(lnR * powerRE - powerIM * angle);
		double newAngle = lnR * powerIM + powerRE * angle;
		return sin(newAngle) * radius + constantIM;
	}
	
	@Override
	public String getName() {
		return "Complex Power Mandelbrot";
	}

}
