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
	private double imgX, imgY, imgScale, imgRotation;
	
	private String imgPath = "C:\\Users\\pedro\\Desktop\\imageBasedTest.png";//"C:\\Users\\pedro\\Downloads/sad_angelo.jpeg";
	private byte[] imgData;
	private int imgLength;
	
	public ImageBasedMandelbrotKernel() {
		super();
		addParameter("angle", 0.5, 1f/64);
		addParameter("h", 1.5, 1f/64);
		addParameter("Image x", 0, 1f/16);
		addParameter("Image y", 0, 1f/16);
		addParameter("Image scale", 1, 1f/16);
		addParameter("Image rotation", 0, 1f/16);
		updateImage(imgPath);
	}
	
	public void updateImage(String imgPath) {
		BufferedImage img = ImageUtils.getImageFromPath(imgPath);
		BufferedImage convertedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		convertedImg.getGraphics().drawImage(img, 0, 0, null);
		imgData = ((DataBufferByte)convertedImg.getRaster().getDataBuffer()).getData();
		imgLength = convertedImg.getWidth();
	}
	
	@Override
	protected void loadParameterValues() {
		this.lightAngle = getParameter("angle").getValue();
		this.h = getParameter("h").getValue();
		this.imgScale = getParameter("Image scale").getValue();
		this.imgX = getParameter("Image x").getValue() - imgScale/2;
		this.imgY = getParameter("Image y").getValue() - imgScale/2;
		this.imgRotation = getParameter("Image rotation").getValue();
		updateImage(imgPath);
		super.loadParameterValues();
	}
	
	
	@Override
	public void run() {		
		int width = this.width;
		
		int iterations = pre_iterations;
		int i = 0;
		
		double orbitX = 1e10;
		double orbitY = 1e10;
		double distance = 1e10;
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
		
		while(zRE * zRE + zIM * zIM <= escapeRadius && iterations < maxIterations) {
			aux = dCRE;
			dCRE = 2 * (dCRE * zRE - dCIM * zIM) + 1;
			dCIM = 2 * (aux * zIM + dCIM * zRE);		//dC = 2 * dC * Z + 1
			
			aux = zRE;
			zRE = iterateRE(zRE, zIM, constantRE, constantIM);
			zIM = iterateIM(aux, zIM, constantRE, constantIM);
			
			if(distance != 0 && zRE > imgX && zRE < imgY + imgScale && zIM > imgY && zIM < imgY + imgScale) {
				distance = 0;
				orbitX = zRE;
				orbitY = zIM;
			}
			
			iterations+=1;
		}
		i = (width * getGlobalId(1) + getGlobalId(0)) * 3;
		int rgb = 0;
		if(iterations < maxIterations) {
			if(distance == 0) {		// Trap
				int x = (int)(((orbitX - imgX) / imgScale) * imgLength);
				int y = (int)(((orbitY - imgY) / imgScale) * imgLength);
				int imgIndex = (imgLength * y + x) * 3;
				rgb = (imgData[imgIndex + 0] << 0) | (imgData[imgIndex + 1] << 8) | (imgData[imgIndex + 2] << 16);
			}else {
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
			
		}
		data[i + 0] = (byte)((rgb >> 0 & 0xFF) * aux);
		data[i + 1] = (byte)((rgb >> 8 & 0xFF) * aux);
		data[i + 2] = (byte)((rgb >> 16 & 0xFF) * aux);
	}

	
	@Override
	public String getName() {
		return "Image Based Mandelbrot";
	}
	
}
