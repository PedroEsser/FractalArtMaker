package rangeUtils;

import java.util.ArrayList;


public class MultiRange<T> implements Range<T>{

	private final ArrayList<RangeWeightTuple> ranges;
	private double sumWeights = 0;
	
	public MultiRange() {
		this.ranges = new ArrayList<>();
	}
	
	public MultiRange(Range<T>... ranges) {
		this();
		for(Range<T> r : ranges)
			this.addGradient(r, 1);
	}
	
	public void addGradient(Range<T> range, double weight) {
		ranges.add(new RangeWeightTuple(range, weight));
		sumWeights += weight;
	}
	
	public void addGradient(Range<T> range) {
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
		
		Range<T> range;
		double weight;
		
		public RangeWeightTuple(Range<T> range, double weight) {
			this.range = range;
			this.weight = weight;
		}
		
	}
	
}
