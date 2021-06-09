package video;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import gradients.Gradient;
import gradients.HSBGradient;
import logic.MandelbrotFrame;
import rangeUtils.Constant;
import rangeUtils.NumericRange;
import rangeUtils.Range;
import utils.GifSequenceWriter;

public class MandelbrotZoomGIF extends MandelbrotVideo {
	
	private final GifSequenceWriter gif;
	private final ImageOutputStream out;
	
	public MandelbrotZoomGIF(Range<MandelbrotFrame> zoom, Range<Range<Color>> gradientRange, int fps, double duration, String filePath, boolean loop) throws FileNotFoundException, IOException {
		super(zoom, gradientRange, fps, duration, filePath);
		this.out = new FileImageOutputStream(new File(filePath));
		this.gif = new GifSequenceWriter(out, BufferedImage.TYPE_3BYTE_BGR, 1000 / fps, loop);
	}
	
	public MandelbrotZoomGIF(Range<MandelbrotFrame> zoom, Range<Range<Color>> gradientRange, int fps, double duration, String filePath) throws FileNotFoundException, IOException {
		this(zoom, gradientRange, fps, duration, filePath, true);
	}
	
	public MandelbrotZoomGIF(Range<MandelbrotFrame> zoom, int fps, double duration, String filePath) throws FileNotFoundException, IOException {
		this(zoom, MandelbrotVideo.DEFAULT_GRADIENT_RANGE, fps, duration, filePath, true);
	}
	
	public MandelbrotZoomGIF(MandelbrotFrame frame, int fps, double duration, String filePath) throws FileNotFoundException, IOException {
		this(new Constant<MandelbrotFrame>(frame), MandelbrotVideo.DEFAULT_GRADIENT_RANGE, fps, duration, filePath, true);
	}

	@Override
	public void encodeImage(BufferedImage img) {
		try {
			gif.writeToSequence(img);
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
