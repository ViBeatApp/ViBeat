package serverModule;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.json.JSONException;


public class readWriteAux {
	SocketChannel socket;
	
	public readWriteAux() throws IOException {
		socket = SocketChannel.open(new InetSocketAddress("localhost", 9999));
	}
	
	public Command recieve() throws IOException, JSONException {
		return readSocket(socket);
	}
	
	public void send(Command cmd) throws IOException, JSONException {
		writeSocket(socket,cmd);
	}
	
	public static Command readSocket(SocketChannel channel) throws IOException, JSONException{
		int size = readSize(channel);
		if (size == -1) return new Command(CommandType.DISCONNECTED);
		return readCommand(channel,size);
	}
	
	public static int readSize(SocketChannel channel) {
		int bytesRead = 0;
		ByteBuffer buf = ByteBuffer.allocate(4);		
		while (buf.hasRemaining()) { 
			try {
				bytesRead += channel.read(buf);
			} 
			catch (IOException e) {
				return -1;
			}
		}
		if(bytesRead < 4) {
			System.out.println("error readSize");
		}
		buf.rewind();
		return buf.getInt();
	}

	public static Command readCommand(SocketChannel channel,int length) throws JSONException, IOException {
		int bytesRead = 0;
		ByteBuffer buf = ByteBuffer.allocate(length);	
		while (buf.hasRemaining()) { 
			bytesRead += channel.read(buf);
		}
		if(bytesRead != length) {
			System.out.println("error - bytesRead != length");
			return null;
		}
		buf.rewind();
		return new Command(buf.array());
	}
	
	public static int writeSocket(SocketChannel channel,Command cmd) throws JSONException{
		byte[] byteArray = cmd.commandTobyte();
		int size = byteArray.length;
		ByteBuffer commandBuf = ByteBuffer.wrap(byteArray);
		ByteBuffer message = ByteBuffer.allocate(4+size).putInt(size).put(commandBuf);
		message.flip();
		int writeRead = 0;
		while(message.hasRemaining()) {
			try {
				writeRead += channel.write(message);
			} 
			catch (IOException e) {
				return -1;
			}
		}
		return writeRead;
	}
}
