package block;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class BlockQueue
{
    private static final ArrayBlockingQueue<Integer> arrayBlockingQueue = new ArrayBlockingQueue<>(5);
    private static final Producter producter = new Producter(arrayBlockingQueue);
    private static final Consumer consumer = new Consumer(arrayBlockingQueue);

    public void work()
    {
        for (int i = 0; i < 5; ++ i) {
            Thread thread1 = new Thread(producter);
            Thread thread2 = new Thread(consumer);
            thread1.start();
            thread2.start();
        }
    }
}
class Producter implements Runnable
{
    private ArrayBlockingQueue<Integer> arrayBlockingQueue;
    private static final Random random = new Random();
    public Producter(ArrayBlockingQueue<Integer> arrayBlockingQueue)
    {
        this.arrayBlockingQueue = arrayBlockingQueue;
    }
    @Override
    public void run()
    {
        int value = random.nextInt()%1000;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                long startTime = System.currentTimeMillis();
                arrayBlockingQueue.put(value);
                Thread.sleep(200);
                System.out.println(Thread.currentThread().getName()+": 把"+value+"放入队列, 用时: "+(System.currentTimeMillis()-startTime));
                Thread.sleep(400);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class Consumer implements Runnable
{
    private ArrayBlockingQueue<Integer> arrayBlockingQueue;
    private static final Random random = new Random();
    public Consumer(ArrayBlockingQueue<Integer> arrayBlockingQueue)
    {
        this.arrayBlockingQueue = arrayBlockingQueue;
    }
    @Override
    public void run()
    {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                long startTime = System.currentTimeMillis();
                int value = arrayBlockingQueue.take();
                Thread.sleep(200);
                System.out.println(Thread.currentThread().getName()+": 把"+value+"取出队列, 用时: "+(System.currentTimeMillis()-startTime));
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}