package game2017;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) throws IOException {

		ServerSocket welcomSocket = new ServerSocket(6789);

		Send send = new Send();

		while (true) {
			Socket connectionSocket = welcomSocket.accept();

			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			Thread recievie = new Recievie(connectionSocket, send);
			send.addSocket(connectionSocket);
			recievie.start();

		}

	}

}
