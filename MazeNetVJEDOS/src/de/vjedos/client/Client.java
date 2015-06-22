package de.vjedos.client;

import java.io.*;
import java.net.Socket;
import java.util.List;

import javax.xml.bind.*;

import de.vjedos.ai.AI;
import de.vjedos.config.Settings;
import de.vjedos.model.AIOutModel;
import de.vjedos.model.BoardType;
import de.vjedos.model.LoginMessageType;
import de.vjedos.model.MazeCom;
import de.vjedos.model.MazeComType;
import de.vjedos.model.MoveMessageType;
import de.vjedos.model.ObjectFactory;
import de.vjedos.model.PositionType;
import de.vjedos.model.TreasureType;
import de.vjedos.model.TreasuresToGoType;
import de.vjedos.networking.*;

public class Client 
{
	 Socket socket;
	 DataOutputStream outstream;
	 DataInputStream instream;
	 UTFOutputStream utfOutputStream;
	 UTFInputStream utfInputStream;
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
	 List<TreasureType> found; //gefundene Schätze
	 TreasureType treasure; //aktueller Schatz
	 List<TreasuresToGoType> treasuresToGo; // noch zu findende
	 
	 int id;
	
	public Client(Socket s) throws IOException, JAXBException
	{
		socket = s;
		outstream = new DataOutputStream(socket.getOutputStream());
		instream = new DataInputStream(socket.getInputStream());
		utfOutputStream = new UTFOutputStream(outstream);
		utfInputStream = new UTFInputStream(instream);
		
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
			utfOutputStream.writeUTF8(out_string);
			
			// Unmarshal Reply
			in_string = utfInputStream.readUTF8();
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
			in_string = utfInputStream.readUTF8();
			byteInStream = new ByteArrayInputStream(in_string.getBytes());
			in_message = (MazeCom) unmarshaller.unmarshal(byteInStream);
			
			switch(in_message.getMcType())
			{
				case AWAITMOVE:
					board = in_message.getAwaitMoveMessage().getBoard();
					found = in_message.getAwaitMoveMessage().getFoundTreasures();
					treasure = in_message.getAwaitMoveMessage().getTreasure();
					treasuresToGo = in_message.getAwaitMoveMessage().getTreasuresToGo();
					move();
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
					if (!in_message.getAcceptMessage().isAccept()) 
					{
						move();
					}
					break;
				case DISCONNECT:
					return;
			}
		}
	}
	
	private void move()
	{
		AI ai = new AI();
		AIOutModel model = ai.move(board, found, treasure, treasuresToGo);
		
		byteOutStream = new ByteArrayOutputStream();
		
		out_message = objectFactory.createMazeCom();
		out_message.setMcType(MazeComType.MOVE);

		MoveMessageType moveType = new MoveMessageType();
		
		PositionType playerPosition = new PositionType();
		playerPosition.setCol(model.player_column);
		playerPosition.setRow(model.player_row);
		
		PositionType cardPosition = new PositionType();
		cardPosition.setCol(model.card_column);
		cardPosition.setRow(model.card_row);
		
		moveType.setNewPinPos(playerPosition);
		moveType.setShiftPosition(cardPosition);
		moveType.setShiftCard(model.shiftCard);

		out_message.setMoveMessage(moveType);
		try 
		{
			marshaller.marshal(out_message, byteOutStream);
			out_string = new String(byteOutStream.toByteArray());
			utfOutputStream.writeUTF8(out_string);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
