package video;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JProgressBar;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;

import fractal.FractalFrame;

//import org.jcodec.api.SequenceEncoder;
//import org.jcodec.api.awt.AWTSequenceEncoder;
//import org.jcodec.codecs.h264.H264Encoder;
//import org.jcodec.codecs.pngawt.PNGEncoder;
//import org.jcodec.common.Codec;
//import org.jcodec.common.Format;
//import org.jcodec.common.io.NIOUtils;
//import org.jcodec.common.io.SeekableByteChannel;
//import org.jcodec.common.model.Picture;
//import org.jcodec.common.model.Rational;
//import org.jcodec.scale.AWTUtil;

import gradient.Constant;
import gradient.Gradient;

public class FractalMP4 extends FractalVideo{

	private final AWTSequenceEncoder encoder;
	
	public FractalMP4(Gradient<FractalFrame> zoom, int fps, double duration, String filePath, JProgressBar progress) throws IOException {
		super(zoom, fps, duration, filePath, progress);
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
