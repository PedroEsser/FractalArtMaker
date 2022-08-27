package main;

import com.aparapi.Range;
import com.aparapi.opencl.OpenCL;

interface FractalTest extends OpenCL<FractalTest>{

	public FractalTest test(Range _range, 
			@GlobalReadOnly("in") byte[] in,
			@GlobalWriteOnly("out") byte[] out);
}
