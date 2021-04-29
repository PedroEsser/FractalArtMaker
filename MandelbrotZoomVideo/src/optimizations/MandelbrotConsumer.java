package optimizations;

public class MandelbrotConsumer extends Thread{

	private MandelbrotProducer producer;
	
	public MandelbrotConsumer(MandelbrotProducer producer) {
		this.producer = producer;
	}

	@Override
	public void run() {
		MandelbrotTask task = producer.getTask();
		while(task != null && producer.isAlive()) {
			task.doTask();
			task = producer.getTask();
		}
	}
	
}
