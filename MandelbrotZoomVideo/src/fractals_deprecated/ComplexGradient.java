package fractals_deprecated;

import gradient.LinearGradient;
import gradient.NumericGradient;
import gradient.Gradient;

public class ComplexGradient implements Gradient<Complex>{

	private final Gradient<Double> re, im;
	
	public ComplexGradient(Gradient<Double> re, Gradient<Double> im) {
		this.re = re;
		this.im = im;
	}

	public ComplexGradient(Complex start, Complex end) {
		this.re = new LinearGradient(start.getRe(), end.getRe());
		this.im = new LinearGradient(start.getIm(), end.getIm());
	}


	@Override
	public Complex valueAt(double percent) {
		return new Complex(re.valueAt(percent), im.valueAt(percent));
	}
	
	

}
