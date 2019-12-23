/**
 * @author joker
 * @time 2019/12/16 下午4:51
 */
public class Boss
{
    public static void main(String[] args)
    {
        // 创建工人类(事件源)
        Worker worker = new Worker();
        // 创建监听器
        WorkerListener listener = new ListenerInstance();
        // 添加监听器
        worker.addListener(listener);
        // 执行方法(触发事件)
        worker.working();
        worker.resting();
    }
}
