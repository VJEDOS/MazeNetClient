package de.vjedos.main;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import de.vjedos.client.Client;
import de.vjedos.config.Settings;

public class Main {

	public static void main(String[] args) throws UnknownHostException, IOException
	{
		Socket s = new Socket("localhost", Settings.PORT);
		Client c = new Client(s);
		c.login();
	}
}
