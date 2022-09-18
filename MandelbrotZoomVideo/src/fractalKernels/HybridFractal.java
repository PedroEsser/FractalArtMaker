package fractalKernels;

import gpuColorGradients.MultiGradient;

public class HybridFractal extends FractalKernel{

	private double lightAngle;
	private double h;
	private int modOffset = 0;
	private double morphPower;
	
	public HybridFractal() {
		super();
		addParameter("angle", 0, 0.0625);
		addParameter("h", 1.5, 0.0625);
		addParameter("mod offset", 0, 1);
		addParameter("C morph power", 1, 1d/16);
		
	}
	
	@Override
	protected void loadParameterValues() {
		this.lightAngle = getParameter("angle").getValue();
		this.h = getParameter("h").getValue();
		this.modOffset = getParameter("mod offset").getValueAsInt();
		this.morphPower = getParameter("C morph power").getValue();
		super.loadParameterValues();
	}
	
	@Override
	public String getName() {
		return "Hybrid Fractal";
	}

	@Override
	public double iterateRE(double currentRE, double currentIM, double constantRE, double constantIM) {
		return 0;
	}

	@Override
	public double iterateIM(double currentRE, double currentIM, double constantRE, double constantIM) {
		return 0;
	}

	@Override
	public void run() {
		int iterations = pre_iterations;
		
		double constantRE = topLeftRE + this.delta * getGlobalId(0);
		double constantIM = topLeftIM + this.delta * getGlobalId(1);
		
		double aux = 0;
		double dCRE = 0;
		double dCIM = 0;
		double reflection = 0;
		double uRE = 0;
		double uIM = 0;
			
		double zRE = 0;
		double zIM = 0;
		
		aux = atan2(constantIM, constantRE) * morphPower;
		uRE = pow(constantRE * constantRE + constantIM * constantIM, morphPower/2);
		constantRE = cos(aux) * uRE;
		constantIM = sin(aux) * uRE;
		
		if(iterations > 1) {
			for(int a = 0 ; a < iterations ; a++) {
				aux = zRE;
				zRE = zRE * zRE - zIM * zIM + constantRE;
				zIM = 2 * aux * zIM + constantIM;
			}
			constantRE = zRE;
			constantIM = zIM;
		}
		
		
		iterations = 0;
		zRE = 0;
		zIM = 0;
		int i = 0;
		int m = 0;
		
		while(zRE * zRE + zIM * zIM < escapeRadius && iterations < maxIterations) {
			uRE = zRE;
			uIM = zIM;
			//	U = Zo
			aux = zRE;
			zRE = zRE * zRE - zIM * zIM;
			zIM = 2 * aux * zIM;
			//	Z = Zo^2
			m = (iterations + modOffset) % mod;
			for(i = 0 ; i < m ; i++) {
				aux = uRE;
				uRE = zRE * uRE - zIM * uIM;
				uIM = aux * zIM + uIM * zRE;
				
				aux = zRE;
				zRE = zRE * zRE - zIM * zIM;
				zIM = 2 * aux * zIM;
			}
			i = (1 << (m + 1));//	i = 2^(m+1)
			// 	U = Zo^(i - 1)
			//	Z = Zo^(i)
			
			aux = dCRE;
			dCRE = i * (dCRE * uRE - dCIM * uIM) + 1;
			dCIM = i * (aux * uIM + dCIM * uRE);		//dC = i * dC * Zo^(i - 1) + 1
			
			zRE += constantRE;
			zIM += constantIM;
			
			iterations+=1;
		}
		int rgb = 0;
		if(iterations < maxIterations) {
			float iterationScore = (float)(iterations + 1 - log(log(zRE * zRE + zIM * zIM)/(log(2)*2))/log(1 << mod));
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
		
		i = (width * getGlobalId(1) + getGlobalId(0)) * 3;
		data[i + 0] = (byte)r;
		data[i + 1] = (byte)g;
		data[i + 2] = (byte)b;
	}

}
