
package video;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JProgressBar;

import fractal.FractalFrame;
import gradient.Gradient;
import utils.ImageUtils;

public class FractalFFMPEG extends FractalFrameSaver {

	private final String audioFilePath;
	
	public FractalFFMPEG(Gradient<FractalFrame> zoom, int fps, double duration, String filePath, String audioFilePath, JProgressBar progress) {
		super(zoom, fps, duration, filePath, progress);
		this.audioFilePath = audioFilePath;
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
	
	private void deleteTempFolder() {
		File tempFolder = new File(folderPath);
		for(File f : tempFolder.listFiles()) 
			f.delete();
		
		tempFolder.delete();
	}

	@Override
	public void finished() {
		String audioCommand = audioFilePath == null ? "" : " -i \"" + audioFilePath + "\" -shortest";
		String command = "ffmpeg -y -framerate " + fps + " -i " + folderPath
				+ "\\Frame%05d.png" + audioCommand + " -c:v libx264 -pix_fmt yuv420p -crf 17 " + folderPath + ".mp4";
		
		//System.out.println("Command: " + command);
		try {
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
			builder.redirectErrorStream(true);
			builder.redirectOutput(Redirect.PIPE);

			Process process = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("frame=")) {
					int currentFrame = Integer.parseInt(line.substring(6, line.indexOf("fps")).trim());
					progress.setValue(currentFrame);
					progress.setString("Encoding frame " + currentFrame + " of " + totalFrames);
				}
				//System.out.println(line);
			}
			process.waitFor();
			reader.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		deleteTempFolder();
		super.finished();
	}

}
