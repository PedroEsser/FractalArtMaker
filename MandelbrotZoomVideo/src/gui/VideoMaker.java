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
import gpuColorGradients.ColorGradient;
import gpuColorGradients.MultiGradient;
import guiUtils.JTuple;
import guiUtils.LabelOptionsTuple;
import guiUtils.LabelTuple;
import guiUtils.LabelValueTuple;
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
	JPanel mainPanel;
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
	FractalVideo video;

	public VideoMaker(FractalVisualizer vis) {
		super("Video Maker");
		this.vis = vis;
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(9, 1, 10, 10));
		
		fps = new LabelValueTuple("Fps:", 30);
		duration = new LabelValueTuple("Duration:", 60);
		
		mainPanel.add(fps);
		mainPanel.add(duration);
		
		width = new LabelValueTuple("Width:", 1920);
		height = new LabelValueTuple("Height:", 1080);
		
		JTuple<LabelValueTuple, LabelValueTuple> dimPanel = new JTuple(width, height);
//		dimPanel.setLayout(new GridLayout(1, 2, 20, 20));
//		dimPanel.add(width);
//		dimPanel.add(height);
		mainPanel.add(dimPanel);
		
		shift = new LabelValueTuple("Shift:", 0);
		mainPanel.add(shift);
		
		visualizer = new GradientVisualizer(vis.getGradient());
		JPanel panel = new LabelTuple("Gradient: ", visualizer);
		mainPanel.add(panel);
		
		JButton b = new JButton("Choose Directory");
		b.addActionListener(a -> dir.setText(chooseDiretory()));
		dir = new JTextField("D:\\MandelbrotStuff\\video");
		JTuple dirTuple = new JTuple(b, dir);
		mainPanel.add(dirTuple);
		
		JLabel nameLabel = new JLabel("Name:");
		name = new JTextField("Mandelbrot");
		JTuple nameTuple = new JTuple(nameLabel, name);
		mainPanel.add(nameTuple);
		
		
		videoOptions = new LabelOptionsTuple("Video type:",  "mp4(ffmpeg)", "mp4", "gif", "save frames");
		JButton audioButton = new JButton("Add Audio File");
		audioButton.addActionListener(a -> {
			File audio = chooseAudio();
			audioPath = audio.getAbsolutePath();
			audioButton.setText(audio.getName());
		});
		JTuple tuple = new JTuple(videoOptions, audioButton);
		mainPanel.add(tuple);
		
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
			MultiGradient gradient = vis.getGradient();
			zoom.setColorGradient(p -> gradient.offseted((float)(p*offset)));
			switch(videoOption) {
				case "mp4(ffmpeg)": video = new FractalFFMPEG(zoom, fp, dur, videoPath, audioPath, progress);
				break;
				case "mp4": video =  new FractalMP4(zoom, fp, dur, videoPath, progress);
				break;
				case "gif": video = new FractalGIF(zoom, fp, dur, videoPath, true, progress);
				break;
				default: video = new FractalFrameSaver(zoom, fp, dur, videoPath, progress);
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
