/**
 * @author joker
 * @time 2019/12/16 下午4:50
 */

public class Event
{
    private Worker worker;
    public Event(Worker worker)
    {
        this.worker = worker;
    }

    public void setWorker(Worker worker)
    {
        this.worker = worker;
    }

    public Worker getWorker()
    {
        return worker;
    }
    // 一个普通的事件
    public void doWork()
    {
        System.out.println("工作中......");
    }
    // 又一个普通的事件
    public void doRest()
    {
        System.out.println("休息中......");
    }
}
