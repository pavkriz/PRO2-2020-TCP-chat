package cz.uhk.pro2.tcpchat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * thread that handles a single connected client
 */

public class TCPServerUserThread extends Thread {

    private final Socket connectedClientSocket;
    private final MessageBroadcaster broadcaster;

    public TCPServerUserThread(Socket connectedClientSocket, MessageBroadcaster broadcaster) {
        this.connectedClientSocket = connectedClientSocket;
        this.broadcaster = broadcaster;
    }

    @Override
    public void run() {
        try {
            InputStream is = connectedClientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is)); // readline() hazi null kdyz dojde na konec souboru/nebo spojeni, je to jedno
            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equals("/time")) {
                    broadcaster.broadcastMessage("Nyni je: " + ZonedDateTime.now(ZoneId.of("Europe/Prague")).format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.uuuu")),connectedClientSocket);
                }
                if (message.equals("/quit")) {
                    broadcaster.broadcastMessage("Sbohem",connectedClientSocket);
                    break;
                }
                System.out.println("New message received " + message + " " + connectedClientSocket);
                broadcaster.broadcastMessage(message,connectedClientSocket);
            }
            System.out.println("User thread ended " + connectedClientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
