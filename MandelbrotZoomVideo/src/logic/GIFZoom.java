package logic;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import gradients.Gradient;
import gradients.HSBGradient;
import rangeUtils.NumericRange;
import rangeUtils.Range;
import utils.GifSequenceWriter;

public class GIFZoom extends MandelbrotVideo {
	
	private static final Gradient DEFAULT_GRADIENT = new HSBGradient();
	private final GifSequenceWriter gif;
	private final ImageOutputStream out;
	private Range<Color> gradient;
	private int currentFrame = 0;
	
	private double offset = 0;
	
	public GIFZoom(Range<MandelbrotFrame> zoom, int fps, double duration, Gradient gradient, String filePath) throws FileNotFoundException, IOException {
		super(zoom, fps, duration, filePath);
		this.out = new FileImageOutputStream(new File(filePath));
		this.gif = new GifSequenceWriter(out, BufferedImage.TYPE_3BYTE_BGR, 1000 / fps, false);
		this.gradient = gradient;
	}
	
	public GIFZoom(Range<MandelbrotFrame> zoom, int fps, double duration, String filePath) throws FileNotFoundException, IOException {
		this(zoom, MandelbrotVideo.DEFAULT_FPS, duration, DEFAULT_GRADIENT, filePath);
	}
	
	public GIFZoom(Range<MandelbrotFrame> zoom, int fps, String filePath) throws FileNotFoundException, IOException {
		this(zoom, fps, MandelbrotVideo.DEFAULT_DURATION, filePath);
	}
	
	public GIFZoom(Range<MandelbrotFrame> zoom, String filePath) throws FileNotFoundException, IOException {
		this(zoom, MandelbrotVideo.DEFAULT_FPS, filePath);
	}
	
	public void setGradient(Range<Color> g) {
		this.gradient = g;
	}

	@Override
	public void encodeFrame(MandelbrotFrame frame) {
		try {
			gif.writeToSequence(frame.toImage(gradient.offset(offset)));
			offset += 0.001;
			if(++currentFrame % 10 == 0)
				System.out.println("Frame " + currentFrame + " of " + totalFrames + " saved.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void finished() {
		try {
			gif.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
