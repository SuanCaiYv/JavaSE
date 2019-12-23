/**
 * @author joker
 * @time 2019/12/16 下午4:53
 */
public class ListenerInstance implements WorkerListener
{
    @Override
    // 监听器具体方法
    public void working(Event event)
    {
        // 虽然传入的是事件,但是可以获得事件源
        Worker worker = event.getWorker();
        System.out.println("工人工作提示: 不要让他们过度劳动,要让他们及时休息");
    }

    @Override
    // 监听器具体方法
    public void resting(Event event)
    {
        Worker worker = event.getWorker();
        System.out.println("工人休息提示: 给工人提供茶水, 避免他们口渴");
    }
}
