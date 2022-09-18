package new_engine;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import fractal.FractalFrame;
import fractal.FractalZoom;
import fractals_deprecated.Complex;
import new_engine.KernelHandler.FractalTask;
import utils.ImageUtils;
import static new_engine.Engine.UpdateReason.*;

public class FractalNavigatorV2 {
	
	private static final double DEFAULT_INCREMENT = 0.002;
	protected FractalZoom zoom;
	protected FractalFrame frame;
	protected BufferedImage lastRenderedImage;
	
	protected double percent = 0;
	protected double percentIncrement = DEFAULT_INCREMENT;
	protected final FractalVisualizerV2 visualizer;
	private FractalTask currentTask;
	
	public FractalNavigatorV2() {
		zoom = new FractalZoom(1, 1);
		frame = zoom.getStart();
		this.visualizer = new FractalVisualizerV2(this);
		visualizer.setMouseWheelCallback(e -> zoom(e.getUnitsToScroll()));
		visualizer.setMousePressCallback(e -> {
			if (SwingUtilities.isRightMouseButton(e)) 
            	move(visualizer.getPointOnPanel(e.getPoint()));
		});
	}
	
	public FractalFrame getFrame() {
		return frame;
	}
	
	public FractalVisualizerV2 getVisualizer() {
		return visualizer;
	}
	
	public FractalFrame getPseudoFrame() {
		return zoom.valueAt(percent);
	}
	
	public void checkResize() {
		if(zoom.getWidth() != visualizer.getPanelWidth() || zoom.getHeight() != visualizer.getPanelHeight()) {
			zoom.setDimensions(visualizer.getPanelWidth(), visualizer.getPanelHeight());
			Engine.updateOnNextFrame(OTHER);
		}
	}
	
	public void zoom(int units) {
		percent -= percentIncrement * units;
		Engine.updateOnNextFrame(ZOOM);
	}
	
	public void move(Point p) {
		Complex newCenter = getPseudoFrame().toComplex(p);
		zoom.setCenter(newCenter);
		Engine.updateOnNextFrame(MOVE);
	}
	
	public void update() {
		FractalFrame nextFrame = getPseudoFrame();
		if(currentTask != null)
			currentTask.cancel();
		currentTask = KernelHandler.doTask(nextFrame.getKernel(), (cancelled) -> {
			frame = nextFrame;
			if(!cancelled)
				kernelFinishedCallback();
		});
	}
	
	public void updateImage() {
		visualizer.updateImage(lastRenderedImage);
	}
	
	protected void kernelFinishedCallback() {
		visualizer.updateImage();
		lastRenderedImage = frame.getImage();
	}
	
	protected double getZoomDifferenceRatio() {
		double oldDelta = frame.getDelta();
		double newDelta = zoom.getDeltaGradient().valueAt(percent);
		return oldDelta / newDelta;
	}
	
	public void previewZoom() {
		lastRenderedImage = ImageUtils.getCroppedAndResizedImage(lastRenderedImage , getZoomDifferenceRatio());
		visualizer.setImg(lastRenderedImage);
	}
	
	public void previewMove() {
		FractalFrame f = getPseudoFrame();
		Point p = frame.toPoint(f.getCenter());
		int minX = Math.max(0, p.x - f.getWidth()/2);
		int minY =  Math.max(0, p.y - f.getHeight()/2);
		int maxX = Math.min(p.x + f.getWidth()/2, f.getWidth());
		int maxY = Math.min(p.y + f.getHeight()/2, f.getHeight());
		
		Rectangle crop = new Rectangle(minX, minY, maxX - minX, maxY - minY);
		BufferedImage c = ImageUtils.getCroppedImage(lastRenderedImage, crop);
		Point diff = new Point(f.getWidth()/2 - p.x, f.getHeight()/2 - p.y);
		crop.x += diff.x;
		crop.y += diff.y;
		
		lastRenderedImage = ImageUtils.copyCroppedImage(new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_3BYTE_BGR), c, crop);
		visualizer.setImg(lastRenderedImage);
	}
	
}
