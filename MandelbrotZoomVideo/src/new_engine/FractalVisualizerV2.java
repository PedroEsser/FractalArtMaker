package new_engine;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import guiUtils.ImagePanel;

public class FractalVisualizerV2 extends ImagePanel{

	private final FractalNavigatorV2 nav;
	
	public FractalVisualizerV2(FractalNavigatorV2 nav) {
		super();
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		this.nav = nav;
	}
	
	public void addKeyStroke(KeyStroke stroke, String actionName, Consumer<ActionEvent> action) {
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, actionName);
		this.getActionMap().put(actionName, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				action.accept(e);
			}
		});
	}
	
	public void updateImage() {
		updateImage(nav.getFrame().toImage());
	}
	
	@Override
	protected void myPaint(Graphics2D g) {
		g.drawImage(this.img, 0, 0, null);
	}

	@Override
	protected void myResize(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

}
