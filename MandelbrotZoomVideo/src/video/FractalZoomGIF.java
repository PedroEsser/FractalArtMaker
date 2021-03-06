package video;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import fractal.FractalFrame;
import gpuColorGradients.ColorGradient;
import gradient.Constant;
import gradient.Gradient;
import utils.GifSequenceWriter;

public class FractalZoomGIF extends FractalVideo {
	
	private final GifSequenceWriter gif;
	private final ImageOutputStream out;
	
	public FractalZoomGIF(Gradient<FractalFrame> zoom, int fps, double duration, String filePath, boolean loop) throws FileNotFoundException, IOException {
		super(zoom, fps, duration, filePath);
		this.out = new FileImageOutputStream(new File(filePath));
		this.gif = new GifSequenceWriter(out, BufferedImage.TYPE_3BYTE_BGR, 1000 / fps, loop);
	}
	
	public FractalZoomGIF(Gradient<FractalFrame> zoom, Gradient<ColorGradient> gradientRange, int fps, double duration, String filePath) throws FileNotFoundException, IOException {
		this(zoom, fps, duration, filePath, false);
	}
	
	public FractalZoomGIF(Gradient<FractalFrame> zoom, int fps, double duration, String filePath) throws FileNotFoundException, IOException {
		this(zoom, fps, duration, filePath, false);
	}
	
	public FractalZoomGIF(FractalFrame frame, int fps, double duration, String filePath) throws FileNotFoundException, IOException {
		this(new Constant<FractalFrame>(frame), fps, duration, filePath, true);
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
