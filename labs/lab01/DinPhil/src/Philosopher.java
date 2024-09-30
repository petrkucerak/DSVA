import java.util.Random;

// One philosoper.
public class Philosopher implements Runnable {
	// Which one I am.
	private final int id;
	// The chopsticks on either side of me.
	private final ChopStick leftChopStick;
	private final ChopStick rightChopStick;
	// Am I full?
	volatile boolean isTummyFull = false;
	// To randomize eat/Think time
	private Random randomGenerator = new Random();
	// Number of times I was able to eat.
	private int noOfTurnsToEat = 0;

	/**
	 * **
	 *
	 * @param id
	 *            Philosopher number
	 *
	 * @param leftChopStick
	 * @param rightChopStick
	 */
	public Philosopher(int id, ChopStick leftChopStick, ChopStick rightChopStick) {
		this.id = id;
		this.leftChopStick = leftChopStick;
		this.rightChopStick = rightChopStick;
	}

	@Override
	public void run() {

		try {
			while (!isTummyFull) {
				// Think for a bit.
				think();
				// Make the mechanism obvious.
				if (leftChopStick.pickUp(this, "left")) {
					if (rightChopStick.pickUp(this, "right")) {
						// Eat some.
						eat();
						// Finished.
						rightChopStick.putDown(this, "right");
					}
					// Finished.
					leftChopStick.putDown(this, "left");
				}
			}
		} catch (Exception e) {
			// Catch the exception outside the loop.
			e.printStackTrace();
		}
	}

	private void think() throws InterruptedException {
		System.out.println(this + " is thinking");
		Thread.sleep(randomGenerator.nextInt(1000));
	}

	private void eat() throws InterruptedException {
		System.out.println(this + " is eating");
		noOfTurnsToEat++;
		Thread.sleep(randomGenerator.nextInt(1000));
	}

	// Accessors at the end.
	public int getNoOfTurnsToEat() {
		return noOfTurnsToEat;
	}

	@Override
	public String toString() {
		return "Philosopher-" + id;
	}
}