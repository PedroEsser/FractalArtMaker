package gui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import fractal.FractalFrame;
import fractal.FractalZoom;
import fractals_deprecated.Complex;
import gpuColorGradients.MultiGradient;
import gradient.Gradient;
import gradient.ExponentialGradient;
import guiUtils.JGradient;
import guiUtils.JTuple;
import guiUtils.LabelOptionsTuple;
import guiUtils.LabelTuple;
import guiUtils.LabelValueTuple;
import guiUtils.Weighted1DPanel;
import utils.GifSequenceWriter;
import utils.ImageUtils;
import video.FractalVideo;
import video.FractalFFMPEG;
import video.FractalFrameSaver;
import video.FractalGIF;
import video.FractalMP4;
import video.VideoManager;

public class VideoMaker extends JFrame{

	private FractalVisualizer vis;
	private String audioPath = null;
	Weighted1DPanel mainPanel;
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
	LabelOptionsTuple videoOptions;
	JGradient jGradient;
	FractalVideo video;

	public VideoMaker(FractalVisualizer vis) {
		super("Video Maker");
		this.vis = vis;
		mainPanel = new Weighted1DPanel(false);
		
		fps = new LabelValueTuple("Fps:", 30);
		duration = new LabelValueTuple("Duration:", 60);
		
		mainPanel.addComponent(fps);
		mainPanel.addComponent(duration);
		
		width = new LabelValueTuple("Width:", 1920);
		height = new LabelValueTuple("Height:", 1080);
		
		JTuple<LabelValueTuple, LabelValueTuple> dimPanel = new JTuple(width, height);
		mainPanel.addComponent(dimPanel);
		
		jGradient = new JGradient(0, 1);
		LabelTuple<JGradient> zoomTuple = new LabelTuple("Zoom", jGradient);
		mainPanel.addComponent(zoomTuple);
		
		shift = new LabelValueTuple("Shift:", 0);
		mainPanel.addComponent(shift);
		
		visualizer = new GradientVisualizer(vis.getGradient());
		JPanel panel = new LabelTuple("Gradient: ", visualizer);
		mainPanel.addComponent(panel);
		
		JButton b = new JButton("Choose Directory");
		b.addActionListener(a -> dir.setText(chooseDiretory()));
		dir = new JTextField("D:\\MandelbrotStuff\\video");
		JTuple dirTuple = new JTuple(b, dir);
		mainPanel.addComponent(dirTuple);
		
		JLabel nameLabel = new JLabel("Name:");
		name = new JTextField("Mandelbrot");
		JTuple nameTuple = new JTuple(nameLabel, name);
		mainPanel.addComponent(nameTuple);
		
		
		videoOptions = new LabelOptionsTuple("Video type:",  "mp4(ffmpeg)", "mp4", "gif", "save frames");
		JButton audioButton = new JButton("Add Audio File");
		audioButton.addActionListener(a -> {
			File audio = chooseAudio();
			if(audio != null) {
				audioPath = audio.getAbsolutePath();
				audioButton.setText(audio.getName());
			}
		});
		JTuple optionsTuple = new JTuple(videoOptions, audioButton);
		mainPanel.addComponent(optionsTuple);
		
		renderToggle = new JButton("Start Rendering!");
		renderToggle.addActionListener(a -> renderToggle());
		progress = new JProgressBar();
		progress.setStringPainted(true);
		progress.setString("");
		
		JTuple videoController = new JTuple(renderToggle, progress);
		mainPanel.addComponent(videoController);
		
		this.add(mainPanel);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setSize(MenuGUI.DEFAULT_MENU_SIZE);
		setVisible(true);
		
	}
	
	private String chooseDiretory() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		      return chooser.getSelectedFile().toString();
		}
		return null;
	}
	
	private File chooseAudio() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter() {
			public String getDescription() {return "Audio Files";}
			
			public boolean accept(File f) {
				return f.getName().matches(".*(.mp3|.wav)$");
			}
		});
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		      return chooser.getSelectedFile();
		}
		return null;
	}
	
	private void renderToggle() {
		if(video == null) 
			produceVideo();
		else 
			if(video.togglePause()) 
				renderToggle.setText("Resume");
			else 
				renderToggle.setText("Pause");
	}
	
	private void produceVideo() {
		String videoPath = dir.getText() + "\\" + name.getText();
		String videoOption = videoOptions.getSelectedOption();
		videoPath += (videoOption == "save frames" || videoOption == "mp4(ffmpeg)") ? "" : ("." + videoOption);
		videoPath = ImageUtils.getNextFileName(videoPath);
		try {
			int fp = (int)fps.getValue();
			double dur = duration.getValue();
			int totalFrames = (int)(fp * dur);
			progress.setMaximum(totalFrames);
			
			FractalZoom zoom = vis.getNavigator().getZoom().clone();
			zoom.setDimensions((int)width.getValue(), (int)height.getValue());
			float offset = (float)shift.getValue();
			MultiGradient gradient = vis.getGradient().copy();
			float startOffset = gradient.getOffset();
			System.out.println(startOffset);
			zoom.setColorGradient(p -> gradient.offseted((float)(startOffset + p*offset)));
			Gradient<FractalFrame> z = zoom.fromNumericRange(jGradient.getGradient());
			switch(videoOption) {
				case "mp4(ffmpeg)": video = new FractalFFMPEG(z, fp, dur, videoPath, audioPath, progress);
				break;
				case "mp4": video =  new FractalMP4(z, fp, dur, videoPath, progress);
				break;
				case "gif": video = new FractalGIF(z, fp, dur, videoPath, true, progress);
				break;
				default: video = new FractalFrameSaver(z, fp, dur, videoPath, progress);
			}
			name.setText(new File(videoPath).getName());
			disableComponents();
			VideoManager.enqueue(video);
			renderToggle.setText("Pause");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void disableComponents() {
		for(Component c : mainPanel.getComponents())
			c.setEnabled(false);
		renderToggle.setEnabled(true);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if(video != null)
			VideoManager.dequeue(video);
	}
}
