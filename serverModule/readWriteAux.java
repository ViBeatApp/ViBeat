import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class readWriteAux {

	public static byte[] readSocket(SocketChannel channel) throws IOException{
		ByteBuffer buf = ByteBuffer.allocate(256);
		int bytesRead = channel.read(buf);
		if(bytesRead == -1) return null;
		System.out.println("reading " + bytesRead + " bytes from client.");
		buf.flip();
		return buf.array();
	}

	public static void writeSocket(SocketChannel channel,byte[] byteArray) throws IOException{
		ByteBuffer buf = ByteBuffer.wrap(byteArray);
		int writeRead = 0;
		while(buf.hasRemaining()) {
			writeRead += channel.write(buf);
		}
		System.out.println("writint " + writeRead + " bytes to client.");
	}
}
