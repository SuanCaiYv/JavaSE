package lock;

public class CounterSynchronized implements Runnable
{
    private int sum = 0;
    public CounterSynchronized()
    {
        this.sum = 0;
    }
    public CounterSynchronized(int sum)
    {
        this.sum = sum;
    }

    @Override
    public synchronized void run()
    {
        try {
            while (sum > 25) {
                wait();
            }
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName()+": 计数器计数: "+sum);
                ++sum;
                if (sum < 10) {
                    Thread.sleep(10);
                }
                else {
                    Thread.sleep(40);
                }
                break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            notifyAll();
        }
    }
}
