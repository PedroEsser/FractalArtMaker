package logic;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import colorGradients.ColorGradient;
import colorGradients.HSBGradient;
import gradient.Gradient;
import optimizations.FractalKernel;
import optimizations.MandelbrotKernel;
import utils.Rectangle;

public class FractalFrame {

	public static final FractalKernel DEFAULT_FRACTAL = new MandelbrotKernel();
	public static final int DEFAULT_MAX_ITERATIONS = 200;
	public static final ColorGradient DEFAULT_GRADIENT = new HSBGradient();
	private static final double DEFAULT_DELTA = 0.01;
	public static int BELONG_COLOR = 0;		// rgb for the color BLACK
	
	private final FractalKernel fractal;
	private final double delta;
	private final int maxIterations;
	private final Complex center;
	private float[] data;
	private final int width;
	
	public FractalFrame(Complex center, int width, int height, double delta, int maxIterations, FractalKernel fractal) {
		this.fractal = fractal;
		this.center = center;
		this.data = new float[width*height];
		this.width = width;
		this.delta = delta;
		this.maxIterations = maxIterations;
		fractal.setFrame(this);
	}
	
	public FractalFrame(Complex center, int width, int height, double delta, int maxIterations) {
		this(center, width, height, delta, maxIterations, DEFAULT_FRACTAL);
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
		return width;
	}
	
	public int getHeight() {
		return data.length / width;
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
	
	public FractalKernel getKernel() {
		return fractal;
	}
	
	public float[] getData() {
		return this.data;
	}
	
	public void setData(float[] data) {
		this.data = data;
	}
	
	public void set(Point p, float percent) {
		set(p.x, p.y, percent);
	}
	
	public void set(int x, int y, float percent) {
		data[y * width + x] = percent;
	}
	
	public float get(int i, int j) {
		return data[j * width + i];
	}
	
	public void copyData(FractalFrame frame, Rectangle r, int dx, int dy) {
		r.loop(p -> {
			set(p.x + dx, p.y + dy, frame.get(p.x,  p.y));
		});
	}
	
//	public void calculate(Rectangle r) {
//		r.loop(p -> calculatePoint(p));
//	}
	
	public void calculateAll() {
		//calculate(allPoints());
		try {
			fractal.executeAll();
		}catch(Exception e) {
		}
		
//		DeviceManager ma = new DeviceManager(fractal);
//		ma.start();
//		try {
//			ma.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
//	public void calculatePoint(Point p) {
//		Complex c = toComplex(p);
//		Iterator<Complex> it = fractal.getIteratorFor(c);
//		
//		int iterations = 0;
//		
//		do {
//			it.next();
//		}while(it.hasNext() && ++iterations < maxIterations);
//		
//		
//		if(iterations == maxIterations) {
//			set(p, maxIterations);
//			return;
//		}				
//
//// iterating 2 more times to add a smooth color transition
//		
//		it.next();
//		Complex lastIt = it.next();
//		
//		float iterationScore = (float)(iterations + 1 - Math.log(Math.log(lastIt.amplitudeSquared())) * LOG2_RECIPROCAL);
//		//percent = (float)iterations/maxIterations;
//		iterationScore =  iterationScore < 0 || Float.isNaN(iterationScore) ? 0 : iterationScore;
//		set(p, iterationScore);
//	}
	
	public double[][] getCoords() {
		double[][] coords = new double[2][];
		coords[0] = new double[width];
		coords[1] = new double[getHeight()];
		Complex topLeft = toComplex(new Point(0, 0));
		for(int i = 0 ; i < width ; i++) {
			coords[0][i] = topLeft.getRe() + i * delta;
		}
		
		for(int i = 0 ; i < coords[1].length ; i++) {
			coords[1][i] = topLeft.getIm() + i * delta;
		}
			
		return coords;
	}
	
	public Complex toComplex(Point p) {
		int dx = p.x - getWidth() / 2;
		int dy = p.y - getHeight() / 2;
		return center.getAdded(dx * delta, dy * delta);
	}
	
	public Rectangle allPoints() {
		return new Rectangle(0, 0, getWidth(), getHeight());
	}
	
	public BufferedImage toImage(Gradient<Color> gradient, float norm) {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		int h = getHeight();
		for(int i = 0 ; i < getWidth() ; i++) {
			for(int j = 0 ; j < h ; j++) {
				float it = get(i, j);
				int rgb = it == maxIterations ? BELONG_COLOR : Float.isNaN(it) ? gradient.valueAt(0).getRGB() : gradient.valueAt(it * norm).getRGB();
				img.setRGB(i, j, rgb);
			}
//			if(i%100 == 0)
//				System.out.println(i);
		}
		return img;
	}
	
	public BufferedImage toImage(Gradient<Color> gradient) {
		return this.toImage(gradient, 1f/maxIterations);
	}
	
	public BufferedImage toImage() {
		return this.toImage(DEFAULT_GRADIENT, 1f/maxIterations);
	}
	
	private void drawPixel(BufferedImage img, Point p, Gradient<Color> gradient, float norm) {
		float it = get(p.x, p.y);
		int rgb = it == maxIterations ? BELONG_COLOR : Float.isNaN(it) ? gradient.valueAt(0).getRGB() : gradient.valueAt(it * norm).getRGB();
		img.setRGB(p.x, p.y, rgb);
		//img.setRGB(p.x, p.y, gradient.valueAt(percent).getRGB());
	}
	
	public void drawPixels(BufferedImage img, Gradient<Color> gradient, Rectangle... areas) {
		float defaultNorm = 1f / maxIterations;
		for(Rectangle area : areas)
			area.loop(p -> drawPixel(img, p, gradient, defaultNorm));
	}
	
	public void drawPixels(BufferedImage img, Gradient<Color> gradient, float norm, Rectangle... areas) {
		for(Rectangle area : areas)
			area.loop(p -> drawPixel(img, p, gradient, norm));
	}
	
}
