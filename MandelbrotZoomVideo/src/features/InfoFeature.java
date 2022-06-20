package features;

import static guiUtils.GUIUtils.getContrastColor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import gui.FractalVisualizer;

public class InfoFeature extends ToggleFeature{

	public InfoFeature(FractalVisualizer vis) {
		super(vis);
	}

	@Override
	public void show(BufferedImage img) {
		if(toggle) {
			Graphics2D g = img.createGraphics();
			g.setFont(new Font("Arial", Font.BOLD, 15));
			g.setColor(Color.WHITE);
			int y = 20;
			for(String info : this.vis.getNavigator().getInfo()) {
				g.drawString(info, 5, y);
				y+= 15;
			}
			//drawCrosshair(img);
			CoordinateSystem.drawCoordinateSystem(img, getCurrentFrame());
		}
	}
	
	private void drawCrosshair(BufferedImage img) {
		Graphics2D g = img.createGraphics();
		int x = this.vis.getPanelWidth() / 2;
		int y = this.vis.getPanelHeight() / 2;
		Color colorXY = new Color(img.getRGB(x, y));
		g.setColor(getContrastColor(colorXY));
		g.fillRect(x - 8, y, 17, 1);
		g.fillRect(x, y - 8, 1, 17);
	}
	
}
