package lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CounterReentrantLock implements Runnable
{
    private int sum = 0;
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    public CounterReentrantLock(int sum)
    {
        this.sum = sum;
    }
    public CounterReentrantLock() {}
    @Override
    public void run()
    {
        try {
            // 利用带超时参数的tryLock()进行处理,防止出现死锁
            if (lock.tryLock(150, TimeUnit.MILLISECONDS)) {
                try {
                    while (sum > 25) {
                        // await()方法会把当前线程放到阻塞队列里面去,并释放锁,当再次得到锁时,会从被阻塞的位置继续执行
                        condition.await();
                    }
                    // 中断检测, 加上异常检测
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            System.out.println(Thread.currentThread().getName()+": 计数器计数: "+sum);
                            ++sum;
                            // 模拟运行了10ms
                            if (sum > 10) {
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
                    // 激活条件队列的所有线程,让他们离开条件队列,成为可运行状态
                    condition.signalAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    // 确保释放锁
                    lock.unlock();
                }
            }
            else {
                System.out.println("未得到锁");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
