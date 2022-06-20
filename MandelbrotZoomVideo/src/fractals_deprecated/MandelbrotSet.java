package fractals_deprecated;

import fractal.Complex;

public class MandelbrotSet extends Fractal{
	
	public MandelbrotSet() {
		super();
	}
	
	public MandelbrotSet(double escapeRadiusSquared) {
		super(escapeRadiusSquared);
	}
	
	@Override
	public Complex iterate(Complex current, Complex c) {
		current.square();
		current.add(c);
		return current;
	}
	
	public static MandelbrotSet mandelbrotFractal(int power, double escapeRadiusSquared) {
		if(power == 2) {
			return new MandelbrotSet(escapeRadiusSquared);
		}
		return new MandelbrotSet(escapeRadiusSquared) {
			@Override
			public Complex iterate(Complex current, Complex c) {
				current = current.integerPower(power);
				current.add(c);
				return current;
			}
		};
	}
	
	public static MandelbrotSet mandelbrotFractal(double power, double escapeRadiusSquared) {
		if(power % 1 == 0 && power >= 0)
			return mandelbrotFractal((int)power, escapeRadiusSquared);
		return new MandelbrotSet(escapeRadiusSquared) {
			@Override
			public Complex iterate(Complex current, Complex c) {
				current = current.realPower(power);
				current.add(c);
				return current;
			}
		};
	}
	
	public static MandelbrotSet mandelbrotFractal(Complex power, double escapeRadiusSquared) {
		if(power.getIm() == 0)
			return mandelbrotFractal(power.getRe(), escapeRadiusSquared);
		return new MandelbrotSet(escapeRadiusSquared) {
			@Override
			public Complex iterate(Complex current, Complex c) {
				current = current.complexPower(power);
				current.add(c);
				return current;
			}
		};
	}
	
	public static MandelbrotSet mandelbrotFractal(Complex power) {
		if(power.getIm() == 0)
			return mandelbrotFractal(power.getRe(), Fractal.DEFAULT_ESCAPE_RADIUS_SQUARED);
		return new MandelbrotSet() {
			@Override
			public Complex iterate(Complex current, Complex c) {
				current = current.complexPower(power);
				current.add(c);
				return current;
			}
		};
	}

}
