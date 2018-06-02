package serverObjects;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;

import org.json.JSONException;


public class ReadWriteAux {
	SocketChannel socket;
	
	public ReadWriteAux(String ipAddress) throws IOException {
		socket = SocketChannel.open(new InetSocketAddress(ipAddress, 2000));
	}
	
	public Command recieve() throws IOException, JSONException {
		return readSocket(socket);
	}
	
	public int send(Command cmd) throws JSONException {
		return writeSocket(socket,cmd);
	}
	
	public static Command readSocket(SocketChannel channel) throws IOException, JSONException{
		return readCommand(channel,readSize(channel));
	}
	
	public static int readSize(SocketChannel channel) {
		int bytesRead = 0;
		ByteBuffer buf = ByteBuffer.allocate(4);		
		while (buf.hasRemaining()) { 
			try {
				bytesRead += channel.read(buf);
				if(bytesRead < 0)
					return -1;
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
		if(length == -1)
			return new Command(CommandType.DISCONNECTED);
		int bytesRead = 0;
		ByteBuffer buf = ByteBuffer.allocate(length);	
		while (buf.hasRemaining()) { 
			bytesRead += channel.read(buf);
			if(bytesRead < 0)
				return new Command(CommandType.DISCONNECTED);
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
			catch (NotYetConnectedException e) {
				return -1;
			}
		}
		return writeRead;
	}
}
