package fractalKernels;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import gpuColorGradients.GradientUtils;
import gpuColorGradients.MultiGradient;
import static gpuColorGradients.GradientUtils.*;
import utils.ImageUtils;

public class OrbitTrapKernel extends FractalKernel{
	
	private double lightAngle;
	private double h;
	private double orbitWeight;
	
	private int nBranches, nPoints;
	private double t, dt, amplitude, amplitudeVariance, rotation, radiiSquared, intersectionX, intersectionY;
	
	private double[] points;
	
	public OrbitTrapKernel() {
		super();
		addParameter("angle", 0, 1f/64);
		addParameter("h", 1.5, 1f/64);
		addParameter("Orbit weight", .25, 1f/16);
		addParameter("Number of Branches", 4, 1);
		addParameter("Number of Points", 10, 1);
		
		addParameter("t", 0, 1f/64);
		addParameter("dt", 1f/16, 1f/32);
		addParameter("Amplitude", 0.5, 1f/16);
		addParameter("Amplitude variance", 0.5, 1f/16);
		addParameter("Rotation", 0, 1f/16);
		addParameter("Radii", 0.1, 1f/256);
		addParameter("X intersect", -1, 1f/16);
		addParameter("Y intersect", 1, 1f/16);
	}
	
	private void circularPath() {
		points = new double[nPoints*2];
		for(int i = 0 ; i < nPoints ; i++) {
			double u = looped(t + i*dt);
			double angle = u * 2 * Math.PI;
			points[i*2] = (float)(cos(angle) * (amplitude + sin(angle*amplitudeVariance)*rotation) + intersectionX);
			points[i*2 + 1] = (float)(sin(angle) * (amplitude + sin(angle*amplitudeVariance)*rotation) + intersectionY);
		}
	}
	
	private void crossPath() {
		points = new double[nPoints*2];
		for(int i = 0 ; i < nPoints ; i++) {
			double u = (t + i*dt) * nBranches;
			int b = GradientUtils.floor(u);
			double s = (u - b) * 2;
			s = s < 1 ? s : 2 - s;
			double angle = (float)(2*Math.PI*(b/nBranches + rotation));
			points[i*2] = (float)(cos(angle) * s * amplitude + intersectionX);
			points[i*2 + 1] = (float)(sin(angle) * s * amplitude + intersectionY);
		}
	}
	
	private void infinityLoopPath() {
		points = new double[nPoints*2];
		for(int i = 0 ; i < nPoints ; i++) {
			double time = t + i*dt;
			double s = looped(time) * 2;
			if(s < 1) {
				double angle = 2 * Math.PI * s + Math.PI + rotation;
				points[i*2] = cos(angle) + cos(rotation);
				points[i*2 + 1] = sin(angle) + sin(rotation);
			}else {
				s = 2 - s;
				double angle = 2 * Math.PI * s + rotation;
				points[i*2] = cos(angle) + cos(-rotation);
				points[i*2 + 1] = sin(angle) + sin(-rotation);
			}
			points[i*2] = points[i*2] * amplitude + intersectionX;
			points[i*2+1] = points[i*2+1] * amplitude + intersectionY;
		}
	}
	
	@Override
	protected void loadParameterValues() {
		this.lightAngle = getParameter("angle").getValue();
		this.h = getParameter("h").getValue();
		this.orbitWeight = getParameter("Orbit weight").getValue();
		this.nBranches = getParameter("Number of Branches").getValueAsInt();
		this.nPoints = getParameter("Number of Points").getValueAsInt();
		
		this.t = getParameter("t").getValue();
		this.dt = getParameter("dt").getValue();
		this.amplitude = getParameter("Amplitude").getValue();
		this.amplitudeVariance = getParameter("Amplitude variance").getValue();
		this.rotation = getParameter("Rotation").getValue();
		this.radiiSquared = getParameter("Radii").getValue();
		this.radiiSquared *= this.radiiSquared;
		this.intersectionX = getParameter("X intersect").getValue();
		this.intersectionY = getParameter("Y intersect").getValue();
		//infinityLoopPath();
		circularPath();
		super.loadParameterValues();
	}
	
	
	@Override
	public void run() {		
		int width = this.width;
		
		int iterations = pre_iterations;
		int i = 0;
		
		double orbitX = 1e10;
		double orbitY = 1e10;
		double distance = 1e10;
		double aux = 0;
		double aux2 = 0;
		double zRE = 0;
		double zIM = 0;
		double constantRE = topLeftRE + this.delta * getGlobalId(0);
		double constantIM = topLeftIM + this.delta * getGlobalId(1);
		
		double dCRE = 0;
		double dCIM = 0;
		double uRE = 0;
		double uIM = 0;
		
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
			
			aux = distance;
			for(i = 0 ; i < nPoints ; i++) {
				aux2 = (zRE - points[i*2])*(zRE - points[i*2]) + (zIM - points[i*2+1])*(zIM - points[i*2+1]);
				aux2 = radiiSquared > aux2 ? 0 : aux2 - radiiSquared;
				aux = min(aux, aux2);
			}
			if(distance > aux) {
				distance = aux;
				orbitX = zRE;
				orbitY = zIM;
			}
			
			iterations+=1;
		}
		i = (width * getGlobalId(1) + getGlobalId(0)) * 3;
		if(iterations < maxIterations) {
			float iterationScore = (float)(iterations + 1 - log(log(sqrt(zRE * zRE + zIM * zIM)))/log(2));
			iterationScore =  iterationScore < 0 ? 0 : iterationScore * norm;
			
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
			
			float orbitScore = (float)(distance == 0 ? orbitWeight : 0);
			int rgb = MultiGradient.colorAtPercent(looped(orbitScore + iterationScore), gradient);
			
			data[i + 0] = (byte)((rgb >> 0 & 0xFF) * aux);
			data[i + 1] = (byte)((rgb >> 8 & 0xFF) * aux);
			data[i + 2] = (byte)((rgb >> 16 & 0xFF) * aux);
		}else {
			data[i + 0] = 0;
			data[i + 1] = 0;
			data[i + 2] = 0;
		}
	}
	
	public double iterateRE(double zRE, double zIM, double constantRE, double constantIM) {
		return zRE * zRE - zIM * zIM + constantRE;
	}
	
	public double iterateIM(double zRE, double zIM, double constantRE, double constantIM) {
		return 2 * zRE * zIM + constantIM;
	}
	
	@Override
	public String getName() {
		return "Orbit Trap Mandelbrot";
	}
	
}
