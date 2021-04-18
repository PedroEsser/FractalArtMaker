package logic;

import java.awt.Point;
import java.awt.image.BufferedImage;

import gradients.Gradient;
import gradients.HSBGradient;
import utils.Rectangle;

public class MandelbrotFrame {

	public static final int DEFAULT_MAX_ITERATION = 200;
	private static final double ESCAPE_RADIUS_SQUARED = 10;
	public static final Gradient DEFAULT_GRADIENT = new HSBGradient();
	private static final double DEFAULT_DELTA = 0.005;
	private static final float LOG2_RECIPROCAL = (float)(1 / Math.log(2));
	public static int BELONG_COLOR = 0;		// rgb for the color BLACK
	private double delta;
	private Complex center;
	private float[][] data;
	
	public MandelbrotFrame(Complex center, int width, int height, double delta) {
		this.center = center;
		this.data = new float[width][height];
		this.delta = delta;
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
	
	public void calculate(Rectangle r, int maxIterations) {
		r.loop(p -> calculatePoint(p, maxIterations));
	}
	
	public void calculateAll(int maxIterations) {
		calculate(allPoints(), maxIterations);
	}
	
	public void calculatePoint(Point p, int maxIterations) {
		Complex c = toComplex(p);
		Complex z = new Complex();
		int iterations = 0;
		while(true) {
			z.square();
			z.add(c);
			if(z.amplitudeSquared() > ESCAPE_RADIUS_SQUARED || ++iterations == maxIterations)
				break;
		}
		if(iterations == maxIterations) {
			data[p.x][p.y] = 1;
			return;
		}
		z.square();
		z.add(c);
		z.square();
		z.add(c);
		float percent = (iterations + 1 - (float)Math.log(Math.log(z.amplitudeSquared())) * LOG2_RECIPROCAL) / maxIterations;
		data[p.x][p.y] = percent < 0 ? 0 : percent > 1 ? 1 : percent;
	}
	
	public Complex toComplex(Point p) {
		int dx = p.x - (getWidth() >> 1);		// x - width / 2
		int dy = p.y - (getHeight() >> 1);		// y - height / 2
		return center.getAdded(dx * delta, dy * delta);
	}
	
	public Rectangle allPoints() {
		return new Rectangle(0, 0, getWidth(), getHeight());
	}
	
	public BufferedImage toImage() {
		return this.toImage(DEFAULT_GRADIENT);
	}
	
	public BufferedImage toImage(Gradient gradient) {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		allPoints().loop(p -> {
			float percent = data[p.x][p.y];
			img.setRGB(p.x, p.y, percent == 1 ? BELONG_COLOR : gradient.valueAt(percent).getRGB());
		});
		return img;
	}
	
}
