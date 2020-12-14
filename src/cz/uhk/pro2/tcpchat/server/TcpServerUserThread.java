package cz.uhk.pro2.tcpchat.server;


/**
 * Thread that handles a single connected client
 */

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;

public class TcpServerUserThread extends Thread {

    private final Socket connectedClientSocket;
    private final MessageBroacaster broacaster;


    public TcpServerUserThread(Socket connectedClientSocket, MessageBroacaster broadcaster) {
        this.connectedClientSocket = connectedClientSocket;
        this.broacaster = broadcaster;
    }

    @Override
    public void run() {
        try {
            InputStream is = connectedClientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String message;
            while ((message = reader.readLine()) != null){
                // TODO "/time" od klienta -> odpovíme mu, kolik je hodin
                //  "/quit" od klienta -> ukončíme komunikaci s klientem (vyskočíme z while cyklu)
                switch (message){
                    case "/time":
                        OutputStream outputStream = connectedClientSocket.getOutputStream();
                        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream), true);
                        printWriter.println(LocalTime.now());
                        break;
                    case "/quit":
                        reader.close();
                        break;
                }

                System.out.println("New message received: " + message + " " + connectedClientSocket);
                broacaster.broadcastMessage(message);
            }
            System.out.println("UserThread ended " + connectedClientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
