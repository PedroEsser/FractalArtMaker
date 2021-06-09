package logic;

import rangeUtils.LinearRange;
import rangeUtils.NumericRange;
import rangeUtils.Range;

public class ComplexRange implements Range<Complex>{

	private final NumericRange re, im;
	
	public ComplexRange(NumericRange re, NumericRange im) {
		this.re = re;
		this.im = im;
	}

	public ComplexRange(Complex start, Complex end) {
		this.re = new LinearRange(start.getRe(), end.getRe());
		this.im = new LinearRange(start.getIm(), end.getIm());
	}


	@Override
	public Complex valueAt(double percent) {
		return new Complex(re.valueAt(percent), im.valueAt(percent));
	}
	
	

}
