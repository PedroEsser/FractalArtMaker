package rangeUtils;

import java.util.Iterator;

public class DiscreteRange<T> implements Iterable<T>{

	private Range<T> range;
	private double percentInc;
	
	public DiscreteRange(Range<T> range, int steps) {
		this.range = range;
		this.percentInc = 1d / (steps - 1);
	}
	
	public T valueAt(int index) {
		return range.valueAt(percentInc * index);
	}

	@Override
	public Iterator<T> iterator() {
		return new RangeIterator();
	}
	
	private class RangeIterator implements Iterator<T>{

		private double current = -percentInc;
		
		@Override
		public boolean hasNext() {
			return current <= (1 - percentInc);
		}

		@Override
		public T next() {
			current += percentInc;
			return range.valueAt(current);
		}
		
	}

}
