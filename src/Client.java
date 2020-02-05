import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author SuanCaiYv
 * @time 2020/2/3 下午4:16
 */
public class Client
{
    public static void main(String[] args) throws IOException
    {
        new Client().run();
    }
    public void run() throws IOException
    {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 8189));
        socketChannel.configureBlocking(false);
        if (socketChannel.isConnected()) {
            Scanner scanner = new Scanner(System.in);
            ByteBuffer byteBufferRead = ByteBuffer.allocate(1024);
            ByteBuffer byteBufferWrite;
            socketChannel.read(byteBufferRead);
            byteBufferRead.clear();
            while (true) {
                String str = scanner.nextLine().trim();
                byteBufferWrite = ByteBuffer.wrap(str.getBytes());
                socketChannel.write(byteBufferWrite);
                byteBufferWrite.clear();
                byteBufferRead.clear();
                int readable = socketChannel.read(byteBufferRead);
                if ( readable != -1) {
                    str = new String(byteBufferRead.array(), 0, readable, StandardCharsets.UTF_8);
                    System.out.println("Server: "+str);
                }
            }
        }
    }
}
