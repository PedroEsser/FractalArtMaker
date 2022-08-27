package video;

import java.util.LinkedList;

public class VideoManager {

	private final static LinkedList<FractalVideo> queue = new LinkedList<FractalVideo>();
	
	public static void enqueue(FractalVideo video) {
		queue.add(video);
		if(queue.size() == 1) 
			produceNextVideo();
	}
	
	public static void dequeue(FractalVideo video) {
		video.cancel();
		queue.remove(video);
	}
	
	private static void produceNextVideo() {
		FractalVideo video = queue.peek();
		if(video != null)
			new Thread(() -> {
				video.start();
				try {
					video.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally {
					dequeue(video);
					produceNextVideo();
				}
			}).start();
	}
	
}
