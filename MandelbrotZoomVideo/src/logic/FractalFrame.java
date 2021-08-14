package logic;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;

import fractals.Fractal;
import fractals.MandelbrotSet;
import gradient.Gradient;
import gradients.ColorGradient;
import gradients.HSBGradient;
import utils.Rectangle;

public class FractalFrame {

	public static final Fractal DEFAULT_FRACTAL = new MandelbrotSet();
	public static final int DEFAULT_MAX_ITERATIONS = 200;
	public static final ColorGradient DEFAULT_GRADIENT = new HSBGradient();
	private static final double DEFAULT_DELTA = 0.01;
	private static final float LOG2_RECIPROCAL = (float)(1 / Math.log(2));
	public static int BELONG_COLOR = 0;		// rgb for the color BLACK
	
	private final Fractal fractal;
	private final double delta;
	private final int maxIterations;
	private final Complex center;
	private final float[][] data;
	
	public FractalFrame(Complex center, int width, int height, double delta, int maxIterations, Fractal fractal) {
		this.fractal = fractal;
		this.center = center;
		this.data = new float[width][height];
		this.delta = delta;
		this.maxIterations = maxIterations;
	}
	
	public FractalFrame(Complex center, int width, int height, double delta) {
		this(center, width, height, delta, DEFAULT_MAX_ITERATIONS, DEFAULT_FRACTAL);
	}
	
	public FractalFrame(int width, int height, double delta) {
		this(new Complex(), width, height, delta);
	}
	
	public FractalFrame(Complex center, int width, int height) {
		this(center, width, height, DEFAULT_DELTA);
	}
	
	public FractalFrame(int width, int height) {
		this(width, height, DEFAULT_DELTA);
	}
	
	public int getWidth() {
		return data.length;
	}
	
	public int getHeight() {
		return data[0].length;
	}
	
	public double getDelta() {
		return delta;
	}
	
	public Complex getCenter() {
		return center;
	}
	
	public int getMaxIterations() {
		return maxIterations;
	}
	
	public void set(Point p, float percent) {
		data[p.x][p.y] = percent;
	}
	
	public void set(int x, int y, float percent) {
		data[x][y] = percent;
	}
	
	public float get(int i, int j) {
		return data[i][j];
	}
	
	public void copyData(FractalFrame frame, Rectangle r, int dx, int dy) {
		r.loop(p -> {
			set(p.x + dx, p.y + dy, frame.data[p.x][p.y]);
		});
	}
	
	public void resize(int width, int height) {
		float[][] newData = new float[width][height];
		int w = Math.min(width, this.getWidth());
		int h = Math.min(height, this.getHeight());
		for(int i = 0 ; i < w ; i++)
			for(int j = 0 ; j < h ; j++)
				newData[i][j] = data[i][j];
		allPoints().loop(p -> {
			if(data[p.x][p.y] == 0) {
				calculatePoint(p);
			}
		});
	}
	
	public void calculate(Rectangle r) {
		r.loop(p -> calculatePoint(p));
	}
	
	public void calculateAll() {
		calculate(allPoints());
	}
	
	public void calculatePoint(Point p) {
		Complex c = toComplex(p);
		Iterator<Complex> it = fractal.getIteratorFor(c);
		
		int iterations = 0;
		
		do {
			it.next();
		}while(it.hasNext() && ++iterations < maxIterations);
		
		
		if(iterations == maxIterations) {
			data[p.x][p.y] = 1;
			return;
		}				

// iterating 2 more times to add a smooth color transition
		
		it.next();
		Complex lastIt = it.next();
		
		float percent = (float)(iterations + 1 - Math.log(Math.log(lastIt.amplitudeSquared())) * LOG2_RECIPROCAL) / maxIterations;
		//percent = (float)iterations/maxIterations;
		percent =  percent < 0 || Float.isNaN(percent) ? 0 : percent > 1 ? 1 : percent;
		data[p.x][p.y] = percent;
	}
	
	public Complex toComplex(Point p) {
		int dx = p.x - getWidth() / 2;
		int dy = p.y - getHeight() / 2;
		return center.getAdded(dx * delta, dy * delta);
	}
	
	public Rectangle allPoints() {
		return new Rectangle(0, 0, getWidth(), getHeight());
	}
	
	public BufferedImage toImage(Gradient<Color> gradient) {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		allPoints().loop(p -> drawPixel(img, p, gradient));
		return img;
	}
	
	public BufferedImage toImage() {
		return this.toImage(DEFAULT_GRADIENT);
	}
	
	private void drawPixel(BufferedImage img, Point p, Gradient<Color> gradient) {
		float percent = data[p.x][p.y];
		img.setRGB(p.x, p.y, percent == 1 ? BELONG_COLOR : gradient.valueAt(percent).getRGB());
		//img.setRGB(p.x, p.y, gradient.valueAt(percent).getRGB());
	}
	
	public void drawPixels(BufferedImage img, Gradient<Color> gradient, Rectangle... areas) {
		for(Rectangle area : areas)
			area.loop(p -> drawPixel(img, p, gradient));
	}
	
}
