package gradient;

public class Constant<T> implements Gradient<T>{

	private final T constant;
	
	public Constant(T constant) {
		this.constant = constant;
	}

	@Override
	public T valueAt(double percent) {
		return constant;
	}
	
}
