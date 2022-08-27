package gpuColorGradients;

import java.awt.Color;
import java.util.ArrayList;

import gradient.Gradient;

import static gpuColorGradients.GradientUtils.*;

public class MultiGradientV2 extends ColorGradientV2{

	private final ArrayList<GradientWeightTuple> gradients;
	
	public MultiGradientV2(boolean loop, float range, float offset, ThreeChannelGradient... gradients) {
		super(4, loop, range);
		offseted(offset);
		this.gradients = new ArrayList<GradientWeightTuple>();
		for(ThreeChannelGradient g : gradients)
			addGradient(g);
	}
	
	public MultiGradientV2(ThreeChannelGradient... gradients) {
		this(true, 1, 0, gradients);
	}
	
	public MultiGradientV2() {
		this(new ThreeChannelGradient[] {});
	}
	
	public MultiGradientV2 offseted(float offset) {
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
		if(gradient.length == 4)	//empty gradient
			return -1;
		if(percent != percent)
			percent = 0;
		percent += getOffset(gradient);
		float p = calculatePercent(percent, gradient, 0);
		float aux = p * getWeightSum(gradient);	// p * sumWeights
		int index = 4;
		while(aux >= 0) {
			float w = gradient[index++];
			if(w >= aux)
				return ThreeChannelGradient.getColorAtPercent(aux/w, gradient, index);
			aux -= w;
			index += ThreeChannelGradient.DATA_SIZE;									//skipping gradient
		}
		return -1;
	}
	
	@Override
	public MultiGradientV2 bounce(float range) {
		return (MultiGradientV2)super.bounce(range);
	}
	
	@Override
	public MultiGradientV2 loop(float range) {
		return (MultiGradientV2)super.loop(range);
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
