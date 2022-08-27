package video;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JProgressBar;

import fractal.FractalFrame;
import gradient.Gradient;
import utils.ImageUtils;

public class FractalFrameSaver extends FractalVideo {

	protected final String folderPath;

	public FractalFrameSaver(Gradient<FractalFrame> zoom, int fps, double duration, String filePath, JProgressBar progress) {
		super(zoom, fps, duration, filePath, progress);
		this.folderPath = filePath;

		try {
			Path p = Paths.get(folderPath);
			Files.createDirectories(p);
		} catch (IOException e) {
			System.err.println("Failed to create directory!" + e.getMessage());
		}
	}

	protected String currentFramePath() {
		return folderPath + "/Frame" + String.format("%05d", currentFrame) + ".png";
	}

	@Override
	public void encodeImage(BufferedImage img) {
		ImageUtils.saveImage(img, currentFramePath());
	}

	@Override
	public void finished() {
		progress.setString("Finished!");
		progress.setValue(totalFrames);
	}

}
