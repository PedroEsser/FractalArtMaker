package rangeUtils;

public class Constant<T> implements Range<T>{

	private final T constant;
	
	public Constant(T constant) {
		this.constant = constant;
	}

	@Override
	public T valueAt(double percent) {
		return constant;
	}
	
}
