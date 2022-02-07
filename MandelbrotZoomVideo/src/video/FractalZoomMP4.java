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
	
	public FractalZoomMP4(Gradient<FractalFrame> zoom, int fps, double duration, String filePath) throws IOException {
		super(zoom, fps, duration, filePath);
		SeekableByteChannel out = NIOUtils.writableFileChannel(filePath);
		encoder = new AWTSequenceEncoder(out, Rational.R(fps, 1));
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
	
//	public static void test() {
//		Options options = new Options();
//		options.put("tune", "zerolatency");
//		options.put("preset", "ultrafast");
//				
//		Encoder encoder;
//		try {
//			encoder = new Encoder(CodecID.H264);
//			encoder.setPixelFormat(PixelFormat.BGR24);
//			encoder.setImageWidth(1280);
//			encoder.setImageHeight(720);
//			encoder.setGOPSize(25);
//			encoder.setBitrate(2000000);
//			encoder.setFramerate(25);
//			encoder.open(options);
//
//			BufferedImage image = new BufferedImage(NORM_PRIORITY, MIN_PRIORITY, MAX_PRIORITY);
//			VideoFrame frame = VideoFrame.create(image);
//
//			MediaPacket packet = encoder.encodeVideo(frame);
//			
////			... send packet over a network, write it to a file or whatever
//
//			encoder.close();
//		} catch (JavaAVException e) {
//			e.printStackTrace();
//		}
//		
//	}

}
