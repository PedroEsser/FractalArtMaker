package video;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;

import logic.MandelbrotFrame;
import rangeUtils.Range;

public class MandelbrotZoomMP4 extends MandelbrotVideo{

	private final AWTSequenceEncoder encoder;
	
	public MandelbrotZoomMP4(Range<MandelbrotFrame> zoom, Range<Range<Color>> gradientRange, int fps, double duration, String filePath) throws IOException {
		super(zoom, gradientRange, fps, duration, filePath);
		SeekableByteChannel out = NIOUtils.writableFileChannel(filePath);
		encoder = new AWTSequenceEncoder(out, Rational.R(fps, 1));
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
