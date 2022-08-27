package gpuColorGradients;

import java.awt.Color;
import java.util.ArrayList;

import static gpuColorGradients.GradientUtils.*;
import gradient.Gradient;

public class MultiGradient extends ColorGradient{

	private final ArrayList<GradientWeightTuple> gradients;
	private float offset = 0;
	private float sumWeights;
	
	public MultiGradient() {
		super(MULTI);
		this.gradients = new ArrayList<>();
	}
	
	public MultiGradient(ColorGradient... gradients) {
		this();
		for(ColorGradient r : gradients)
			this.addGradient(r);
	}
	
	public void addGradient(ColorGradient gradient, float weight) {
		gradients.add(new GradientWeightTuple(gradient, weight));
		sumWeights += toMyFloat(toMyInt(weight));
	}
	
	public void addGradient(ColorGradient gradient) {
		this.addGradient(gradient, 1);
	}
	
	public static int colorAt(float percent, int[] gradient) {
		if(percent != percent)
			percent = 0;
		percent += toMyFloat(gradient[3]);					//shift
		float p = ColorGradient.percentFor(percent, 0, gradient);//
		int index = 4;
		float aux = p * toMyFloat(gradient[index++]);	// p * sumWeights
		while(aux >= 0) {
			float w = toMyFloat(gradient[index++]);
			if(w >= aux)
				return ColorGradient.genericColorAt(aux / w, index, gradient);
			aux -= w;
			index += 5;									//skipping gradient
		}
		return -1;
	}
	
	public MultiGradient clone() {
		MultiGradient clone = new MultiGradient();
		clone.gradients.addAll(this.gradients);
		clone.sumWeights = this.sumWeights;
		clone.setOffset(offset);
		return clone;
	}
	
	public void setOffset(float offset) {
		this.offset = offset;
	}
	
	public MultiGradient offseted(float offset) {
		MultiGradient offseted = this.clone();
		offseted.setOffset(offset);
		return offseted;
	}
	
	@Override
	public Gradient<Color> toGradient() {
		return p -> new Color(colorAt((float)p, toPrimitive()));
	}
	
	@Override
	public int[] toPrimitive() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int b : getBase())
			list.add(b);
		list.add(toMyInt(offset));
		list.add(toMyInt(sumWeights));
		for(GradientWeightTuple t : gradients) {
			list.add(toMyInt(t.weight));
			for(int i : t.gradient.toPrimitive())
				list.add(i);
		}
		
		int[] result = new int[list.size()];
		for(int i = 0 ; i < list.size() ; i++)
			result[i] = list.get(i);
		
		
		return result;
	}
	
	private class GradientWeightTuple{
		
		ColorGradient gradient;
		float weight;
		
		public GradientWeightTuple(ColorGradient gradient, float weight) {
			this.gradient = gradient;
			this.weight = weight;
		}
		
	}
	
}
