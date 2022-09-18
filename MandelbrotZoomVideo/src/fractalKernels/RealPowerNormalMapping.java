package fractalKernels;

import gpuColorGradients.MultiGradient;

public class RealPowerNormalMapping extends RealPowerMandelbrotKernel{

	private double lightAngle;
	private double h;
	private double[] z = new double[] {0, 0};
	
	public RealPowerNormalMapping() {
		super();
		addParameter("angle", 0, 0.0625);
		addParameter("h", 1.5, 0.0625);
	}
	
	@Override
	protected void loadParameterValues() {
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
		
		double constantRE = topLeftRE + this.delta * i;
		double constantIM = topLeftIM + this.delta * j;
		
		z[0] = 1e-150;
		z[1] = 1e-150;
		
		double aux = 0;
		double dCRE = 0;
		double dCIM = 0;
		double reflection = 0;
		double uRE = 0;
		double uIM = 0;
		double angle = 0;
		double radius = 0;
		
		if(iterations > 1) {
			for(int a = 0 ; a < iterations ; a++) {
				angle = Math.atan2(z[1], z[0]) * power;
				radius = Math.pow(z[0] * z[0] + z[1] * z[1], power/2);
				z[0] = Math.cos(angle) * radius;
				z[1] = Math.sin(angle) * radius;
				z[0] += constantRE;
				z[1] += constantIM;
			}
			constantRE = z[0];
			constantIM = z[1];
		}
			
		z[0] = 1e-150;
		z[1] = 1e-150;
		iterations = 1;
		
		while(z[0] * z[0] + z[1] * z[1] < escapeRadius && iterations < maxIterations) {
			uRE = z[0];
			uIM = z[1];
			if(iterations % 2 == 1) {
				aux = dCRE;
				dCRE = 2 * (dCRE * z[0] - dCIM * z[1]) + 1;
				dCIM = 2 * (aux * z[1] + dCIM * z[0]);		//dC = 2 * dC * Z + 1
				
				z[0] = z[0] * z[0] - z[1] * z[1];
				z[1] = 2 * uRE * z[1];
			}else {
				angle = Math.atan2(z[1], z[0]) * (power-1);
				radius = Math.pow(z[0] * z[0] + z[1] * z[1], (power-1)/2);
				z[0] = Math.cos(angle) * radius;
				z[1] = Math.sin(angle) * radius;
				aux = dCRE;
				dCRE = power * (dCRE * z[0] - dCIM * z[1]) + 1;
				dCIM = power * (aux * z[1] + dCIM * z[0]);		//dC = power * dC * Z^(power - 1) + 1
				
				aux = z[0];
				z[0] = uRE * z[0] - uIM * z[1];
				z[1] = uRE * z[1] + uIM * aux;
			}
			
			z[0] += constantRE;
			z[1] += constantIM;
			
			iterations+=1;
		}
		int rgb = 0;
		if(iterations < maxIterations) {
			float iterationScore = (float)(iterations + 1 - log(log(sqrt(z[0] * z[0] + z[1] * z[1])))/log(power));
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			rgb = MultiGradient.colorAtPercent(iterationScore * norm, gradient);
			aux = dCRE*dCRE + dCIM*dCIM;
			uRE = (z[0] * dCRE + z[1] * dCIM)/aux;
			uIM = (-z[0] * dCIM + z[1] * dCRE)/aux;			//u = Z / dC
			
			aux = Math.sqrt(uRE*uRE + uIM*uIM);
			uRE /= aux;
			uIM /= aux;									//u = u/abs(u)
			
			aux = 2 * Math.PI * lightAngle;
			z[0] = cos(aux);
			z[1] = sin(aux);
			reflection = uRE * z[0] + uIM * z[1] + h;	//reflection = dot(u, v) + h2
			reflection /= (1 + h);
			
			if(reflection < 0)
				reflection = 0;
		}


		int r = (int)(reflection * (rgb >> 0 & 0xFF));
		int g = (int)(reflection * (rgb >> 8 & 0xFF));
		int b = (int)(reflection * (rgb >> 16 & 0xFF));
		
		i = (width * j + i) * 3;
		data[i + 0] = (byte)r;
		data[i + 1] = (byte)g;
		data[i + 2] = (byte)b;
	}
	
	public static void realPower(double[] z, double power) {
		double angle = Math.atan2(z[1], z[0]) * power;
		double radius = Math.pow(z[0] * z[0] + z[1] * z[1], power/2);
		z[0] = Math.cos(angle) * radius;
		z[1] = Math.sin(angle) * radius;
	}
	
}
