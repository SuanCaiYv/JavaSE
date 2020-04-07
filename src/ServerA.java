import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class ServerA implements Runnable
{
    private static final int PORT = 8189;
    private static final int BYTE_SIZE = 1024;
    @Override
    public void run()
    {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.socket().bind(new InetSocketAddress(PORT));
            SelectionKey aSelectionKey = server.register(selector, SelectionKey.OP_ACCEPT);
            String msg = null;
            int cnt = 0;
            while (true) {
                // 此方法会发生阻塞,比如有一个读操作,但没东西可读,就会阻塞, 有一个accept操作,但是没有客户端过来
                long startTime = System.currentTimeMillis();
                selector.select();
                // 此size()代表已注册的管道流的个数,而不是就绪的个数
                System.out.println("Set size:"+selector.keys().size());
                System.out.println("这里被select()阻塞了: "+(System.currentTimeMillis()-startTime)+"ms");
                System.out.println("第"+cnt+"次轮徇");
                // 此集合代表就绪的管道,既已经注册且可以实行注册事件操作的管道流,比如注册为"读"且此管道可读,那么这就是一个就绪的管道流
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        doAccept(key);
                    }
                    if (key.isReadable()) {
                        msg = doRead(key);
                    }
                    if (key.isWritable()) {
                        assert msg != null;
                        doWrite(key, msg);
                    }
                }
                ++cnt;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void doAccept(SelectionKey selectionKey) throws IOException
    {
        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
        // 返回一个新的套接字连接实例
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        client.register(selectionKey.selector(), SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }
    public String doRead(SelectionKey selectionKey) throws IOException
    {
        SocketChannel client = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(BYTE_SIZE);
        long readLength = client.read(byteBuffer);
        String msg = null;
        while (readLength > 0) {
            byteBuffer.flip();
            byte[] data = byteBuffer.array();
            msg = new String(data).trim();
            System.out.println("Client says: "+msg);
            byteBuffer.clear();
            readLength = client.read(byteBuffer);
        }
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        channel.configureBlocking(false);
        // 对管道流进行重新设置属性,不会新建管道流,只会改变它的事件属性
        channel.register(selectionKey.selector(), SelectionKey.OP_WRITE);
        return msg;
    }
    public void doWrite(SelectionKey selectionKey, String msg) throws IOException
    {
        if (msg == null) {
            msg = "default";
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(BYTE_SIZE);
        SocketChannel client = (SocketChannel) selectionKey.channel();
        byteBuffer.put(msg.getBytes());
        byteBuffer.flip();
        client.write(byteBuffer);
        byteBuffer.compact();
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        channel.configureBlocking(false);
        channel.register(selectionKey.selector(), SelectionKey.OP_READ);
    }
}
