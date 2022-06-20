package fractals_deprecated;

import java.util.Iterator;

import fractal.Complex;

public abstract class Fractal{

	public static final double DEFAULT_ESCAPE_RADIUS_SQUARED = 4;
	protected final double escapeRadiusSquared;
	
	public Fractal(double escapeRadiusSquared) {
		this.escapeRadiusSquared = escapeRadiusSquared;
	}
	
	public Fractal() {
		this(DEFAULT_ESCAPE_RADIUS_SQUARED);
	}
	
	public abstract Complex iterate(Complex current, Complex c);
	
	public boolean escaped(Complex current) {
		return current.amplitudeSquared() > escapeRadiusSquared;
	}
	
	public Iterator<Complex> getIteratorFor(Complex c){ return new FractalIterator(c);}
	
	
	public class FractalIterator implements Iterator<Complex>{
		
		protected Complex current;
		protected final Complex c;
		
		public FractalIterator(Complex c) {
			this.current = new Complex();
			this.c = c;
		}
		
		@Override
		public Complex next() {
			current = iterate(current, c);
			return current;
		}
		
		@Override
		public boolean hasNext() {
			return !escaped(current);
		}
	}
	
}