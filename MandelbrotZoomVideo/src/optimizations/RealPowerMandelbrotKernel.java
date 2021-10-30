package optimizations;

public class RealPowerMandelbrotKernel extends FractalKernel{

	private final double[] realPower;
	
	public RealPowerMandelbrotKernel(double power) {
		super();
		this.realPower = new double[] {power};
	}

	@Override
	public void run() {
		int width = getGlobalSize(0);
		int i = getGlobalId(0);
		int j = getGlobalId(1);
		int index = j * width + i;
		
		double constantRE = re[i];
		double constantIM = im[j];
		
		double currentRE = 0;
		double currentIM = 0;
		
		int iterations = 0;
		double radius = 0;
		
		while(radius <= 4 && iterations < maxIterations[0]) {
			
			double angle = Math.atan2(currentIM, currentRE) * realPower[0];
			radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), realPower[0]);
			currentRE = Math.cos(angle) * radius + constantRE;
			currentIM = Math.sin(angle) * radius + constantIM;
			iterations++;
		}
		
		if(iterations == maxIterations[0]) {
			chunkData[getGlobalId()] = maxIterations[0];
		}else {
			
			double angle = Math.atan2(currentIM, currentRE) * realPower[0];
			radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), realPower[0]);
			currentRE = Math.cos(angle) * radius + constantRE;
			currentIM = Math.sin(angle) * radius + constantIM;
			angle = Math.atan2(currentIM, currentRE) * realPower[0];
			radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), realPower[0]);
			currentRE = Math.cos(angle) * radius + constantRE;
			currentIM = Math.sin(angle) * radius + constantIM;
			
			float iterationScore = (float)(iterations + 1 - Math.log(Math.log(Math.sqrt(currentRE * currentRE + currentIM * currentIM))) * LOG2_RECIPROCAL[0]);
			iterationScore =  iterationScore < 0 ? 0 : iterationScore;
			chunkData[getGlobalId()] = iterationScore;
		}
	}

	@Override
	public FractalKernel copy(int offset) {
		// TODO Auto-generated method stub
		return null;
	}

}
