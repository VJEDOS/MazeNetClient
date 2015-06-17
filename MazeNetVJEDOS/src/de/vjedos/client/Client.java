package de.vjedos.client;

import java.io.*;
import java.net.Socket;
import java.util.List;

import javax.xml.bind.*;

import de.vjedos.config.Settings;
import de.vjedos.model.BoardType;
import de.vjedos.model.LoginMessageType;
import de.vjedos.model.MazeCom;
import de.vjedos.model.MazeComType;
import de.vjedos.model.ObjectFactory;
import de.vjedos.model.TreasureType;
import de.vjedos.model.TreasuresToGoType;
import de.vjedos.networking.*;

public class Client 
{
	 Socket socket;
	 DataOutputStream outstream;
	 DataInputStream instream;
	 UTFOutputStream utf_outstream;
	 UTFInputStream utf_instream;
	 ObjectFactory objectFactory;
	 JAXBContext context;
	 Marshaller marshaller;
	 Unmarshaller unmarshaller;
	 ByteArrayOutputStream byteOutStream;
	 ByteArrayInputStream byteInStream;
	 String out_string;
	 String in_string;
	 MazeCom out_message;
	 MazeCom in_message;
	 
	 BoardType board;
	 List<TreasureType> found; //those treasures already found
	 TreasureType treasure; //no idea
	 List<TreasuresToGoType> treasuresToGo;
	 
	 int id;
	
	public Client(Socket s) throws IOException, JAXBException
	{
		socket = s;
		outstream = new DataOutputStream(socket.getOutputStream());
		instream = new DataInputStream(socket.getInputStream());
		utf_outstream = new UTFOutputStream(outstream);
		utf_instream = new UTFInputStream(instream);
		
		objectFactory = new ObjectFactory();
		context = JAXBContext.newInstance(MazeCom.class);
		
		marshaller = context.createMarshaller();
		unmarshaller = context.createUnmarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	}
	
	public void login() throws IOException, JAXBException, InterruptedException
	{
		out_message = objectFactory.createMazeCom();
		
		boolean b = false;
		while(true)
		{
			byteOutStream = new ByteArrayOutputStream();
			
			LoginMessageType loginType = new LoginMessageType();
			loginType.setName(Settings.NAME);
			out_message.setLoginMessage(loginType);
			out_message.setMcType(MazeComType.LOGIN);
			
			// Marshalling
			marshaller.marshal(out_message, byteOutStream);
			out_string = new String(byteOutStream.toByteArray());
			utf_outstream.writeUTF8(out_string);
			
			// Unmarshal Reply
			in_string = utf_instream.readUTF8();
			byteInStream = new ByteArrayInputStream(in_string.getBytes());
			in_message = (MazeCom) unmarshaller.unmarshal(byteInStream);
			
			// Check server reply
			switch(in_message.getMcType())
			{
				case LOGINREPLY:
					// Login okay
					id = in_message.getId();
					b = true;
					break;
				case ACCEPT:
					// Noch einmal versuchen
					break;
				case DISCONNECT:
					// Zu viele versuche
					return;
				default:
					break;
			}
			if(b)
			{
				break;
			}
		}
		
		try 
		{
			gameLoop();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public void gameLoop() throws Exception
	{
		while(true)
		{
			in_string = utf_instream.readUTF8();
			byteInStream = new ByteArrayInputStream(in_string.getBytes());
			in_message = (MazeCom) unmarshaller.unmarshal(byteInStream);
			
			switch(in_message.getMcType())
			{
				case AWAITMOVE:
					board = in_message.getAwaitMoveMessage().getBoard();
					found = in_message.getAwaitMoveMessage().getFoundTreasures();
					treasure = in_message.getAwaitMoveMessage().getTreasure();
					treasuresToGo = in_message.getAwaitMoveMessage().getTreasuresToGo();
					break;
					
				case WIN:
					int winner = in_message.getWinMessage().getWinner().getId();
					if(winner == id)
					{
						System.out.println("Gewonnen");
					}
					else
					{
						System.out.println("Verloren");
					}
					break;
				case ACCEPT:
					//whether or not our move is acceptable
					if (!in_message.getAcceptMessage().isAccept()) 
					{
						move();
					}
					break;
					
				case DISCONNECT:
					//too many tries
					return;
			}
		}
	}
	
	private void move()
	{
		
	}
}
