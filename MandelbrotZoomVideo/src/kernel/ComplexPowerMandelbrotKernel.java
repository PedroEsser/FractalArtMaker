package kernel;

import fractal.Complex;
import fractal.FractalFrame;
import gpuColorGradients.MultiGradient;

public class ComplexPowerMandelbrotKernel extends FractalKernel{
	
	private final double[] complexPower;
	
	public ComplexPowerMandelbrotKernel(Complex c) {
		super();
		this.complexPower = new double[] {c.getRe(), c.getIm()};
	}
	
	public ComplexPowerMandelbrotKernel(double re, double im) {
		this(new Complex(re, im));
	}

	@Override
	public void run() {
		int width = this.width[0];
		
		int i = getGlobalId(0);
		int j = getGlobalId(1);
		
		double currentRE = 1E-150;
		double currentIM = 1E-150;
		double constantRE = topLeft[0] + this.delta[0] * i;
		double constantIM = topLeft[1] + this.delta[0] * j;
		
		int iterations = initial_iterations[0];
		double angle = 0;
		double lnR = 0;
		double radius = 0;
		double newAngle = 0;
		
		for(int a = 0 ; a < iterations ; a++) {
			angle = atan2(currentIM, currentRE);
			lnR = log(sqrt(currentRE * currentRE + currentIM * currentIM));
			radius = exp(lnR * complexPower[0] - complexPower[1] * angle);
			newAngle = lnR * complexPower[1] + complexPower[0] * angle;
			currentRE = cos(newAngle) * radius + constantRE;
			currentIM = sin(newAngle) * radius + constantIM;
		}
			
		constantRE = currentRE;
		constantIM = currentIM;
		iterations = 0;
			
		currentRE = 1E-150;
		currentIM = 1E-150;
		
		while(currentRE*currentRE + currentIM*currentIM <= 10 && iterations < maxIterations[0]) {
			angle = atan2(currentIM, currentRE);
			lnR = log(sqrt(currentRE * currentRE + currentIM * currentIM));
			radius = exp(lnR * complexPower[0] - complexPower[1] * angle);
			newAngle = lnR * complexPower[1] + complexPower[0] * angle;
			currentRE = cos(newAngle) * radius + constantRE;
			currentIM = sin(newAngle) * radius + constantIM;

			angle = atan2(currentIM, currentRE);
			lnR = log(sqrt(currentRE * currentRE + currentIM * currentIM));
			radius = exp(lnR * complexPower[0] - complexPower[1] * angle);
			newAngle = lnR * complexPower[1] + complexPower[0] * angle;
			currentRE = cos(newAngle) * radius + constantRE;
			currentIM = sin(newAngle) * radius + constantIM;
			
			angle = atan2(currentIM, currentRE);
			lnR = log(sqrt(currentRE * currentRE + currentIM * currentIM));
			radius = exp(lnR * complexPower[0] - complexPower[1] * angle);
			newAngle = lnR * complexPower[1] + complexPower[0] * angle;
			currentRE = cos(newAngle) * radius + constantRE;
			currentIM = sin(newAngle) * radius + constantIM;
			
			angle = atan2(currentIM, currentRE);
			lnR = log(sqrt(currentRE * currentRE + currentIM * currentIM));
			radius = exp(lnR * complexPower[0] - complexPower[1] * angle);
			newAngle = lnR * complexPower[1] + complexPower[0] * angle;
			currentRE = cos(newAngle) * radius + constantRE;
			currentIM = sin(newAngle) * radius + constantIM;
			
			angle = atan2(currentIM, currentRE);
			lnR = log(sqrt(currentRE * currentRE + currentIM * currentIM));
			radius = exp(lnR * complexPower[0] - complexPower[1] * angle);
			newAngle = lnR * complexPower[1] + complexPower[0] * angle;
			currentRE = cos(newAngle) * radius + constantRE;
			currentIM = sin(newAngle) * radius + constantIM;
			
			iterations+=5;
		}
		
		if(iterations >= maxIterations[0]) {
			int in = (width * j + i) * 3;
			data[in + 0] = 0;
			data[in + 1] = 0;
			data[in + 2] = 0;
		}else {
			float iterationScore = (float)(iterations + 1 - log(log(sqrt(currentRE*currentRE + currentIM*currentIM))) * LOG2_RECIPROCAL[0]);
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			int rgb = MultiGradient.colorAt(iterationScore * norm[0], gradient);
			saveToColor(rgb, (width * j + i), data);
		}	
	}

	@Override
	public double iterateRE(double currentRE, double currentIM) {
		double angle = atan2(currentRE, currentIM);
		double lnR = log(sqrt(currentIM * currentIM + currentRE * currentRE));
		double radius = exp(lnR * complexPower[0] - complexPower[1] * angle);
		double newAngle = lnR * complexPower[1] + complexPower[0] * angle;
		return cos(newAngle) * radius;
	}

	@Override
	public double iterateIM(double currentRE, double currentIM) {
		double angle = atan2(currentRE, currentIM);
		double lnR = log(sqrt(currentIM * currentIM + currentRE * currentRE));
		double radius = exp(lnR * complexPower[0] - complexPower[1] * angle);
		double newAngle = lnR * complexPower[1] + complexPower[0] * angle;
		return sin(newAngle) * radius;
	}

}
