package new_engine;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import fractal.FractalFrame;
import utils.ImageUtils;

public class Engine extends Thread{
	
	private static final int DEFAULT_MILLIS_PER_FRAME = 20;
	private static Engine engine;
	
	private MyWindow window;
	private FractalNavigatorV3 navigator;
	private int millis_per_frame;
	private Update update = new Update();
	
	private Engine() {
		this.millis_per_frame = DEFAULT_MILLIS_PER_FRAME;
	}
	
	public void start() {
		navigator = new FractalNavigatorV3();
		window = new MyWindow();
		FractalVisualizerV2 visualizer = navigator.getVisualizer();
		window.add(visualizer);
		window.showWindow();
		
		while(true) {
			long t = System.currentTimeMillis();
			
			navigator.checkResize();
			update.update();
			
			long pauseTime = millis_per_frame + t - System.currentTimeMillis();
			pause(pauseTime);
		}
	}
	
	public static void startEngine() {
		if(engine == null) {
			engine = new Engine();
			engine.start();
		}
	}
	
	private static void pause(long millis) {
		if(millis > 0)
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	public static void updateOnNextFrame(UpdateReason reason) {
		engine.update.reasons.add(reason);
	}
	
	public static void toggleFullscreen() {
		engine.window = engine.window.toggleFullscreen();
	}
	
	class Update{
		List<UpdateReason> reasons = new ArrayList();
		
		void update() {
			if(reasons.contains(UpdateReason.ZOOM)) {
				navigator.previewZoom();
			}
			if(reasons.contains(UpdateReason.MOVE)) {
				navigator.previewMove();
			}
			
			if(!reasons.isEmpty()) {
				navigator.updateImage();
				navigator.update();
			}
				
			reasons.clear();
		}
	}
	
	public enum UpdateReason{
		MOVE, ZOOM, GRADIENT, OTHER;
	}
	
}
