
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class UDPclient 
{

	private DatagramSocket socket;
	
	public UDPclient() throws SocketException
	{
		socket= new DatagramSocket();
		socket.setSoTimeout(1000);
	}
	
	public void closeSocket()
	{
		socket.close();
	}
	
	public long sendAndReceive(String host, int port, String messaggio) throws UnsupportedEncodingException, UnknownHostException, IOException
	{
		byte[] bufferRequest=new byte[8192];
		byte[] bufferAnswer=new byte[8];
		DatagramPacket request;
		DatagramPacket answer;
		ByteBuffer data = ByteBuffer.allocate(8);
		
		InetAddress address=InetAddress.getByName(host);
		long rispostaServer = 0;
		
		bufferRequest=messaggio.getBytes("ISO-8859-1");
		request=new DatagramPacket(bufferRequest, bufferRequest.length, address, port);
		answer=new DatagramPacket(bufferAnswer, bufferAnswer.length);
		socket.send(request);
		socket.receive(answer);
		if (answer.getAddress().getHostAddress().compareTo(host)==0 && answer.getPort()==port)
		{
			data.clear();
			data.put(answer.getData());
		}
		closeSocket();
		data.flip();
		rispostaServer=data.getLong();
		return rispostaServer;
	}
	
	public static void main(String[] args) 
	{
		long rispostaServer;
		String host="127.0.0.1";
		int port=2000;
		String messaggio="timestamp";
		try 
		{
			UDPclient echoClient=new UDPclient();
			rispostaServer=echoClient.sendAndReceive(host, port, messaggio);
			System.out.println("Numero di secondi trascorsi dal 1 gennaio 1970: "+rispostaServer);
		} 
		catch (SocketTimeoutException e)
		{
			System.err.println("Timeout");
		}
		catch (SocketException e)
		{
			System.err.println("Impossibile istanziare il socket");
		} 
		catch (UnsupportedEncodingException e)
		{
			System.err.println("Charset non supportato");
		} 
		catch (UnknownHostException e) 
		{
			System.err.println("Host sconosciuto");
		} 
		catch (IOException e) 
		{
			System.err.println("Errore generico di I/O");
		}
		

	}

}
