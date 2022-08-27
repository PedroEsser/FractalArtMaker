package fractals_deprecated;

import java.util.Iterator;

public class FractalCeption extends Fractal{
	
	private int cIterations = 0;
	private final Fractal fractal;
	
	public FractalCeption(int cIterations, Fractal fractal) {
		this.cIterations = cIterations;
		this.fractal = fractal;
	}
	
	public FractalCeption(int cIterations) {
		this(cIterations, new MandelbrotSet());
	}
	
	public Iterator<Complex> getIteratorFor(Complex c){ return new FractalCeptionCeptionIterator(c);}
	
	public class FractalCeptionCeptionIterator extends FractalIterator{
		
		protected Complex currentC;
		
		public FractalCeptionCeptionIterator(Complex c) {
			super(c);
			Iterator<Complex> it = fractal.getIteratorFor(c.clone());
			for(int i = 0 ; i < cIterations ; i++)
				it.next();
			currentC = it.next();
		}
		
		@Override
		public Complex next() {
			current = iterate(current, currentC);
			return current;
		}
		
	}

	@Override
	public Complex iterate(Complex current, Complex c) {
		return fractal.iterate(current, c);
	}

}
