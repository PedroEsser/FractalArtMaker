package gui;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import fractal.Complex;
import fractal.FractalFrame;
import fractal.FractalZoom;
import gpuColorGradients.ColorGradient;
import gpuColorGradients.MultiGradient;
import guiUtils.JTuple;
import guiUtils.LabelTuple;
import guiUtils.LabelValueTuple;
import utils.ImageUtils;
import video.FractalVideo;
import video.FractalZoomFrameSaver;
import video.FractalZoomMP4;

public class VideoMaker extends JFrame{

	private FractalVisualizer vis;
	LabelValueTuple fps;
	LabelValueTuple duration;
	LabelValueTuple width;
	LabelValueTuple height;
	JTextField name;
	LabelValueTuple shift;
	JTextField dir;
	GradientVisualizer visualizer;
	JButton renderToggle;
	JProgressBar progress;
	FractalVideo video;

	public VideoMaker(FractalVisualizer vis) {
		super("Video Maker");
		this.vis = vis;
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(8, 1, 10, 10));
		
		fps = new LabelValueTuple("Fps:", 30);
		duration = new LabelValueTuple("Duration:", 50);
		
		mainPanel.add(fps);
		mainPanel.add(duration);
		
		width = new LabelValueTuple("Width:", 1920);
		height = new LabelValueTuple("Height:", 1080);
		
		JPanel dimPanel = new JPanel();
		dimPanel.setLayout(new GridLayout(1, 2, 20, 20));
		dimPanel.add(width);
		dimPanel.add(height);
		mainPanel.add(dimPanel);
		
		shift = new LabelValueTuple("Shift:", 0);
		mainPanel.add(shift);
		
		visualizer = new GradientVisualizer(vis.getGradient());
		JPanel panel = new LabelTuple("Gradient: ", visualizer);
		mainPanel.add(panel);
		
		JButton b = new JButton("Choose Directory");
		b.addActionListener(a -> chooseDiretory());
		dir = new JTextField("C:\\Users\\pedro\\Desktop\\MandelbrotStuff");
		JTuple dirTuple = new JTuple(b, dir);
		mainPanel.add(dirTuple);
		
		JLabel nameLabel = new JLabel("Name:");
		name = new JTextField("Mandelbrot");
		JTuple nameTuple = new JTuple(nameLabel, name);
		mainPanel.add(nameTuple);
		
		renderToggle = new JButton("Start Rendering!");
		renderToggle.addActionListener(a -> renderToggle());
		progress = new JProgressBar();
		progress.setStringPainted(true);
		progress.setString("");
		
		JTuple videoController = new JTuple(renderToggle, progress);
		mainPanel.add(videoController);
		
		this.add(mainPanel);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setSize(MenuGUI.DEFAULT_MENU_SIZE);
		setVisible(true);
		
	}
	
	private void chooseDiretory() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		      dir.setText(chooser.getSelectedFile().toString());
		}else {
			System.out.println("No Selection ");
		}
	}
	
	private void renderToggle() {
		if(video == null) {
			String videoPath = ImageUtils.getNextFileName(dir.getText() + "/" + name.getText() + ".mp4");
//			String videoPath = dir.getText() + "/" + name.getText();
			try {
				int fp = (int)fps.getValue();
				double dur = duration.getValue();
				int totalFrames = (int)(fp * dur);
				progress.setMaximum(totalFrames);
				FractalZoom zoom = vis.getNavigator().getZoom().clone();
				zoom.setDimensions((int)width.getValue(), (int)height.getValue());
				float offset = (float)shift.getValue();
				MultiGradient gradient = vis.getGradient();
				zoom.setColorGradient(p -> gradient.shifted((float)(p*offset)));
//				FractalZoom zoomInOut = zoom.bounce();
//				zoomInOut.setDimensions((int)width.getValue(), (int)height.getValue());
//				zoomInOut.setNorm(zoom.getNorm());
//				
//				FractalZoom zoomOut = zoom.clone();
//				zoomOut.setDeltaGradient(new LinearGradient(zoomInOut.getEnd().getDelta(), Math.PI*2+.5));
//				zoomOut.setMaxIteration(100);
//				zoomOut.setDimensions((int)width.getValue(), (int)height.getValue());
//				zoomOut.setNorm(zoom.getNorm());
//				
//				MultiGradient<FractalFrame> z = new MultiGradient<FractalFrame>(zoomInOut);
//				z.addGradient(zoomOut, 2);
				
//				video = new FractalZoomMP4(zoom, fp, dur, videoPath) {
				video = new FractalZoomFrameSaver(zoom, fp, dur, videoPath) {
					@Override
					public void encodeImage(BufferedImage img) {
						super.encodeImage(img);
						progress.setValue(currentFrame);
						progress.setString("Frame " + currentFrame + " of " + totalFrames + " rendered.");
					}
					@Override
					public void finished() {
						super.finished();
						progress.setValue(currentFrame);
						progress.setString("Video finished rendering!");
					}
				};
				video.start();
				renderToggle.setText("Pause");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			if(video.togglePause()) {
				renderToggle.setText("Resume");
			}else {
				renderToggle.setText("Pause");
			}
		}
	}
	
	
	@Override
	public void dispose() {
		super.dispose();
	}
}
