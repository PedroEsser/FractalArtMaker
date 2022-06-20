package features;

import gui.FractalVisualizer;

public class InitialterationsFeature extends Feature{

	private int initialIterations = 1;
	
	public InitialterationsFeature(FractalVisualizer vis) {
		super(vis);
	}

	public void setInitialIterations(int initialIterations) {
		this.initialIterations = initialIterations;
		this.getCurrentFrame().getKernel().setInitial_iterations(initialIterations);
		this.vis.getNavigator().workAndUpdate();
		System.out.println("Initial iterations: " + initialIterations);
	}
	
	public void incInitialIterations() {
		setInitialIterations(initialIterations+1);
	}
	
	public void decInitialIterations() {
		setInitialIterations(initialIterations-1);
	}
	
}
