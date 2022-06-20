package features;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import gui.FractalVisualizer;
import kernel.FractalKernel;

public class FractalOrbitVisualizer extends ToggleFeature{

	private double[] lastComplex;
	private int iterations = 50;
	
	public FractalOrbitVisualizer(FractalVisualizer vis) {
		super(vis);
	}
	
			
	@Override
	public void show(BufferedImage img) {
		if(lastComplex == null || !toggle)
			return;
		FractalKernel k = this.getCurrentFrame().getKernel();
		Graphics2D g = img.createGraphics();
		g.setFont(new Font("Arial", Font.PLAIN, 14));
		double[] current = lastComplex.clone();
		for(int i = 1 ; i < this.getCurrentFrame().getMaxIterations() ; i++) {
			Point p = this.vis.getNavigator().getFrame().toPoint(current);
			g.setColor(new Color(255, 255, 255, 160));
			g.drawString("" + i, p.x, p.y);
			
			double[] aux = current.clone();
			current[0] = k.iterateRE(aux[0], aux[1]) + lastComplex[0];
			current[1] = k.iterateIM(aux[0], aux[1]) + lastComplex[1];
			if(Double.isNaN(aux[0]) || Double.isNaN(aux[1]))
				return;
			drawConnection(current, aux, g);
		}
	}
	
	private void drawConnection(double[] current, double[] next, Graphics2D g) {
		Point cur = this.getCurrentFrame().toPoint(current);
		Point nex = this.getCurrentFrame().toPoint(next);
		g.setColor(new Color(255, 255, 255, 50));
		g.drawLine(cur.x, cur.y, nex.x, nex.y);
	}
	
	
//	private void drawComplex(double[] complex, Graphics2D g) {
//		g.setColor(Color.GREEN);
//		Point p = this.getCurrentFrame().toPoint(complex);
//		g.fillRect(p.x-1, p.y-1, 3, 3);
//	}
	
	public void orbitAt(Point p) {
		this.lastComplex = this.getCurrentFrame().complexAt(p.x, p.y);
		this.toggle = true;
		this.vis.update();
	}
	
}
