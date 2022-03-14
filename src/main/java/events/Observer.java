package events;

import apps.Main;
import railway.Railway;
import railway.Train;

import java.util.logging.Logger;

import static railway.Railway.instance;

abstract public class Observer implements Runnable {
	protected static final Railway railway;
	private static final int sleep;
	private static int nextID;

	static {
		railway = instance();
		sleep = 200;
		nextID = 0;
	}

	protected Train train;
	protected Logger logger;
	private int id;
	private boolean running;

	protected Observer(Train t) {
		this.train = t;
		this.logger = Main.getObserverLogger(getId());
		this.running = true;
		this.id = nextID++;
	}

	/**
	 * Method to be overloaded to test for an event happening
	 *
	 * @return true if it happened
	 */
	protected abstract boolean test();

	/**
	 * Method called after the event tested for in test() happened
	 */
	protected abstract void process();

	/**
	 * Gives an Id that can be used in the logger
	 *
	 * @return logger-friendly id of this observer
	 */
	public String getId() {
		return "generic_obs_" + id;
	}

	protected void start() {
		logger.info("Observer started.");
		new Thread(this).start();
	}

	private boolean testWrapper() {
		if (!running) {
			return false;
		}
		return test();
	}

	@Override
	public void run() {
		while (!testWrapper()) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		process();
		railway.getObservers().remove(this);
	}


	public void pauseThread() {
		running = false;
	}

	public void resumeThread() {
		running = true;
	}

	public Train getTrain(){
		return train;
	}
}
