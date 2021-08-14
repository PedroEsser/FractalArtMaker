package video;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;

import gradient.Constant;
import gradient.Gradient;
import logic.FractalFrame;

public class FractalZoomMP4 extends FractalVideo{

	private final AWTSequenceEncoder encoder;
	
	public FractalZoomMP4(Gradient<FractalFrame> zoom, Gradient<Gradient<Color>> gradientRange, int fps, double duration, String filePath) throws IOException {
		super(zoom, gradientRange, fps, duration, filePath);
		SeekableByteChannel out = NIOUtils.writableFileChannel(filePath);
		encoder = new AWTSequenceEncoder(out, Rational.R(fps, 1));
	}
	
	public FractalZoomMP4(Gradient<FractalFrame> zoom, int fps, double duration, String filePath) throws IOException {
		this(zoom, FractalVideo.DEFAULT_GRADIENT_RANGE, fps, duration, filePath);
	}
	
	public FractalZoomMP4(FractalFrame frame, int fps, double duration, String filePath) throws FileNotFoundException, IOException {
		this(new Constant<FractalFrame>(frame), fps, duration, filePath);
	}
	
	@Override
	public void encodeImage(BufferedImage img) {
		try {
			encoder.encodeImage(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void finished() {
		try {
			encoder.finish();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
