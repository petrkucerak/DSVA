class Consumer extends Thread {
	private Queue shrQueue;
	private int number;

	public Consumer(Queue q, int number) {
		shrQueue = q;
		this.number = number;
	}

	public void run() {
		int value = 0;
		for (int i = 0; i < 10; i++) {
			value = shrQueue.get();
			System.out.println("Consumer #" + this.number + " got: " + value);
		}
	}
}