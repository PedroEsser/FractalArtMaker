package features;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import fractalKernels.FractalKernel;
import gui.FractalNavigatorGUI;
import gui.FractalVisualizer;

public class FractalOrbitVisualizer extends ToggleFeature{

	private List<double[]> orbits = new ArrayList<>();
	private int iterations = 50;
	
	public FractalOrbitVisualizer(FractalNavigatorGUI gui) {
		super(gui);
	}
	
			
	@Override
	public void show(BufferedImage img) {
		if(orbits.isEmpty() || !toggle)
			return;
		FractalKernel k = this.getCurrentFrame().getKernel();
		Graphics2D g = img.createGraphics();
		g.setFont(new Font("Arial", Font.PLAIN, 14));
		for(double[] orbit : orbits) {
			double[] current = orbit.clone();
			Point p = this.gui.getVisualizer().getNavigator().getFrame().toPoint(current);
			g.setColor(Color.red);
			g.fillOval(p.x-2, p.y-2, 5, 5);
			for(int i = 1 ; i < this.getCurrentFrame().getMaxIterations()/2 ; i++) {
				double[] aux = current.clone();
				current[0] = k.iterateRE(aux[0], aux[1], orbit[0], orbit[1]);
				current[1] = k.iterateIM(aux[0], aux[1], orbit[0], orbit[1]);
				
				p = this.gui.getVisualizer().getNavigator().getFrame().toPoint(current);
				//g.setColor(new Color(255, 255, 255, 160));
				//g.drawString("" + i, p.x, p.y);
				g.setColor(new Color(0, 0, 255, 255));
				g.fillOval(p.x-1, p.y-1, 3, 3);
				
				if(Double.isNaN(aux[0]) || Double.isNaN(aux[1]))
					break;
				drawConnection(current, aux, g);
			}
		}
	}
	
	private void drawConnection(double[] current, double[] next, Graphics2D g) {
		Point cur = this.getCurrentFrame().toPoint(current);
		Point nex = this.getCurrentFrame().toPoint(next);
		g.setColor(new Color(255, 255, 255, 40));
		g.drawLine(cur.x, cur.y, nex.x, nex.y);
	}
	
	public void orbitAt(Point p, boolean drag) {
		if(drag)
			this.orbits.remove(this.orbits.size()-1);
		this.orbits.add(this.getCurrentFrame().complexAt(p.x, p.y));
		this.toggle = true;
		this.gui.getVisualizer().update();
	}
	
	@Override
	public void toggle(boolean toggle) {
		super.toggle(toggle);
		if(!toggle)
			orbits = new ArrayList<>();
	}
	
}
