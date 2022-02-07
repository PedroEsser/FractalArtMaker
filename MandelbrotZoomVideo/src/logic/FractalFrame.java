package logic;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

import gpuColorGradients.ColorGradient;
import gpuColorGradients.HSBGradient;
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
	private final ColorGradient gradient;
	private final double delta;
	private final int maxIterations;
	private final Complex center;
	private byte[] data;
	private final int width, height;
	private float norm;
	
	public FractalFrame(Complex center, int width, int height, double delta, int maxIterations, ColorGradient gradient, FractalKernel fractal, float norm) {
		this.fractal = fractal;
		this.gradient = gradient;
		this.center = center;
		this.data = new byte[width * height * 3];
		this.width = width;
		this.height = height;
		this.delta = delta;
		this.maxIterations = maxIterations;
		this.norm = norm;
		fractal.setFrame(this);
	}
	
	public FractalFrame(Complex center, int width, int height, double delta, int maxIterations, ColorGradient gradient) {
		this(center, width, height, delta, maxIterations, gradient, DEFAULT_FRACTAL, 1f/maxIterations);
	}
	
	public FractalFrame(Complex center, int width, int height, double delta, int maxIterations) {
		this(center, width, height, delta, maxIterations, DEFAULT_GRADIENT);
	}
	
	public FractalFrame(Complex center, int width, int height, double delta) {
		this(center, width, height, delta, DEFAULT_MAX_ITERATIONS, DEFAULT_GRADIENT);
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
		return height;
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
	
	public ColorGradient getGradient() {
		return gradient;
	}
	
	public FractalKernel getKernel() {
		return fractal;
	}
	
	public float getNorm() {
		return norm;
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
//	public void copyData(FractalFrame frame, Rectangle r, int dx, int dy) {
//		r.loop(p -> {
//			set(p.x + dx, p.y + dy, frame.get(p.x,  p.y));
//		});
//	}
	
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
	
	public BufferedImage toImage() {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		DataBuffer buffer = new DataBufferByte(data, data.length);
		SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, width, height, 3, width*3, new int[]{2,1,0});
		Raster raster = Raster.createRaster(sampleModel, buffer, null);
		img.setData(raster);
		
		return img;
	}
	
//	public BufferedImage toImage(Gradient<Color> gradient, float norm) {
//		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//		int h = getHeight();
//		for(int i = 0 ; i < getWidth() ; i++) {
//			for(int j = 0 ; j < h ; j++) {
//				float it = get(i, j);
//				int rgb = it == maxIterations ? BELONG_COLOR : Float.isNaN(it) ? gradient.valueAt(0).getRGB() : gradient.valueAt(it * norm).getRGB();
//				img.setRGB(i, j, rgb);
//			}
////			if(i%100 == 0)
////				System.out.println(i);
//		}
//		return img;
//	}
//	
//	public BufferedImage toImage(Gradient<Color> gradient) {
//		return this.toImage(gradient, 1f/maxIterations);
//	}
//	
//	public BufferedImage toImage() {
//		return this.toImage(DEFAULT_GRADIENT, 1f/maxIterations);
//	}
//	
//	private void drawPixel(BufferedImage img, Point p, Gradient<Color> gradient, float norm) {
//		float it = get(p.x, p.y);
//		int rgb = it == maxIterations ? BELONG_COLOR : Float.isNaN(it) ? gradient.valueAt(0).getRGB() : gradient.valueAt(it * norm).getRGB();
//		img.setRGB(p.x, p.y, rgb);
//		//img.setRGB(p.x, p.y, gradient.valueAt(percent).getRGB());
//	}
//	
//	public void drawPixels(BufferedImage img, Gradient<Color> gradient, Rectangle... areas) {
//		float defaultNorm = 1f / maxIterations;
//		for(Rectangle area : areas)
//			area.loop(p -> drawPixel(img, p, gradient, defaultNorm));
//	}
//	
//	public void drawPixels(BufferedImage img, Gradient<Color> gradient, float norm, Rectangle... areas) {
//		for(Rectangle area : areas)
//			area.loop(p -> drawPixel(img, p, gradient, norm));
//	}
	
}
