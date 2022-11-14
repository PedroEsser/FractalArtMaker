package fractalKernels;

import fractals_deprecated.Complex;
import gpuColorGradients.MultiGradient;

public class ComplexPowerMandelbrotKernel extends FractalKernel{
	
	private double powerRE = 0;
	private double powerIM = 0;
	
	private double lightAngle;
	private double h;
	
	public ComplexPowerMandelbrotKernel(Complex c) {
		super();
		addParameter("Re(power)", c.getRe(), 1d/256);
		addParameter("Im(power)", c.getIm(), 1d/1024);
		addParameter("angle", 0.5, 1f/64);
		addParameter("h", 1.5, 1f/64);
	}
	
	public ComplexPowerMandelbrotKernel(double re, double im) {
		this(new Complex(re, im));
	}
	
	public ComplexPowerMandelbrotKernel() {
		this(2, 0);
	}
	
	@Override
	protected void loadParameterValues() {
		this.powerRE = getParameter("Re(power)").getValue();
		this.powerIM = getParameter("Im(power)").getValue();
		this.lightAngle = getParameter("angle").getValue();
		this.h = getParameter("h").getValue();
		super.loadParameterValues();
	}

	@Override
	public void run() {
		int width = this.width;
		
		int i = getGlobalId(0);
		int j = getGlobalId(1);
		
		int iterations = pre_iterations;
		
		double zRE = 1E-100;
		double zIM = 1E-100;
		double constantRE = topLeftRE + this.delta * i;
		double constantIM = topLeftIM + this.delta * j;
		
		double angle = 0;
		double lnR = 0;
		double radius = 0;
		double newAngle = 0;
		
		double dCRE = 0;
		double dCIM = 0;
		double uRE = 0;
		double uIM = 0;
		
		angle = atan2(constantIM, constantRE) * morphPower;
		uRE = pow(constantRE * constantRE + constantIM * constantIM, morphPower/2);
		constantRE = cos(angle) * uRE;
		constantIM = sin(angle) * uRE;
		
		if(iterations > 1) {
			for(int a = 0 ; a < iterations ; a++) {
				angle = atan2(zIM, zRE);
				lnR = log(sqrt(zRE * zRE + zIM * zIM));
				radius = exp(lnR * powerRE - powerIM * angle);
				newAngle = lnR * powerIM + powerRE * angle;
				zRE = cos(newAngle) * radius + constantRE;
				zIM = sin(newAngle) * radius + constantIM;
			}
			constantRE = zRE;
			constantIM = zIM;
		}
		
		iterations = 0;
		zRE = 1E-100;
		zIM = 1E-100;
		
		while(zRE*zRE + zIM*zIM <= escapeRadius && iterations < maxIterations) {
			angle = atan2(zIM, zRE);
			lnR = log(zRE * zRE + zIM * zIM)/2;
			radius = exp(lnR * (powerRE-1) - powerIM * angle);
			newAngle = lnR * powerIM + (powerRE-1) * angle;
			uRE = cos(newAngle) * radius;						// u = Z^(power-1)
			uIM = sin(newAngle) * radius;
			newAngle = uRE;
			uRE = dCRE * uRE - dCIM * uIM;						// u = dC * Z^(power-1)
			uIM = newAngle * dCIM + uIM * dCRE;
			dCRE = uRE * powerRE - uIM * powerIM + 1;			// dC = power * Z^(power-1) * dC + 1
			dCIM = uRE * powerIM + uIM * powerRE;
			
			radius = exp(lnR * powerRE - powerIM * angle);
			newAngle = lnR * powerIM + powerRE * angle;
			zRE = cos(newAngle) * radius + constantRE;
			zIM = sin(newAngle) * radius + constantIM;
			
			iterations+=1;
		}
		
		int rgb = 0;
		if(iterations < maxIterations) {
			float iterationScore = (float)(iterations + 1 - log(log(zRE * zRE + zIM * zIM)/2)/log(2));
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			rgb = MultiGradient.colorAtPercent(iterationScore * norm, gradient);
			
			angle = dCRE*dCRE + dCIM*dCIM;
			uRE = (zRE * dCRE + zIM * dCIM)/angle;
			uIM = (-zRE * dCIM + zIM * dCRE)/angle;			//u = Z / dC
			
			angle = Math.sqrt(uRE*uRE + uIM*uIM);
			uRE /= angle;
			uIM /= angle;									//u = u/abs(u)
			
			angle = 2 * Math.PI * lightAngle;
			zRE = cos(angle);
			zIM = sin(angle);
			angle = uRE * zRE + uIM * zIM + h;	//reflection = dot(u, v) + h2
			angle /= (1 + h);
			
			if(angle < 0)
				angle = 0;
		}
		//angle = 1;
		int r = (int)(angle * (rgb >> 0 & 0xFF));
		int g = (int)(angle * (rgb >> 8 & 0xFF));
		int b = (int)(angle * (rgb >> 16 & 0xFF));
		
		i = (width * j + i) * 3;
		data[i + 0] = (byte)r;
		data[i + 1] = (byte)g;
		data[i + 2] = (byte)b;
	}

	@Override
	public double iterateRE(double zRE, double zIM, double constantRE, double constantIM) {
		double angle = atan2(zRE, zIM);
		double lnR = log(sqrt(zIM * zIM + zRE * zRE));
		double radius = exp(lnR * powerRE - powerIM * angle);
		double newAngle = lnR * powerIM + powerRE * angle;
		return cos(newAngle) * radius + constantRE;
	}

	@Override
	public double iterateIM(double zRE, double zIM, double constantRE, double constantIM) {
		double angle = atan2(zRE, zIM);
		double lnR = log(sqrt(zIM * zIM + zRE * zRE));
		double radius = exp(lnR * powerRE - powerIM * angle);
		double newAngle = lnR * powerIM + powerRE * angle;
		return sin(newAngle) * radius + constantIM;
	}
	
	@Override
	public String getName() {
		return "Complex Power Mandelbrot";
	}

}
