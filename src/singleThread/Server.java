package singleThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author SuanCaiYv
 * @time 2020/2/3 下午4:16
 */
public class Server
{
    /**
     * 不管是Acceptor还是Handler用的都是同一个Selector(轮询器)
     */
    private Selector selector;

    public static void main(String[] args) throws IOException
    {
        new Server().run();
    }
    public Server() throws IOException
    {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8189));
        selector = Selector.open();
        serverSocketChannel.configureBlocking(false);
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 绑定处理器
        selectionKey.attach(new Acceptor(selectionKey, selector));
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
 * 用于处理连接被建立时的情况
 * Runnable纯粹作为接口使用
 */
class Acceptor implements Runnable
{
    private SelectionKey selectionKey;
    private Selector selector;
    public Acceptor(SelectionKey selectionKey, Selector selector)
    {
        this.selectionKey = selectionKey;
        this.selector = selector;
    }
    @Override
    public void run()
    {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        try {
            // 获取与客户端连接的管道流
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);

            // 0代表接受任意类型的事件
            SelectionKey selectionKey0 = socketChannel.register(selector, 0);
            // 给此SelectionKey绑定一个处理器
            selectionKey0.attach(new Handler(selectionKey0, selector));
            // 准备接受读事件
            selectionKey0.interestOps(SelectionKey.OP_READ);
            // 唤醒轮询器
            selector.wakeup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 用于处理IO事件
 * Runnable作用同上
 */
class Handler implements Runnable
{
    private SelectionKey selectionKey;
    private static final int READ = 1;
    private static final int WRITE = 2;
    private int state = READ;
    private ByteBuffer byteBufferRead = ByteBuffer.allocate(1024);
    private String msg;

    public Handler(SelectionKey selectionKey, Selector selector)
    {
        this.selectionKey = selectionKey;
    }

    @Override
    public void run()
    {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        if (!socketChannel.isConnected()) {
            System.out.println("连接关闭");
            return ;
        }
        if (state == READ) {
            try {
                read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (state == WRITE) {
            try {
                write();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void read() throws IOException
    {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        byteBufferRead.clear();
        int readable = socketChannel.read(byteBufferRead);
        msg = new String(byteBufferRead.array(), 0, readable, StandardCharsets.UTF_8);
        System.out.println("Client: "+msg);
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        state = WRITE;
    }
    public void write() throws IOException
    {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBufferWrite = ByteBuffer.wrap(msg.getBytes());
        socketChannel.write(byteBufferWrite);
        byteBufferWrite.clear();
        selectionKey.interestOps(SelectionKey.OP_READ);
        state = READ;
    }
}
