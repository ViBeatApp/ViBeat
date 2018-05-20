import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.json.JSONException;


public class readWriteAux {

	public static Command readSocket(SocketChannel channel) throws IOException, JSONException{
		ByteBuffer buf = ByteBuffer.allocate(1000);
		int bytesRead = channel.read(buf);
		if(bytesRead == -1) return null;
		System.out.println("reading " + bytesRead + " bytes from client.");
		buf.flip();
		return new Command(buf.array());
	}

	public static void writeSocket(SocketChannel channel,Command cmd) throws IOException, JSONException{
		byte[] byteArray = cmd.commandTobyte();
		ByteBuffer buf = ByteBuffer.wrap(byteArray);
		int writeRead = 0;
		while(buf.hasRemaining()) {
			writeRead += channel.write(buf);
		}
		System.out.println("writing " + writeRead + " bytes to client.");
	}
}
