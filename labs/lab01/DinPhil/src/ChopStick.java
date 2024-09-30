import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChopStick {
	// Make sure only one philosopher can have me at any time.
	Lock up = new ReentrantLock();
	// Who I am.
	private final int id;

	public ChopStick(int id) {
		this.id = id;
	}

	public boolean pickUp(Philosopher who, String where) throws InterruptedException {
		if (up.tryLock(10, TimeUnit.MILLISECONDS)) {
			System.out.println(who + " picked up " + where + " " + this);
			return true;
		}
		return false;
	}

	public void putDown(Philosopher who, String name) {
		up.unlock();
		System.out.println(who + " put down " + name + " " + this);
	}

	@Override
	public String toString() {
		return "Chopstick-" + id;
	}
}