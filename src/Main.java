import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ServerA server = new ServerA();
        executorService.submit(server);
        executorService.shutdown();
    }
}
