import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Client implements Runnable
{
    private static final int PORT = 8189;
    private static final int BYTE_SIZE = 1024;
    private static final Scanner scanner = new Scanner(System.in);
    @Override
    public synchronized void run()
    {
        SocketChannel client = null;
        Selector selector = null;
        try {
            selector = Selector.open();
            client = SocketChannel.open();
            client.configureBlocking(false);
            client.connect(new InetSocketAddress("localhost", PORT));
            client.register(selector, SelectionKey.OP_CONNECT);
            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isConnectable()) {
                        doConnect(key);
                    }
                    if (key.isWritable()) {
                        doWrite(key);
                    }
                    if (key.isReadable()) {
                        doRead(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void doConnect(SelectionKey selectionKey) throws IOException
    {
        SocketChannel client = (SocketChannel) selectionKey.channel();
        client.configureBlocking(false);
        client.register(selectionKey.selector(), SelectionKey.OP_WRITE);
        if (client.isConnectionPending()){
            client.finishConnect();
        }
        System.out.println("Client connect successfully!");
    }
    public void doWrite(SelectionKey selectionKey) throws IOException
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(BYTE_SIZE);
        SocketChannel client = (SocketChannel) selectionKey.channel();
        byteBuffer.clear();
        String msg = scanner.nextLine();
        byteBuffer.put(msg.getBytes());
        byteBuffer.flip();
        client.write(byteBuffer);
        client.configureBlocking(false);
        client.register(selectionKey.selector(), SelectionKey.OP_READ);
    }
    public void doRead(SelectionKey selectionKey) throws IOException
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(BYTE_SIZE);
        SocketChannel client = (SocketChannel) selectionKey.channel();
        byteBuffer.clear();
        client.read(byteBuffer);
        byte[] data = byteBuffer.array();
        String msg = new String(data).trim();
        System.out.println("Server says: "+msg);
        client.configureBlocking(false);
        client.register(selectionKey.selector(), SelectionKey.OP_WRITE);
    }
}
