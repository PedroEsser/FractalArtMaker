package rangeUtils;

public class LoopRange<T> implements Range<T>{

	private final Range<Double> numericRange;
	private final Range<T> range;
	
	public LoopRange(Range<T> range, Range<Double> numericRange) {
		this.range = range;
		this.numericRange = numericRange;
	}
	
	public LoopRange(Range<T> range, double start, double end) {
		this(range, new LinearRange(start, end));
	}
	
	public LoopRange(Range<T> range, double loopCount) {
		this(range, 0, loopCount);
	}
	
	public LoopRange(Range<T> range) {
		this(range, 2);
	}
	
	@Override
	public T valueAt(double percent) {
		double aux = numericRange.valueAt(percent);
		int index = (int)(aux);
		aux -= index;
		if(index % 2 == 0) 
			return this.range.valueAt(aux);
		return this.range.valueAt(1 - aux);
	}
	
}
