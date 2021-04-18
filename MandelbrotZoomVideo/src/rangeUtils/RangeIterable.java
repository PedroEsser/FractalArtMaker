package rangeUtils;

import java.util.Iterator;

public class RangeIterable<T> implements Iterable<T>{

	private Range<T> range;
	private double percentInc;
	
	public RangeIterable(Range<T> range, int steps) {
		this.range = range;
		this.percentInc = 1d / steps;
	}

	@Override
	public Iterator<T> iterator() {
		return new RangeIterator();
	}
	
	private class RangeIterator implements Iterator<T>{

		private double current;
		
		@Override
		public boolean hasNext() {
			return current <= 1;
		}

		@Override
		public T next() {
			current += percentInc;
			return range.valueAt(current - percentInc);
		}
		
	}

}
