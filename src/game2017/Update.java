package game2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Platform;

public class Update extends Thread {

	private Socket clientSocket;
	private Main main;
	ArrayList<String> names = new ArrayList<>();

	public Update(Socket clientSocket, String name, Main main) {
		this.clientSocket = clientSocket;
		this.main = main;
		names.add(name);
	}

	@Override
	public void run() {

		String modifiedSentence;

		while (true) {
			try {
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				modifiedSentence = inFromServer.readLine();
				String[] info = modifiedSentence.split(" ");

				if (!names.contains(info[2])) {
					names.add(info[2]);
					Player player = new Player(info[2], Integer.parseInt(info[0]), Integer.parseInt(info[1]), info[5]);
					Main.players.add(player);

					Platform.runLater(() -> {
						main.spawnPlayer(Integer.parseInt(info[0]), Integer.parseInt(info[1]));
					});

				}
				Platform.runLater(() -> {
					main.playerMoved(info[2], Integer.parseInt(info[3]), Integer.parseInt(info[4]), info[5]);
				});

			} catch (IOException e) {
				try {
					clientSocket.close();
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
}