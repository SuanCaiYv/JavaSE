package mainSubstrThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author SuanCaiYv
 * @time 2020/2/5 下午4:41
 */
public class Server
{
    private Selector bossSelector;
    private Selector workSelector;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    public static void main(String[] args) throws IOException
    {
        new Server().run();
    }
    public Server() throws IOException
    {
        bossSelector = Selector.open();
        workSelector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8189));
        serverSocketChannel.configureBlocking(false);
        SelectionKey selectionKey = serverSocketChannel.register(bossSelector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor(selectionKey, workSelector));
    }
    public void run()
    {
        BossSelector bossSelector_ = new BossSelector(bossSelector);
        WorkSelector workSelector_ = new WorkSelector(workSelector);
        executorService.execute(bossSelector_);
        executorService.execute(workSelector_);
    }
    public static void dispatch(SelectionKey selectionKey)
    {
        Runnable runnable = (Runnable) selectionKey.attachment();
        if (runnable != null) {
            runnable.run();
        }
    }
}
class BossSelector implements Runnable
{
    private Selector bossSelector;

    public BossSelector(Selector bossSelector)
    {
        this.bossSelector = bossSelector;
    }

    @Override
    public void run()
    {
        try {
            while (true) {
                bossSelector.select();
                Set<SelectionKey> selectionKeys = bossSelector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    Server.dispatch(selectionKey);
                }
                selectionKeys.clear();
                bossSelector.wakeup();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class WorkSelector implements Runnable
{
    private Selector workSelector;

    public WorkSelector(Selector workSelector)
    {
        this.workSelector = workSelector;
    }

    @Override
    public void run()
    {
        try {
            while (true) {
                workSelector.select();
                Set<SelectionKey> selectionKeys = workSelector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    Server.dispatch(selectionKey);
                }
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class Acceptor implements Runnable
{
    private SelectionKey selectionKey;
    private Selector workSelector;

    public Acceptor(SelectionKey selectionKey, Selector workSelector)
    {
        this.selectionKey = selectionKey;
        this.workSelector = workSelector;
    }

    @Override
    public void run()
    {
        try {
            SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
            socketChannel.configureBlocking(false);
            SelectionKey selectionKey0 = socketChannel.register(workSelector, 0);
            selectionKey0.interestOps(SelectionKey.OP_READ);
            selectionKey0.attach(new Handler(selectionKey0));
            // 记得唤醒因为阻塞时间长而睡眠的workSelector
            workSelector.wakeup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class Handler implements Runnable
{
    private SelectionKey selectionKey;
    private static final int READ = 1;
    private static final int WRITE = 2;
    private int state;
    private String msg;
    private ExecutorService executorService = Executors.newFixedThreadPool(6);

    public Handler(SelectionKey selectionKey)
    {
        this.selectionKey = selectionKey;
        this.state = READ;
    }
    /**
     * 相较于单线程模式, 这里使用线程池去完成IO操作, 所以是多线程的
     */
    @Override
    public void run()
    {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        if (state == READ) {
            try {
                Future<String> future = executorService.submit(new Read(socketChannel));
                msg = future.get();
                System.out.println("Client: "+msg);
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                state = WRITE;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        else if (state == WRITE) {
            executorService.execute(new Write(socketChannel, msg));
            selectionKey.interestOps(SelectionKey.OP_READ);
            state = READ;
        }
    }
}
class Read implements Callable<String>
{
    private SocketChannel socketChannel;

    public Read(SocketChannel socketChannel)
    {
        this.socketChannel = socketChannel;
    }

    @Override
    public String call() throws Exception
    {
        synchronized (this) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int readable = socketChannel.read(byteBuffer);
            return new String(byteBuffer.array(), 0, readable, StandardCharsets.UTF_8);
        }
    }
}
class Write implements Runnable
{
    private SocketChannel socketChannel;
    private String msg;

    public Write(SocketChannel socketChannel, String msg)
    {
        this.socketChannel = socketChannel;
        this.msg = msg;
    }

    @Override
    public synchronized void run()
    {
        ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
        try {
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
