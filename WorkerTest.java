package project;

import java.util.concurrent.*;

class CSPExample {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		BlockingQueue queue = new ArrayBlockingQueue<Order>(5);
		ExecutorService threadPool = Executors.newFixedThreadPool(11);
		threadPool.submit(new Worker(new Order(2, "NEW"), 400, queue));
		threadPool.submit(new Worker(new Order(3, "NEW"), 400, queue));
		threadPool.submit(new Worker(new Order(5, "NEW"), 400, queue));
		threadPool.submit(new Worker(new Order(9, "NEW"), 400, queue));
		threadPool.submit(new Worker(new Order(7, "NEW"), 400, queue));
		threadPool.submit(new Worker(new Order(4, "NEW"), 400, queue));
		threadPool.submit(new Worker(new Order(6, "NEW"), 400, queue));
		threadPool.submit(new Worker(new Order(8, "NEW"), 400, queue));
		threadPool.submit(new Worker(new Order(1, "NEW"), 400, queue));
		threadPool.submit(new Worker(new Order(2, "NEW"), 400, queue));
		new getResult(queue).call();
		threadPool.shutdownNow();
	}

	private static final class Worker implements Runnable {

		private final Order ord;
		private final int sleepPeriod;
		private final BlockingQueue queue;

		public Worker(Order myord, int sleepPeriod, BlockingQueue queue) {
			this.ord = myord;
			this.sleepPeriod = sleepPeriod;
			this.queue = queue;
		}

		@Override
		public void run() {
			try {

				while (true) {

					Thread.sleep(sleepPeriod);
					queue.put(ord);
					if (!ord.status().equals("PROCESSED"))
						System.out.println( ord.status() + " " + ord.getId());

				}

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private static final class getResult implements Callable {

		private final BlockingQueue queue;

		public getResult(BlockingQueue queue) {
			this.queue = queue;
		}

		@Override
		public Object call() {
			try {
				int count = 0;
				while (count < 10) {
					Order ord = (Order) queue.take();
					
					ord.process();
					
					count++;

				}
				return null;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return -1; 
			}
		}
	}

	public static final class Order {

		int id;
		String status;

		Order(int mid, String stat) {
			this.id = mid;
			this.status = stat;
		}

		void process() {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			status = "PROCESSED";
			System.out.println(status + " " + id);
		}

		public int getId() {
			return id;
		}

		public String status() {
			return status;
		}

	}
}
