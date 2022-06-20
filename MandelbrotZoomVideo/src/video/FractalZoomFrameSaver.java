package video;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import fractal.FractalFrame;
import gradient.Gradient;
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
	
	private String currentFrameName() {
		return "/Frame" + String.format("%05d", currentFrame) + ".png";
	}
	
	@Override
	public void encodeImage(BufferedImage img) {
		ImageUtils.saveImage(img, folderPath + currentFrameName());
	}

	@Override
	public void finished() {
//		ProcessBuilder pb = new ProcessBuilder();
//		pb.command("cmd.exe", "/c", "ffmpeg -framerate 30 -i " + folderPath + "/Frame%05d.png -c:v libx264 -pix_fmt yuv420p -crf 17 " + folderPath + "/out.mp4");//
//		System.out.println("badabim");
//        try {
//            Process process = pb.start();
//            
//            int exitCode = process.waitFor();
//            System.out.println("Exited with error code : " + exitCode);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
	}

}
