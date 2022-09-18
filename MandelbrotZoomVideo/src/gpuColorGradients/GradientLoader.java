package gpuColorGradients;

import static guiUtils.GUIUtils.*;

import java.awt.Dimension;
import java.io.File;
import java.io.Serializable;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import guiUtils.GUIUtils;
import guiUtils.LabelValueTuple;
import guiUtils.Weighted1DPanel;

public class GradientLoader extends JFrame{

	private static final String GRADIENT_FOLDER_PATH = "gradients";
	public static final Dimension DEFAULT_LOADER_SIZE = new Dimension(1200, 600);
	private Weighted1DPanel mainPanel;
	private Weighted1DPanel savedGradientsPanel;
	
	
	private Consumer<ColorGradient> loadCallback;
	
	public GradientLoader(MultiGradient gradient) {
		super();
		mainPanel = new Weighted1DPanel(false, 5, 0, 5, 0);
		savedGradientsPanel = new Weighted1DPanel(false, 5, 0, 5, 0);
		savedGradientsPanel.addComponent(label("Saved Gradients"), 1);
		JScrollPane pane = new JScrollPane(savedGradientsPanel);
		savedGradientsPanel.addComponent(pane, 5);
		
		
		
		this.add(mainPanel);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setSize(DEFAULT_LOADER_SIZE);
		setLocationRelativeTo(null);
		setVisible(true);
		checkGradientFolder();
	}
	
	
	public static void checkGradientFolder() {
		File gradientFolder = new File(GRADIENT_FOLDER_PATH);
		if (!gradientFolder.exists()) 
			gradientFolder.mkdir();
		
	}
	
	
	
	class NameGradientTuple implements Serializable{
		String name;
		ColorGradient gradient;
		public NameGradientTuple(String name, ColorGradient gradient) {
			super();
			this.name = name;
			this.gradient = gradient;
		}
		
	}
	
}
