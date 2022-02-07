package optimizations;

import gpuColorGradients.ColorGradient;
import gpuColorGradients.MultiGradient;

public class RealPowerMandelbrotKernel extends FractalKernel{

	private final double[] realPower;
	
	public RealPowerMandelbrotKernel(double power) {
		super();
		this.realPower = new double[] {power};
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
		double radius = 0;
		
		while(radius <= 4 && iterations < maxIterations[0]) {
			double angle = Math.atan2(currentIM, currentRE) * realPower[0];
			radius = Math.pow(Math.sqrt(currentRE * currentRE + currentIM * currentIM), realPower[0]);
			currentRE = Math.cos(angle) * radius + constantRE;
			currentIM = Math.sin(angle) * radius + constantIM;
			iterations++;
		}
		
		if(iterations == maxIterations[0]) {
			int in = getGlobalId() * 3;
			chunkData[in + 0] = 0;
			chunkData[in + 1] = 0;
			chunkData[in + 2] = 0;
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
			int rgb = MultiGradient.colorAt(iterationScore * norm[0], gradient);
			int in = getGlobalId() * 3;
			chunkData[in + 0] = (byte)(rgb & 0xFF);
			chunkData[in + 1] = (byte)(rgb >> 8 & 0xFF);
			chunkData[in + 2] = (byte)(rgb >> 16 & 0xFF);
		}
	}

	@Override
	public FractalKernel copy(int offset) {
		// TODO Auto-generated method stub
		return null;
	}

}
