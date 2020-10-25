package multiThread;

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
 * @time 2020/2/5 下午3:36
 */
public class Server
{
    private Selector selector;
    public static void main(String[] args) throws IOException
    {
        new Server().run();
    }
    public Server() throws IOException
    {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.bind(new InetSocketAddress(8189));
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor(selector, selectionKey));
    }
    public void run() throws IOException
    {
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            List<SelectionKey> list = new ArrayList<>(selectionKeys.size());
            for (SelectionKey selectionKey : selectionKeys) {
                dispatch(selectionKey);
                list.add(selectionKey);
            }
            // 清空已处理过的SelectionKey
            for (SelectionKey selectionKey : list) {
                selectionKeys.remove(selectionKey);
            }
            // 直接clear()似乎有点不太合适，因为如果在处理时又有新的Key那不也给清空了吗？
        }
    }
    public void dispatch(SelectionKey selectionKey)
    {
        Runnable runnable = (Runnable) selectionKey.attachment();
        if (runnable != null) {
            runnable.run();
        }
    }
}

/**
 * 此处的Runnable纯粹作为接口使用, 不是多线程
 */
class Acceptor implements Runnable
{
    private Selector selector;
    private SelectionKey selectionKey;

    public Acceptor(Selector selector, SelectionKey selectionKey)
    {
        this.selector = selector;
        this.selectionKey = selectionKey;
    }

    @Override
    public void run()
    {
        try {
            SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
            socketChannel.configureBlocking(false);
            // 注册到Selector
            SelectionKey selectionKey0 = socketChannel.register(selector, 0);
            selectionKey0.interestOps(SelectionKey.OP_READ);
            selectionKey0.attach(new Handler(selectionKey0));
            // 唤醒轮询器
            selector.wakeup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * Runnable作用同上
 */
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
