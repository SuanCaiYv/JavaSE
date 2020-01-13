import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author SuanCaiYv
 * @time 2020/1/13 下午3:29
 */
public class Main
{
    public static void main(String[] args)
    {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(()->{
            System.out.println("csw是猪");
        });
        service.shutdown();
        Runnable runnable = ()->{
            System.out.println("asdf");
        };
        FuncTwo funcTwo = (String msg) -> {
            System.out.println(msg);
        };
        funcTwo.print("测试文本");
        FuncOne funcOne = (a, b)-> {
            return a+b;
        };
        System.out.println(funcOne.add(1, 2));
    }
}
