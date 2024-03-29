package video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JProgressBar;

import fractal.FractalFrame;
import gradient.Gradient;
import utils.GifSequenceWriter;

public class FractalGIF extends FractalVideo {
	
	private final GifSequenceWriter gif;
	private final ImageOutputStream out;
	
	public FractalGIF(Gradient<FractalFrame> zoom, int fps, double duration, String filePath, boolean loop, JProgressBar progress) throws FileNotFoundException, IOException {
		super(zoom, fps, duration, filePath, progress);
		this.out = new FileImageOutputStream(new File(filePath));
		this.gif = new GifSequenceWriter(out, BufferedImage.TYPE_3BYTE_BGR, 1000 / fps, loop);
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
