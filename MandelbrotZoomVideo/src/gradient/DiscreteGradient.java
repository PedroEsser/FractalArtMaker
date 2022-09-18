package gradient;

import java.util.Iterator;

public class DiscreteGradient<T> implements Iterable<T>{

	private Gradient<T> range;
	private int steps;
	
	public DiscreteGradient(Gradient<T> range, int steps) {
		this.range = range;
		this.steps = steps;
	}
	
	public T valueAt(int index) {
		return range.valueAt((double)index / (steps-1));
	}

	public int getSteps() {
		return steps;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new GradientIterator();
	}
	
	private class GradientIterator implements Iterator<T>{

		private int current = 0;
		
		@Override
		public boolean hasNext() {
			return current < steps;
		}

		@Override
		public T next() {
			return valueAt(current++);
		}
		
	}

}
