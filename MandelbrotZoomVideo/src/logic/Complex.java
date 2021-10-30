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
	
	public double angle() {
		return Math.atan2(im, re);
	}
	
	public double amplitude() {
		return Math.sqrt(amplitudeSquared());
	}
	
	public double amplitudeSquared() {
		return re * re + im * im;
	}
	
	public double getRe() {
		return re;
	}

	public double getIm() {
		return im;
	}
	
	public void setRe(double re) {
		this.re = re;
	}
	
	public void setIm(double im) {
		this.im = im;
	}
	
	
	
	public void add(Complex c){
	    this.re += c.re;
	    this.im += c.im;
	}

	public Complex getAdded(double a, double b){
	    return new Complex(this.re + a, this.im + b);
	}
	
	public void scale(double scale) {
		this.re *= scale;
		this.im *= scale;
	}
	
	public void times(Complex c) {
		double aux = this.re;
		this.re =  this.re * c.re - this.im * c.im;
		this.im =  c.re * this.im + c.im * aux;
	}
	
	public void square() {
		double aux = this.re;
		this.re =  this.re * this.re - this.im * this.im;
		this.im =  2 * aux * this.im;
	}
	
	public Complex integerPower(int power) {
		Complex result = new Complex(1, 0);
		Complex aux = this.clone();
		int power2 = 1;
		while(power2 <= power) {
			if((power2 & power) != 0) {
				result.times(aux);
			}
			aux.square();
			power2 <<= 1;
		}
		return result;
	}
	
	public Complex realPower(double power) {
		if(this.isZero())
			return new Complex();
		
		double newAngle = this.angle() * power;
		double newRadius = Math.pow(this.amplitude(), power);
		return fromAngle(newAngle, newRadius);
	}
	
	public Complex complexPower(Complex c) {
		if(this.isZero())
			return new Complex();
		
		double angle = this.angle();
		double lnR = Math.log(this.amplitude());
		double newRadius = Math.exp(lnR * c.re - c.im * angle);
		double newAngle = lnR * c.im + c.re * angle;
		return fromAngle(newAngle, newRadius);
	}
	
	public Complex conjugate() {
		return new Complex(this.re, -this.im);
	}
	
	public Complex clone() {
		return new Complex(this.re, this.im);
	}
	
	public boolean isZero() {
		return this.re == 0 && this.im == 0;
	}
	
	@Override
	public String toString() {
		return "Re:" + re + ", Im:" + im;
	}
	
	
	public static Complex fromAngle(double angle, double radius) {
		Complex c = new Complex(Math.cos(angle) * radius, Math.sin(angle) * radius);
		return c;
	}
	
	public static Complex fromAngle(double angle) {
		return fromAngle(angle, 1);
	}
	
}
