class Producer extends Thread {
	private Queue shrQueue;
	private int number;

	public Producer(Queue q, int number) {
		shrQueue = q;
		this.number = number;
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			shrQueue.put(i);
			System.out.println("Producer #" + this.number + " put: " + i);
			try {
				sleep((int) (Math.random() * 100));
			} catch (InterruptedException e) {
			}
		}
	}
}
