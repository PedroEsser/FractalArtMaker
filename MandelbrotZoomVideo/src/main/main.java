package main;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import com.aparapi.internal.opencl.OpenCLKernel;
import com.aparapi.internal.opencl.OpenCLPlatform;
import com.formdev.flatlaf.FlatDarkLaf;

import gpuColorGradients.GenericGradient;
import gpuColorGradients.GradientFactory;
import gpuColorGradients.MultiGradientV2;
import gpuColorGradients.ThreeChannelGradient;
import gui.FractalNavigatorGUI;
import gui.GradientVisualizer;



public class main {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception e) {
			e.printStackTrace();
		}

//		GenericGradient a = new GenericGradient(false, 0, 0.5f, 0, 1f, -1f, 0.3f);
//		a.test(0.2f);
//		a.loop(0.5f, 2.5f);
//		a.test(0.2f);
		
		//new FractalNavigatorGUI();
//		new Mandelbrot64();
		
		GradientVisualizer.showcase(GradientFactory.testV2(5).toGradient());
		GradientVisualizer.showcase(GradientFactory.testV2(5).toGradient());
		GradientVisualizer.showcase(GradientFactory.testV2(5).toGradient());
		GradientVisualizer.showcase(GradientFactory.testV2(5).toGradient());
		GradientVisualizer.showcase(GradientFactory.testV2(5).toGradient());
		GradientVisualizer.showcase(GradientFactory.testV2(5).toGradient());
		
//		String source = "int index(int x, int y, int width) {\n"
//				+ "  return width*y + x;\n"
//				+ "}\n"
//				+ "\n"
//				+ "// Turn the raw x coordinates [0, 1] into a scaled x coordinate\n"
//				+ "// [0, 1] -> [-2, 1.25]\n"
//				+ "float mapX(float x) {\n"
//				+ "  return x*3.25 - 2;\n"
//				+ "}\n"
//				+ "\n"
//				+ "// Same purpose as mapX\n"
//				+ "// [0, 1] -> [-1.25, 1.25]\n"
//				+ "float mapY(float y) {\n"
//				+ "  return y*2.5 - 1.25;\n"
//				+ "}\n"
//				+ "\n"
//				+ "__kernel void test(__global int *out) {\n"
//				+ "  int x_dim = get_global_id(0);\n"
//				+ "  int y_dim = get_global_id(1);\n"
//				+ "  size_t width = get_global_size(0);\n"
//				+ "  size_t height = get_global_size(1);\n"
//				+ "  int idx = index(x_dim, y_dim, width);\n"
//				+ "\n"
//				+ "  float x_origin = mapX((float) x_dim / width);\n"
//				+ "  float y_origin = mapY((float) y_dim / height);\n"
//				+ "\n"
//				
//				+ "  float x = 0.0;\n"
//				+ "  float y = 0.0;\n"
//				
//				+ "  int iteration = 0;\n"
//				
//				+ "  int max_iteration = 256;\n"
//				+ "  while(x*x + y*y <= 4 && iteration < max_iteration) {\n"
//				+ "    float xtemp = x*x - y*y + x_origin;\n"
//				+ "    y = 2*x*y + y_origin;\n"
//				+ "    x = xtemp;\n"
//				+ "    iteration++;\n"
//				+ "  }\n"
//				
//				+ "  if(iteration == max_iteration) {\n"
//				+ "    out[idx] = 0;\n"
//				+ "  } else {\n"
//				+ "    out[idx] = iteration;\n"
//				+ "  }\n"
//				+ "}";
//		
//		OpenCLDevice device = (OpenCLDevice) KernelManager.instance().getDefaultPreferences().getPreferredDevice(null);
//		System.out.println(device.getShortDescription());
//		MandelbrotTest test = device.bind(MandelbrotTest.class, source);
//		System.out.println(mbtest(1000, test)[12]);
	}
//
//	private static byte[] test(int size, FractalTest test) {
//		byte[] in = new byte[size];
//		for(int i = 0 ; i < in.length ; i++)
//			in[i] = (byte)i;
//		byte[] out = new byte[in.length];
//		long t = System.currentTimeMillis();
//		test.test(Range.create(in.length), in, out);
//		System.out.println(System.currentTimeMillis() - t + " millis passed");
//		return out;
//	}
	
	private static int[] mbtest(int size, MandelbrotTest test) {
		int[] out = new int[size*size];
		long t = System.currentTimeMillis();
		test.test(Range.create2D(size, size), out);
		System.out.println(System.currentTimeMillis() - t + " millis passed");
		return out;
	}
	
}
