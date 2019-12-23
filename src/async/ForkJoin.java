package async;

import java.util.concurrent.RecursiveTask;

public class ForkJoin extends RecursiveTask<Integer>
{
    private Integer from;
    private Integer to;
    public ForkJoin() {}
    public ForkJoin(Integer from, Integer to)
    {
        this.from = from;
        this.to = to;
    }
    @Override
    protected Integer compute()
    {
        if (to-from < 10) {
            Integer sum = 0;
            for (int i = from; i <= to; ++ i) {
                sum += i;
            }
            return sum;
        }
        else {
            Integer mid = (from+to)/2;
            ForkJoin left = new ForkJoin(from, mid);
            ForkJoin right = new ForkJoin(mid+1, to);
            // 或者invokeAll(left, right);
            left.fork();
            right.fork();
            return left.join()+right.join();
        }
    }
}
