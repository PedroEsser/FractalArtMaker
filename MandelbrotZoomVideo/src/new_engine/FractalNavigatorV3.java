package new_engine;

import static javax.swing.KeyStroke.getKeyStroke;
import static new_engine.Engine.UpdateReason.*;

import java.awt.Point;

import javax.swing.SwingUtilities;

import fractal.FractalFrame;
import fractals_deprecated.Complex;

public class FractalNavigatorV3 extends FractalNavigatorV2{
	
	private boolean zoomAndMove = false;
	
	public FractalNavigatorV3() {
		super();
		visualizer.setMouseWheelCallback(e -> {
			if(zoomAndMove){
				zoomAndMove(visualizer.getPointOnPanel(e.getPoint()), e.getUnitsToScroll());
			}else
				zoom(e.getUnitsToScroll());
		});
		visualizer.setMousePressCallback(e -> {
			if (SwingUtilities.isRightMouseButton(e)) 
            	move(visualizer.getPointOnPanel(e.getPoint()));
			if (SwingUtilities.isMiddleMouseButton(e))
				zoomAndMove = !zoomAndMove;
		});
		addKeyStrokes();
	}
	
	private void zoomAndMove(Point p, int units) {
		Complex c = getPseudoFrame().toComplex(p);
		percent -= percentIncrement * units;
		FractalFrame nextFrame = getPseudoFrame();
		Point nextP = nextFrame.toPoint(c);
		Point diff = new Point(nextP.x - p.x, nextP.y - p.y);
		zoom.setCenter(nextFrame.toComplex(nextFrame.getWidth()/2 + diff.x, nextFrame.getHeight()/2 + diff.y));
		Engine.updateOnNextFrame(MOVE);
		Engine.updateOnNextFrame(ZOOM);
	}
	
	public void addKeyStrokes() {
//		visualizer.addKeyStroke(getKeyStroke("T"), "info", e -> info.toggle());
//		visualizer.addKeyStroke(getKeyStroke("M"), "menu", e -> new MenuGUI(this));
//		visualizer.addKeyStroke(getKeyStroke("I"), "imageSave", e -> new ImageSaver(visualizer));
//		visualizer.addKeyStroke(getKeyStroke("V"), "videoSave", e -> new VideoMaker(visualizer));
//		visualizer.addKeyStroke(getKeyStroke("G"), "gradient", e -> randomiseGradient());
//		visualizer.addKeyStroke(getKeyStroke("RIGHT"), "toggleRightParameter", e -> toggler.toggleRight());
//		visualizer.addKeyStroke(getKeyStroke("LEFT"), "toggleLeftParameter", e -> toggler.toggleLeft());
//		visualizer.addKeyStroke(getKeyStroke("UP"), "incParameter", e -> toggler.inc());
//		visualizer.addKeyStroke(getKeyStroke("PLUS"), "offset+", e -> offsetGradient(1f/512));
//		visualizer.addKeyStroke(getKeyStroke("MINUS"), "offset-", e -> offsetGradient(-1f/512));
//		visualizer.addKeyStroke(getKeyStroke("DOWN"), "decParameter", e -> toggler.dec());
		visualizer.addKeyStroke(getKeyStroke("F"), "fullScreen", e -> Engine.toggleFullscreen());
		
//		for(int i = 0 ; i <= 9 ; i++) {
//			final int d = i;
//			visualizer.addKeyStroke(getKeyStroke("" + d), "toPercent" + d, e -> goToPercent(d));
//		}
			
	}
	
}
