package features;

import static guiUtils.GUIUtils.getContrastColor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fractalKernels.FractalParameter;
import gui.FractalNavigatorGUI;
import gui.FractalVisualizer;

public class InfoFeature extends ToggleFeature{

	private static final String[] keyBindings = new String[] {
			"[T] Toggle HUD",
			"[M] Open menu",
			"[RIGHT-CLICK] Center",
			"[MIDDLE-CLICK] Visualize orbit",
			"[SCROLL] Zoom",
			"[O] Clear orbits",
			"[F] Toggle fullscreen",
			"[I] Save Image",
			"[V] Create Video",
			"[G] Edit gradient",
			"[0-9] Different zoom levels"
	};
	
	public InfoFeature(FractalNavigatorGUI gui) {
		super(gui);
		toggle = true;
	}

	@Override
	public void show(BufferedImage img) {
		if(toggle) {
			Graphics2D g = img.createGraphics();
			g.setFont(new Font("Arial", Font.BOLD, 15));
			g.setColor(Color.WHITE);
			int y = 20;
			for(String info : this.gui.getVisualizer().getNavigator().getInfo()) {
				g.drawString(info, 5, y);
				y+= 15;
			}
			for(FractalParameter par : getCurrentFrame().getKernel().getFractalParameters()) {
				g.setColor(gui.toggler.getSelectedParameter().t.equals(par.name) ? Color.RED : Color.WHITE);
				g.drawString(par.name + ": " + par.getValue(), 5, y);
				y+= 15;
			}
			g.setColor(Color.white);
			y = img.getHeight() - keyBindings.length * 18;
			for(String key : keyBindings) {
				g.drawString(key, 5, y);
				y+= 18;
			}
			//drawCrosshair(img);
			CoordinateSystem.drawCoordinateSystem(img, getCurrentFrame());
		}
	}
	
	private void drawCrosshair(BufferedImage img) {
		Graphics2D g = img.createGraphics();
		int x = this.gui.getVisualizer().getPanelWidth() / 2;
		int y = this.gui.getVisualizer().getPanelHeight() / 2;
		Color colorXY = new Color(img.getRGB(x, y));
		g.setColor(getContrastColor(colorXY));
		g.fillRect(x - 8, y, 17, 1);
		g.fillRect(x, y - 8, 1, 17);
	}
	
}
