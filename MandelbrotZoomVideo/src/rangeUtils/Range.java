package rangeUtils;

public interface Range<T> {

	public T valueAt(double percent);
	
	public default T random() {
		return this.valueAt(Math.random());
	}
	
	public default T getStart() {
		return this.valueAt(0);
	}
	
	public default T getEnd() {
		return this.valueAt(1);
	}
	
	public default Iterable<T> getIterable(int steps){
		return new RangeIterable<T>(this, steps);
	}
	
}