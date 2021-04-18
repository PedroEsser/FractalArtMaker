package rangeUtils;

public class LoopRange<T> implements Range<T>{

	private double loopCount;
	private final Range<T> range;
	
	public LoopRange(Range<T> range, double loopCount) {
		this.range = range;
		this.loopCount = loopCount;
	}
	
	public LoopRange(Range<T> range) {
		this(range, 2);
	}
	
	@Override
	public T valueAt(double percent) {
		float aux = (float)(percent * loopCount);
		int index = (int)(aux);
		aux -= index;
		if(index % 2 == 0) 
			return this.range.valueAt(aux);
		return this.range.valueAt(1 - aux);
	}
	
}
