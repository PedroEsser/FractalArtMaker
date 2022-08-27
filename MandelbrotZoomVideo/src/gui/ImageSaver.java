package gui;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fractal.FractalFrame;
import guiUtils.JTuple;
import guiUtils.LabelValueTuple;
import utils.ImageUtils;

public class ImageSaver  extends JFrame{

	private FractalVisualizer vis;
	GradientVisualizer visualizer;
	
	public ImageSaver(FractalVisualizer vis) {
		super("Video Maker");
		this.vis = vis;
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(5, 1, 10, 10));
		
		
		LabelValueTuple width = new LabelValueTuple("Width:", vis.getFrame().getWidth());
		LabelValueTuple height = new LabelValueTuple("Height:", vis.getFrame().getHeight());
		
		mainPanel.add(width);
		mainPanel.add(height);
		
		JButton b = new JButton("Choose Directory");
		JTextField dir = new JTextField("D:\\MandelbrotStuff\\images");
		b.addActionListener(a -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			      dir.setText(chooser.getSelectedFile().toString());
			}
		});
		
		JTuple dirTuple = new JTuple(b, dir);
		mainPanel.add(dirTuple);
		
		JTextField name = new JTextField("Fractal");
		JTuple nameTuple = new JTuple(new JLabel("File name", SwingConstants.LEFT), name);
		mainPanel.add(nameTuple);
		
		JButton saveButton = new JButton("Save Image");
		saveButton.setFont(new Font("Arial", Font.BOLD, 25));
		saveButton.addActionListener(a -> saveImage((int)width.getValue(), (int)height.getValue(), dir.getText() + "/" + name.getText() + ".png"));
		mainPanel.add(saveButton);
		
		add(mainPanel);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setSize(MenuGUI.DEFAULT_MENU_SIZE);
		setVisible(true);
	}
	
	private void saveImage(int width, int height, String path) {
		FractalFrame clone = vis.getFrame().clone(width, height);
		clone.calculateAll();
		ImageUtils.saveImage(clone.toImage(), ImageUtils.getNextFileName(path));
	}
	
}
