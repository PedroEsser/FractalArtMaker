package fractalKernels;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import gpuColorGradients.GradientUtils;
import gpuColorGradients.MultiGradient;
import static gpuColorGradients.GradientUtils.*;
import utils.ImageUtils;

public class ImageBasedMandelbrotKernel extends MandelbrotKernel{

	private double lightAngle;
	private double h;
	private double imgX, imgY, imgScaleX, imgScaleY, imgRotation;
	
	private byte[] imgData;
	private int imgWidth, imgHeight;
	
	public ImageBasedMandelbrotKernel() {
		this("C:\\Users\\pedro\\Desktop/fotoFenix.jpg");//"C:\\Users\\pedro\\Desktop/wednesdayFrog.png"
	}
	
	public ImageBasedMandelbrotKernel(String imgPath) {
		super();
		addParameter("angle", 0.5, 1f/64);
		addParameter("h", 1.5, 1f/64);
		addParameter("Image x", -1, 1f/16);
		addParameter("Image y", -1, 1f/16);
		addParameter("Image scale x", 1, 1f/16);
		addParameter("Image scale y", 1, 1f/16);
		addParameter("Image rotation", 0, 1f/16);
		updateImage(imgPath);
	}
	
	public void updateImage(String imgPath) {
		BufferedImage img = ImageUtils.getImageFromPath(imgPath);
		BufferedImage convertedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		convertedImg.getGraphics().drawImage(img, 0, 0, null);
		imgData = ((DataBufferByte)convertedImg.getRaster().getDataBuffer()).getData();
		System.out.println(Byte.toUnsignedInt(imgData[0]) + ", " + imgData[1] + ", " + imgData[2] + ", " + imgData[3]);
		imgWidth = convertedImg.getWidth();
		imgHeight = convertedImg.getHeight();
		double max = Math.max(imgWidth, imgHeight);
		getParameter("Image scale x").setValue(imgWidth / max);
		getParameter("Image scale y").setValue(imgHeight / max);
	}
	
	public int mapToImage(double re, double im) {
		re -= imgX;
		im -= imgY;
		double aux = re;
		re = re * cos(imgRotation) + im * sin(imgRotation);
		im = aux * sin(-imgRotation) + im * cos(imgRotation);
		re = ((re + imgScaleX/2) / imgScaleX) * imgWidth;
		im = ((im + imgScaleY/2) / imgScaleY) * imgHeight;
		int x = (int)re;
		int y = (int)im;
		if(x < 0 || x >= imgWidth || y < 0 || y >= imgHeight)
			return -1;
		int index = (imgWidth * y + x)*4;
		if(imgData[index] == 0)
			return -1;
		/*double r = 0, g = 0, b = 0, a = 0;
		for(int i = 0 ; i < 4 ; i++) {
			index = (imgWidth * (y + i/2) + (x + i%2))*4;
			aux = (1 - abs(re - (x + i%2))) * (1 - abs(im - (y + i/2)));
			r += Byte.toUnsignedInt(imgData[index+3]) * aux;
			g += Byte.toUnsignedInt(imgData[index+2]) * aux;
			b += Byte.toUnsignedInt(imgData[index+1]) * aux;
			a += Byte.toUnsignedInt(imgData[index+0]) * aux;
		}
		return toRGB((int)r, (int)g, (int)b);*/
		return getRGBAt(index);
	}
	
	public int getRGBAt(int imgIndex) {
		return toRGB(Byte.toUnsignedInt(imgData[imgIndex+3]), Byte.toUnsignedInt(imgData[imgIndex+2]), Byte.toUnsignedInt(imgData[imgIndex+1]));
	}
	
	@Override
	protected void loadParameterValues() {
		this.lightAngle = getParameter("angle").getValue();
		this.h = getParameter("h").getValue();
		this.imgScaleX = getParameter("Image scale x").getValue();
		this.imgScaleY = getParameter("Image scale y").getValue();
		this.imgX = getParameter("Image x").getValue();
		this.imgY = getParameter("Image y").getValue();
		this.imgRotation = getParameter("Image rotation").getValue() * 2 * Math.PI;
		super.loadParameterValues();
	}
	
	
	@Override
	public void run() {		
		int width = this.width;
		
		int iterations = pre_iterations;
		int i = 0;
		int rgb = -1;
		
		double aux = 0;
		double zRE = 0;
		double zIM = 0;
		double constantRE = topLeftRE + this.delta * getGlobalId(0);
		double constantIM = topLeftIM + this.delta * getGlobalId(1);
		
		double dCRE = 0;
		double dCIM = 0;
		double uRE = 0;
		double uIM = 0;
		
		for(int a = 0 ; a < iterations ; a++) {
			aux = zRE;
			zRE = iterateRE(zRE, zIM, constantRE, constantIM);
			zIM = iterateIM(aux, zIM, constantRE, constantIM);
		}
			
		constantRE = zRE;
		constantIM = zIM;
		
		zRE = 0;
		zIM = 0;
		iterations = 0;
		
		while(zRE * zRE + zIM * zIM <= escapeRadius && iterations < maxIterations/* && rgb == -1*/) {
			aux = dCRE;
			dCRE = 2 * (dCRE * zRE - dCIM * zIM) + 1;
			dCIM = 2 * (aux * zIM + dCIM * zRE);		//dC = 2 * dC * Z + 1
			
			aux = zRE;
			zRE = iterateRE(zRE, zIM, constantRE, constantIM);
			zIM = iterateIM(aux, zIM, constantRE, constantIM);
			
			if(rgb == -1) 
				rgb = mapToImage(zRE, zIM);
			
			iterations+=1;
		}
		i = (width * getGlobalId(1) + getGlobalId(0)) * 3;
		if(iterations < maxIterations) {
			
			if(rgb == -1) {
				float iterationScore = (float)(iterations + 1 - log(log(sqrt(zRE * zRE + zIM * zIM)))/log(2));
				iterationScore =  iterationScore < 0 ? 0 : iterationScore * norm;
				rgb = MultiGradient.colorAtPercent(iterationScore, gradient);
			}
			
			aux = dCRE*dCRE + dCIM*dCIM;
			uRE = (zRE * dCRE + zIM * dCIM)/aux;
			uIM = (-zRE * dCIM + zIM * dCRE)/aux;			//u = Z / dC
			
			aux = Math.sqrt(uRE*uRE + uIM*uIM);
			uRE /= aux;
			uIM /= aux;									//u = u/abs(u)
			
			aux = 2 * Math.PI * lightAngle;
			zRE = cos(aux);
			zIM = sin(aux);
			aux = uRE * zRE + uIM * zIM + h;	//reflection = dot(u, v) + h2
			aux /= (1 + h);
			
			if(aux < 0)
				aux = 0;
			
		}else
			rgb = 0;
		
		data[i + 0] = (byte)((rgb >> 0 & 0xFF) * aux);
		data[i + 1] = (byte)((rgb >> 8 & 0xFF) * aux);
		data[i + 2] = (byte)((rgb >> 16 & 0xFF) * aux);
	}

	
	@Override
	public String getName() {
		return "Image Based Mandelbrot";
	}
	
}
