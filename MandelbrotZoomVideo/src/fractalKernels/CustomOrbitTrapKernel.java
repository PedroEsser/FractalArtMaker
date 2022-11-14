package fractalKernels;

import gpuColorGradients.GradientUtils;
import gpuColorGradients.MultiGradient;

public class CustomOrbitTrapKernel extends MandelbrotKernel{

	private double lightAngle, h;
	private double x, y, angle, height;
	private double frequency, phaseOffset, amplitude, spacing;
	private int nLines = 4;
	
	public CustomOrbitTrapKernel() {
		super();
		addParameter("light angle", 0, 1f/64);
		addParameter("h", 1.5, 1f/64);
		addParameter("x", 0, 1f/64);
		addParameter("y", 0, 1f/32);
		addParameter("angle", 0, 1f/64);
		addParameter("height", 1, 1f/64);
		addParameter("frequency", 1, 1f/64);
		addParameter("phaseOffset", 0, 1f/32);
		addParameter("amplitude", 2, 1f/64);
		addParameter("spacing", 2, 1f/64);
		addParameter("nLines", 4, 1);
	}
	
	@Override
	protected void loadParameterValues() {
		this.lightAngle = getParameter("light angle").getValue();
		this.h = getParameter("h").getValue();
		this.x = getParameter("x").getValue();
		this.y = getParameter("y").getValue();
		this.angle = 2*Math.PI*getParameter("angle").getValue();
		this.height = getParameter("height").getValue();
		this.frequency = getParameter("frequency").getValue();
		this.phaseOffset = getParameter("phaseOffset").getValue();
		this.amplitude = getParameter("amplitude").getValue();
		this.spacing = getParameter("spacing").getValue();
		this.nLines = getParameter("nLines").getValueAsInt();
		super.loadParameterValues();
	}
	
	public int lineTrap(double re, double im) {
		im = abs(im);
		double dist = cos(angle)*(y - im) - sin(angle)*(x - re);
		if(dist > height || dist < 0)
			return -1;
		return MultiGradient.colorAtPercent((float)(dist/height), gradient);
	}
	
	public int linesTrap(double re, double im, double offset) {
		//nLines;
		re -= x;
		im -= y;
		double a = atan2(im, re) - angle;
		a = GradientUtils.genericMod(a, 2*Math.PI/nLines);
		a = sin(a) * sqrt(re*re + im*im) + height/2;
		if(abs(a) < height/2)
			return MultiGradient.colorAtPercent((float)(a/height + 1f/2), gradient);
		return -1;
	}
	
	public int sineLineTrap(double re, double im) {
		double sine = sin(re*frequency + phaseOffset) * amplitude + y;
		double dist = sine - abs(im) + height/2;
		if(dist > height || dist < 0)
			return -1;
		return MultiGradient.colorAtPercent((float)(dist/height), gradient);
	}
	
	public int ringTrap(double re, double im, int i) {
		double xDiff = GradientUtils.genericMod(re - x, spacing) - spacing/2;
		double yDiff = GradientUtils.genericMod(im - y, spacing) - spacing/2;
		double dist = xDiff*xDiff + yDiff*yDiff - amplitude;
		if(abs(dist) > height)
			return -1;
		return MultiGradient.colorAtPercent((float)(dist/height/2 + (float)i/frequency + 1f/2), gradient);
	}
	
	public int squareRingTrap(double re, double im, int i) {
		double xDiff = re - x;
		double yDiff = im - y;
		double dist = xDiff;
		xDiff = cos(angle)*xDiff - sin(angle)*yDiff;
		yDiff = cos(angle)*yDiff + sin(angle)*dist;
		dist = abs(xDiff) + abs(yDiff) - amplitude;
		if(abs(dist) > height)
			return -1;
		return MultiGradient.colorAtPercent((float)(dist/height/2 + (float)i/frequency + 1f/2), gradient);
	}
	
	@Override
	public void run() {		
		int width = this.width;
		
		int iterations = pre_iterations;
		int i = 0;
		int rgb = -1;
		
		double zRE = 0;
		double zIM = 0;
		double constantRE = this.delta * (getGlobalId(0) - width/2);
		double constantIM = this.delta * (getGlobalId(1) - height/2);
		double aux = constantRE;
		constantRE = cos(angle)*aux - sin(angle)*constantIM + centerRE;
		constantIM = cos(angle)*constantIM + sin(angle)*aux + centerIM;
		
		double dCRE = 0;
		double dCIM = 0;
		double uRE = 0;
		double uIM = 0;
		
		aux = atan2(constantIM, constantRE) * morphPower;
		uRE = pow(constantRE * constantRE + constantIM * constantIM, morphPower/2);
		constantRE = cos(aux) * uRE;
		constantIM = sin(aux) * uRE;
		
		for(int a = 0 ; a < iterations && zRE * zRE + zIM * zIM <= escapeRadius ; a++) {
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
			
			i = linesTrap(zRE, zIM, (double)i/maxIterations);
			if(i != -1)
				rgb = i;
			
			iterations+=1;
		}
		i = (width * getGlobalId(1) + getGlobalId(0)) * 3;
		if(iterations < maxIterations) {
			
			aux = dCRE*dCRE + dCIM*dCIM;
			uRE = (zRE * dCRE + zIM * dCIM)/aux;
			uIM = (-zRE * dCIM + zIM * dCRE)/aux;			//u = Z / dC
			
			aux = Math.sqrt(uRE*uRE + uIM*uIM);
			uRE /= aux;
			uIM /= aux;									//u = u/abs(u)
			
			aux = 2 * Math.PI * lightAngle;
			zRE = cos(aux);
			zIM = sin(aux);
			aux = uRE * zRE + uIM * zIM + h;	//reflection = dot(u, v) + h2
			aux /= (1 + h);
			
			if(aux < 0)
				aux = 0;
			
		}else
			rgb = 0;
		
		data[i + 0] = (byte)((rgb >> 0 & 0xFF) * aux);
		data[i + 1] = (byte)((rgb >> 8 & 0xFF) * aux);
		data[i + 2] = (byte)((rgb >> 16 & 0xFF) * aux);
	}

	
	@Override
	public String getName() {
		return "Custom Orbit Trap Mandelbrot";
	}
	
}
