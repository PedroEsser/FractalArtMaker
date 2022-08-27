package fractalKernels;

public class FractalParameter {
	
	public final String name;
	private double value;
	private double inc;
	
	public FractalParameter(String name, double value, double inc) {
		this.name = name;
		this.value = value;
		this.inc = inc;
	}
	
	public FractalParameter(String name, double value) {
		this(name, value, 1);
	}
	
	public FractalParameter(String name) {
		this(name, 0);
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	public int getValueAsInt() {
		return (int)value;
	}
	
	public void setInc(double inc) {
		this.inc = inc;
	}
	
	public double getInc() {
		return inc;
	}
	
	public void inc() {
		setValue(value + inc);
	}
	
	public void dec() {
		setValue(value - inc);
	}
	
}
