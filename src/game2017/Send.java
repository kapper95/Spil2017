package game2017;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Send {

	private ArrayList<Socket> sockets = new ArrayList<>();
	private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

	public void addSocket(Socket socket) {
		sockets.add(socket);
		System.out.println(socket.getInetAddress());
		TakeFromQueue tfq = new TakeFromQueue();
		tfq.start();
	}

	public void updatePos(String pos) {
		try {
			queue.put(pos);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class TakeFromQueue extends Thread {

		@Override
		public void run() {

			while (true) {
				String pos = null;
				try {
					pos = queue.take();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (pos != null) {
					for (Socket s : sockets) {
						try {

							DataOutputStream outToClient = new DataOutputStream(s.getOutputStream());
							outToClient.writeBytes(pos + "\n");
						} catch (IOException e) {
							sockets.remove(s);
							e.printStackTrace();
						}

					}
				}
			}
		}

	}

}
