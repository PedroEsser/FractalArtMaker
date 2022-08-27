package fractals_deprecated;

public class BurningShip extends Fractal{

	@Override
	public Complex iterate(Complex current, Complex c) {
		current.setIm(abs(current.getIm()));
		current.setRe(abs(current.getRe()));
		current.square();
		current.add(c);
		return current;
	}
	
	private static double abs(double a) {
		return a < 0 ? -a : a;
	}

}
