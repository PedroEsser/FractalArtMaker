package logic;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import gradients.Gradient;
import gradients.HSBGradient;
import rangeUtils.Range;
import utils.Rectangle;

public class MandelbrotFrame {

	public static final int DEFAULT_MAX_ITERATIONS = 200;
	private static final double ESCAPE_RADIUS_SQUARED = 4;
	public static final Gradient DEFAULT_GRADIENT = new HSBGradient();
	private static final double DEFAULT_DELTA = 0.01;
	private static final float LOG2_RECIPROCAL = (float)(1 / Math.log(2));
	public static int BELONG_COLOR = 0;		// rgb for the color BLACK
	
	private final double delta;
	private final int maxIterations;
	private final Complex center;
	private float[][] data;
	
	public MandelbrotFrame(Complex center, int width, int height, double delta, int maxIterations) {
		this.center = center;
		this.data = new float[width][height];
		this.delta = delta;
		this.maxIterations = maxIterations;
	}
	
	public MandelbrotFrame(Complex center, int width, int height, double delta) {
		this(center, width, height, delta, DEFAULT_MAX_ITERATIONS);
	}
	
	public MandelbrotFrame(int width, int height, double delta) {
		this(new Complex(), width, height, delta);
	}
	
	public MandelbrotFrame(Complex center, int width, int height) {
		this(center, width, height, DEFAULT_DELTA);
	}
	
	public MandelbrotFrame(int width, int height) {
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
	
	public void copyData(MandelbrotFrame frame, Rectangle r, int dx, int dy) {
//		float[][] copy = r.slice(frame.data);
//		
//		r.loop(p -> {
//			set(p.x + dx, p.y + dy, copy[p.x - r.x1][p.y - r.y1]);
//		});
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
		Complex z = new Complex();
		int iterations = 0;
		while(true) {
			z.square();
			z.add(c);
			if(z.amplitudeSquared() > ESCAPE_RADIUS_SQUARED || ++iterations >= maxIterations)
				break;
		}
		if(iterations == maxIterations) {
			data[p.x][p.y] = 1;
			return;
		}				// adding smooth transition
		z.square();
		z.add(c);
		z.square();
		z.add(c);
		float percent = (float)(iterations + 1 - Math.log(Math.log(z.amplitudeSquared())) * LOG2_RECIPROCAL) / maxIterations;
		//percent = (float)iterations/maxIterations;
		percent =  percent < 0 || Float.isNaN(percent) ? 0 : percent > 1 ? 1 : percent;
		data[p.x][p.y] = percent;
	}
	
	public Complex toComplex(Point p) {
		int dx = p.x - (getWidth() >> 1);		// x - width / 2
		int dy = p.y - (getHeight() >> 1);		// y - height / 2
		return center.getAdded(dx * delta, dy * delta);
	}
	
	public Rectangle allPoints() {
		return new Rectangle(0, 0, getWidth(), getHeight());
	}
	
	public BufferedImage toImage(Range<Color> gradient) {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		allPoints().loop(p -> drawPixel(img, p, gradient));
		return img;
	}
	
	public BufferedImage toImage() {
		return this.toImage(DEFAULT_GRADIENT);
	}
	
	private void drawPixel(BufferedImage img, Point p, Range<Color> gradient) {
		float percent = data[p.x][p.y];
		if(gradient == null)
			System.out.println("geh");
		img.setRGB(p.x, p.y, percent == 1 ? BELONG_COLOR : gradient.valueAt(percent).getRGB());
	}
	
	public void drawPixels(BufferedImage img, Range<Color> gradient, Rectangle... areas) {
		for(Rectangle area : areas)
			area.loop(p -> drawPixel(img, p, gradient));
	}
	
}
