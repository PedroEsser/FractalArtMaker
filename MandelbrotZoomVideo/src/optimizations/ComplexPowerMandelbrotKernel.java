package optimizations;

import logic.Complex;
import logic.FractalFrame;

public class ComplexPowerMandelbrotKernel extends FractalKernel{
	
	private final double[] complexPower;
	
	public ComplexPowerMandelbrotKernel(FractalFrame f, Complex c) {
		super(f, 0);
		this.complexPower = new double[] {c.getRe(), c.getIm()};
	}
	
	public ComplexPowerMandelbrotKernel(Complex c) {
		super();
		this.complexPower = new double[] {c.getRe(), c.getIm()};
	}
	
	public ComplexPowerMandelbrotKernel(double re, double im) {
		this(new Complex(re, im));
	}
	
	public ComplexPowerMandelbrotKernel(ComplexPowerMandelbrotKernel k, int offset) {
		super(k, offset);
		this.complexPower = k.complexPower;
	}

	@Override
	public void run() {
		
		int width = re.length;
		int index = getGlobalId() + offset[0];
		int i = index % width;
		int j = index / width;
		
		double constantRE = re[i];
		double constantIM = im[j];
		
		double currentRE = 0;
		double currentIM = 0;
		
		int iterations = 0;
		double newRadius = 0;
		
		while(newRadius <= 4 && iterations < maxIterations[0]) {
			if(currentIM != 0 || currentRE != 0){
				double angle = Math.atan2(currentIM, currentRE);
				double lnR = Math.log(Math.sqrt(currentRE * currentRE + currentIM * currentIM));
				newRadius = Math.exp(lnR * complexPower[0] - complexPower[1] * angle);
				double newAngle = lnR * complexPower[1] + complexPower[0] * angle;
				currentRE = Math.cos(newAngle) * newRadius;
				currentIM = Math.sin(newAngle) * newRadius;
			}
			currentRE += constantRE;
			currentIM += constantIM;
			iterations++;
		}
		
		if(iterations == maxIterations[0]) {
			chunkData[getGlobalId()] = maxIterations[0];
		}else {
			if(currentIM != 0 || currentRE != 0){
				double angle = Math.atan2(currentIM, currentRE);
				double lnR = Math.log(Math.sqrt(currentRE * currentRE + currentIM * currentIM));
				newRadius = Math.exp(lnR * complexPower[0] - complexPower[1] * angle);
				double newAngle = lnR * complexPower[1] + complexPower[0] * angle;
				currentRE = Math.cos(newAngle) * newRadius;
				currentIM = Math.sin(newAngle) * newRadius;
			}
			currentRE += constantRE;
			currentIM += constantIM;
			if(currentIM != 0 || currentRE != 0){
				double angle = Math.atan2(currentIM, currentRE);
				double lnR = Math.log(Math.sqrt(currentRE * currentRE + currentIM * currentIM));
				newRadius = Math.exp(lnR * complexPower[0] - complexPower[1] * angle);
				double newAngle = lnR * complexPower[1] + complexPower[0] * angle;
				currentRE = Math.cos(newAngle) * newRadius;
				currentIM = Math.sin(newAngle) * newRadius;
			}
			currentRE += constantRE;
			currentIM += constantIM;
			
			float iterationScore = (float)(iterations + 1 - Math.log(Math.log(Math.sqrt(currentRE * currentRE + currentIM * currentIM))) * LOG2_RECIPROCAL[0]);
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			chunkData[getGlobalId()] = iterationScore;
		}	
	}

	@Override
	public FractalKernel copy(int offset) {
		return new ComplexPowerMandelbrotKernel(this, offset);
	}

}
