package gpuColorGradients;

import java.awt.Color;
import java.awt.Dimension;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

import guiUtils.LabelValueTuple;
import guiUtils.Weighted1DPanel;
import static guiUtils.GUIUtils.*;

public class GradientFactoryGUI extends JFrame{

	public static final Dimension DEFAULT_EDITOR_SIZE = new Dimension(900, 500);
	private MultiGradientEditor editor;
	private Weighted1DPanel mainPanel;
	private Weighted1DPanel bottomPanel;
	private LabelValueTuple weightParameter;
	private ThreeChannelGradientEditor selectedGradientEditor;
	private JLabel selectLabel;
	private Consumer<MultiGradient> updateCallback;
	
	public GradientFactoryGUI(MultiGradient gradient) {
		super();
		mainPanel = new Weighted1DPanel(false, 5, 0, 5, 0);
		
		this.editor = new MultiGradientEditor(gradient.copy());
		this.editor.setSelectCallback(g -> gradientSelected(g));
		Weighted1DPanel actionsPanel = new Weighted1DPanel();
		actionsPanel.addComponent(button("New", e -> addGradient()));
		actionsPanel.addComponent(button("Load", e -> new GradientLoader(gradient)));
		actionsPanel.addComponent(button("Remove", e -> editor.removeSelectedGradient()));
		
		this.editor.addComponent(actionsPanel, 0.5);
		mainPanel.addComponent(editor, 2);
		
		bottomPanel = new Weighted1DPanel(false);
		selectLabel = label("Selected Gradient", 20);
		selectLabel.setBorder(BorderFactory.createLineBorder(Color.white, 2, true));
		bottomPanel.addComponent(selectLabel, 0.2);
		bottomPanel.setBorder(BorderFactory.createLineBorder(Color.white, 2, true));
		
		mainPanel.addComponent(bottomPanel, 1);
		mainPanel.addComponent(button("Update", a -> updateCallback.accept(editor.getGradient().copy())));
		gradientSelected(null);
		
		this.add(mainPanel);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setSize(DEFAULT_EDITOR_SIZE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public GradientFactoryGUI(ThreeChannelGradient gradient) {
		this(new MultiGradient(gradient));
	}
	
	private void gradientSelected(ThreeChannelGradient g) {
		if(selectedGradientEditor != null)
			bottomPanel.remove(selectedGradientEditor);
		
		
		selectedGradientEditor = new ThreeChannelGradientEditor(g) {
			@Override
			protected void updateGradient() {
				super.updateGradient();
				editor.updateGradient();
			}
		};
		if(g != null) {
			weightParameter = new LabelValueTuple("Weight:", editor.getGradient().getWeightForGradient(g));
			weightParameter.setValueChangeListener(w -> {
				editor.getGradient().setWeightForGradient(selectedGradientEditor.getGradient(), w.floatValue());
				editor.updateGradient();
			});
			selectedGradientEditor.addComponent(weightParameter, 0.2);
		}
		
		bottomPanel.addComponent(selectedGradientEditor, 1);
		bottomPanel.updateUI();
		selectLabel.setText(g == null ? "No Selecion" : "Selected Gradient");
	}
	
	private void addGradient(ThreeChannelGradient g) {
		editor.getGradient().addGradient(g);
		editor.updateGradient();
	}
	
	private void addGradient() {
		this.addGradient(new ThreeChannelGradient());
	}
	
	public void setUpdateCallback(Consumer<MultiGradient> updateCallback) {
		this.updateCallback = updateCallback;
	}
	
}
