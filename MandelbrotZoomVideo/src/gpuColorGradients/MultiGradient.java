package gpuColorGradients;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import gradient.Gradient;

public class MultiGradient extends ColorGradient implements Serializable{

	private final ArrayList<GradientWeightTuple> gradients;
	
	public MultiGradient(boolean loop, float range, float offset, ThreeChannelGradient... gradients) {
		super(4, loop, range);
		offseted(offset);
		this.gradients = new ArrayList<GradientWeightTuple>();
		for(ThreeChannelGradient g : gradients)
			addGradient(g);
	}
	
	public MultiGradient(boolean loop, float range, float offset) {
		this(loop, range, offset, new ThreeChannelGradient[] {});
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
	
	public boolean removeGradient(ThreeChannelGradient gradient) {
		return gradients.remove(findTuple(gradient));
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
	
	public float getOffset() {
		return getOffset(this.gradient);
	}
	
	public float getModifiedPercent(float percent) {
		percent += getOffset();
		float p = calculatePercent(percent, gradient, 0);
		return p * getWeightSum();
	}
	
	public int getIndexForGradientAt(float percent) {
		float p = getModifiedPercent(percent);
		for(int i = 0 ; i < gradients.size() ; i++) {
			GradientWeightTuple gw = gradients.get(i);
			if(gw.weight > p)
				return i;
			p -= gw.weight;
		}
		return -1;
	}
	
	public ThreeChannelGradient getGradientAtIndex(int index) {
		return gradients.get(index).gradient;
	}
	
	public int getNumberOfGradients() {
		return gradients.size();
	}
	
	public float[] getIntervalForGradient(ThreeChannelGradient gradient) {
		float[] range = new float[] {0, 0};
		for(GradientWeightTuple gw : gradients) {
			if(gw.gradient == gradient){
				range[1] = (range[0] + gw.weight)/getWeightSum();
				range[0] /= getWeightSum();
				return range;
			}
			range[0] += gw.weight;
		}
		return null;
	}
	
	private GradientWeightTuple findTuple(ThreeChannelGradient g) {
		for(GradientWeightTuple gw : gradients)
			if(gw.gradient == g)
				return gw;
		return null;
	}
	
	public void setWeightForGradient(ThreeChannelGradient g, float weight) {
		findTuple(g).weight = weight;
	}
	
	public float getWeightForGradient(ThreeChannelGradient g) {
		return findTuple(g).weight;
	}
	
	public static float getWeightSum(float[] gradient) {
		return gradient[3];
	}
	
	public float getWeightSum() {
		return getWeightSum(gradient);
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
	public MultiGradient copy() {
		MultiGradient copy = new MultiGradient(isLoop(gradient, 0), getRange(gradient, 0), LOOP_BIT);
		copy.offseted(getOffset());
		for(GradientWeightTuple gw : gradients)
			copy.addGradient(gw.gradient.copy(), gw.weight);
		return copy;
	}
	
	@Override
	public Gradient<Color> toGradient() {
		updateGradientData();
		return p -> new Color(colorAtPercent((float)p, gradient));
	}
	
	class GradientWeightTuple implements Serializable{
		
		private static final long serialVersionUID = 1L;
		ThreeChannelGradient gradient;
		float weight;
		
		public GradientWeightTuple(ThreeChannelGradient gradient, float weight) {
			this.gradient = gradient;
			this.weight = weight;
		}
		
	}

	@Override
	public Color colorAt(double percent) {
		return new Color(colorAtPercent((float)percent, gradient));
	}

}
