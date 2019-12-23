import async.ForkJoin;
import async.Printer;
import block.BlockQueue;
import lock.CounterReentrantLock;
import lock.CounterSynchronized;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main
{
    public static void main(String[] args) throws InterruptedException, ExecutionException
    {
        System.out.println("ReentrantLock及其同步实现的测试:");
        CounterReentrantLock counterReentrantLock = new CounterReentrantLock();
        for (int i = 0; i < 20; ++ i) {
            Thread thread = new Thread(counterReentrantLock);
            Thread.sleep(20);
            thread.start();
        }
        Thread.sleep(2000);
        System.out.println("############################");
        System.out.println("synchronized及其同步实现的测试:");
        CounterSynchronized counterSynchronized = new CounterSynchronized();
        for (int i = 0; i < 20; ++ i) {
            Thread thread = new Thread(counterSynchronized);
            Thread.sleep(20);
            thread.start();
        }
        Thread.sleep(2000);
        System.out.println("############################");
        Printer<String> printer1 = new Printer<>();
        printer1.setData("我是1号打印机");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 50, 2000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        // submit()提交的不是线程,而是可执行类对象
        Future<String> future = threadPoolExecutor.submit(printer1);
        Printer<String> printer2 = new Printer<>();
        printer2.setData("我是2号打印机");
        FutureTask<String> futureTask = new FutureTask<>(printer2);
        // 若提交的是FutureTask,那么返回值将会被封装进去
        threadPoolExecutor.submit(futureTask);
        while (!future.isDone()) {
            Thread.sleep(20);
        }
        while (!futureTask.isDone()) {
            Thread.sleep(20);
        }
        System.out.println(future.get());
        System.out.println(futureTask.get());
        Printer<String> printer3 = new Printer<>();
        printer3.setData("我是3号打印机");
        Printer<String> printer4 = new Printer<>();
        printer4.setData("我是4号打印机");
        Printer<String> printer5 = new Printer<>();
        printer5.setData("我是5号打印机");
        List<Callable<String>> tasks = new ArrayList<>();
        tasks.add(printer1);
        tasks.add(printer2);
        tasks.add(printer3);
        tasks.add(printer4);
        tasks.add(printer5);
        // 使用工厂方法创建线程池
        ExecutorService executorService = Executors.newScheduledThreadPool(10);
        // 提交任务列表,返回结果集
        List<Future<String>> futures = executorService.invokeAll(tasks);
        Thread.sleep(2000);
        System.out.println("############################");
        System.out.println("并发执行线程:");
        for (Future result : futures) {
            System.out.println(result.get());
        }
        threadPoolExecutor.shutdown();
        executorService.shutdown();
        ForkJoin forkJoin = new ForkJoin(0, 1000);
        forkJoin.fork();
        Thread.sleep(2000);
        System.out.println("############################");
        System.out.println("0-1000的合为: "+forkJoin.join());
        Thread.sleep(2000);
        System.out.println("############################");
        BlockQueue blockQueue = new BlockQueue();
        blockQueue.work();
    }
}
