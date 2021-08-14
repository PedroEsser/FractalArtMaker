package optimizations;

public class FractalConsumer extends Thread{

	private FractalProducer producer;
	
	public FractalConsumer(FractalProducer producer) {
		this.producer = producer;
	}

	@Override
	public void run() {
		FractalTask task = producer.getTask();
		while(task != null && producer.isAlive()) {
			task.doTask();
			task = producer.getTask();
		}
	}
	
}
