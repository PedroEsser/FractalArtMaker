package gpuColorGradients;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.function.Consumer;

import javax.swing.JButton;

import static guiUtils.GUIUtils.*;

import gui.GradientVisualizer;
import guiUtils.ColorPanel;
import guiUtils.LabelValueTuple;
import guiUtils.Weighted1DPanel;

public abstract class GradientEditor extends Weighted1DPanel{
	
	protected GradientVisualizer visualizer;
	protected ColorGradient gradient;
	protected JButton loopToggle;
	protected LabelValueTuple rangeParameter;
	
	public GradientEditor(ColorGradient gradient) {
		super(false, 2, 5, 2, 5);
		this.gradient = gradient;
		addVisualizer();
		addRangePanel();
	}
	
	public GradientEditor() {
		this(null);
	}
	
	protected void addVisualizer() {
		visualizer = new GradientVisualizer(gradient);
		this.addComponent(visualizer, 5);
	}
	
	protected void addRangePanel() {
		Weighted1DPanel rangePanel = new Weighted1DPanel();
		rangeParameter = new LabelValueTuple("Range", gradient == null ? 0 : gradient.getRange());
		rangeParameter.setValueChangeListener(v -> updateGradient());
		rangePanel.addComponent(rangeParameter, 3);
		
		loopToggle = button(gradient != null && gradient.isLoop() ? "Loop" : "Bounce", e -> {
			loopToggle.setText(loopSelected() ? "Bounce" : "Loop");
			updateGradient();
		});
		rangePanel.addComponent(loopToggle, .5);
		
		this.addComponent(rangePanel, 0.5);
	}
	
	public ColorGradient getGradient() {
		return gradient;
	}
	
	public void setGradient(ColorGradient gradient) {
		this.gradient = gradient;
		rangeParameter.setValue(gradient.getRange());
		loopToggle.setText(gradient.isLoop() ? "Loop" : "Bounce");
	}
	
	protected void updateGradient(){
		if(gradient != null) {
			if(loopSelected())
				gradient.loop((float)rangeParameter.getValue());
			else
				gradient.bounce((float)rangeParameter.getValue());
			
			visualizer.setGradient(gradient);
		}
	}
	
	public boolean loopSelected() {
		return loopToggle.getText().equals("Loop");
	}
	
}
