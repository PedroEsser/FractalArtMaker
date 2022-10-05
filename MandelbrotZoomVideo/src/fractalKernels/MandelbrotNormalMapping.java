package fractalKernels;

import gpuColorGradients.MultiGradient;

public class MandelbrotNormalMapping extends MandelbrotKernel{

	private double angle;
	private double h;
	private double morphPower;
	
	public MandelbrotNormalMapping() {
		super();
		addParameter("angle", 0.5, 1f/64);
		addParameter("h", 1.5, 1f/64);
		addParameter("C power morph", 1, 1f/64);
	}
	
	@Override
	protected void loadParameterValues() {
		this.angle = getParameter("angle").getValue();
		this.h = getParameter("h").getValue();
		this.morphPower = getParameter("C power morph").getValue();
		super.loadParameterValues();
	}
	
	//	https://www.math.univ-toulouse.fr/~cheritat/wiki-draw/index.php/Mandelbrot_set#Variation:_.28partial.29_antialias_effect_without_oversampling
	
	@Override
	public void run() {		
		int width = this.width;
		
		int i = getGlobalId(0);
		int j = getGlobalId(1);
		
		int iterations = pre_iterations;
		
		double constantRE = topLeftRE + this.delta * i;
		double constantIM = topLeftIM + this.delta * j;
		
		double aux = 0;
		double zRE = 0;
		double zIM = 0;
		
		double dCRE = 0;
		double dCIM = 0;
		double uRE = 0;
		double uIM = 0;
		
		aux = atan2(constantIM, constantRE) * morphPower;
		uRE = pow(constantRE * constantRE + constantIM * constantIM, morphPower/2);
		constantRE = cos(aux) * uRE;
		constantIM = sin(aux) * uRE;
		// C = C^morphPower
		
		for(int a = 0 ; a < iterations ; a++) {
			aux = zRE;
			zRE = iterateRE(zRE, zIM, constantRE, constantIM);
			zIM = iterateIM(aux, zIM, constantRE, constantIM);
		}
			
		constantRE = zRE;
		constantIM = zIM;
		
		zRE = 0;
		zIM = 0;
		iterations = 0;
		
		while(zRE * zRE + zIM * zIM <= escapeRadius && iterations < maxIterations) {
			aux = dCRE;
			dCRE = 2 * (dCRE * zRE - dCIM * zIM) + 1;
			dCIM = 2 * (aux * zIM + dCIM * zRE);		//dC = 2 * dC * Z + 1
			
			aux = zRE;
			zRE = iterateRE(zRE, zIM, constantRE, constantIM);
			zIM = iterateIM(aux, zIM, constantRE, constantIM);
			
			iterations+=1;
		}
		int rgb = 0;
		if(iterations < maxIterations) {
			float iterationScore = (float)(iterations + 1 - log(log(sqrt(zRE * zRE + zIM * zIM)))/log(2));
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			rgb = MultiGradient.colorAtPercent(iterationScore * norm, gradient);
			
			aux = dCRE*dCRE + dCIM*dCIM;
			uRE = (zRE * dCRE + zIM * dCIM)/aux;
			uIM = (-zRE * dCIM + zIM * dCRE)/aux;			//u = Z / dC
			
			aux = Math.sqrt(uRE*uRE + uIM*uIM);
			uRE /= aux;
			uIM /= aux;									//u = u/abs(u)
			
			aux = 2 * Math.PI * angle;
			zRE = cos(aux);
			zIM = sin(aux);
			aux = uRE * zRE + uIM * zIM + h;	//reflection = dot(u, v) + h2
			aux /= (1 + h);
			
			if(aux < 0)
				aux = 0;
			
		}
		int r = (int)(aux * (rgb >> 0 & 0xFF));
		int g = (int)(aux * (rgb >> 8 & 0xFF));
		int b = (int)(aux * (rgb >> 16 & 0xFF));
		
		i = (width * j + i) * 3;
		data[i + 0] = (byte)r;
		data[i + 1] = (byte)g;
		data[i + 2] = (byte)b;
	}
	
}
