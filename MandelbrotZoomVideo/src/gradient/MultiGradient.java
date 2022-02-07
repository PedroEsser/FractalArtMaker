package gradient;

import java.util.ArrayList;


public class MultiGradient<T> implements Gradient<T>{

	private final ArrayList<GradientWeightTuple> gradients;
	private double sumWeights = 0;
	
	public MultiGradient() {
		this.gradients = new ArrayList<>();
	}
	
	public MultiGradient(Gradient<T>... gradients) {
		this();
		for(Gradient<T> r : gradients)
			this.addGradient(r, 1);
	}
	
	public void addGradient(Gradient<T> gradient, double weight) {
		gradients.add(new GradientWeightTuple(gradient, weight));
		sumWeights += weight;
	}
	
	public void addGradient(Gradient<T> range) {
		this.addGradient(range, 1);
	}
	
	@Override
	public T valueAt(double percent) {
		double aux = percent * sumWeights;
		for(GradientWeightTuple rw : gradients) {
			if(rw.weight >= aux) {
				return rw.range.valueAt(aux / rw.weight);
			}
			aux -= rw.weight;
		}
		return null;
	}
	
	private double getSumWeights() {
		double sum = 0;
		for(GradientWeightTuple rw : gradients)
			sum += rw.weight;
		return sum;
	}
	
	private class GradientWeightTuple{
		
		Gradient<T> range;
		double weight;
		
		public GradientWeightTuple(Gradient<T> range, double weight) {
			this.range = range;
			this.weight = weight;
		}
		
	}
	
}
