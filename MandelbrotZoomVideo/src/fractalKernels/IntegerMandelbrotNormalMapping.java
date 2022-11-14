package fractalKernels;

import gpuColorGradients.MultiGradient;

public class IntegerMandelbrotNormalMapping extends IntegerPowerMandelbrotKernel{

	private double lightAngle;
	private double h;
	
	public IntegerMandelbrotNormalMapping() {
		super();
		addParameter("lightAngle", 0, 0.0625);
		addParameter("h", 1.5, 0.0625);
	}
	
	@Override
	protected void loadParameterValues() {
		this.lightAngle = getParameter("lightAngle").getValue();
		this.h = getParameter("h").getValue();
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
		
		double re = 0;
		double im = 0;
		double zRE = 0;
		double zIM = 0;
		
		double dCRE = 0;
		double dCIM = 0;
		double reflection = 0;
		double uRE = 0;
		double uIM = 0;
		
		for(int a = 0 ; a < iterations ; a++) {
			re = 1;
			im = 0;
			aux = 0;
			for(int power2 = 1 ; power2 <= power ; power2 <<= 1) {
				if((power2 & power) != 0) {
					aux = re;
					re = re * zRE - im * zIM;
					im = aux * zIM + im * zRE;
				}
				aux = zRE;
				zRE = zRE * zRE - zIM * zIM;
				zIM = 2 * aux * zIM;
			}
			zRE = re + constantRE;
			zIM = im + constantIM;
		}
			
		constantRE = zRE;
		constantIM = zIM;
		
		zRE = 0;
		zIM = 0;
		
		iterations = 0;
		int p = 0;
		
		while(zRE * zRE + zIM * zIM <= escapeRadius && iterations < maxIterations) {
			uRE = zRE;
			uIM = zIM;
			
			re = 1;
			im = 0;
			p = power;
			for(int power2 = 1 ; power2 <= p-1 ; power2 <<= 1) {
				if((power2 & (p-1)) != 0) {
					aux = re;
					re = re * zRE - im * zIM;
					im = aux * zIM + im * zRE;
				}
				aux = zRE;
				zRE = zRE * zRE - zIM * zIM;
				zIM = 2 * aux * zIM;
			}
			zRE = re;
			zIM = im;
			
			aux = dCRE;
			dCRE = p * (dCRE * zRE - dCIM * zIM) + 1;
			dCIM = p * (aux * zIM + dCIM * zRE);		//dC = power * dC * Z^(power - 1) + 1
			
			aux = zRE;
			zRE = uRE * zRE - uIM * zIM;
			zIM = uRE * zIM + uIM * aux;
			zRE += constantRE;
			zIM += constantIM;
			
			iterations+=1;
		}
		int rgb = 0;
		if(iterations < maxIterations) {
			float iterationScore = (float)(iterations + 1 - log(log(zRE * zRE + zIM * zIM)/(log(2) * 2))/log(power));	// log magic makes gradient smooth :)
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			rgb = MultiGradient.colorAtPercent(iterationScore * norm, gradient);
			
			aux = dCRE*dCRE + dCIM*dCIM;
			uRE = (zRE * dCRE + zIM * dCIM)/aux;
			uIM = (-zRE * dCIM + zIM * dCRE)/aux;			//u = Z / dC
			
			aux = Math.sqrt(uRE*uRE + uIM*uIM);
			uRE /= aux;
			uIM /= aux;									//u = u/abs(u)
			
			aux = 2 * Math.PI * lightAngle;
			zRE = cos(aux);
			zIM = sin(aux);
			reflection = uRE * zRE + uIM * zIM + h;	//reflection = dot(u, v) + h2
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
	
	public static void integerPower(double zRE, double zIM, int power) {
		double re = 1;
		double im = 0;
		double aux = 0;
		for(int power2 = 1 ; power2 <= power ; power2 <<= 1) {
			if((power2 & power) != 0) {
				aux = re;
				re = re * zRE - im * zIM;
				im = aux * zIM + im * zRE;
			}
			aux = zRE;
			zRE = zRE * zRE - zIM * zIM;
			zIM = 2 * aux * zIM;
		}
		zRE = re;
		zIM = im;
	}
	
}
