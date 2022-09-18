package gpuColorGradients;


import javax.swing.JButton;

import static guiUtils.GUIUtils.*;
import guiUtils.JParameters;
import guiUtils.LabelTuple;
import guiUtils.LabelValueTuple;
import guiUtils.Weighted1DPanel;

public class ThreeChannelGradientEditor extends GradientEditor{

	private LabelTuple<JParameters>[] channels = new LabelTuple[3];
	private JButton hsbToggle;
	
	public ThreeChannelGradientEditor(ThreeChannelGradient g) {
		super(g);
		Weighted1DPanel channelPanel = new Weighted1DPanel(true);
		
		Weighted1DPanel rangePanel = new Weighted1DPanel(false);
		for(int i = 0 ; i < 3 ; i++) {
			JParameters channelI = new JParameters();
			channelI.addParameter("Start", 0);
			channelI.addParameter("Range", 0);
			channels[i] = new LabelTuple<JParameters>("", channelI);
			rangePanel.addComponent(channels[i]);
		}
		channelPanel.addComponent(rangePanel, 5);
		
		hsbToggle = button("HSB" , e -> {
			hsbToggle.setText(hsbSelected() ? "RGB" : "HSB");
			updateColorScheme();
			updateGradient();
		});
		
		channelPanel.addComponent(hsbToggle, 0.5);
		
		this.addComponent(channelPanel, 1);
		if(g != null)
			setGradient(g);
	}
	
	public ThreeChannelGradientEditor() {
		this(null);
	}
	
	@Override
	public ThreeChannelGradient getGradient() {
		return (ThreeChannelGradient)gradient;
	}
	
	public boolean hsbSelected() {
		return hsbToggle.getText().equals("HSB");
	}
	
	private void updateColorScheme() {
		channels[0].setText(hsbSelected() ? "Hue" : "Red");
		channels[1].setText(hsbSelected() ? "Saturation" : "Green");
		channels[2].setText(hsbSelected() ? "Brightness" : "Blue");
	}
	
	@Override
	public void setGradient(ColorGradient gradient) {
		assert gradient instanceof ThreeChannelGradient : "Gradient must be 3-channel";
		ThreeChannelGradient g = (ThreeChannelGradient)gradient;
		for(int i = 0 ; i < 3 ; i++) {
			LabelValueTuple start = channels[i].getRight().findParameter("Start");
			LabelValueTuple range = channels[i].getRight().findParameter("Range");
			start.setValue(g.getNthChannelStart(i));
			range.setValue(g.getNthChannelRange(i));
			start.setValueChangeListener(d -> updateGradient());
			range.setValueChangeListener(d -> updateGradient());
		}
		hsbToggle.setText(g.isHSB() ? "HSB" : "RGB");
		updateColorScheme();
		super.setGradient(g);
	}
	
	@Override
	protected void updateGradient() {
		if(gradient != null) {
			if(hsbSelected())
				getGradient().setHSB();
			else
				getGradient().setRGB();
			
			for(int i = 0 ; i < 3 ; i++) {
				getGradient().setNthChannelStart(i, (float)channels[i].getRight().getValueFor("Start"));
				getGradient().setNthChannelRange(i, (float)channels[i].getRight().getValueFor("Range"));
			}
			super.updateGradient();
		}
	}

}
