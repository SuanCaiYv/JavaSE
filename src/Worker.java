/**
 * @author joker
 * @time 2019/12/16 下午4:50
 */
public class Worker
{
    private WorkerListener listener;
    public void addListener(WorkerListener listener)
    {
        this.listener = listener;
    }
    // 事件源(worker)执行事件
    public void working()
    {
        Event event = new Event(this);
        System.out.println("工人开始工作了");
        if (listener != null) {
            // 监听器开始监听...
            listener.working(event);
        }
        // 事件源执行事件
        event.doWork();
    }
    // 事件源(worker)执行事件
    public void resting()
    {
        Event event = new Event(this);
        System.out.println("工人开始休息了");
        if (listener != null) {
            // 监听器开始监听...
            listener.resting(event);
        }
        // 事件源执行事件
        event.doRest();
    }
}
