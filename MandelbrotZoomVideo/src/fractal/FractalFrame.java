package fractal;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

import fractalKernels.FractalKernel;
import fractalKernels.MandelbrotKernel;
import fractals_deprecated.Complex;
import gpuColorGradients.ColorGradient;
import gpuColorGradients.HSBGradient;
import gpuColorGradients.MultiGradient;
import utils.Rectangle;

public class FractalFrame {

	public static final FractalKernel DEFAULT_FRACTAL = new MandelbrotKernel();
	public static final int DEFAULT_MAX_ITERATIONS = 200;
	public static final MultiGradient DEFAULT_GRADIENT = new MultiGradient(new HSBGradient());
	private static final double DEFAULT_DELTA = 0.01;
	public static int BELONG_COLOR = 0;		// rgb for the color BLACK
	
	private final FractalKernel fractal;
	private final MultiGradient gradient;
	private final double delta;
	private final int maxIterations;
	private final Complex center;
	private final byte[] data;
	private final int width, height;
	private float norm;
	
	public FractalFrame(Complex center, int width, int height, double delta, int maxIterations, MultiGradient gradient, FractalKernel fractal, float norm, byte[] data) {
		this.fractal = fractal;
		this.gradient = gradient;
		this.center = center;
		this.data = data;
		this.width = width;
		this.height = height;
		this.delta = delta;
		this.maxIterations = maxIterations;
		this.norm = norm;
		fractal.setFrame(this);
	}
	
	public FractalFrame(Complex center, int width, int height, double delta, int maxIterations, MultiGradient gradient, FractalKernel fractal, float norm) {
		this(center, width, height, delta, maxIterations, gradient, fractal, norm, new byte[width * height * 3]);
	}
	
	public FractalFrame(Complex center, int width, int height, double delta, int maxIterations, MultiGradient gradient) {
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
	
	public void calculateAll() {
		try {
			fractal.executeAll();
		}catch(Exception e) {
		}
	}
	
	public int realToX(double real) {
		return (int)((real - center.getRe()) / delta + width/2);
	}
	
	public int imagToY(double imag) {
		return (int)((imag - center.getIm()) / delta + height/2);
	}

	public Point toPoint(double[] complex) {
		return new Point(realToX(complex[0]), imagToY(complex[1]));
	}
	
	public Point toPoint(Complex c) {
		return new Point(realToX(c.getRe()), imagToY(c.getIm()));
	}
	
	public double xToReal(int x) {
		return (x - getWidth() / 2) * delta + center.getRe();
	}
	
	public double yToImag(int y) {
		return (y - getHeight() / 2) * delta + center.getIm();
	}
	
	public double[] complexAt(int x, int y) {
		return new double[] {xToReal(x), yToImag(y)};
	}
	
	public Complex toComplex(int x, int y) {
		return new Complex(complexAt(x, y));
	}
	
	public Complex toComplex(Point p) {
		return toComplex(p.x, p.y);
	}
	
	public BufferedImage toImage() {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		DataBuffer buffer = new DataBufferByte(data, data.length);
		SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, width, height, 3, width*3, new int[]{2,1,0});
		Raster raster = Raster.createRaster(sampleModel, buffer, null);
		img.setData(raster);
		
		return img;
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
	
	public MultiGradient getGradient() {
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
	
	public FractalFrame clone(int newWidth, int newHeight) {
		return new FractalFrame(center, newWidth, newHeight, delta, maxIterations, gradient, fractal, norm);
	}

}
