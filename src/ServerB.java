import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerB
{
    private static final int PORT = 8189;
    private static final int BYTE_SIZE = 1024;
    public void run()
    {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.socket().bind(new InetSocketAddress(PORT));server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server Register");
            String msg = null;
            SocketChannel channel1 = SocketChannel.open();
            SocketChannel channel2 = SocketChannel.open();
            channel1.configureBlocking(false);
            channel2.configureBlocking(false);
            channel1.register(selector, SelectionKey.OP_WRITE);
            channel2.register(selector, SelectionKey.OP_READ);
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isWritable()) {
                    System.out.println("W");
                }
                if (selectionKey.isReadable()) {
                    System.out.println("R");
                }
                if (selectionKey.isAcceptable()) {
                    System.out.println("A");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args)
    {
        ServerB server = new ServerB();
        server.run();
    }
}

