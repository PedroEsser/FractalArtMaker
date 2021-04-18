package rangeUtils;

import java.util.ArrayList;


public class MultiRange<T> implements Range<T>{

	private final ArrayList<RangeWeightTuple> ranges;
	private float sumWeights = 0;
	
	public MultiRange() {
		this.ranges = new ArrayList<>();
	}
	
	public MultiRange(Range<T>... ranges) {
		this();
		for(Range<T> r : ranges)
			this.addGradient(r, 1);
	}
	
	public void addGradient(Range<T> range, float weight) {
		ranges.add(new RangeWeightTuple(range, weight));
		sumWeights += weight;
	}
	
	@Override
	public T valueAt(double percent) {
		float aux = (float)percent * sumWeights;
		for(RangeWeightTuple rw : ranges) {
			if(rw.weight >= aux) {
				return rw.range.valueAt(aux / rw.weight);
			}
			aux -= rw.weight;
		}
		return null;
	}
	
	private class RangeWeightTuple{
		
		Range<T> range;
		float weight;
		
		public RangeWeightTuple(Range<T> range, float weight) {
			this.range = range;
			this.weight = weight;
		}
		
	}
	
}
