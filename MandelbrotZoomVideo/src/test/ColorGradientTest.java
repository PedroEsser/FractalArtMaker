package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

import gpuColorGradients.GradientUtils;
import gpuColorGradients.MultiGradientV2;
import gpuColorGradients.ThreeChannelGradient;
import gui.GradientVisualizer;

import static gpuColorGradients.GradientUtils.*;
import static gpuColorGradients.MultiGradientV2.*;
import static gpuColorGradients.ThreeChannelGradient.*;

public class ColorGradientTest {

	private static final float MAX_DIFFERENCE_ERROR = 1E-5f;
	
	@Test
	public void testGradientUtils() {
		assertAlmostEqual(0.4f, looped(1.4f));
		assertAlmostEqual(0.6f, bounced(1.4f));
	}
	
	@Test
	public void threeChannelGradientTest() {
		ThreeChannelGradient gradient = (ThreeChannelGradient)new ThreeChannelGradient(0.75f, 0.5f, 0, -0.5f, 0.5f, 1f);
		float[] g = gradient.getGradientData();
		assertEquals(0f, calculatePercent(0, g, 0));
		assertEquals(0.5f, calculatePercent(0.5f, g, 0));
		
		assertTrue(ThreeChannelGradient.isLoop(g, 0));
		assertTrue(ThreeChannelGradient.isHSB(g, 0));
		gradient.setRGB();
		assertFalse(ThreeChannelGradient.isHSB(g, 0));
		
		assertAlmostEqual(0.375f, getNthChannelAtPercent(0.75f, 0, g, 0));
		assertAlmostEqual(0.875f, getNthChannelAtPercent(0.75f, 1, g, 0));
		assertAlmostEqual(0.75f, getNthChannelAtPercent(0.75f, 2, g, 0));
		
		assertEquals(0x005fdfbf, getColorAtPercent(0.75f, g, 0));
		assertEquals(0x00bf7f00, getColorAtPercent(0, g, 0));
		
		gradient.bounce(1.3f);
		assertAlmostEqual(0.7f, calculatePercent(1, g, 0));
		assertFalse(ThreeChannelGradient.isLoop(g, 0));
		gradient.loop(1.6f);
		assertAlmostEqual(0.6f, calculatePercent(1, g, 0));
	}
	
	@Test
	public void multiGradientTest() {
		ThreeChannelGradient g1 = new ThreeChannelGradient(0f, 0.5f, 0.75f, -0.5f, 0.5f, -1f).setRGB();
		ThreeChannelGradient g2 = new ThreeChannelGradient(0.75f, 0.5f, 0, -0.5f, 0.5f, 1f).setRGB();
		
		MultiGradientV2 multi = (MultiGradientV2)new MultiGradientV2(g1, g2).offseted(0.25f).bounce(2);
		float[] gradient = multi.getGradientData();
		
		assertAlmostEqual(0.25f, getOffset(gradient));
		multi.offseted(0);
		assertAlmostEqual(0, getOffset(gradient));
		assertAlmostEqual(2, getRange(gradient, 0));
		assertEquals(2, getWeightSum(gradient));
		
		assertAlmostEqual(0.75f, calculatePercent(0.75f/2, gradient, 0));
		
		assertEquals(0x005fdfbf, getColorAtPercent(0.75f, gradient, 14));
		assertEquals(0x005fdfbf, colorAtPercent(7f/16, gradient));
		assertEquals(0x00bf7f00, colorAtPercent(0.5f, gradient));
		
		MultiGradientV2 m = new MultiGradientV2(g2);
		float[] g = m.getGradientData();
		float p = (float)Math.random();
		assertEquals(colorAtPercent(p, g), getColorAtPercent(p, g2.getGradientData(), 0));
	}
	
	public static void assertAlmostEqual(float expected, float actual) {
		float dist = Math.abs(expected - actual);
		assert dist < MAX_DIFFERENCE_ERROR : "Not almost equal, expected: " + expected + ", actual: " + actual + ", difference: " + dist;
	}
	
}
