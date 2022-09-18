package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.jcodec.common.model.Rect;

public class ImageUtils {

	public static BufferedImage getImageFromPath(String path) {
		try {
			return ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveImage(BufferedImage img, String path) {
		try {
			ImageIO.write(img, "png", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void swapColors(BufferedImage img, int rgb1, int rgb2) {
		int w = img.getWidth();
		int h = img.getHeight();
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++) 
				if(img.getRGB(x, y) == rgb1)
					img.setRGB(x, y, rgb2);
	}

	public static BufferedImage getScaledImage(BufferedImage img, int scale) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage newImg = new BufferedImage(w * scale, h * scale, BufferedImage.TYPE_3BYTE_BGR);
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++) {
				int rgb = img.getRGB(x, y);
				int xScale = x * scale;
				int yScale = y * scale;
				for (int i = 0; i < scale; i++)
					for (int j = 0; j < scale; j++)
						newImg.setRGB(xScale + i, yScale + j, rgb);
			}
		return newImg;
	}

	public static BufferedImage getScaledImage2(BufferedImage img, int scale) {
		int w = img.getWidth() * scale;
		int h = img.getHeight() * scale;
		BufferedImage newImg = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++) {
				int rgb = img.getRGB(x/scale, y/scale);
				newImg.setRGB(x, y, rgb);
			}
		return newImg;
	}

	public static BufferedImage getCroppedImage(BufferedImage img, Rectangle r) {
		BufferedImage croppedImage = new BufferedImage(r.width, r.height, BufferedImage.TYPE_3BYTE_BGR);
		int limitX = r.x + r.width;
		int limitY = r.y + r.height;
		for (int i = r.x; i < limitX; i++)
			for (int j = r.y; j < limitY; j++)
				croppedImage.setRGB(i - r.x, j - r.y, img.getRGB(i, j));
		return croppedImage;
	}
	
	public static BufferedImage copyCroppedImage(BufferedImage img, BufferedImage crop, Rectangle r) {
		assert crop.getWidth() == r.width && crop.getHeight() == r.height : "Dimensions should match";
		for (int i = r.x; i < r.x + r.width; i++)
			for (int j = r.y; j < r.y + r.height; j++)
				img.setRGB(i, j, crop.getRGB(i - r.x, j - r.y));
		return img;
	}
	
	public static BufferedImage getCroppedAndResizedImage(BufferedImage img, double scale) {
		BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		if(scale > 1) {
			double width = (int)(img.getWidth() / scale);
			double height = (int)(img.getHeight() / scale);
			double x = (img.getWidth() - width)/2;
			double y = (img.getHeight() - height)/2;
			for (int i = 0 ; i < img.getWidth() ; i++)
				for (int j = 0 ; j < img.getHeight() ; j++) {
					int rgb = img.getRGB((int)(x + i/scale), (int)(y + j/scale));
					result.setRGB(i, j, rgb);
				}
		}else {
			double width = (int)(img.getWidth() * scale);
			double height = (int)(img.getHeight() * scale);
			double x = (img.getWidth() - width)/2;
			double y = (img.getHeight() - height)/2;
			for (int i = 0 ; i < width ; i++)
				for (int j = 0 ; j < height ; j++) {
					int rgb = img.getRGB((int)(i/scale), (int)(j/scale));
					result.setRGB((int)x + i, (int)y + j, rgb);
				}
		}
		return result;
	}
	
	public static Color averageColor(BufferedImage img, Rectangle rect) {
		int r = 0, g = 0, b = 0;
		int exceptions = 0;
		for (int i = rect.x; i < rect.x + rect.width; i++)
			for (int j = rect.y; j < rect.y + rect.height; j++) {
				try {
					Color c = new Color(img.getRGB(i, j));
					r += c.getRed();
					g += c.getGreen();
					b += c.getBlue();
				}catch (ArrayIndexOutOfBoundsException e) {
					exceptions ++;
				}
			}
		int n = rect.width * rect.height - exceptions;
		r /= n;
		g /= n;
		b /= n;
		return new Color(r, g, b);
	}
	
	public static void loopRectangle(Rectangle rect, Consumer<Point> function) {
		for (int i = rect.x; i <= rect.x + rect.width; i++)
			for (int j = rect.y; j <= rect.y + rect.height; j++)
				function.accept(new Point(i, j));
	}
	
	public static void processImage(BufferedImage img, Function<Color, Color> process) {
		for(int i = 0 ; i < img.getWidth() ; i++)
			for(int j = 0 ; j < img.getHeight() ; j++){
				int rgb = process.apply(new Color(img.getRGB(i, j))).getRGB();
				img.setRGB(i, j, rgb);
			}
	}
	
	private static FileFilter getFileFilter(String name, String extension) {
		FileFilter ff = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String s = pathname.getName();
				if(s.startsWith(name) && s.endsWith(extension)) 
					try {
						System.out.println(name + " _ " + extension);
						Integer.parseInt(s.substring(name.length(), s.length() - extension.length()));
						return true;
					}catch(NumberFormatException e) {
						return false;
					}
				return false;
			}
		};
		return ff;
	}

	public static String getNextFileName(String directory, String name, String extension) {
		int largestIndex = -1;
		
		for (File f : new File(directory).listFiles()) {
			String fileName = f.getName();
			if(fileName.startsWith(name)) {
				String id = fileName.substring(name.length(), fileName.length() - extension.length());
				if(id.isEmpty() && largestIndex == -1)
					largestIndex = 0;
				try {
					int i = Integer.parseInt(id);
					if (largestIndex < i)
						largestIndex = i;
				}catch(NumberFormatException e) {
				}
			}
		}
		return directory + "\\" + name + (largestIndex == -1 ? "" : (largestIndex + 1)) + extension;
	}
	
	public static String getNextFileName(String fullPath) {
		File f = new File(fullPath);
		String name = f.getName();
		int extensionIndex = name.lastIndexOf('.');
		if(extensionIndex != -1)
			return getNextFileName(f.getParent(), name.substring(0, extensionIndex), name.substring(extensionIndex, name.length()));
		return getNextFileName(f.getParent(), name, "");
	}

}
