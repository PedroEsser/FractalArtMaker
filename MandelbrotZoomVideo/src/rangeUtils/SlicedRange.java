package rangeUtils;

public class SlicedRange<T> implements Range<T>{

	private Range<T> range;
	private final Range<Double> numericRange;
	
	public SlicedRange(Range<T> range, Range<Double> numericRange) {
		this.range = range;
		this.numericRange = numericRange;
	}
	
	public SlicedRange(Range<T> range, double start, double end) {
		this(range, new LinearRange(start, end));
	}
	
	public SlicedRange(Range<T> range, double loopCount) {
		this(range, 0, loopCount);
	}
	
	public SlicedRange(Range<T> range) {
		this(range, 2);
	}

	@Override
	public T valueAt(double percent) {
		return range.valueAt(Range.wrap(numericRange.valueAt(percent)));
	}

}
