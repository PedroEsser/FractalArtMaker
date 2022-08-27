package main;

import com.aparapi.Range;
import com.aparapi.opencl.OpenCL;

interface MandelbrotTest extends OpenCL<MandelbrotTest>{

	public MandelbrotTest test(Range _range, 
			@GlobalWriteOnly("out") int[] out);
}