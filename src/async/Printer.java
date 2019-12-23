package async;

import java.util.concurrent.Callable;

public class Printer<T> implements Callable<T>
{
    private T data;
    public Printer()
    {
        String msg = "我是打印机";
        this.data = (T) msg;
    }
    public Printer(T data)
    {
        this.data = data;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    @Override
    public T call() throws Exception
    {
        // 模拟运行耗时
        Thread.sleep(200);
        return this.data;
    }
}
