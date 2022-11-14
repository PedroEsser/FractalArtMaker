package fractalKernels;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gpuColorGradients.GradientUtils;
import gpuColorGradients.MultiGradient;
import static gpuColorGradients.GradientUtils.*;
import utils.ImageUtils;

public class CircleOrbitTrapKernel extends FractalKernel{
	
	private double lightAngle;
	private double h;
	private double orbitWeight;
	
	private int nBranches, nPoints, nAnimations;
	private double t, dt, amplitude, amplitudeVariance, amplitudeVarianceFrequency, rotation, radiiSquared, intersectionX, intersectionY, d;
	
	private List<Double> points;
	private double[] pointsArray;
	
	public CircleOrbitTrapKernel() {
		super();
		addParameter("angle", 0, 1f/64);
		addParameter("h", 1.5, 1f/64);
		addParameter("Orbit weight", .02, 1f/64);
		addParameter("Number of Branches", 4, 1);
		addParameter("Number of Points", 30, 1);
		
		addParameter("t", 0, 1f/64);
		addParameter("dt", 0.015, 1f/256);
		addParameter("Amplitude", 0.25, 1f/64);
		addParameter("Amplitude variance", 0.1, 1f/64);
		addParameter("Amplitude variance frequency", 5, 1f/64);
		addParameter("Rotation", 0, 1f/16);
		addParameter("Radii", 0.06, 1f/256);
		addParameter("X intersect", -1, 1f/16);
		addParameter("Y intersect", 1, 1f/16);
		addParameter("d", .125, 1f/64);
	}
	
	private void circularPath() {
		nAnimations++;
		for(int i = 0 ; i < nPoints ; i++) {
			double u = loopedDouble(t + i*dt);
			double angle = u * 2 * Math.PI;
			points.add(cos(angle) * (amplitude + sin(angle*amplitudeVarianceFrequency)*amplitudeVariance) + intersectionX);
			points.add(sin(angle) * (amplitude + sin(angle*amplitudeVarianceFrequency)*amplitudeVariance) + intersectionY);
		}
	}
	
	private void branchPath() {
		nAnimations++;
		for(int i = 0 ; i < nPoints ; i++) {
			double u = (t + i*dt) * nBranches;
			double b = GradientUtils.floorDouble(u);
			double s = (u - b) * 2;
			s = s < 1 ? s : 2 - s;
			double angle = (float)(2*Math.PI*(b/nBranches + rotation));
			points.add(cos(angle) * s * amplitude + intersectionX + cos(u*2*Math.PI)*amplitude/3);
			points.add(sin(angle) * s * amplitude + intersectionY + sin(u*2*Math.PI)*amplitude/3);
		}
	}
	
	private void infinityLoopPath() {
		nAnimations++;
		for(int i = 0 ; i < nPoints ; i++) {
			double time = t + i*dt;
			double s = loopedDouble(time) * 2;
			double re = 0;
			double im = 0;
			if(s < 1) {
				double angle = 2 * Math.PI * s + Math.PI;
				re = 1 + cos(angle);
				im = sin(angle);
			}else {
				s -= 1;
				double angle = -2 * Math.PI * s;
				re = -1 + cos(angle);
				im = sin(angle);
			}
			s = re;
			re = re*cos(rotation*2*Math.PI) + im*sin(rotation*2*Math.PI) + cos(time*2*Math.PI)*amplitude/8;
			im = s*sin(rotation*2*Math.PI) + im*cos(rotation*2*Math.PI) + sin(time*2*Math.PI)*amplitude/8;
			points.add(re * amplitude + intersectionX);
			points.add(im * amplitude + intersectionY);
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
		this.amplitudeVarianceFrequency = getParameter("Amplitude variance frequency").getValue();
		this.rotation = getParameter("Rotation").getValue();
		this.radiiSquared = getParameter("Radii").getValue();
		this.radiiSquared *= this.radiiSquared;
		this.intersectionX = getParameter("X intersect").getValue();
		this.intersectionY = getParameter("Y intersect").getValue();
		this.d = getParameter("d").getValue();
		nAnimations = 0;
		points = new ArrayList<Double>();
		//branchPath();
		//this.intersectionX += d;
		infinityLoopPath();
		/*this.intersectionX += cos(2*Math.PI/3)*d - d;
		this.intersectionY += sin(2*Math.PI/3)*d;
		circularPath();
		this.intersectionX += cos(4*Math.PI/3)*d - cos(2*Math.PI/3)*d;
		this.intersectionY += sin(4*Math.PI/3)*d - sin(2*Math.PI/3)*d;
		t *= -1;
		circularPath();*/
		pointsArray = new double[points.size()];
		for(int i = 0 ; i < pointsArray.length ; i++)
			pointsArray[i] = points.get(i);
		super.loadParameterValues();
	}
	
	
	@Override
	public void run() {		
		int width = this.width;
		
		int iterations = pre_iterations;
		int i = 0;
		
//		double orbitX = 1e10;
//		double orbitY = 1e10;
		double traps = 0;
		double aux = 0;
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
			
			for(int j = 0 ; j < nAnimations ; j++)
				for(i = j * nPoints ; i < (j+1) * nPoints ; i++) {
					aux = (zRE - pointsArray[i*2])*(zRE - pointsArray[i*2]) + (zIM - pointsArray[i*2+1])*(zIM - pointsArray[i*2+1]);
					if(radiiSquared > aux) {
						traps++;
						i = (j+1) * nPoints;
					}
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
			
			float orbitScore = (float)(traps * orbitWeight);
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
