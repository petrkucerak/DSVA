import java.util.Random;

public class TestingThread implements Runnable {

	private String name = "Unknown";
	private int count = 5;

	public TestingThread(String name, int count) {
		this.name = name;
		this.count = count;
	}

	@Override
	public void run() {
		System.out.println("My name is " + name + " and I am starting");
		Random rnd = new Random();
		while (count >= 0) {
			System.out.println("My name is " + name + " and I am counting ... " + count--);
			try {
				Thread.sleep(rnd.nextInt(5000)+2000, 0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("My name is " + name + " and I am ending");
	}

}
