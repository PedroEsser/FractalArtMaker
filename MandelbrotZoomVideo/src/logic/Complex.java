package logic;

public class Complex {

	private double re, im;
	
	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}
	
	public Complex() {
		this(0, 0);
	}
	
	public double amplitudeSquared() {
		return re * re + im * im;
	}
	
	public double amplitude() {
		return Math.sqrt(amplitudeSquared());
	}
	
	public void square() {
		double aux = this.re;
		this.re =  this.re * this.re - this.im * this.im;
		this.im =  aux * this.im + aux * this.im;
	}
	
	public void add(Complex c){
	    this.re += c.re;
	    this.im += c.im;
	}
	
	public double getRe() {
		return re;
	}

	public double getIm() {
		return im;
	}

	public Complex getAdded(double a, double b){
	    return new Complex(this.re + a, this.im + b);
	}
	
	@Override
	public String toString() {
		return "Re:" + re + ", Im:" + im;
	}
	
}
