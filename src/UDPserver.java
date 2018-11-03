
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

public class UDPserver extends Thread
{

	private DatagramSocket socket;
	
	public UDPserver (int port) throws SocketException
	{
		socket=new DatagramSocket(port);
		socket.setSoTimeout(1000);
		
	}
	
	public void run()
	{
		byte[] bufferRequest= new byte[8192];
		byte[] bufferAnswer= new byte[8];
		DatagramPacket request=new DatagramPacket(bufferRequest, bufferRequest.length);
		DatagramPacket answer;
		long timestamp;
		ByteBuffer data=ByteBuffer.allocate(8);
		
		while (!interrupted())
		{
			try 
			{
				socket.receive(request);
				//answer=new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
				timestamp=System.currentTimeMillis()/1000;
				data.clear();
				data.putLong(timestamp);
				data.flip(); //attenzione: prima di leggere è necessario flip()!
				answer= new DatagramPacket(data.array(),data.array().length, request.getAddress(),request.getPort());
				socket.send(answer);		
			} 
			catch (SocketTimeoutException e) 
			{
				System.err.println("Timeout");
			}
			catch (IOException e) 
			{
				
				e.printStackTrace();
			}
		}
		closeSocket();
		
	}
	
	public void closeSocket()
	{
		socket.close();
	}
	public static void main(String[] args)
	{
		ConsoleInput tastiera= new ConsoleInput();
		try 
		{
			UDPserver echoServer= new UDPserver(2000);
			echoServer.start();
			tastiera.readLine();
			echoServer.interrupt();
			
		} 
		catch (SocketException e) 
		{
			System.err.println("Impossibile istanziare il socket");
		} 
		catch (IOException e) 
		{
			System.out.println("Errore generico di I/O dalla tastiera");
		}

	}

}
