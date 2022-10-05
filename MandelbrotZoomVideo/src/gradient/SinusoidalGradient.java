package gradient;

public class SinusoidalGradient implements NumericGradient {

	private double offset, phaseOffset, amplitude, frequency;
	
	public SinusoidalGradient(double offset, double phaseOffset, double amplitude, double frequency) {
		this.offset = offset;
		this.phaseOffset = phaseOffset;
		this.amplitude = amplitude;
		this.frequency = frequency;
	}

	public double getOffset() {
		return offset;
	}

	public double getPhaseOffset() {
		return phaseOffset;
	}

	public double getAmplitude() {
		return amplitude;
	}

	public double getFrequency() {
		return frequency;
	}

	@Override
	public Double valueAt(double percent) {
		return Math.sin((percent * frequency + phaseOffset) * 2*Math.PI) * amplitude + offset;
	}

	@Override
	public double getPercentFor(double value) {
		return 0;
	}

}
