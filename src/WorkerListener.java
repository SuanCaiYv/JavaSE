/**
 * @author joker
 * @time 2019/12/16 下午4:51
 */
public interface WorkerListener
{
    // 监听器接口,避免过度耦合
    void working(Event event);
    void resting(Event event);
}
