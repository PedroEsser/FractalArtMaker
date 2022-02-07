package video;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import gradient.Gradient;
import logic.FractalFrame;
import utils.ImageUtils;

public class FractalZoomFrameSaver extends FractalVideo{

	private final String folderPath;
	
	public FractalZoomFrameSaver(Gradient<FractalFrame> zoom, int fps, double duration, String filePath) {
		super(zoom, fps, duration, filePath);
		this.folderPath = filePath;
		
		try {
			Path p = Paths.get(folderPath);
		    Files.createDirectories(p);
		  } catch (IOException e) {
		    System.err.println("Failed to create directory!" + e.getMessage());
		  }
	}

	@Override
	public void encodeImage(BufferedImage img) {
		ImageUtils.saveImage(img, folderPath + "/Frame" + currentFrame + ".png");
	}

	@Override
	public void finished() {
	}

}
