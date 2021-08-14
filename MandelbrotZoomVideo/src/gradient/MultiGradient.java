package gradient;

import java.util.ArrayList;


public class MultiGradient<T> implements Gradient<T>{

	private final ArrayList<RangeWeightTuple> ranges;
	private double sumWeights = 0;
	
	public MultiGradient() {
		this.ranges = new ArrayList<>();
	}
	
	public MultiGradient(Gradient<T>... ranges) {
		this();
		for(Gradient<T> r : ranges)
			this.addGradient(r, 1);
	}
	
	public void addGradient(Gradient<T> range, double weight) {
		ranges.add(new RangeWeightTuple(range, weight));
		sumWeights += weight;
	}
	
	public void addGradient(Gradient<T> range) {
		this.addGradient(range, 1);
	}
	
	@Override
	public T valueAt(double percent) {
		double aux = percent * sumWeights;
		for(RangeWeightTuple rw : ranges) {
			if(rw.weight >= aux) {
				return rw.range.valueAt(aux / rw.weight);
			}
			aux -= rw.weight;
		}
		return null;
	}
	
	private double getSumWeights() {
		double sum = 0;
		for(RangeWeightTuple rw : ranges)
			sum += rw.weight;
		return sum;
	}
	
	private class RangeWeightTuple{
		
		Gradient<T> range;
		double weight;
		
		public RangeWeightTuple(Gradient<T> range, double weight) {
			this.range = range;
			this.weight = weight;
		}
		
	}
	
}
