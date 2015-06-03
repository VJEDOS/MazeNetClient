package de.vjedos.client;

import java.io.IOException;
import java.net.Socket;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import de.vjedos.model.LoginMessageType;
import de.vjedos.model.MazeCom;
import de.vjedos.networking.*;

public class Client 
{
	XmlOutStream outStream;
	
	public Client(Socket s) throws IOException
	{
		outStream = new XmlOutStream(s.getOutputStream());
	}
	
	public void login() throws IOException
	{
		try 
		{
			JAXBContext context = JAXBContext.newInstance( MazeCom.class );
			
			MazeCom mc = new MazeCom();
			mc.setMcType(de.vjedos.model.MazeComType.LOGIN);
			
			LoginMessageType login = new LoginMessageType();
			login.setName("VJEDOS");
			
			mc.setLoginMessage(login);
			
			outStream.write(mc);
			
		}
		catch (JAXBException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
