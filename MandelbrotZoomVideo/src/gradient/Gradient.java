package gradient;

import java.util.function.Function;

import gradient.GradientUtils;

public interface Gradient<T> {

	public T valueAt(double percent);
	
	public default T random() {
		return this.valueAt(Math.random());
	}
	
	public default T getStart() {
		return this.valueAt(0);
	}
	
	public default T getEnd() {
		return this.valueAt(1);
	}
	
	public default DiscreteGradient<T> toDiscrete(int steps){
		return new DiscreteGradient<T>(this, steps);
	}
	
	
	public default Gradient<T> invert(){ return percent -> valueAt(1 - percent);}
	
	public default Gradient<T> offset(double offset){ return percent -> valueAt(percent + offset);}
	
	
	public default Gradient<T> bounce(){ return new BounceGradient<>(this);}
	
	public default Gradient<T> bounce(double bounceCount){ return bounce(0, bounceCount);}
	
	public default Gradient<T> bounce(double start, double end){ return new BounceGradient<>(this, start, end);}
	
	
	public default Gradient<T> loop(){ return percent -> valueAt(GradientUtils.loop(percent));}
	
	public default Gradient<T> loop(double end){ return loop(0, end);}
	
	public default Gradient<T> loop(double start, double end){ return new LoopGradient<T>(this, start, end);}
	
	
	public default Gradient<T> truncateBelow(double min){ return percent -> valueAt(GradientUtils.truncateBelow(percent, min));}
	
	public default Gradient<T> truncateAbove(double max){ return percent -> valueAt(GradientUtils.truncateAbove(percent, max));}
	
	public default Gradient<T> truncate(double min, double max){ return percent -> valueAt(GradientUtils.truncate(percent, min, max));}
	
	
	public default Gradient<T> fromNumericRange(Gradient<Double> range){ return percent -> valueAt(range.valueAt(percent));}
	
	public default <U> Gradient<U> transform(Function<T, U> f){ return percent -> f.apply(valueAt(percent));}
	
	
}