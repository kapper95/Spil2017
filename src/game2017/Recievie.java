package game2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Recievie extends Thread
{

    private Socket clientSocket;
    private Send send;

    public Recievie(Socket clientSocket, Send send)
    {
        this.clientSocket = clientSocket;
        this.send = send;
    }

    @Override
    public void run()
    {

        String modifiedSentence;

        while (true) {
            try {
                BufferedReader inFromServer = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                modifiedSentence = inFromServer.readLine();
                System.out.println(modifiedSentence);
                send.updatePos(modifiedSentence);

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