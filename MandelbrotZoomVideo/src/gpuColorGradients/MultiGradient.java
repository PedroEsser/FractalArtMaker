package gpuColorGradients;

import java.awt.Color;
import java.util.ArrayList;

import gradient.Gradient;

import static gpuColorGradients.GradientUtils.*;

public class MultiGradient extends ColorGradient{

	private final ArrayList<GradientWeightTuple> gradients;
	
	public MultiGradient(boolean loop, float range, float offset, ThreeChannelGradient... gradients) {
		super(4, loop, range);
		offseted(offset);
		this.gradients = new ArrayList<GradientWeightTuple>();
		for(ThreeChannelGradient g : gradients)
			addGradient(g);
	}
	
	public MultiGradient(ThreeChannelGradient... gradients) {
		this(true, 1, 0, gradients);
	}
	
	public MultiGradient() {
		this(new ThreeChannelGradient[] {});
	}
	
	public MultiGradient offseted(float offset) {
		this.gradient[2] = offset;
		return this;
	}
	
	public void addGradient(ThreeChannelGradient gradient, float weight) {
		gradients.add(new GradientWeightTuple(gradient, weight));
	}
	
	public void addGradient(ThreeChannelGradient gradient) {
		addGradient(gradient, 1);
	}
	
	public void updateGradientData() {
		int size = 4 + (1 + ThreeChannelGradient.DATA_SIZE) * gradients.size();
		float[] newData = new float[size];
		newData[0] = gradient[0];
		newData[1] = gradient[1];
		newData[2] = gradient[2];
		
		float weightSum = 0;
		int index = 4;
		for(GradientWeightTuple gw : gradients) {
			weightSum += gw.weight;
			newData[index++] = gw.weight;
			float[] data = gw.gradient.getGradientData();
			for(int i = 0 ; i < data.length ; i++) 
				newData[index + i] = data[i];
			index += ThreeChannelGradient.DATA_SIZE;
		}
		newData[3] = weightSum;
		this.gradient = newData;
	}
	
	public static float getOffset(float[] gradient) {
		return gradient[2];
	}
	
	public static float getWeightSum(float[] gradient) {
		return gradient[3];
	}
	
	public static int colorAtPercent(float percent, float[] gradient) {
		if(percent != percent)
			percent = 0;
		percent += gradient[2];
		float p = calculatePercent(percent, gradient, 0);
		float aux = p * gradient[3];	// p * sumWeights
		int index = 4;
		while(aux >= 0) {
			float w = gradient[index++];
			if(w >= aux)
				return ThreeChannelGradient.colorAtPercent(aux/w, gradient, index);
			aux -= w;
			index += ThreeChannelGradient.DATA_SIZE;									//skipping gradient
		}
		return -1;
	}
	
	@Override
	public MultiGradient bounce(float range) {
		return (MultiGradient)super.bounce(range);
	}
	
	@Override
	public MultiGradient loop(float range) {
		return (MultiGradient)super.loop(range);
	}
	
	@Override
	public float[] getGradientData() {
		updateGradientData();
		return super.getGradientData();
	}
	
	@Override
	public Gradient<Color> toGradient() {
		updateGradientData();
		return p -> new Color(colorAtPercent((float)p, gradient));
	}
	
	private class GradientWeightTuple{
		
		ThreeChannelGradient gradient;
		float weight;
		
		public GradientWeightTuple(ThreeChannelGradient gradient, float weight) {
			this.gradient = gradient;
			this.weight = weight;
		}
		
	}

}
