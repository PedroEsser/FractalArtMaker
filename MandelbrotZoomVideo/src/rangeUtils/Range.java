package rangeUtils;

public interface Range<T> {

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
	
	public default DiscreteRange<T> toDiscrete(int steps){
		return new DiscreteRange<T>(this, steps);
	}
	
	public default Range<T> wrap(){ return percent -> valueAt(wrap(percent));}
	
	public default Range<T> invert(){ return percent -> valueAt(1 - percent);}
	
	public default Range<T> offset(double offset){ return percent -> valueAt(wrap(percent + offset));}
	
	
	public default Range<T> loop(){ return new LoopRange<>(this);}
	
	public default Range<T> loop(double loopCount){ return new LoopRange<>(this, loopCount);}
	
	public default Range<T> loop(double start, double end){return new LoopRange<>(this, start, end);}
	
	
	public default Range<T> slice(double end){ return slice(0, end);}
	
	public default Range<T> slice(double start, double end){ return new SlicedRange<T>(this, start, end);}
	
	
	public static double wrap(double percent) {
		return percent - Math.floor(percent);
	}
	
}