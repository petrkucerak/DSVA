
public class ThreadRunner {
	
	public static void main(String[] args) {
		TestingThread tt1 = new TestingThread("TestThread1", 5);
		TestingThread tt2 = new TestingThread("TestThread2", 5);
		TestingThread tt3 = new TestingThread("TestThread3", 5);
		TestingThread tt4 = new TestingThread("TestThread4", 5);
		
		System.out.println("Thread runner - start");
		
		new Thread(tt1).start();
		new Thread(tt2).start();
		System.out.println("Thread runner - middle");
		new Thread(tt3).start();
		new Thread(tt4).start();
		
		System.out.println("Thread runner - end");
	}

}
